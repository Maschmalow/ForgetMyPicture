package net.tenwame.forgetmypicture.database;


import android.graphics.Bitmap;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import net.tenwame.forgetmypicture.ForgetMyPictureApp;
import net.tenwame.forgetmypicture.PictureAccess;
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
    private static int cur_id;
    static {
        try {
            cur_id = (int) ForgetMyPictureApp.getHelper().getRequestDao().countOf();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public enum Kind {QUICK, EXHAUSTIVE}

    public enum Status {
        FETCHING, //still searching for new pictures
        PROCESSING, //some pictures are being processed
        PENDING, //waiting for user to take action
        FINISHED } //done

    Request() {}

    public Request(List<String> keywords, Bitmap originalPic) {
        id = cur_id++; //assumes that every newly created request will be stored
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

    @DatabaseField(id = true)
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

    @ForeignCollectionField(eager = true)
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
        Set<Result> newResults = new HashSet<>();
        for(Result result : results)
            if(this.results.add(result))
                newResults.add(result);

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