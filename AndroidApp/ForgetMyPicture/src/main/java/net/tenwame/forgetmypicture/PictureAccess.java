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
    protected final String path;


    public PictureAccess(String path) {
        this.path = path;
    }

    public File getFile() {
        return new File(path);
    }


    /**
     * gives a raw stream of the disk file
     * @return the picture stream
     */
    public final InputStream openStream() throws FileNotFoundException {
        return new FileInputStream(getFile());
    }

    /**
     * get the bitmap saved on disk
     * @return the bitmap, or null if there is an error
     */
    public final Bitmap get() {
        return BitmapFactory.decodeFile(getFile().getAbsolutePath());
    }

    /**
     * Returns te stored bitmap, scaled match the supplied size.
     * Only the needed for the target bitmap memory is allocated,
     * and the aspect ratio is conserved.
     * @param width the desired width
     * @param height the desired height
     */
    public final Bitmap get(int width, int height) {
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
    public final void set(Bitmap pic) {
        if(pic == null) {
            if(!getFile().delete())
                Log.e(TAG, "Could not delete picture " + path);
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


    public static class Internal extends PictureAccess{

        public Internal(String path) {
            super(path);
        }

        @Override
        public File getFile() {
            return ForgetMyPictureApp.getContext().getFileStreamPath(path);
        }
    }
}
