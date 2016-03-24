package database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Antoine on 18/03/2016.
 * requests table
 */
@DatabaseTable(tableName = "request")
public class Request {

    public enum Kind {QUICK, EXHAUSTIVE}

    public Request() {}

    @DatabaseField(id = true) //ORMLite doesn't support multiple primary keys
    private String id;  //as a workaround, we set the id to be "deviceId_requestId"

    @DatabaseField(canBeNull = false)
    private String kind;

    @DatabaseField
    private String originalPicPath;

    @DatabaseField(foreign = true)
    private User user;

    public int getId() {
        return Integer.parseInt(id.substring(id.indexOf('_')+1));
    }

    public Kind getKind() {
        return Kind.valueOf(kind);
    }

    public String getOriginalPicPath() {
        return originalPicPath;
    }

    public User getUser() {
        return user;
    }
}
