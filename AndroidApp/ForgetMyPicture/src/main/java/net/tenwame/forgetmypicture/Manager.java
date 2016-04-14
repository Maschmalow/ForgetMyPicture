package net.tenwame.forgetmypicture;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import net.tenwame.forgetmypicture.database.Request;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Antoine on 10/03/2016.
 * multi purpose class
 * - handle new requests
 * - start/stop service when needed
 * - periodically get updates from server
 */
public class Manager {
    private static final String TAG = Manager.class.getSimpleName();

    private static Manager instance = null;
    public static Manager getInstance() {
        if(instance == null)
            synchronized (UserData.class) {
                if(instance == null)
                    instance = new Manager();
            }

        return instance;
    }

    public enum Event {
        SEARCHER_EXCEPTION,
        REGISTER_FAILED,
        FEED_FAILED,
        GET_INFO_FAILED,
        NEW_REQUEST_FAILED,
        FILL_FORM_FAILED,
        SEND_MAIL_FAILED
    }


    private static final long UPDATE_DELAY = 100*60*5; //time between each update, ms

    private Context context = ForgetMyPictureApp.getContext();
    private AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    private boolean areAlarmsScheduled = false;

    private Manager() {
        if(ForgetMyPictureApp.isNetworkConnected())
            scheduleAlarms();
    }


    public Request startNewRequest(List<String> keywords, Bitmap originalPic) {
        Request request = new Request(keywords, originalPic);
        try {
            ForgetMyPictureApp.getHelper().getRequestDao().create(request);
        } catch (SQLException e) {
            Log.e(TAG, "Could not create new request", e);
            return null;
        }
        Bundle params = new Bundle();
        params.putInt(ServerInterface.EXTRA_REQUEST_ID_KEY, request.getId());
        ServerInterface.execute(ServerInterface.ACTION_NEW_REQUEST, params);

        return request;
    }

    public void notify(Event event) {
        if(event == Event.SEARCHER_EXCEPTION) {

        } //TODO: handle other events
    }


    private void scheduleAlarms() {
        if(areAlarmsScheduled) return;
        scheduleAction(AlarmReceiver.ACTION_DO_UPDATE, UPDATE_DELAY);
        scheduleAction(AlarmReceiver.ACTION_DO_SEARCH, SearchService.SEARCH_DELAY);
        Log.i(TAG, "Alarms scheduled");
        areAlarmsScheduled = true;
    }

    private void cancelAlarms() {
        if(!areAlarmsScheduled) return;
        cancelAction(AlarmReceiver.ACTION_DO_UPDATE);
        cancelAction(AlarmReceiver.ACTION_DO_SEARCH);
        Log.i(TAG, "Alarms removed");
        areAlarmsScheduled = false;
    }

    private void cancelAction(String action) {
        alarm.cancel(getPendingIntent(action));
    }

    private void scheduleAction(String action, long interval) {
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                interval,
                getPendingIntent(action));
    }

    private PendingIntent getPendingIntent(String action) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, AlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if( ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if(ForgetMyPictureApp.isNetworkConnected())
                    getInstance().scheduleAlarms();
                else
                    getInstance().cancelAlarms();
            }
        }

    }

    public static class AlarmReceiver extends  BroadcastReceiver {
        public static final int REQUEST_CODE = 0; //not used
        public static final String ACTION_DO_SEARCH = ForgetMyPictureApp.getName() + ".alarm.search";
        public static final String ACTION_DO_UPDATE = ForgetMyPictureApp.getName() + ".alarm.update";

        @Override
        public void onReceive(Context context, Intent intent) {
            if(!ForgetMyPictureApp.isNetworkConnected())
                return;

            if(ACTION_DO_SEARCH.equals(intent.getAction()))
                SearchService.execute();

            if(ACTION_DO_UPDATE.equals(intent.getAction()))
                ServerInterface.execute(ServerInterface.ACTION_GET_INFO);
        }
    }

}
