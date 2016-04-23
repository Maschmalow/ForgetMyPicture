package net.tenwame.forgetmypicture.services;

import android.os.Bundle;
import android.util.Log;

import net.tenwame.forgetmypicture.DatabaseHelper;
import net.tenwame.forgetmypicture.ForgetMyPictureApp;
import net.tenwame.forgetmypicture.UserData;
import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;
import net.tenwame.forgetmypicture.database.Selfie;
import net.tenwame.forgetmypicture.database.User;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by Antoine on 24/02/2016.
 * service to call when communicating with the back-end server
 * updates are directly made to the database
 */
public class ServerInterface extends NetworkService {
    private static final String TAG = ServerInterface.class.getSimpleName();

    public static final String EXTRA_REQUEST_ID_KEY = ForgetMyPictureApp.getName() + ".requestId";
    public static final String EXTRA_RESULTS_KEY = ForgetMyPictureApp.getName() + ".results";

    public static final String ACTION_REGISTER = ForgetMyPictureApp.getName() + ".register";
    public static final String ACTION_NEW_REQUEST = ForgetMyPictureApp.getName() + ".new_request";
    public static final String ACTION_FEED = ForgetMyPictureApp.getName() + ".feed";
    public static final String ACTION_GET_INFO = ForgetMyPictureApp.getName() + ".get_info";
    public static final String ACTION_SEND_MAIL = ForgetMyPictureApp.getName() + ".send_mail";
    public static final String ACTION_WIPE_USER = ForgetMyPictureApp.getName() + ".wipe_user";

    private static final String BASE_URL = "http://adurand00005.rtrinity.enseirb.fr";
    private static final String REGISTER_URL = "/register.php";
    private static final String NEW_REQUEST_URL = "/new_request.php";
    private static final String FEED_URL = "/feed.php";
    private static final String GET_INFO_URL = "/get_info.php";
    private static final String SEND_MAIL_URL = "/send_mail.php";
    private static final String WIPE_USER_URL = "/wipe_user.php";


    private DatabaseHelper helper = ForgetMyPictureApp.getHelper();

    public ServerInterface() {
        super(TAG);
        handlers.put(ACTION_SEND_MAIL, sendMail);
        handlers.put(ACTION_REGISTER, register);
        handlers.put(ACTION_NEW_REQUEST, newRequest);
        handlers.put(ACTION_GET_INFO, getInfo);
        handlers.put(ACTION_FEED, feed);
        handlers.put(ACTION_WIPE_USER, wipeUser);
    }

    public static void execute(String action) {
        execute(action, null);
    }

    public static void execute(String action, Request request) {
        execute(action, request, null);
    }

    public static void execute(String action, Request request, Collection<Result> results) {
        Bundle params = new Bundle();
        if(request != null)
            params.putInt(EXTRA_REQUEST_ID_KEY, request.getId());
        if(results != null) {
            Set<String> ids = new HashSet<>(results.size());
            for(Result result : results)
                ids.add(result.getId());
            params.putStringArrayList(EXTRA_RESULTS_KEY, new ArrayList<>(ids));
        }

        execute(ServerInterface.class, action, params);
    }


    private final ActionHandler register = new NetworkService.ActionHandler() {
        @Override
        public void handle(Bundle params) throws Exception {
            User user = UserData.getUser();

            Integer hash = 1; //TODO
            Connection connection = Jsoup.connect(BASE_URL + REGISTER_URL)
                    .data("h", hash.toString())
                    .data("deviceId", user.getDeviceId())
                    .data("email", user.getEmail())
                    .data("name", user.getName())
                    .data("forename", user.getForename());

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
    };

    private final ActionHandler wipeUser = new ActionHandler() {
        @Override
        public void handle(Bundle params) throws Exception {
            Jsoup.connect(BASE_URL + WIPE_USER_URL)
                    .data("deviceId", UserData.getDeviceId())
                    .post();
        }
    };

    private final ActionHandler newRequest = new NetworkService.ActionHandler() {
        @Override
        public void handle(Bundle params) throws Exception {
            Request request = getRequest(params);

            Connection connection = Jsoup.connect(BASE_URL + NEW_REQUEST_URL)
                    .data("deviceId", UserData.getDeviceId())
                    .data("requestId", request.getId().toString());

            if(request.getKind() == Request.Kind.QUICK) {
                InputStream stream = request.getOriginalPic().openStream();
                connection.data("originalPic[]", "originalPic", stream).post();
                stream.close();
            } else {
                connection.post();
            }

            Log.d(TAG, "new request registered: " + request.getId());
        }
    };

    private final ActionHandler feed = new NetworkService.ActionHandler() {
        @SuppressWarnings("ConstantConditions") //will be catch'd and handled later
        @Override
        public void handle(Bundle params) throws Exception {
            Request request = getRequest(params);

            List<Result> results = new ArrayList<>();
            for(String resultId : params.getStringArrayList(EXTRA_RESULTS_KEY))
                results.add(ForgetMyPictureApp.getHelper().getResultDao().queryForId(resultId));
            if(results.isEmpty())
                return;

            Connection connection = Jsoup.connect(BASE_URL + FEED_URL)
                    .data("deviceId", UserData.getDeviceId())
                    .data("requestId", request.getId().toString());
            for(Result result : results)
                connection.data("result[]", result.getPicURL());
            connection.post();

            for(Result result : results) {
                result.setSent();
                ForgetMyPictureApp.getHelper().getResultDao().update(result);
            }

            Log.d(TAG, "Sent results for request " + request.getId());
        }
    };

    private final ActionHandler getInfo = new NetworkService.ActionHandler() {
        @Override
        public void handle(Bundle params) throws Exception {
            String resp = Jsoup.connect(BASE_URL + GET_INFO_URL)
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .data("deviceId", UserData.getDeviceId()).execute().body();

            JsonObject update = Json.createReader(new StringReader(resp)).readObject();
            for(String requestId : update.keySet()) {
                Request request = helper.getRequestDao().queryForId(Integer.valueOf(requestId));
                if(request == null) {
                    Log.w(TAG, "GetInfo: invalid request: " + requestId + " (ignored)");
                    continue;
                }
                JsonObject requestUpdate = update.getJsonObject(requestId);
                for(String resultURL : requestUpdate.keySet()) {
                    Result result = helper.getResultDao().queryForId(Result.makeId(resultURL, request));
                    if(result == null) {
                        Log.w(TAG, "GetInfo: invalid result: " + resultURL + " (ignored)");
                        continue;
                    }
                    result.setMatch(Integer.parseInt(requestUpdate.getString(resultURL)));
                    helper.getResultDao().update(result);
                }

                request.updateStatus();
                helper.getRequestDao().update(request);
            }

            Log.d(TAG, "GetInfo: Updated " + update.size() + " requests.");
        }
    };

    private ActionHandler sendMail = new NetworkService.ActionHandler() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public void handle(Bundle params) throws Exception {

            List<Result> results = new ArrayList<>();
            for(String resultId : params.getStringArrayList(EXTRA_RESULTS_KEY))
                results.add(ForgetMyPictureApp.getHelper().getResultDao().queryForId(resultId));
            if(results.isEmpty())
                return;

            Set<String> hostURLs = new HashSet<>(results.size());
            for(Result result : results) //remove duplicates
                hostURLs.add(new URL(result.getPicRefURL()).getHost());

            Connection connection = Jsoup.connect(BASE_URL + SEND_MAIL_URL)
                    .data("deviceId", UserData.getDeviceId());

            for(String host : hostURLs)
                connection.data("host[]", host);

            connection.post();

            Log.d(TAG, "Mail sent (" + hostURLs.size() + " hosts");

        }
    };


    private Request getRequest(Bundle params) throws Exception{
        int requestId = params.getInt(EXTRA_REQUEST_ID_KEY, -1);
        Request request = helper.getRequestDao().queryForId(requestId);
        if(request == null)
            throw new IllegalArgumentException("Missing or invalid request: Id " + requestId);

        return request;
    }

}
