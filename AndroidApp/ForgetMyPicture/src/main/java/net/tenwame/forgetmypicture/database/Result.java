package net.tenwame.forgetmypicture.database;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

/**
 * Created by Antoine on 18/03/2016.
 * results table
 */
@DatabaseTable(tableName = "result")
public class Result {

    Result() {}

    public Result(String picURL, String picRefURL, Request request) {
        this.picURL = picURL + " " + UUID.randomUUID().toString();
        //this is bad, but we need unique results for the whole app
        //and ORMLite doesn't support multiple primary keys
        this.picRefURL = picRefURL;
        this.request = request;
        sent = false;
        match = -1;
    }

    @DatabaseField(id = true)
    private String picURL;

    @DatabaseField(canBeNull = false)
    private String picRefURL;

    @DatabaseField(canBeNull = false)
    private int match;

    @DatabaseField(canBeNull = false)
    private boolean sent;

    @DatabaseField(foreign = true)
    private Request request;

    public boolean isProcessed() {
        return match != -1;
    }

    public void setSent() {
        sent = true;
    }

    public boolean isSent() {
        return sent;
    }

    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match;
    }

    public String getId() {
        return picURL;
    }

    public String getPicURL() {
        return picURL.substring(0, picURL.indexOf(' '));
    }

    public String getPicRefURL() {
        return picRefURL;
    }

    public Request getRequest() {
        return request;
    }

    //We want Sets and Maps to behave accordingly to the database
    @Override
    public boolean equals(Object o) {
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;

        return picURL.equals(((Result) o).picURL);
    }

    @Override
    public int hashCode() {
        return picURL.hashCode();
    }

}
