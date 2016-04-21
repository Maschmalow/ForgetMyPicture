package net.tenwame.forgetmypicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Antoine on 26/03/2016.
 * used by database objects to store pictures on disk
 */
public class PictureAccess {

    private final String path;

    public PictureAccess(String path) {
        this.path = path;
    }

    public InputStream openStream() throws FileNotFoundException {
        return ForgetMyPictureApp.getContext().openFileInput(path);
    }
    
    public Bitmap get() {
        try {
            InputStream stream = ForgetMyPictureApp.getContext().openFileInput(path);
            try {
                return BitmapFactory.decodeStream(stream);
            } finally {
                if(stream != null)
                    stream.close();
            }
        } catch (FileNotFoundException e) {
            return null;
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(Bitmap pic) {
        if(pic == null) {
            ForgetMyPictureApp.getContext().deleteFile(path);
            return;
        }

        try {
            OutputStream stream = ForgetMyPictureApp.getContext().openFileOutput(path, Context.MODE_PRIVATE);
            try {
                pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
            } finally {
                if(stream != null)
                    stream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
