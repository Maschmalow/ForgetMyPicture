package net.tenwame.forgetmypicture;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crittercism.app.Crittercism;

import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;
import net.tenwame.forgetmypicture.services.NetworkService;
import net.tenwame.forgetmypicture.services.Searcher;
import net.tenwame.forgetmypicture.services.ServerInterface;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Antoine on 10/03/2016.
 * multi purpose class (with Singleton pattern)
 * - handle new requests
 * - launch searches when possible
 * - periodically get updates from server
 * - handle failure (not entirely implemented yet)
 */
public class Manager {
    private static final String TAG = Manager.class.getSimpleName();
    private static final int MAX_REQUEST_FETCHING = 100*60*60*24*7; // one week, in ms

    private static Manager instance = null;
    public static Manager getInstance() {
        if(instance == null)
            synchronized (Manager.class) {
                if(instance == null)
                    instance = new Manager();
            }

        return instance;
    }

    private static final long UPDATE_DELAY = 100*60*5; //time between each update, ms

    private Context context = ForgetMyPictureApp.getContext();
    private AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    private boolean areAlarmsScheduled = false;
    private boolean shouldResent = false;

    private Manager() {
        setAlarms();
        NetworkService.registerListener(ServerInterface.class, new NetworkService.EmptyListener() {
            @Override
            public void onActionFailed(@NonNull String action) {
                if(action.equals(ServerInterface.ACTION_FEED))
                    shouldResent = true;
            }

            @Override
            public void onActionFinished(@NonNull String action) {
                if(action.equals(ServerInterface.ACTION_GET_INFO) ||
                        action.equals(ServerInterface.ACTION_NEW_REQUEST))
                    setAlarms();
            }
        });

    }


    public Request startNewRequest(List<String> keywords, Bitmap originalPic) {
        if(Util.powerSetSize(keywords) >= MAX_REQUEST_FETCHING/Searcher.SEARCH_DELAY )
            return null;
        Request request = new Request(keywords, originalPic);
        try {
            ForgetMyPictureApp.getHelper().getRequestDao().create(request);
        } catch (SQLException e) {
            Log.e(TAG, "Could not create new request", e);
            Crittercism.logHandledException(e);
            return null;
        }
        ServerInterface.execute(ServerInterface.ACTION_NEW_REQUEST, request);

        return request;
    }


    public void resendResults() {
        if(!shouldResent) return;

        List<Result> unSent;
        try {
            unSent = ForgetMyPictureApp.getHelper().getResultDao().queryForFieldValuesArgs(Collections.singletonMap("sent", (Object)Boolean.FALSE));
        } catch (SQLException e) {
            Log.e(TAG, "Unable to recover from feed fail", e);
            shouldResent = false; //we disable resend for now
            Crittercism.logHandledException(e);
            return;
        }
        Map<Request, List<Result>> unSentRequests = new HashMap<>();
        for(Result result : unSent) {
            List<Result> cur  = unSentRequests.get(result.getRequest());
            if(cur == null) {
                cur = new ArrayList<>();
                unSentRequests.put(result.getRequest(), cur);
            }
            cur.add(result);
        }

        for(Map.Entry<Request,List<Result>> set: unSentRequests.entrySet())
            ServerInterface.execute(ServerInterface.ACTION_FEED, set.getKey(), set.getValue());
        shouldResent = false;
    }

    /**
     * Notify the manager that the updates (search and server update) may have
     * to be rescheduled.
     * The Manager will ultimately decide what to do.
     */
    public void setAlarms() {

        List<Request> requests = Collections.emptyList();
        try {
            requests = ForgetMyPictureApp.getHelper().getRequestDao().queryForAll();
        } catch (SQLException e) {
            Log.e(TAG, "Could not retrieve requests", e);
            Crittercism.logHandledException(e);
        }


        if(ForgetMyPictureApp.isNetworkAvailable() &&
                !Util.applyFilter(requests, new Util.Filter<Request>() {
                    @Override
                    public boolean isAllowed(Request candidate) {
                        return !candidate.getStatus().isAfter(Request.Status.UNLOCKED);
                    }}).isEmpty())
            scheduleAlarms();
        else
            cancelAlarms();

    }

    private void scheduleAlarms() {
        if(areAlarmsScheduled) return;
        scheduleAction(AlarmReceiver.ACTION_DO_UPDATE, UPDATE_DELAY);
        scheduleAction(AlarmReceiver.ACTION_DO_SEARCH, Searcher.SEARCH_DELAY);
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

    /**
     * Broadcast receiver used to track network status
     */
    public static class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if( ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()))
                getInstance().setAlarms();
        }
    }

    /**
     * Internal receiver that fires relevant NetworkService when an alarm is triggered.
     * This allow searching and update to continue even if the app is closed.
     * The user can decide to disable theses.
     */
    public static class AlarmReceiver extends  BroadcastReceiver {
        public static final int REQUEST_CODE = 0; //not used
        public static final String ACTION_DO_SEARCH = ForgetMyPictureApp.getName() + ".alarm.search";
        public static final String ACTION_DO_UPDATE = ForgetMyPictureApp.getName() + ".alarm.update";

        @Override
        public void onReceive(Context context, Intent intent) {
            if(!ForgetMyPictureApp.isNetworkAvailable())
                return;

            if(ACTION_DO_SEARCH.equals(intent.getAction()))
                Searcher.execute();

            if(ACTION_DO_UPDATE.equals(intent.getAction())) {
                ServerInterface.execute(ServerInterface.ACTION_GET_INFO);
                //getInstance().resendResults();
            }
        }
    }

}
