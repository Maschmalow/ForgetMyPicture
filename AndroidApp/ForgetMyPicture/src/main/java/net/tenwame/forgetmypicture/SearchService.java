package net.tenwame.forgetmypicture;

import android.app.IntentService;
import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.util.Log;

import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Antoine on 16/02/2016.
 * class that do Google searches on behalf of the user
 * and update requests accordingly
 */
public class SearchService extends IntentService{
    private static final String TAG = SearchService.class.getSimpleName();

    private static final String URL = "https://www.google.fr/search";
    public static final long SEARCH_DELAY = 100*60; //time in ms between each search

    private DatabaseHelper helper = ForgetMyPictureApp.getHelper();
    private String userAgent;
    private Map<String, String> queryData;
    private Request curRequest;

    public SearchService() {
        super(TAG);
        queryData = new HashMap<>();
        queryData.put("tbm", "isch");
        queryData.put("safe", "off");
        queryData.put("num", "100");
        queryData.put("qws_rd", "ssl"); //try to see if it works without this
    }


    public static void execute() {
        ForgetMyPictureApp.getContext().startService(new Intent(ForgetMyPictureApp.getContext(), SearchService.class).setPackage(ForgetMyPictureApp.getName()));
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "started");
        try { // TODO: 10/04/2016 get initial request without explicit id
            curRequest = helper.getRequestDao().queryForId(1);
        } catch (SQLException e) {
            Manager.getInstance().notify(Manager.Event.SEARCHER_EXCEPTION);
            Log.e(TAG, "Could not fetch requests", e);
            return;
        }
        if(curRequest == null) {
            Log.i(TAG, "No request to process");
            return;
        }

        for( Request request : helper.getRequestDao() )
            if( request.getStatus() == Request.Status.FETCHING )
                if( request.getProgress() < curRequest.getProgress() )
                    curRequest = request;
        if(curRequest.getStatus() != Request.Status.FETCHING)
            return; //no request to process

        doSearch();
    }


    private void doSearch() {
        int progress = curRequest.getProgress();
        setCurKeywords(Util.powerSet(curRequest.getKeywords()).get(progress));
        setCurUserAgent();
        Set<Result> results = scrapeData();
        if(results.isEmpty()) {
            Log.w(TAG, "doSearch: No results");
            return;
        }
        feedServer(curRequest.addResults(results));
        curRequest.setProgress(progress + 1);
        curRequest.updateStatus();

        try {
            helper.getRequestDao().update(curRequest);
        } catch (SQLException e) {
            Manager.getInstance().notify(Manager.Event.SEARCHER_EXCEPTION);
            Log.e(TAG, "doSearch: Could not save or update request " + curRequest.getId(), e);
        }
    }


    private Set<Result> scrapeData() {
        Set<Result> results = new HashSet<>(); //convert this to a list to support server prioritisation
        final Document doc;

        try {
            doc = Jsoup.connect(URL).data(queryData).userAgent(userAgent).get();
        } catch (IOException e) {
            Manager.getInstance().notify(Manager.Event.SEARCHER_EXCEPTION);
            Log.e(TAG, "scrapeData: Could not start search", e);
            return results;
        }
        Log.d(TAG, "Request: " + doc.baseUri());


        for( Element elem : doc.select("div.rg_di.rg_el.ivg-i > a[href]")) {
            UrlQuerySanitizer query = new UrlQuerySanitizer(elem.attr("href"));
            if(query.hasParameter("imgurl") && query.hasParameter("imgrefurl")) {
                try {
                    new URL(query.getValue("imgurl"));
                    new URL(query.getValue("imgrefurl"));
                } catch (MalformedURLException e) {
                    Log.e(TAG, "scrapeData: Invalid result: " + query, e);
                    continue; //ignore this result
                }
                results.add(new Result(query.getValue("imgurl"), query.getValue("imgrefurl"), curRequest));
            }
        }

        Log.d(TAG, "Parsed: " + results.size() + " results.");
        return results;
    }

    private void feedServer(Set<Result> newResults) {
        ArrayList<String> idSet = new ArrayList<>(newResults.size()); //used to map id's
        for(Result result : newResults)
            idSet.add(result.getPicURL());
        Bundle params = new Bundle();
        params.putInt(ServerInterface.EXTRA_REQUEST_ID_KEY, curRequest.getId());
        params.putStringArrayList(ServerInterface.EXTRA_RESULTS_KEY, idSet);
        ServerInterface.execute(ServerInterface.ACTION_FEED, params);
    }

    private void setCurUserAgent() { //TODO: check what is really needed here
        userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1";
    }


    private void setCurKeywords(Collection<String> keywords) {
        String joined = UserData.getUser().getForename() + " " + UserData.getUser().getName() + " ";
        for( String keyword : keywords ) {
            joined += keyword + " ";
        }
        queryData.put("q", joined);
    }


}
