package net.tenwame.forgetmypicture.database;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Antoine on 18/03/2016.
 * results table
 */
@DatabaseTable(tableName = "result")
public class Result {

    Result() {}

    public Result(String picURL, String picRefURL, Request request) {
        this.picRefURL = picRefURL;
        this.picURL = picURL;
        this.request = request;
        match = -1;
    }

    @DatabaseField(id = true)
    private String picURL;

    @DatabaseField(canBeNull = false)
    private String picRefURL;

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

    public String getPicRefURL() {
        return picRefURL;
    }

    public Request getRequest() {
        return request;
    }
}
