package net.tenwame.forgetmypicture.database;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import net.tenwame.forgetmypicture.PictureAccess;

import java.io.File;
import java.util.Collection;
import java.util.UUID;


/**
 * Created by Antoine on 18/03/2016.
 * User table
 */

@DatabaseTable(tableName = "user")
public class User {
    private static final String TAG = User.class.getSimpleName();
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

    public void setup(String email, String name, String forename, Collection<String> selfies) {
        this.email = email.replace(" ", "");
        this.name = name;
        this.forename = forename;
        this.selfies.clear();
        for( String path : selfies )
            addSelfie(path);
    }

    private void addSelfie(String path) {
        Selfie selfie = new Selfie(BitmapFactory.decodeFile(path));
        if(selfie.getPic() == null)
            return;
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

    public ForeignCollection<Request> getRequests() {
        return requests;
    }
}
