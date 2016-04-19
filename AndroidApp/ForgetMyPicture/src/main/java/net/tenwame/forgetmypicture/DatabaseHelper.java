package net.tenwame.forgetmypicture;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;
import net.tenwame.forgetmypicture.database.Selfie;
import net.tenwame.forgetmypicture.database.User;

import java.sql.SQLException;

/**
 * Created by Antoine on 22/03/2016.
 * for DAOs and stuff
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "ForgetMyPicture.db";
    private static final int DATABASE_VERSION = 5;


    private Dao<Result, String> resultDao;
    private Dao<Request, Integer> requestDao;
    private Dao<User, String> userDao;
    private Dao<Selfie, String> selfieDao;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        try {
            resultDao = getDao(Result.class);
            requestDao = getDao(Request.class);
            userDao = getDao(User.class);
            selfieDao = getDao(Selfie.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        createDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        wipeDatabase();
    }

    public void wipeDatabase() {
        try {
            TableUtils.dropTable(connectionSource, Request.class, false);
            TableUtils.dropTable(connectionSource, Result.class, false);
            TableUtils.dropTable(connectionSource, User.class, false);
            TableUtils.dropTable(connectionSource, Selfie.class, false);
            createDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Log.i(TAG, "Database wiped");
    }

    private void createDatabase() {
        try {
            TableUtils.createTableIfNotExists(connectionSource, Result.class);
            TableUtils.createTableIfNotExists(connectionSource, Request.class);
            TableUtils.createTableIfNotExists(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, Selfie.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Log.i(TAG, "Database created");
    }

    public Dao<Result, String> getResultDao() {
        return resultDao;
    }

    public Dao<Selfie, String> getSelfieDao() {
        return selfieDao;
    }

    public Dao<User, String> getUserDao() {
        return userDao;
    }

    public Dao<Request, Integer> getRequestDao() {
        return requestDao;
    }
}