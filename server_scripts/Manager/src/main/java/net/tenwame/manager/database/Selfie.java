package net.tenwame.manager.database;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Antoine on 20/03/2016.
 * slfies pictures table
 */
public class Selfie {

    Selfie() {}

    @DatabaseField(generatedId = true)
    private int id; //for redbeans

    @DatabaseField(canBeNull = false)
    private String path;

    @DatabaseField(canBeNull = false)
    private String user_id;

    public String getPath() {
        return path;
    }

}
