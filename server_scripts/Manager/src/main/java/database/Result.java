package database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Antoine on 18/03/2016.
 * results table
 */
@DatabaseTable(tableName = "result")
public class Result {

    public Result() {}

    @DatabaseField(id = true)
    private String picURL;

    @DatabaseField()
    private String picTempPath;

    @DatabaseField(canBeNull = false)
    private int match;

    @DatabaseField(foreign = true)
    private Request request;

    public boolean isProcessed() {
        return match != -1;
    }

    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match;
    }

    public String getPicURL() {
        return picURL;
    }


    public String getPicTempPath() {
        return picTempPath;
    }

    public void setPicTempPath(String picTempPath) {
        this.picTempPath = picTempPath;
    }

    public Request getRequest() {
        return request;
    }
}
