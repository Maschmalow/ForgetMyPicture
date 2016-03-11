package net.tenwame.forgetmypicture;

import android.os.AsyncTask;
import android.util.Log;

import net.tenwame.forgetmypicture.search.Searcher;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Stack;

/**
 * Created by Antoine on 24/02/2016.
 * class to use when communicating with the back-end server
 */
public class ServerInterface {
    private static final String TAG = ServerInterface.class.getSimpleName();

    private static final String BASE_URL = "https://api.tenwame.net";
    private static final String REGISTER_URL = "/register";
    private static final String FEED_URL = "/feed";
    private static final String GET_INFO_URL = "/getinfo";

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

    private static void feedNewResultsrASync(Collection<Searcher.Result> results, Integer requestId) throws IOException{
        Connection connection = Jsoup.connect(BASE_URL + FEED_URL)
                .data("deviceId", UserData.getDeviceId())
                .data("requestId", requestId.toString());
        for(Searcher.Result result : results)
            connection.data("result", result.getPicURL());
        connection.post();

        Log.d(TAG, "Sent " + results.size() + " results for request " + requestId);
    }

    private static void getSearchInforASync(Integer requestId) throws IOException{
        Document doc = Jsoup.connect(BASE_URL + GET_INFO_URL)
                .data("deviceId", UserData.getDeviceId())
                .data("requestId", requestId.toString()).get();

        //TODO: parse and return info
        Log.d(TAG, "New status for request " + requestId);
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

    public static void feedNewResults(final Collection<Searcher.Result> results, final Integer requestId) {
        Log.d(TAG, "feeding " + results.size() + " results for request " + requestId);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    feedNewResultsrASync(results, requestId);
                } catch (IOException e) {
                    throw new RuntimeException("Error in feedNewResults()", e);
                }
                return null;
            }
        }.execute((Void) null);
    }

    public static void getSearchInfo(final Integer requestId) {
        Log.d(TAG, "get info for request " + requestId);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    getSearchInforASync(requestId);
                } catch (IOException e) {
                    throw new RuntimeException("Error in getSearchInfo()", e);
                }
                return null;
            }
        }.execute((Void) null);
    }

    

}
