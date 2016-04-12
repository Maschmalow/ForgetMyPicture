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
        this.picURLs = picURL + " " + picRefURL;
        this.request = request;
        match = -1;
    }

    @DatabaseField(id = true) //same thing here, we have to merge picURL and picRefURL
    private String picURLs;  //because ORMLite doesn't support multiple primary keys

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
        return picURLs.substring(0, picURLs.indexOf(' '));
    }

    public String getPicRefURL() {
        return picURLs.substring(picURLs.indexOf(' '), picURLs.length());
    }

    public Request getRequest() {
        return request;
    }

    @Override
    public boolean equals(Object o) {
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;

        return picURLs.equals(((Result) o).picURLs);
    }

    @Override
    public int hashCode() {
        return picURLs.hashCode();
    }
}
