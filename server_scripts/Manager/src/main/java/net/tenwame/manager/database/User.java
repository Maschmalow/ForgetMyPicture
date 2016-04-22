package net.tenwame.manager.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.tenwame.manager.Main;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Antoine on 18/03/2016.
 * User table
 */

@DatabaseTable(tableName = "user")
public class User {

    User() {}

    @DatabaseField(generatedId = true)
    private int id; //for redbeans

    @DatabaseField(canBeNull = false)
    private String device_id;

    @DatabaseField(canBeNull = false)
    private String email;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private String forename;


    public String getDeviceId() {
        return device_id;
    }

    public List<Selfie> getSelfies() throws SQLException {
        return Main.getSelfieDao().queryForEq("user_id", device_id);
    }
}
