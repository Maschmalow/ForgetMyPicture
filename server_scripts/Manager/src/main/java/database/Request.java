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

    @DatabaseField(id = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String kind;

    @DatabaseField
    private String originalPicPath;

    @DatabaseField(foreign = true)
    private User user;

    public int getId() {
        return id;
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
