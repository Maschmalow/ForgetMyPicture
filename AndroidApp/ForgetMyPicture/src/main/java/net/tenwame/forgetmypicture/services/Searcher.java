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
 * Created by Antoine on 16/02/2016.
 * class that do Google searches on behalf of the user
 * and update requests accordingly
 */
public class Searcher extends NetworkService{
    private static final String TAG = Searcher.class.getSimpleName();

    public static final String ACTION_SEARCH = ForgetMyPictureApp.getName() + ".search";

    private static final String URL = "https://www.google.fr/search";
    public static final long SEARCH_DELAY = 100*60; //time in ms between each search
    public static final int AVG_RESULTS_NB = 100;  //average number of results per search

    private final  DatabaseHelper helper = ForgetMyPictureApp.getHelper();
    private final Map<String, String> queryData;
    private String userAgent;
    private Request curRequest;

    public Searcher() {
        super(TAG);
        handlers.put(ACTION_SEARCH, search);
        queryData = new HashMap<>();
        queryData.put("tbm", "isch");
        queryData.put("safe", "off");
        queryData.put("num", "100"); //most of the time, this is ignored
        queryData.put("qws_rd", "ssl"); //try to see if it works without this
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


    private Set<Result> scrapeData() throws IOException {
        Set<Result> results = new HashSet<>(); //convert this to a list to support server prioritisation
        final Document doc;

        doc = Jsoup.connect(URL).data(queryData).userAgent(userAgent).get();
        Log.d(TAG, "Request: " + doc.baseUri());


        for( Element elem : doc.select("div.rg_di.rg_el.ivg-i > a[href]")) {
            UrlQuerySanitizer query = new UrlQuerySanitizer(elem.attr("href"));
            if(query.hasParameter("imgurl") && query.hasParameter("imgrefurl")) {
                try {
                    new URL(query.getValue("imgurl"));
                    new URL(query.getValue("imgrefurl"));
                } catch (MalformedURLException e) {
                    Log.w(TAG, "scrapeData: Invalid result: " + query, e);
                    continue; //ignore this result
                }
                results.add(new Result(query.getValue("imgurl"), query.getValue("imgrefurl"), curRequest));
            }
        }

        if(results.isEmpty())
            throw new RuntimeException("No results found");

        Log.d(TAG, "Parsed: " + results.size() + " results.");
        return results;
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
