package net.tenwame.forgetmypicture;

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;
import net.tenwame.forgetmypicture.database.Selfie;
import net.tenwame.forgetmypicture.database.User;

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

    private static final String BASE_URL = "http://adurand00005.rtrinity.enseirb.fr";
    private static final String REGISTER_URL = "/register.php";
    private static final String NEW_REQUEST_URL = "/new_request.php";
    private static final String FEED_URL = "/feed.php";
    private static final String GET_INFO_URL = "/get_info.php";



    private static void registerASync() throws IOException{
        if(Looper.myLooper() == Looper.getMainLooper()) {
            Log.e(TAG, "Server interaction must be done in a separate thread");
            return;
        }

        DatabaseHelper helper = OpenHelperManager.getHelper(ForgetMyPictureApp.getContext(), DatabaseHelper.class);
        User user = UserData.getInstanceUser(helper);

        Integer hash = 1; //TODO
        Connection connection = Jsoup.connect(BASE_URL + REGISTER_URL)
                .data("h", hash.toString())
                .data("deviceId", user.getDeviceId())
                .data("email", user.getEmail());

        Stack<InputStream> streams = new Stack<>();
        for(Selfie selfie : user.getSelfies()) {
            streams.push(selfie.getPic().openStream());
            connection.data("selfie[]", "selfie_" + streams.size(), streams.peek());
        }

        connection.post();

        for(InputStream stream : streams)
            stream.close();


        Log.d(TAG, "Device " + UserData.getDeviceId() + " successfully registered.");
    }

    private static void newRequestASync(Request request) throws IOException{
        if(Looper.myLooper() == Looper.getMainLooper()) {
            Log.e(TAG, "Server interaction must be done in a separate thread");
            return;
        }
        Connection connection = Jsoup.connect(BASE_URL + NEW_REQUEST_URL)
                .data("deviceId", UserData.getDeviceId())
                .data("requestId", request.getId().toString());

        if(request.getKind() == Request.Kind.QUICK) {
            InputStream stream = request.getOriginalPic().openStream();
            connection.data("originalPic", "originalPic", stream).post();
            stream.close();
        } else {
            connection.post();
        }


        Log.d(TAG, "new request registered: " + request.getId());
    }

    private static void feedNewResultsASync(Collection<Result> results, Request request) throws IOException{
        if(Looper.myLooper() == Looper.getMainLooper()) {
            Log.e(TAG, "Server interaction must be done in a separate thread");
            return;
        }
        Connection connection = Jsoup.connect(BASE_URL + FEED_URL)
                .data("deviceId", UserData.getDeviceId())
                .data("requestId", request.getId().toString());
        for(Result result : results)
            connection.data("result[]", result.getPicURL());
        connection.post();

        Log.d(TAG, "Sent " + results.size() + " results for request " + request.getId());
    }

    private static void getRequestInfoASync() throws IOException{
        if(Looper.myLooper() == Looper.getMainLooper()) {
            Log.e(TAG, "Server interaction must be done in a separate thread");
            return;
        }
        DatabaseHelper helper = OpenHelperManager.getHelper(ForgetMyPictureApp.getContext(), DatabaseHelper.class);
        String resp = Jsoup.connect(BASE_URL + GET_INFO_URL)
                .ignoreContentType(true)
                .method(Connection.Method.GET)
                .data("deviceId", UserData.getDeviceId()).execute().body();

        JsonObject update = Json.createReader(new StringReader(resp)).readObject();
        for(String strId : update.keySet()) {
            Request request = helper.getRequestDao().queryForId(Integer.valueOf(strId));
            JsonObject requestUpdate = update.getJsonObject(strId);
            for(String resultURL : requestUpdate.keySet()) {
                Result result = helper.getResultDao().queryForId(resultURL);
                result.setMatch(requestUpdate.getInt(resultURL));
                helper.getResultDao().update(result);
            }

            for(Result result : request.getResults()) {
                if(!result.isProcessed()) break;
            }

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

    public static void newRequest(final Request request) {
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

    public static void feedNewResults(final Collection<Result> results, final Request request) {
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
        OpenHelperManager.releaseHelper();
    }

    

}
