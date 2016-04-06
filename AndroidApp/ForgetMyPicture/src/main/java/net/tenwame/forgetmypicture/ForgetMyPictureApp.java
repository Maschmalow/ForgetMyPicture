package net.tenwame.forgetmypicture;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by Antoine on 19/02/2016.
 * Application, for context
 */
public class ForgetMyPictureApp extends Application {
    private static Context context;
    private static DatabaseHelper helper;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public static Context getContext() {
        return ForgetMyPictureApp.context;
    }

    public static DatabaseHelper getHelper() {
        return helper;
    }

    public static void startService(Class<?> serviceClass) {
        context.startService(new Intent(context, serviceClass));
    }


    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static String getName() {
        return context.getPackageName();
    }
}
