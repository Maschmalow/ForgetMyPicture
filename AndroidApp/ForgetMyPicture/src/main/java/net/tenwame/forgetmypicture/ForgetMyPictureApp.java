package net.tenwame.forgetmypicture;

import android.app.Application;
import android.content.Context;

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
}
