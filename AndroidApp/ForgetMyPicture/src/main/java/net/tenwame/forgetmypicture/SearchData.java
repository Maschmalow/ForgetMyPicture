package net.tenwame.forgetmypicture;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;

/**
 * Created by Antoine on 04/03/2016.
 * a class used as a gateway for stored requests
 * requests data can be modified in background, so we're not dealing with cached values for the demo
 */
public class SearchData {
    private static final Context context = ForgetMyPictureApp.getAppContext();
    private static final String REQUEST_URI_PREFIX = "request_";

    public static Request getRequest(Integer id) {
        if(id == null || id < 0 || id >= getCurId()) return null;
        return new Request(id);
    }

    public static List<Request> getRequests() {
        List<Request> requests = new ArrayList<>();
        int curId = getCurId();
        for(int i =0; i< curId; i++)
            requests.add(i, new Request(i));
        return requests;
    }

    private static Integer getCurId() {
        JsonArray arr = (JsonArray) Util.readJsonStructure(REQUEST_URI_PREFIX + "curId", context);
        return arr.getInt(0);
    }

    private static void setCurId(Integer id) {
        Util.writeJsonStructure(Json.createArrayBuilder().add(id).build(), REQUEST_URI_PREFIX + "curId", context);
    }

    static Request newRequest(List<String> keywords) {
        Integer curId = getCurId();
        setCurId(curId+1);

        Request ret = new Request(curId);
        ret.setStatus(Request.Status.FETCHING);
        ret.setKeywords(keywords);

        return new Request(curId);
    }

    //A Request instance still represents an access to the storage
    //it is only focused on a particular search
    public static class Request {

        public enum Status {
            FETCHING, //still searching for new pictures
            PROCESSING, //some pictures are being processed
            PENDING, //waiting for user to take action
            FINISHED } //done

        private final Integer id;
        private final String URI;

        private Request(Integer id) {
            if(id ==  null) throw new IllegalArgumentException("id can't be null");
            this.id = id;
            URI = REQUEST_URI_PREFIX + id.toString();
        }

        public Integer getId() {
            return id;
        }

        public Status getStatus() { //Status must be setup one time on creation (no default value)
            JsonObject obj = (JsonObject) readJsonStructure();
            return Status.valueOf(obj.getString("status"));
        }

        void setStatus(Status status) {
            if(status == null) return;
            JsonObject obj = (JsonObject) readJsonStructure();
            obj = Util.jsonObjectToBuilder(obj).add("status", status.toString()).build();
            writeJsonStructure(obj);
        }

        public int getProgress() { //progress is the number of keywords combination done
            return ((JsonObject) readJsonStructure()).getInt("progress", 0);
        }

        public void setProgres(int progress) {
            JsonObject obj = (JsonObject) readJsonStructure();
            obj = Util.jsonObjectToBuilder(obj).add("progress", progress).build();
            writeJsonStructure(obj);
        }

        public String getMotive() {
            return  ((JsonObject) readJsonStructure()).getString("motive", null);
        }

        public void setMotive(String motive) {
            JsonObject obj = (JsonObject) readJsonStructure();
            if(motive == null)
                obj = Util.jsonObjectToBuilder(obj).add("motive", JsonValue.NULL).build();
            else
                obj = Util.jsonObjectToBuilder(obj).add("motive", motive).build();
            writeJsonStructure(obj);
        }

        public List<String> getKeywords() { //keywords must be setup one time on creation (no default value)
            JsonArray arr = ((JsonObject) readJsonStructure()).getJsonArray("keywords");
            List<String> keywords = new ArrayList<>();
            for(int i=0; i<arr.size(); i++)
                keywords.add(arr.getString(i));
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            JsonObject obj = (JsonObject) readJsonStructure();
            JsonArrayBuilder arrayBuilder =  Json.createArrayBuilder();
            for( String keyword : keywords)
                arrayBuilder.add(keyword);
            obj = Util.jsonObjectToBuilder(obj).add("keywords", arrayBuilder).build();
            
            writeJsonStructure(obj);
        }

        public Set<SearchService.Result> addResults(Set<SearchService.Result> results) { //returns subset of new results
            Set<SearchService.Result> curResults = getResults();
            Set<SearchService.Result> newResults = new HashSet<>();
            for( SearchService.Result result : results)
                if(curResults.contains(result)) {
                    curResults.remove(result);
                    curResults.add(result); //because we may want to update the result
                }
                else
                    newResults.add(result);

            JsonObject obj = (JsonObject) readJsonStructure();
            JsonArrayBuilder arrayBuilder =  Json.createArrayBuilder();
            for( SearchService.Result result : curResults)
                arrayBuilder.add(Json.createObjectBuilder()
                        .add("picURL", result.getPicURL())
                        .add("picRefURL", result.getPicRefURL())
                        .add("match", result.getMatch()));
            obj = Util.jsonObjectToBuilder(obj).add("results", arrayBuilder).build();

            writeJsonStructure(obj);

            return newResults;
        }

        public Set<SearchService.Result> getResults() {
            JsonObject obj = (JsonObject) readJsonStructure();
            Set<SearchService.Result> results = new HashSet<>();
            if(obj.getJsonArray("results") == null) return results;
            for(JsonValue result : obj.getJsonArray("results")) {
                JsonObject resObj = (JsonObject) result;
                SearchService.Result res = new SearchService.Result(resObj.getString("picURL"), resObj.getString("picRefURL"));
                res.setMatch(Double.valueOf(resObj.getString("match")));
                results.add(res);
            }

            return results;
        }
    
        private JsonStructure readJsonStructure() {
            return Util.readJsonStructure(URI, context);
        }
    
        private boolean writeJsonStructure(JsonStructure st)  {
            return Util.writeJsonStructure(st, URI, context);
        }
        
    }

}
