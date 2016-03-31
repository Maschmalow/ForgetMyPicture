package net.tenwame.forgetmypicture.database;

import android.graphics.Bitmap;

import com.j256.ormlite.field.DatabaseField;

import net.tenwame.forgetmypicture.PictureAccess;
import net.tenwame.forgetmypicture.UserData;

import java.util.UUID;

/**
 * Created by Antoine on 21/03/2016.
 * wrapper for selfie path storage
 */
public class Selfie {
    private static final String SELFIE_PREFIX = "user_selfie_";

    Selfie() {}

    public Selfie(Bitmap selfie) {
        path = SELFIE_PREFIX + UUID.randomUUID().toString();
        getPic().set(selfie);
        this.user = UserData.getUser();
    }

    @DatabaseField(id = true)
    private String path;

    @DatabaseField(foreign = true)
    private User user;

    public User getUser() {
        return user;
    }

    public PictureAccess getPic() {
        return new PictureAccess(path);
    }
}
