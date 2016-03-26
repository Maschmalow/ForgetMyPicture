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

    private String path;
    private PathGenerator generator;

    public PictureAccess(String path, PathGenerator generator) {
        this.path = path;
        this.generator = generator;
    }

    public InputStream openStream() throws FileNotFoundException {
        return ForgetMyPictureApp.getContext().openFileInput(path);
    }
    
    public Bitmap get() {
        try(InputStream stream = ForgetMyPictureApp.getContext().openFileInput(path)) {
            return BitmapFactory.decodeStream(stream);
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
        if(path == null)
            path = generator.setNewPath();

        try(OutputStream stream = ForgetMyPictureApp.getContext().openFileOutput(path, Context.MODE_PRIVATE)) {
            pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public interface PathGenerator {
        String setNewPath();
    }

}
