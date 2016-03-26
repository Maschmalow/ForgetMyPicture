package net.tenwame.forgetmypicture;

import android.app.Application;
import android.content.Context;

/**
 * Created by Antoine on 19/02/2016.
 * Application, for context
 */
public class ForgetMyPictureApp extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        ForgetMyPictureApp.context = getApplicationContext();
    }

    public static Context getContext() {
        return ForgetMyPictureApp.context;
    }
}
