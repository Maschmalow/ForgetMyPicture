package net.tenwame.forgetmypicture;

import android.view.View;

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


    /**
     * Equivalent to {@Link Util#powerSet}(collection).size()
     */
    public static <T> int powerSetSize(Collection<T> collection) {
        return 1 << collection.size();
    }

    /**
     * Equivalent to powerSet(collection).get(index)
     * O(collection.size()) complexity
     */
    public static <T> List<T> powerSetAtIndex(Collection<T> collection, int index) {
        if(index > powerSetSize(collection))
            return null;

        int curMask = 1;
        List<T> set = new ArrayList<>();
        for(T item : collection) {
            if((index & curMask) != 0)
                set.add(item);
            curMask = curMask << 1;
        }

        return set;
    }

    public interface Filter<T> {
        boolean isAllowed(T candidate);
    }

    public static void setViewVisibleWhen(boolean visible, View v) {
        if(visible)
            v.setVisibility(View.VISIBLE);
        else
            v.setVisibility(View.GONE);
    }

}
