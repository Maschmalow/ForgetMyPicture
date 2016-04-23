package net.tenwame.forgetmypicture;

import android.provider.Settings.Secure;
import android.util.Log;

import com.crittercism.app.Crittercism;
import com.j256.ormlite.dao.Dao;

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
    private static User user;

    private UserData() { }

    public static User getUser() {
        if(user == null)
            synchronized (UserData.class) {
                if(user == null)
                    setUser();
            }

        try {
            ForgetMyPictureApp.getHelper().getUserDao().refresh(user);
        } catch (SQLException e) {
            Log.w(TAG, "Could not refresh user", e);
            Crittercism.logHandledException(e);
        }
        return user;
    }

    private static User setUser() {
        final Dao<User, String> dao = ForgetMyPictureApp.getHelper().getUserDao();
        try {
            user = dao.queryForId(deviceId);
            if(user == null) {
                user = new User(deviceId);
                dao.create(user);
            }
            dao.refresh(user); //this is tricky. We refresh the user so ORMLite setup the ForeignCollections
        } catch (SQLException e) {
            throw new RuntimeException("Could not fetch or create user", e);
        }
        dao.registerObserver(new Dao.DaoObserver() {
            @Override
            public void onChange() {
                try {
                    dao.refresh(user);
                } catch (SQLException e) {
                    Log.w(TAG, "Could not refresh user", e);
                    Crittercism.logHandledException(e);
                    synchronized (UserData.class) {
                        user = null;
                    }
                }
            }
        });

        return user;
    }

    public static String getDeviceId() {
        return deviceId;
    }


}
