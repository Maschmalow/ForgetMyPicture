package net.tenwame.forgetmypicture.database;

import android.graphics.Bitmap;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import net.tenwame.forgetmypicture.PictureAccess;

import java.util.Collection;
import java.util.UUID;


/**
 * Created by Antoine on 18/03/2016.
 * User table
 */

@DatabaseTable(tableName = "user")
public class User {
    private static final String IDCARD_PREFIX = "user_idcard_";

    User() {}

    public User(String deviceId) {
        this.deviceId = deviceId;
    }

    @DatabaseField(id = true)
    private String deviceId;

    @DatabaseField()
    private String email;

    @DatabaseField()
    private String name;

    @DatabaseField()
    private String forename;

    @DatabaseField()
    private String idCardPath;

    @ForeignCollectionField
    private ForeignCollection<Request> requests;

    @ForeignCollectionField()
    private ForeignCollection<Selfie> selfies;

    public void setupUser(String email, String name, String forename, Bitmap idCard, Collection<Bitmap> selfies) {
        this.email = email;
        this.name = name;
        this.forename = forename;
        getIdCard().set(idCard);
        for( Bitmap selfiePic : selfies )
            this.selfies.add(new Selfie(selfiePic));

    }

    public PictureAccess getIdCard() {
        return new PictureAccess(idCardPath, new PictureAccess.PathGenerator() {
            @Override
            public String setNewPath() {
                return idCardPath = IDCARD_PREFIX + UUID.randomUUID().toString();
            }
        });
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getForename() {
        return forename;
    }

    public ForeignCollection<Selfie> getSelfies() {
        return selfies;
    }
}
