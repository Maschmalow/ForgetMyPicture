package net.tenwame.forgetmypicture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Antoine on 26/03/2016.
 * used to get access to pictures stored on disk (png format)
 */
public class PictureAccess {
    private static final String TAG = PictureAccess.class.getSimpleName();
    private final String path;

    /**
     * Control whether the file is stored in the package internal dir (Context methods)
     * or should be treated as a public file (absolute path)
     */
    private final boolean internal;

    public PictureAccess(String path) {
        this(path, true);
    }

    public PictureAccess(String path, boolean internal) {
        this.path = path;
        this.internal = internal;
    }

    private File getFile() {
        if(internal)
            return ForgetMyPictureApp.getContext().getFileStreamPath(path);
        else
            return new File(path);
    }


    /**
     * gives a raw stream of the disk file
     * @return the picture stream
     */
    public InputStream openStream() throws FileNotFoundException {
        return new FileInputStream(getFile());
    }

    /**
     * get the bitmap saved on disk
     * @return the bitmap, or null if there is an error
     */
    public Bitmap get() {
        return BitmapFactory.decodeFile(getFile().getAbsolutePath());
    }

    /**
     * Returns te stored bitmap, scaled match the supplied size.
     * Only the needed for the target bitmap memory is allocated,
     * and the aspect ratio is conserved.
     * @param width the desired width
     * @param height the desired height
     */
    public Bitmap get(int width, int height) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getFile().getAbsolutePath(), opts);
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = Math.min(opts.outWidth/width, opts.outHeight/height);

        return BitmapFactory.decodeFile(getFile().getAbsolutePath(), opts);
    }

    /**
     * set the on-disk picture.
     * if pic is null, then it is deleted
     * @param pic the bitmap to set
     */
    public void set(Bitmap pic) {
        if(pic == null) {
            if(!getFile().delete())
                Log.e(TAG, "Could not delete picture " + path + (internal? " (internal)" : "") );
            return;
        }

        try {
            OutputStream stream = new FileOutputStream(getFile());
            try {
                pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
