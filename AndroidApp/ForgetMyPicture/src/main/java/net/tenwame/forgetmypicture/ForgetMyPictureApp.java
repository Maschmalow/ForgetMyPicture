package net.tenwame.forgetmypicture;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.crittercism.app.Crittercism;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import net.tenwame.forgetmypicture.activities.Settings;

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
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(
                new DisplayImageOptions.Builder()
                        .showImageOnFail(android.R.drawable.ic_delete)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build()
        ).build());
        Crittercism.initialize(context, "f540f2393bac4199bd54307a928e1a0a00444503");
        Crittercism.setUsername(UserData.getDeviceId());
        helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        Manager.getInstance(); //initialize manager
    }

    public static Context getContext() {
        return ForgetMyPictureApp.context;
    }

    public static DatabaseHelper getHelper() {
        return helper;
    }


    public static boolean isNetworkAvailable() {
        if(Settings.offlineMode()) return false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        if(network == null || !network.isConnectedOrConnecting())
            return false;

        return !Settings.dataOnWifi() || network.getType() == ConnectivityManager.TYPE_WIFI;

    }

    public static String getName() {
        return context.getPackageName();
    }
}
