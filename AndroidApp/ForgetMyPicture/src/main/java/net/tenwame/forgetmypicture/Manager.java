package net.tenwame.forgetmypicture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

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
public class Manager extends BroadcastReceiver{
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


    private static final long TRACKING_DELAY = 100*60*5; //time between each update, ms
    private final Thread trackingThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while( true ) {
                ServerInterface.getRequestInfo();
                startService();
                try {
                    Thread.sleep(TRACKING_DELAY);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    });

    private Context curContext = ForgetMyPictureApp.getContext();
    private boolean isServiceLaunched = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        curContext = context;
        if( ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if(isNetworkConnected()) {
                startService();
                startTracking();
            } else {
                stopService();
                stopTracking();
            }
        }
        curContext = ForgetMyPictureApp.getContext();
    }

    public Request startNewRequest(List<String> keywords) {
        DatabaseHelper helper = OpenHelperManager.getHelper(curContext, DatabaseHelper.class);
        Request request = new Request(keywords);
        try {
            helper.getRequestDao().create(request);
        } catch (SQLException e) {
            Log.e(TAG, "Could not create new request", e);
            return null;
        }
        ServerInterface.newRequest(request);
        startService();

        OpenHelperManager.releaseHelper();
        return request;
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) curContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private boolean startTracking() {
        if(trackingThread.isAlive() || !isNetworkConnected()) return false;
        trackingThread.start();
        return true;
    }

    private boolean stopTracking() {
        if(!trackingThread.isAlive()) return false;
        trackingThread.interrupt();
        return true;
    }
    private boolean startService() {
        if(isServiceLaunched || !isNetworkConnected()) return false;
        curContext.startService(new Intent(curContext, SearchService.class));
        return true;
    }

    private boolean stopService() {
        if(!isServiceLaunched) return false;
        curContext.stopService(new Intent(curContext, SearchService.class));
        return true;
    }
}
