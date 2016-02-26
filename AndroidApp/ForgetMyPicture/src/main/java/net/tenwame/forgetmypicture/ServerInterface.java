package net.tenwame.forgetmypicture;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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


    public static void register() throws IOException{ //TODO: See? please, make UserData great again
        Integer hash = 1;

        Connection connection = Jsoup.connect(BASE_URL + REGISTER_URL)
                .data("h", hash.toString())
                .data("deviceId", UserData.getDeviceId());

        Stack<InputStream> streams = new Stack<>();
        for(UserData.UserProperty<?> property : userData.getProperties()) {
            streams.push(UserData.openFile(property.getURI()));
            connection.data(property.getName(), property.getURI(), streams.peek());
        }

        connection.post();

        for(InputStream stream : streams)
            stream.close();


        Log.d(TAG, "Device " + UserData.getDeviceId() + " successfully registered.");
    }

    public static void feedNewResults(Collection<Searcher.Result> results, Integer requestId) throws IOException{
        Connection connection = Jsoup.connect(BASE_URL + FEED_URL)
                .data("deviceId", UserData.getDeviceId())
                .data("requestId", requestId.toString());
        for(Searcher.Result result : results)
            connection.data("result", result.getPicURL());
        connection.post();

        Log.d(TAG, "Sent " + results.size() + " results for request " + requestId);
    }

    public static void getSearchInfo(Integer requestId) throws IOException{
        Document doc = Jsoup.connect(BASE_URL + GET_INFO_URL)
                .data("deviceId", UserData.getDeviceId())
                .data("requestId", requestId.toString()).get();

        //TODO: parse and return info
        Log.d(TAG, "New status for request " + requestId);
    }

}
