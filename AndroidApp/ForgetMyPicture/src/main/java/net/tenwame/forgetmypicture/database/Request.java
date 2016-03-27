package net.tenwame.forgetmypicture.database;


import android.graphics.Bitmap;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import net.tenwame.forgetmypicture.PictureAccess;
import net.tenwame.forgetmypicture.UserData;

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

    public enum Kind {QUICK, EXHAUSTIVE}

    public enum Status {
        FETCHING, //still searching for new pictures
        PROCESSING, //some pictures are being processed
        PENDING, //waiting for user to take action
        FINISHED } //done

    Request() {}

    public Request(List<String> keywords, Bitmap originalPic) {
        setStatus(Status.FETCHING);
        setKind(Kind.QUICK);
        user = UserData.getInstance().getCachedUser();
        this.keywords = new ArrayList<>(keywords);
        if(originalPic == null) throw new IllegalArgumentException("original picture is null");
        originalPicPath = REQUEST_PREFIX + id + "_original_" + UUID.randomUUID().toString();
        getOriginalPic().set(originalPic);
        progress = 0;
    }

    public Request(List<String> keywords) {
        this.keywords = new ArrayList<>(keywords);
        setStatus(Status.FETCHING);
        setKind(Kind.EXHAUSTIVE);
        user = UserData.getInstance().getCachedUser();
        progress = 0;

    }

    @DatabaseField(id = true)
    private int id;

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
    }

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

    public Status updateStatus() {
        if(getStatus() == Status.FETCHING && progress == getMaxProgress())
            setStatus(Status.PROCESSING);
        if(getStatus() != Status.PROCESSING)
            return getStatus();

        for(Result result : results) {
            if(!result.isProcessed())
                return getStatus();
        }
        setStatus(Status.PENDING);
        return Status.PENDING;
    }

    public User getUser() {
        return user;
    }
}