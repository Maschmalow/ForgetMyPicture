package database;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Antoine on 18/03/2016.
 * User table
 */

@DatabaseTable(tableName = "user")
public class User {

    @DatabaseField(id = true)
    private String deviceId;

    @DatabaseField(canBeNull = false)
    private String email; //this one is used only in php

    @ForeignCollectionField()
    private ForeignCollection<String> selfies;

    public String getDeviceId() {
        return deviceId;
    }

    public ForeignCollection<String> getSelfies() {
        return selfies;
    }
}
