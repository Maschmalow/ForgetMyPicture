package net.tenwame.forgetmypicture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Antoine on 04/03/2016.
 * what a mess
 */
public class Util {

    public static String camelToSnakeCase(String fieldName) {
        char[] array = fieldName.toCharArray();
        String ret = "";
        for(char c : array) {
            if( Character.isUpperCase(c) )
                ret +=  "_" + Character.toLowerCase(c);
            else
                ret += c;
        }
        return ret;
    }

    public static <T> List<List<T>> powerSet(Collection<T> list) {
        List<List<T>> ps = new ArrayList<>();
        ps.add(new ArrayList<T>());

        for (T item : list) {
            List<List<T>> newPs = new ArrayList<>();

            for (List<T> subset : ps) {
                newPs.add(subset);
                List<T> newSubset = new ArrayList<>(subset);
                newSubset.add(item);
                newPs.add(newSubset);
            }

            ps = newPs;
        }
        return ps;
    }
}
