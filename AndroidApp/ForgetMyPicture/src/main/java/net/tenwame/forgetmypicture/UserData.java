package net.tenwame.forgetmypicture;

import android.provider.Settings.Secure;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import net.tenwame.forgetmypicture.database.User;

import java.sql.SQLException;

/**
 * Created by Antoine on 20/02/2016.
 * class used as a gateway to the user personal data
 * implements singleton pattern for the user table
 */
public class UserData {
    private static final String TAG = UserData.class.getSimpleName();

    private static final String deviceId = Secure.getString(ForgetMyPictureApp.getContext().getContentResolver(), Secure.ANDROID_ID);
    private User user;

    private static UserData instance = null;
    public static UserData getInstance() {
        if(instance == null)
            synchronized (UserData.class) {
                if(instance == null)
                    instance = new UserData();
            }

        return instance;
    }

    private UserData() {
        DatabaseHelper helper = OpenHelperManager.getHelper(ForgetMyPictureApp.getContext(), DatabaseHelper.class);
        user = helper.getUserDao().queryForId(deviceId);
        if(user == null) {
            user = new User(deviceId);
            helper.getUserDao().create(user);
        }

        OpenHelperManager.releaseHelper();
    }

    public User getCachedUser() {
        return user;
    }

    public User getUser(DatabaseHelper helper) throws SQLException {
        helper.getUserDao().refresh(user);
        return user;
    }

    public static User getInstanceUser(DatabaseHelper helper) throws SQLException {
        return getInstance().getUser(helper);
    }

    public static String getDeviceId() {
        return deviceId;
    }


}
