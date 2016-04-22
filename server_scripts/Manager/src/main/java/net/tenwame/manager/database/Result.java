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

    Result() {}

    @DatabaseField(generatedId = true)
    private int id; //for redbeans

    @DatabaseField(canBeNull = false)
    private String pic_url;

    @DatabaseField(defaultValue = "NULL")
    private int pic_match;

    @DatabaseField(canBeNull = false)
    private String request_id;

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
}
