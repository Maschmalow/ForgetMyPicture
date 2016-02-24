package net.tenwame.forgetmypicture;

import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class Searcher {

    private static final String TAG = Searcher.class.getSimpleName();
    private static final String URL = "https://www.google.fr/search";
    private static final long DELAY = 60000; //time in milli sec between each requests (1 min)

    private static long lastRequest = System.currentTimeMillis() - DELAY; //make first request start immediately
    private static long curRequestId = 0;

    private String userAgent;
    private Map<String, String> queryData;
    private Set<Result> results;
    private long requestId;

    private Searcher() {
        requestId = curRequestId++;
        results = new HashSet<>();
        queryData = new HashMap<>();
        queryData.put("tbm", "isch");
        queryData.put("safe", "off");
    }


    public static void startSearch(String[] keywords) {
        if(!UserData.getInstance().isSet()) {
            Log.e(TAG, "Search aborted: user data is not set.");
            return;
        }
        new AsyncTask<String, Void, Void>() {
            @Override
            protected final Void doInBackground(String... keywords) {
                new Searcher().startSearchOnThread(Arrays.asList(keywords));
                return null;
            }
        }.execute(keywords);
    }

    private void startSearchOnThread(List<String> keywords) {
        for(List<String> subset : powerSet(keywords)) {
            setCurKeywords(subset);
            setCurUserAgent();
            delay();
            handleNewResults(scrapeData());
        }
    }

    private void handleNewResults(List<Result> scraped) {
        List<Result> toSend = new ArrayList<>();
        for(Result result : scraped)
            if(results.add(result))
                toSend.add(result);
        //serv.send(toSend, requestId);
    }


    private List<Result> scrapeData() { //// TODO: 23/02/2016 thread safety
        lastRequest = System.currentTimeMillis();
        List<Result> results = new ArrayList<>(); //should this be a set?
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

        Log.i(TAG,"\nParsed: " + results.size() + "results.\n");
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

    private void delay() { //TODO: thread safety
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

        private Result(String picURL, String picRefURL) {
            this.picRefURL = picRefURL;
            this.picURL = picURL;
        }

        public String getPicRefURL() {
            return picRefURL;
        }

        public String getPicURL() {
            return picURL;
        }

        // equals and hashCode are used in hashSet, so make sur this is what we want
        @Override
        public boolean equals(Object o) {
            return this == o || !(o == null || getClass() != o.getClass()) && picURL.equals(((Result) o).picURL);
        }

        @Override
        public int hashCode() {
            return picURL.hashCode();
        }
    }

    //I would like this to be hidden somewhere in a library, but I couldn't find anything.
    private static <T> List<List<T>> powerSet(Collection<T> list) {
        List<List<T>> ps = new ArrayList<>();
        ps.add(new ArrayList<T>());   // add the empty set

        // for every item in the original list
        for (T item : list) {
            List<List<T>> newPs = new ArrayList<>();

            for (List<T> subset : ps) {
                // copy all of the current powerSet's subsets
                newPs.add(subset);

                // plus the subsets appended with the current item
                List<T> newSubset = new ArrayList<>(subset);
                newSubset.add(item);
                newPs.add(newSubset);
            }

            // powerSet is now powerSet of list.subList(0, list.indexOf(item)+1)
            ps = newPs;
        }
        return ps;
    }
}
