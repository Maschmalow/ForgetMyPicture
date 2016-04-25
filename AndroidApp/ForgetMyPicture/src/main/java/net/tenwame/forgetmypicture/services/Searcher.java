package net.tenwame.forgetmypicture.services;

import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.util.Log;

import net.tenwame.forgetmypicture.DatabaseHelper;
import net.tenwame.forgetmypicture.ForgetMyPictureApp;
import net.tenwame.forgetmypicture.UserData;
import net.tenwame.forgetmypicture.Util;
import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Antoine on 16/02/2016. (see {@Link NetworkService} )
 * Network Service that do Google searches on behalf of the user
 * and update requests accordingly
 * This service has only one action (execute one search) with no parameters
 * This service should not be called more than once each {@Link SEARCH_DELAY} ms,
 * or the user can be blacklisted from Google
 */
public class Searcher extends NetworkService{
    private static final String TAG = Searcher.class.getSimpleName();

    public static final String ACTION_SEARCH = ForgetMyPictureApp.getName() + ".search";

    private static final String URL = "https://www.google.fr/search";
    public static final long SEARCH_DELAY = 100*60; //time in ms between each search
    public static final int AVG_RESULTS_NB = 100;  //average number of results per search (for display)

    private final  DatabaseHelper helper = ForgetMyPictureApp.getHelper();
    private final Map<String, String> queryData; //google search parameters
    private String userAgent;
    private Request curRequest;

    public Searcher() {
        super(TAG);
        handlers.put(ACTION_SEARCH, search);
        queryData = new HashMap<>();
        queryData.put("tbm", "isch");  //image search
        queryData.put("safe", "off");  //obviously
        queryData.put("num", String.valueOf(AVG_RESULTS_NB)); //most of the time, this is ignored
        queryData.put("qws_rd", "ssl"); //it appears that search can sometimes fail without this
    }


    public static void execute() {
        execute(Searcher.class, ACTION_SEARCH);
    }


    private NetworkService.ActionHandler search = new NetworkService.ActionHandler() {
        @Override
        public void handle(Bundle params) throws Exception {
            // TODO: 10/04/2016 get initial request without explicit id
            curRequest = helper.getRequestDao().queryForId(1);
            if(curRequest == null) {
                Log.i(TAG, "No request to process");
                return;
            }

            for( Request request : helper.getRequestDao() )
                if( request.getStatus() == Request.Status.FETCHING )
                    if( request.getProgress() < curRequest.getProgress() )
                        curRequest = request;
            if(curRequest.getStatus() != Request.Status.FETCHING) {
                Log.i(TAG, "All requests finished");
                return;
            }

            doSearch();
        }
    };



    private void doSearch() throws Exception {
        int progress = curRequest.getProgress();
        setCurKeywords(Util.powerSetAtIndex(curRequest.getKeywords(), progress));
        setCurUserAgent();
        Set<Result> newResults = curRequest.addResults(scrapeData());
        ServerInterface.execute(ServerInterface.ACTION_FEED, curRequest, newResults);
        curRequest.setProgress(progress + 1);
        curRequest.updateStatus();

        helper.getRequestDao().update(curRequest);
    }


    /*
     * Retrieve a Result Set from current keywords
     */
    private Set<Result> scrapeData() throws IOException {
        Set<Result> results = new HashSet<>(); //convert this to a list to support server prioritisation
        final Document doc;

        doc = Jsoup.connect(URL).data(queryData).userAgent(userAgent).get();
        Log.d(TAG, "Request: " + doc.baseUri());

        //get picture link
        for( Element elem : doc.select("div.rg_di.rg_el.ivg-i > a[href]")) {
            UrlQuerySanitizer query = new UrlQuerySanitizer(elem.attr("href"));
            if(query.hasParameter("imgurl") && query.hasParameter("imgrefurl")) {
                try { //all intersting data is within link query
                    new URL(query.getValue("imgurl"));
                    new URL(query.getValue("imgrefurl"));
                } catch (MalformedURLException e) {
                    Log.w(TAG, "scrapeData: Invalid result: " + query, e);
                    continue; //ignore this result
                }
                results.add(new Result(query.getValue("imgurl"), query.getValue("imgrefurl"), curRequest));
            }
        }

        if(results.isEmpty()) {
            Log.w(TAG, "No results");
        }

        Log.d(TAG, "Parsed: " + results.size() + " results.");
        return results;
    }


    // this is hard to set, but it seems that it should be changed from times to times
    // (rather than used the phone default)
    private void setCurUserAgent() {
        userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A";
    }


    private void setCurKeywords(Collection<String> keywords) {
        String joined = UserData.getUser().getForename() + " " + UserData.getUser().getName() + " ";
        for( String keyword : keywords ) {
            joined += keyword + " ";
        }
        queryData.put("q", joined);
    }


}
