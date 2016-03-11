package net.tenwame.forgetmypicture;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;

/**
 * Created by Antoine on 04/03/2016.
 * what a mess
 */
public class Util {

    public static JsonObjectBuilder jsonObjectToBuilder(JsonObject jo) {
        JsonObjectBuilder job = Json.createObjectBuilder();

        for (Map.Entry<String, JsonValue> entry : jo.entrySet()) {
            job.add(entry.getKey(), entry.getValue());
        }

        return job;
    }

    public static JsonStructure readJsonStructure(String URI, Context context) {
        JsonStructure st;
        try (JsonReader reader = Json.createReader(context.openFileInput(URI))) {
            st = reader.read();
        } catch (Exception e) {
            return null;
        }
        return st;
    }

    public static boolean writeJsonStructure(JsonStructure st, String URI, Context context)  {
        try (JsonWriter writer = Json.createWriter(context.openFileOutput(URI, Context.MODE_PRIVATE))) {
            writer.write(st);
        } catch (Exception e) {
            return false;
        }

        return true;
    }


    public static <T> List<List<T>> powerSet(Collection<T> list) {
        List<List<T>> ps = new ArrayList<>();
        ps.add(new ArrayList<T>());   // add the empty set

        // for every item in the original list
        for (T item : list) {
            List<List<T>> newPs = new ArrayList<>();

            for (List<T> subset : ps) {
                // copy all of the current powerSet's subsets
                newPs.add(subset);

                // plus the subsets appended with the current item
                List<T> newSubset = new ArrayList<>(subset);
                newSubset.add(item);
                newPs.add(newSubset);
            }

            // powerSet is now powerSet of list.subList(0, list.indexOf(item)+1)
            ps = newPs;
        }
        return ps;
    }
}
