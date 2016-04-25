package net.tenwame.forgetmypicture;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
     * Computes the power set of a collection, and returns the element at the given index
     * i.e. equivalent to powerSet(collection).get(index)
     * O(collection.size()) complexity
     * note: the PowerSet implementation is not needed for this app
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

    /**
     * Equivalent to powerSet(collection).size()
     */
    public static <T> int powerSetSize(Collection<T> collection) {
        return 1 << collection.size();
    }

    /**
     * generic interface used to filter collections
     */
    public interface Filter<T> {
        boolean isAllowed(T candidate);
    }

    /**
     * Apply a given filter to a collection.
     * If the filter is null, then nothing is done
     * @param collection the collection to filter
     * @param filter the filter to apply.
     * @return the same collection, filterd
     */
    public static <T> Collection<T> applyFilter(@Nullable Collection<T> collection, @Nullable Filter<T>filter) {
        if(collection == null || filter == null) return collection;
        for( Iterator<T> iterator = collection.iterator(); iterator.hasNext(); )
            if( !filter.isAllowed(iterator.next()) )
                iterator.remove();

        return collection;
    }

    /**
     * helper method to set View visibility
     */
    public static void setViewVisibleWhen(boolean visible, View v) {
        if(visible)
            v.setVisibility(View.VISIBLE);
        else
            v.setVisibility(View.GONE);
    }

    /**
     * Rotate the given picture according to the EXIF data
     * Some devices camera rotate the picture in "random" ways,
     * so we have to put it in portrait mode manually
     * @throws IOException if the EXIF data can't be read
     */
    public static void rotatePicture(PictureAccess pic) throws IOException{

        ExifInterface ei = new ExifInterface(pic.getFile().getAbsolutePath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if(orientation == ExifInterface.ORIENTATION_NORMAL ||
                orientation == ExifInterface.ORIENTATION_UNDEFINED)
            return;

        Bitmap buf = pic.get();
        Matrix rotation = new Matrix();
        if(orientation == ExifInterface.ORIENTATION_ROTATE_270)
            rotation.setRotate(270, buf.getWidth()/2,buf.getHeight()/2);
        else if(orientation == ExifInterface.ORIENTATION_ROTATE_180)
            rotation.setRotate(180, buf.getWidth()/2,buf.getHeight()/2);
        else if(orientation == ExifInterface.ORIENTATION_ROTATE_90)
            rotation.setRotate(90, buf.getWidth() / 2, buf.getHeight() / 2);
        else
            return;

        Bitmap rotated = Bitmap.createBitmap(buf, 0, 0, buf.getWidth(), buf.getHeight(), rotation, true);
        pic.set(rotated);
        rotated.recycle();
        buf.recycle();
    }

}
