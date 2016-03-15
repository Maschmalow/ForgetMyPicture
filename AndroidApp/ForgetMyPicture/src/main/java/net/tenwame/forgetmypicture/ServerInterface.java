package net.tenwame.forgetmypicture;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.Stack;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by Antoine on 24/02/2016.
 * class to use when communicating with the back-end server
 */
public class ServerInterface {
    private static final String TAG = ServerInterface.class.getSimpleName();

    private static final String BASE_URL = "https://api.tenwame.net";
    private static final String REGISTER_URL = "/register";
    private static final String NEW_REQUEST_URL = "/newRequest";
    private static final String FEED_URL = "/feed";
    private static final String GET_INFO_URL = "/getInfo";

    private static UserData userData = UserData.getInstance();


    private static void registerASync() throws IOException{
        Integer hash = 1;

        Connection connection = Jsoup.connect(BASE_URL + REGISTER_URL)
                .data("h", hash.toString())
                .data("deviceId", UserData.getDeviceId());

        Stack<InputStream> streams = new Stack<>();
        for(UserData.UserProperty<?> property : userData.getProperties()) {
            if(property.isAssignableFrom(String.class)) {
                connection.data(property.getName(), (String) property.getValue());
            } else {
                streams.push(UserData.openFile(property.getURI()));
                connection.data(property.getName(), property.getURI(), streams.peek());
            }
        }

        connection.post();

        for(InputStream stream : streams)
            stream.close();


        Log.d(TAG, "Device " + UserData.getDeviceId() + " successfully registered.");
    }

    private static void newRequestASync(SearchData.Request request) throws IOException{
        Jsoup.connect(BASE_URL + NEW_REQUEST_URL)
                .data("deviceId", UserData.getDeviceId())
                .data("requestId", request.getId().toString()).post();

        Log.d(TAG, "new request registered: " + request.getId());
    }

    private static void feedNewResultsASync(Collection<SearchService.Result> results, SearchData.Request request) throws IOException{
        Connection connection = Jsoup.connect(BASE_URL + FEED_URL)
                .data("deviceId", UserData.getDeviceId())
                .data("requestId", request.getId().toString());
        for(SearchService.Result result : results)
            connection.data("result", result.getPicURL());
        connection.post();

        Log.d(TAG, "Sent " + results.size() + " results for request " + request.getId());
    }

    private static void getRequestInfoASync() throws IOException{
        String resp = Jsoup.connect(BASE_URL + GET_INFO_URL)
                .ignoreContentType(true)
                .method(Connection.Method.GET)
                .data("deviceId", UserData.getDeviceId()).execute().body();

        JsonObject update = Json.createReader(new StringReader(resp)).readObject();
        for(String strId : update.keySet()) {
            SearchData.Request request = SearchData.getRequest(Integer.valueOf(strId));
            JsonObject requestUpdate = update.getJsonObject(strId);
            for(String resultURL : requestUpdate.keySet());

        }
        //TODO: parse and update info
        //Log.d(TAG, "Request " + request.getId());
    }


    public static void register() {
        Log.d(TAG, "register");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    registerASync();
                } catch (IOException e) {
                    throw new RuntimeException("Error in register()", e);
                }
                return null;
            }
        }.execute((Void) null);
    }

    public static void newRequest(final SearchData.Request request) {
        Log.d(TAG, "registering new request " + request.getId());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    newRequestASync(request);
                } catch (IOException e) {
                    throw new RuntimeException("Error in feedNewResults()", e);
                }
                return null;
            }
        }.execute((Void) null);
    }

    public static void feedNewResults(final Collection<SearchService.Result> results, final SearchData.Request request) {
        Log.d(TAG, "feeding " + results.size() + " results for request " + request.getId());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    feedNewResultsASync(results, request);
                } catch (IOException e) {
                    throw new RuntimeException("Error in feedNewResults()", e);
                }
                return null;
            }
        }.execute((Void) null);
    }

    public static void getRequestInfo() {
        Log.d(TAG, "info update");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    getRequestInfoASync();
                } catch (IOException e) {
                    throw new RuntimeException("Error in getRequestInfo()", e);
                }
                return null;
            }
        }.execute((Void) null);
    }

    

}
