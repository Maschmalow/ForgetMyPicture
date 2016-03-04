package net.tenwame.forgetmypicture.search;

import android.content.Context;

import net.tenwame.forgetmypicture.ForgetMyPictureApp;
import net.tenwame.forgetmypicture.Util;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

/**
 * Created by Antoine on 04/03/2016.
 * a class used as a gateway for stored searches
 */
public class Data {
    private static final Context context = ForgetMyPictureApp.getAppContext();
    private static final String SEARCH_URI_PREFIX = "search_";

    public static Search getSearch(Integer id) {
        if(id == null || id < 0 || id > getCurId()) return null;
        return new Search(id);
    }

    public static List<Search> getSearches() {
        List<Search> searches = new ArrayList<>();
        int curId = getCurId();
        for(int i =0; i< curId; i++)
            searches.add(i, new Search(i));
        return searches;
    }

    private static Integer getCurId() {
        JsonArray arr = Util.readJsonStructure(SEARCH_URI_PREFIX + "curId", context);
        return arr.getInt(0);
    }

    public static Search newSearch(List<String> keywords) {
        Integer curId = getCurId();
        curId++;
        Util.writeJsonStructure(Json.createArrayBuilder().add(curId).build(), SEARCH_URI_PREFIX + "curId", context);

        Search ret = new Search(curId);
        ret.setStatus(Search.Status.FETCHING);
        ret.setKeywords(keywords);

        return new Search(curId);
    }

    //A Search instance still represents an acess to the storage
    //it is only focused on a particular search
    public static class Search {

        public enum Status {FETCHING, PROCESSING, FINISHED}

        private final Integer id;
        private final String URI;

        private Search(Integer id) {
            if(id ==  null) throw new IllegalArgumentException("id can't be null");
            this.id = id;
            URI = SEARCH_URI_PREFIX + id.toString();
        }

        public Integer getId() {
            return id;
        }

        public Status getStatus() { //Status must be setup one time on creation (no default value)
            JsonObject obj = readJsonStructure();
            return Status.valueOf(obj.getString("status"));
        }

        public void setStatus(Status status) {
            if(status == null) return;
            JsonObject obj = readJsonStructure();
            obj = Util.jsonObjectToBuilder(obj).add("status", status.toString()).build();
            writeJsonStructure(obj);
        }

        public String getMotive() {
            JsonString string = ((JsonObject) readJsonStructure()).getJsonString("motive");
            return string == null? null : string.getString();
        }

        public void setMotive(String motive) {
            JsonObject obj = readJsonStructure();
            obj = Util.jsonObjectToBuilder(obj).add("motive", (motive == null)? JsonValue.NULL : motive).build();
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
            JsonObject obj = readJsonStructure();
            JsonArrayBuilder arrayBuilder =  Json.createArrayBuilder();
            for( String keyword : keywords)
                arrayBuilder.add(keyword);
            obj = Util.jsonObjectToBuilder(obj).add("keywords", arrayBuilder).build();
            
            writeJsonStructure(obj);
        }

        public Set<Searcher.Result> addResults(Set<Searcher.Result> results) { //returns subset of new results
            Set<Searcher.Result> curResults = getResults();
            Set<Searcher.Result> newResults = new HashSet<>();
            for( Searcher.Result result : results)
                if(curResults.add(result))
                    newResults.add(result);

            JsonObject obj = readJsonStructure();
            JsonArrayBuilder arrayBuilder =  Json.createArrayBuilder();
            for( Searcher.Result result : curResults)
                arrayBuilder.add(Json.createObjectBuilder()
                        .add("picURL", result.getPicURL())
                        .add("picRefURL", result.getPicRefURL())
                        .add("match", result.getMatch()));
            obj = Util.jsonObjectToBuilder(obj).add("results", arrayBuilder).build();

            writeJsonStructure(obj);

            return newResults;
        }

        public Set<Searcher.Result> getResults() {
            JsonObject obj = readJsonStructure();
            Set<Searcher.Result> results = new HashSet<>();
            if(obj.getJsonArray("results") == null) return results;
            for(JsonValue result : obj.getJsonArray("results")) {
                JsonObject resObj = (JsonObject) result;
                Searcher.Result res = new Searcher.Result(resObj.getString("picURL"), resObj.getString("picRefURL"));
                res.setMatch(Double.valueOf(resObj.getString("match")));
                results.add(res);
            }

            return results;
        }
    
        private JsonStructure readJsonStructure() throws FileNotFoundException {
            return Util.readJsonStructure(URI, context);
        }
    
        private void writeJsonStructure(JsonStructure st) throws FileNotFoundException {
            Util.writeJsonStructure(st, URI, context);
        }
        
    }

}
