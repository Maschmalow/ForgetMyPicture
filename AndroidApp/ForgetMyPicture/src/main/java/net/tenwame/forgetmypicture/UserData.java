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
    private static final String deviceId = Secure.getString(ForgetMyPictureApp.getContext().getContentResolver(), Secure.ANDROID_ID);
    private static User user = setUser();

    private UserData() { }

    public static User getUser() {
        if(user == null)
            setUser();

        return user;
    }

    private static User setUser() {
        DatabaseHelper helper = OpenHelperManager.getHelper(ForgetMyPictureApp.getContext(), DatabaseHelper.class);
        try {
            user = helper.getUserDao().queryForId(deviceId);
            if(user == null) {
                user = new User(deviceId);
                helper.getUserDao().create(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not fetch or create user", e);
        }
        OpenHelperManager.releaseHelper();

        return user;
    }

    public static String getDeviceId() {
        return deviceId;
    }


}
