package database;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Antoine on 20/03/2016.
 * slfies pictures table
 */
public class Selfie {

    public String getPath() {
        return path;
    }

    @DatabaseField(id = true)
    private String path;

    @DatabaseField(foreign = true)
    private User user;
}
