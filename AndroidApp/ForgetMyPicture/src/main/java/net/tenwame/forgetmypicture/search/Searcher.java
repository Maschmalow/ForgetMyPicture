package net.tenwame.forgetmypicture.search;

import android.app.IntentService;
import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.util.Log;

import net.tenwame.forgetmypicture.ServerInterface;
import net.tenwame.forgetmypicture.UserData;
import net.tenwame.forgetmypicture.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Antoine on 16/02/2016.
 * Class that performs actual requests and send results to the server
 * Only one instance per search, in one thread.
 * Only static data should be shared inbetween threads
 * limited to one instance for now
 */
public class Searcher extends IntentService{

    private static final String TAG = Searcher.class.getSimpleName();
    private static final String URL = "https://www.google.fr/search";
    private static final long DELAY = 60000; //time in milli sec between each requests (1 min)

    private static long lastRequest = System.currentTimeMillis() - DELAY; //make first request start immediately

    private String userAgent;
    private Map<String, String> queryData;
    private List<Data.Search> searches;
    private Data.Search curSearch;

    public Searcher() {
        super("SearcherService");
        searches = Data.getSearches();
        queryData = new HashMap<>();
        queryData.put("tbm", "isch");
        queryData.put("safe", "off");
        queryData.put("qws_rd", "ssl"); //try to see if it works without this
    }


    @Override
    protected void onHandleIntent(Intent intent) {
    }



    private void startSearch() {
        for(List<String> subset : Util.powerSet(curSearch.getKeywords())) {
            setCurKeywords(subset);
            setCurUserAgent();
            delay();
            handleNewResults(scrapeData());
        }
    }

    private void handleNewResults(Set<Result> scraped) {
        ServerInterface.feedNewResults(curSearch.addResults(scraped), curSearch.getId());
    }


    private Set<Result> scrapeData() {
        lastRequest = System.currentTimeMillis();
        Set<Result> results = new HashSet<>(); //convert this to a list to support server prioritisation
        final Document doc;

        try {
            doc = Jsoup.connect(URL).data(queryData).userAgent(userAgent).get();
        } catch (IOException e) {
            Log.e(TAG, "Could not start search.", e);
            return results;
        }
        Log.d(TAG, "Request:\n" + doc.baseUri());


        for( Element elem : doc.select("div.rg_di.rg_el.ivg-i > a")) {
            UrlQuerySanitizer query = new UrlQuerySanitizer(elem.attr("href"));
            results.add(new Result(query.getValue("imgurl"), query.getValue("imgrefurl")));
        }

        Log.d(TAG,"\nParsed: " + results.size() + "results.");
        return results;
    }

    private void setCurUserAgent() {
        userAgent = "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/43.0.2357.65 Mobile Safari/535.19";
    }


    private void setCurKeywords(Collection<String> keywords) {
        String joined = UserData.getInstance().getForename() + " " + UserData.getInstance().getName() + " ";
        for( String keyword : keywords ) {
            joined += keyword + " ";
        }
        queryData.put("q", joined);
    }

    private void delay() {
        long elapsedTime = System.currentTimeMillis() - lastRequest;
        while( elapsedTime < DELAY) {
            try {
                Thread.sleep(DELAY - elapsedTime);
            } catch (InterruptedException e) {
                elapsedTime = System.currentTimeMillis() - lastRequest;
            }
        }

    }

    static public class Result{

        private String picURL;
        private String picRefURL;
        private double match;

        Result(String picURL, String picRefURL) {
            this.picRefURL = picRefURL;
            this.picURL = picURL;
            match = -1;
        }

        public double getMatch() {
            return match;
        }

        public void setMatch(double match) {
            this.match = match;
        }

        public String getPicRefURL() {
            return picRefURL;
        }

        public String getPicURL() {
            return picURL;
        }

        // equals and hashCode are used in hashSet, so make sure this is what we want
        @Override
        public boolean equals(Object o) {
            return this == o || !(o == null || getClass() != o.getClass()) && picURL.equals(((Result) o).picURL);
        }

        @Override
        public int hashCode() {
            return picURL.hashCode();
        }
    }

}
