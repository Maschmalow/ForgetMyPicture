package net.tenwame.forgetmypicture;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends Activity {

    private static final String TAG = SearchActivity.class.getCanonicalName();
    private static final String URL = "https://www.google.fr/search";
    private static final String userAgent = "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/43.0.2357.65 Mobile Safari/535.19";
    private static final Map<String, String> queryData = new HashMap<>();
    static {
        queryData.put("tbm", "isch");
        queryData.put("safe", "off");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    private List<Result> scrapeData() {
        List<Result> results = new ArrayList<>();
        Document doc;

        try {
            doc = Jsoup.connect(URL).data(queryData).userAgent(userAgent).get();
        } catch (IOException e) {
            Log.e(TAG,"Could not start search.", e);
            return results;
        }

        return results;
    }


    private void setKeywords(Collection<String> keywords) {
        String joined = "";
        for( String keyword : keywords ) {
            joined += keyword + " ";
        }
        queryData.put("q", joined);
    }

    static public class Result{

        private String picDirectURL;
        private String picSourceURL;


        public String getPicSourceURL() {
            return picSourceURL;
        }

        public String getPicDirectURL() {
            return picDirectURL;
        }

    }
}
