package net.tenwame.manager.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import net.tenwame.manager.Main;

import java.sql.SQLException;

/**
 * Created by Antoine on 18/03/2016.
 * results table
 */
@DatabaseTable(tableName = "result")
public class Result {

    Result() {
        nb_fail = 0; //default value
    }

    @DatabaseField(generatedId = true)
    private int id; //for redbeans

    @DatabaseField(canBeNull = false)
    private String pic_url;

    @DatabaseField(canBeNull = false)
    private int pic_match;

    @DatabaseField(canBeNull = false)
    private String request_id;

    @DatabaseField(defaultValue = "0", canBeNull = false)
    private int nb_fail;

    public int getId() {
        return id;
    }

    public boolean isProcessed() {
        return pic_match != -1;
    }

    public int getMatch() {
        return pic_match;
    }

    public void setMatch(int match) {
        this.pic_match = match;
    }

    public String getPicURL() {
        return pic_url;
    }

    public Request getRequest() throws SQLException {
        return Main.getRequestDao().queryForEq("request_id", request_id).get(0);
    }

    public int getNb_fail() {
        return nb_fail;
    }
    public void incNbFail() {
        nb_fail++;
    }

    @Override
    public boolean equals(Object o) {
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;

        Result result = (Result) o;

        if( !pic_url.equals(result.pic_url) ) return false;
        return request_id.equals(result.request_id);

    }

    @Override
    public int hashCode() {
        int result = pic_url.hashCode();
        result = 31 * result + request_id.hashCode();
        return result;
    }


}
