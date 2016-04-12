package net.tenwame.forgetmypicture;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;
import net.tenwame.forgetmypicture.database.Selfie;
import net.tenwame.forgetmypicture.database.User;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Stack;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by Antoine on 24/02/2016.
 * service to call when communicating with the back-end server
 * updates are directly made to the database
 */
public class ServerInterface extends IntentService {
    private static final String TAG = ServerInterface.class.getSimpleName();

    public static final String EXTRA_REQUEST_ID_KEY = ForgetMyPictureApp.getName() + ".requestId";
    public static final String EXTRA_RESULTS_KEY = ForgetMyPictureApp.getName() + ".results";

    public static final String ACTION_REGISTER = ForgetMyPictureApp.getName() + ".register";
    public static final String ACTION_NEW_REQUEST = ForgetMyPictureApp.getName() + ".new_request";
    public static final String ACTION_FEED = ForgetMyPictureApp.getName() + ".feed";
    public static final String ACTION_GET_INFO = ForgetMyPictureApp.getName() + ".get_info";
    public static final String ACTION_SEND_MAIL = ForgetMyPictureApp.getName() + ".send_mail";

    private static final String BASE_URL = "http://adurand00005.rtrinity.enseirb.fr";
    private static final String REGISTER_URL = "/register.php";
    private static final String NEW_REQUEST_URL = "/new_request.php";
    private static final String FEED_URL = "/feed.php";
    private static final String GET_INFO_URL = "/get_info.php";
    private static final String SEND_MAIL_URL = "/send_mail.php";


    private DatabaseHelper helper = ForgetMyPictureApp.getHelper();
    private Intent curIntent;

    public ServerInterface() {
        super(TAG);
    }

    public static void execute(String action) {
        execute(action, null);
    }

    public static void execute(String action, Bundle extras) {
        Intent intent = new Intent(ForgetMyPictureApp.getContext(), ServerInterface.class);
        intent.setPackage(ForgetMyPictureApp.getName());
        intent.setAction(action);
        if(extras != null)
            intent.putExtras(extras);
        ForgetMyPictureApp.getContext().startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Request for action: " + intent.getAction());
        curIntent = intent;

        try {
            if( ACTION_FEED.equals(intent.getAction()) )
                feedNewResults();
            if( ACTION_GET_INFO.equals(intent.getAction()) )
                getRequestInfo();
            if( ACTION_NEW_REQUEST.equals(intent.getAction()) )
                newRequest();
            if( ACTION_REGISTER.equals(intent.getAction()) )
                register();
            if( ACTION_SEND_MAIL.equals(intent.getAction()))
                sendMail();

        }catch (Exception e) {
            Manager.getInstance().notify(getFailEvent(intent.getAction()));
            Log.e(TAG, "Server interface exception", e);
        }

        curIntent = null;
    }


    private void register() throws Exception{
        User user = UserData.getUser();

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

    private void newRequest() throws Exception{
        Request request = getRequestFromIntent();

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

    private void feedNewResults() throws Exception{
        Request request = getRequestFromIntent();

        Connection connection = Jsoup.connect(BASE_URL + FEED_URL)
                .data("deviceId", UserData.getDeviceId())
                .data("requestId", request.getId().toString());
        for(String resultURL : curIntent.getStringArrayListExtra(EXTRA_RESULTS_KEY))
            connection.data("result[]", resultURL);
        connection.post();

        Log.d(TAG, "Sent results for request " + request.getId());
    }

    private void getRequestInfo() throws Exception{

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

            request.updateStatus();
            helper.getRequestDao().update(request);
        }

        Log.d(TAG, "Updated " + update.size() + " requests.");
    }

    private void sendMail() throws Exception {
        Request request = getRequestFromIntent();

        Jsoup.connect(BASE_URL + SEND_MAIL_URL)
                .data("deviceId", UserData.getDeviceId())
                .data("requestId", request.getId().toString())
                .get();
    }

    private Request getRequestFromIntent() throws Exception{
        int requestId = curIntent.getIntExtra(EXTRA_REQUEST_ID_KEY, -1);
        Request request = helper.getRequestDao().queryForId(requestId);
        if(request == null)
            throw new RuntimeException("No request found for request Id: " + requestId);
        return request;
    }


    public Manager.Event getFailEvent(String action) {
        if(ACTION_REGISTER.equals(action))
            return Manager.Event.REGISTER_FAILED;
        if(ACTION_NEW_REQUEST.equals(action))
            return Manager.Event.NEW_REQUEST_FAILED;
        if(ACTION_GET_INFO.equals(action))
            return Manager.Event.GET_INFO_FAILED;
        if(ACTION_FEED.equals(action))
            return Manager.Event.FEED_FAILED;
        if(ACTION_SEND_MAIL.equals(action))
            return Manager.Event.SEND_MAIL_FAILED;

        return null;
    }
}
