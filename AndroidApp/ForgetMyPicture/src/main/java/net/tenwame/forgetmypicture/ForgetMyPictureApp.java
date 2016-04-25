package net.tenwame.forgetmypicture;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.crittercism.app.Crittercism;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import net.tenwame.forgetmypicture.activities.Settings;

import java.sql.SQLException;

/**
 * Created by Antoine on 19/02/2016.
 * Application, for context and early initialisation
 */
public class ForgetMyPictureApp extends Application {
    private static final String TAG = ForgetMyPictureApp.class.getSimpleName();
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

    public static AlertDialog getAgreementDialog(final Context context) {
        return new AlertDialog.Builder(context)
                .setMessage(R.string.agreement)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            UserData.getUser().setAgreementAccepted();
                        } catch (SQLException e) {
                            Log.e(TAG, "Could not save user", e);
                            Crittercism.logHandledException(e);
                            return;
                        }
                        Log.i(TAG, "Agreement accepted");
                    }
                })
                .setNegativeButton(R.string.refuse, null)
                .setNeutralButton(R.string.email_preview_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getInfoDialog(context, R.string.email_preview).show();
                    }
                })
                .setTitle(R.string.agreement_title)
                .create();
    }

    public static AlertDialog getInfoDialog(Context context, int messageResId) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.info_dialog_title)
                .setMessage(messageResId)
                .setNeutralButton(R.string.ok, null)
                .create();
    }
}
