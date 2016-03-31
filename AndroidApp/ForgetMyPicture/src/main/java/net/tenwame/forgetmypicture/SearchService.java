package net.tenwame.forgetmypicture;

import android.app.IntentService;
import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.util.Log;

import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Antoine on 16/02/2016.
 * service that do Google searches on behalf of the user
 * and update requests accordingly
 */
public class SearchService extends IntentService{
    private static final String TAG = SearchService.class.getSimpleName();

    private static final String URL = "https://www.google.fr/search";
    private static final long DELAY = 100*60; //time in ms between each search

    private static long lastRequest;

    private DatabaseHelper helper;
    private String userAgent;
    private Map<String, String> queryData;
    private Request curRequest;

    public SearchService() {
        super(TAG + "Service");
        queryData = new HashMap<>();
        queryData.put("tbm", "isch");
        queryData.put("safe", "off");
        queryData.put("qws_rd", "ssl"); //try to see if it works without this
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        helper = ForgetMyPictureApp.getHelper();

        try {
            curRequest = helper.getRequestDao().queryForId(0);
        } catch (SQLException e) {
            throw new RuntimeException("Could not start service", e);
        }
        if( curRequest == null) return;

        while(curRequest.getStatus() == Request.Status.FETCHING) {

            for( Request request : helper.getRequestDao() )
                if( request.getStatus() == Request.Status.FETCHING )
                    if( request.getProgress() < curRequest.getProgress() )
                        curRequest = request;

            doSearch();
        }

    }



    private void doSearch() {
        int progress = curRequest.getProgress();
        setCurKeywords(Util.powerSet(curRequest.getKeywords()).get(progress));
        setCurUserAgent();
        Set<Result> newResults = curRequest.addResults(scrapeData());
        ServerInterface.feedNewResults(newResults, curRequest);
        curRequest.setProgress(++progress);
        curRequest.updateStatus();

        try {
            helper.getRequestDao().update(curRequest);
            delay();
            helper.getRequestDao().refresh(curRequest);
        } catch (SQLException e) { //restart service
            throw new RuntimeException("Could not save or update request", e);
        }
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
            results.add(new Result(query.getValue("imgurl"), query.getValue("imgrefurl"), curRequest));
        }

        Log.d(TAG, "\nParsed: " + results.size() + "results.");
        return results;
    }

    private void setCurUserAgent() { //TODO: fetch an adequate user-agent from user default browser
        userAgent = "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/43.0.2357.65 Mobile Safari/535.19";
    }


    private void setCurKeywords(Collection<String> keywords) {
        String joined = UserData.getUser().getForename() + " " + UserData.getUser().getName() + " ";
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


}
