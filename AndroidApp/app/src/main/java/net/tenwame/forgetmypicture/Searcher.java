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
import java.util.List;
import java.util.Map;

/**
 * Created by Antoine on 16/02/2016.
 * Class that performs actual requests and send results to the server
 * Only one instance at a time
 */
public class Searcher {

    private static final String TAG = Searcher.class.getCanonicalName();
    private static final String URL = "https://www.google.fr/search";
    private static final long DELAY = 360000; //time in milli sec between each requests (6 min)

    private static Searcher instance = null;
    public static Searcher getInstance() {
        if(instance == null)
            synchronized (Searcher.class) {
                if(instance == null)
                    instance = new Searcher();
            }

        return instance;
    }

    private long lastRequest;
    private String userAgent;
    private Map<String, String> queryData;

    private Searcher() {
        queryData = new HashMap<>();
        queryData.put("tbm", "isch");
        queryData.put("safe", "off");
    }


    public void startSearch(String[] keywords) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected final Void doInBackground(String... keywords) {
                startSearchOnThread(Arrays.asList(keywords));
                return null;
            }
        }.execute(keywords);
    }

    public void startSearchOnThread(List<String> keywords) {
        for(List<String> subset : powerSet(keywords)) {
            if(subset.size() == 0) continue; // we can remove this once name has been made permanent keyword
            setCurKeywords(subset);
            setCurUserAgent();
            scrapeData();
            delay();
        }
    }


    private List<Result> scrapeData() {
        lastRequest = System.currentTimeMillis();
        List<Result> results = new ArrayList<>();
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
        String joined = "";
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

        @Override
        public boolean equals(Object o) {
            return !(o == null || (o.getClass() != this.getClass())) &&
                    picURL.equals(((Result) o).picURL);
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
