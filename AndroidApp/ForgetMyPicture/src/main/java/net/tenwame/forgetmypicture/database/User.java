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
        idCardPath = IDCARD_PREFIX + UUID.randomUUID().toString();
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

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Request> requests;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Selfie> selfies;

    public void setup(String email, String name, String forename, Bitmap idCard, Collection<Bitmap> selfies) {
        this.email = email;
        this.name = name;
        this.forename = forename;
        getIdCard().set(idCard);
        this.selfies.clear();
        for( Bitmap selfiePic : selfies )
            this.selfies.add(new Selfie(selfiePic));
    }

    public boolean isValid() {
        if(selfies.isEmpty()) return false;
        for(Selfie selfie : selfies)
            if(selfie.getPic().get() == null)
                return false;
        return email != null && name != null && forename != null && getIdCard().get() != null;
    }

    public PictureAccess getIdCard() {
        return new PictureAccess(idCardPath);
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
