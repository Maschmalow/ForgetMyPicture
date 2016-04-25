package net.tenwame.forgetmypicture.database;

import android.util.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import net.tenwame.forgetmypicture.ForgetMyPictureApp;
import net.tenwame.forgetmypicture.PictureAccess;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;


/**
 * Created by Antoine on 18/03/2016.
 * User table (see Request for generic explanations)
 */

@DatabaseTable(tableName = "user")
public class User {
    private static final String TAG = User.class.getSimpleName();
    private static final String IDCARD_PREFIX = "user_idcard_";

    User() {}

    public User(String deviceId) {
        this.deviceId = deviceId;
        idCardPath = IDCARD_PREFIX + UUID.randomUUID().toString();
        acceptedAgreement = false;
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

    @DatabaseField(canBeNull = false)
    private boolean acceptedAgreement;

    public void setup(String email, String name, String forename, Collection<String> selfies) throws SQLException {
        this.email = email.replace(" ", "");
        this.name = name;
        this.forename = forename;
        this.selfies.clear();
        for( String path : selfies )
            addSelfie(path);
        save();
    }

    private void addSelfie(String path) {
        Selfie selfie = new Selfie(new PictureAccess(path).get());
        this.selfies.add(selfie);
        if(!new File(path).delete())
            Log.w(TAG, "Could not delete file " + path);
    }

    public boolean isValid() {
        if(selfies.isEmpty()) return false;
        for(Selfie selfie : selfies)
            if(selfie.getPic().get() == null)
                return false;
        return email != null && name != null && forename != null;
    }

    public PictureAccess getIdCard() {
        return new PictureAccess.Internal(idCardPath);
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

    public ForeignCollection<Request> getRequests() {
        return requests;
    }

    public boolean isAgreementAccepted() {
        return acceptedAgreement;
    }

    public void setAgreementAccepted() throws SQLException {
        acceptedAgreement = true;
        save();
    }

    private void save() throws SQLException {
        ForgetMyPictureApp.getHelper().getUserDao().update(this);
    }
}
