package net.tenwame.forgetmypicture;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.crittercism.app.Crittercism;
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
        Crittercism.initialize(getApplicationContext(), "f540f2393bac4199bd54307a928e1a0a00444503");
        context = getApplicationContext();
        helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        Manager.getInstance(); //initialize manager
    }

    public static Context getContext() {
        return ForgetMyPictureApp.context;
    }

    public static DatabaseHelper getHelper() {
        return helper;
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
