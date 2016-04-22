package net.tenwame.manager.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.tenwame.manager.Main;

import java.sql.SQLException;

/**
 * Created by Antoine on 18/03/2016.
 * requests table
 */
@DatabaseTable(tableName = "request")
public class Request {

    public enum Kind {QUICK, EXHAUSTIVE}

    Request() {}

    @DatabaseField(generatedId = true)
    int id;  //for redbeans

    @DatabaseField(canBeNull = false) //ORMLite doesn't support multiple primary keys
    private String request_id; //as a workaround, it is prefixed by deviceId

    @DatabaseField(canBeNull = false)
    private String kind;

    @DatabaseField
    private String original_pic_path;

    @DatabaseField(canBeNull = false)
    private String user_id;

    public int getId() {
        return Integer.parseInt(request_id.substring(request_id.indexOf('_')+1));
    }

    public Kind getKind() {
        return Kind.valueOf(kind);
    }

    public String getOriginalPicPath() {
        return original_pic_path;
    }

    public User getUser() throws SQLException {
        return Main.getUserDao().queryForEq("device_id", user_id).get(0);
    }
}
