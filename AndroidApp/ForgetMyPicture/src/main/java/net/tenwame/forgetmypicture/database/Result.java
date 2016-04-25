package net.tenwame.forgetmypicture.database;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Antoine on 18/03/2016.
 * results table (see Request for generic explanations)
 */
@DatabaseTable(tableName = "result")
public class Result {

    Result() {}

    public Result(String picURL, String picRefURL, Request request) {
        id = makeId(picURL, request); //simulate mixed primary key
        this.picURL = picURL;
        this.picRefURL = picRefURL;
        this.request = request;
        sent = false;
        match = -1;
    }

    public static String makeId(String picURL, Request request) {
        return picURL + " " + request.getId();
    }

    @DatabaseField(id = true)
    private String id;

    @DatabaseField(canBeNull = false)
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
        return id;
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

    //We want Sets and Maps to behave accordingly to the database
    @Override
    public boolean equals(Object o) {
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;

        return id.equals(((Result) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
