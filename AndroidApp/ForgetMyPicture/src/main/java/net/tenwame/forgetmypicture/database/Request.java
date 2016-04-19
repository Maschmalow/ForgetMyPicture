package net.tenwame.forgetmypicture.database;


import android.content.res.Resources;
import android.graphics.Bitmap;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import net.tenwame.forgetmypicture.PictureAccess;
import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.UserData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Antoine on 18/03/2016.
 * requests table
 */
@DatabaseTable(tableName = "request")
public class Request {
    private static final String REQUEST_PREFIX = "request_";

    public enum Kind {QUICK, EXHAUSTIVE;

        public String getString(Resources res) {
            if( this == QUICK)
                return res.getString(R.string.enum_request_kind_quick);
            else  //EXHAUSTIVE
                return res.getString(R.string.enum_request_kind_exhaustive);
        }
    }

    public enum Status {
        FETCHING, //still searching for new pictures
        PROCESSING, //some pictures are being processed
        PENDING, //waiting for user
        PAYED, //paying features unlocked
        FINISHED; //done

        public String getString(Resources res) {
            if(this == FETCHING)
                return res.getString(R.string.enum_request_status_fetching);
            else if(this == PROCESSING)
                return res.getString(R.string.enum_request_status_processing);
            else if(this == PENDING)
                return res.getString(R.string.enum_request_status_pending);
            else if(this == PAYED)
                return res.getString(R.string.enum_request_status_payed);
            else  //FINISHED
                return res.getString(R.string.enum_request_status_finished);
        }
    }

    Request() {}

    public Request(List<String> keywords, Bitmap originalPic) {
        this.keywords = new ArrayList<>(keywords);
        setStatus(Status.FETCHING);
        user = UserData.getUser();
        progress = 0;

        if(originalPic == null) {
            setKind(Kind.EXHAUSTIVE);
        } else {
            setKind(Kind.QUICK);
            originalPicPath = REQUEST_PREFIX + id + "_original_pic_" + UUID.randomUUID().toString();
            getOriginalPic().set(originalPic);
        }
    }

    @DatabaseField(generatedId = true)
    private int id; //TODO: migrate to String (in server too!) and use a user-defined name

    @DatabaseField(canBeNull = false)
    private String status;

    @DatabaseField
    private String motive;

    @DatabaseField
    private int progress;

    @DatabaseField(canBeNull = false)
    private String kind;

    @DatabaseField
    private String originalPicPath;

    @DatabaseField(foreign = true)
    private User user;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> keywords;

    @ForeignCollectionField
    private ForeignCollection<Result> results;

    public Integer getId() {
        return id;
    }

    public Kind getKind() {
        return Kind.valueOf(kind);
    }

    private void setKind(Kind kind) {
        this.kind = kind.toString();
    }

    public Status getStatus() {
        return Status.valueOf(status);
    }

    public void setStatus(Status status) {
        this.status = status.toString();
    }

    public String getMotive() {
        return motive;
    }

    public void setMotive(String motive) {
        this.motive = motive;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getMaxProgress() {
        return  1 << keywords.size();
    } //number of keywords set in powerset

    public List<String> getKeywords() {
        return keywords;
    }

    public PictureAccess getOriginalPic() {
        return new PictureAccess(originalPicPath);
    }

    public ForeignCollection<Result> getResults() {
        return results;
    }

    public Set<Result> addResults(Set<Result> results) {
        try {
            this.results.refreshCollection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Set<Result> newResults = new HashSet<>();
        for(Result result : results)
            if(!this.results.contains(result)) { //this is retarded, but ORMLite's add crashes instead of returning false
                this.results.add(result);        //okay, maybe they don't want to make too much assumptions, but still
                newResults.add(result);
            }

        return newResults;
    }

    public void updateStatus() {
        if(getStatus() == Status.FETCHING && progress == getMaxProgress())
            setStatus(Status.PROCESSING);

        if(getStatus() != Status.PROCESSING)
            return;
        for(Result result : results)
            if(!result.isProcessed())
                return;

        setStatus(Status.PENDING);
    }

    public User getUser() {
        return user;
    }
}