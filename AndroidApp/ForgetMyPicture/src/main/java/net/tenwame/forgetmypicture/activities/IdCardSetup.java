package net.tenwame.forgetmypicture.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.crittercism.app.Crittercism;

import net.tenwame.forgetmypicture.PictureAccess;
import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.UserData;
import net.tenwame.forgetmypicture.Util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Activity that let the user add his ID card.
 */
public class IdCardSetup extends Activity {
    private static final String TAG = IdCardSetup.class.getSimpleName();

    public static final String CUR_TMP_FILE_KEY = "CUR_TMP_FILE";
    private static final int REQUEST_IDCARD_PIC = 1;

    private String curIdCardPath;

    private ImageView idCardThumb;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_card_setup);

        idCardThumb = (ImageView) findViewById(R.id.idcard_thumb);
        saveButton = (Button) findViewById(R.id.save_btn);
    }

    public void takeIdCardFromUI(View v) {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(picIntent.resolveActivity(getPackageManager()) == null) {
            Log.e(TAG, "Could not find app to take picture");
            Toast.makeText(this, "Could not find app to take picture", Toast.LENGTH_SHORT).show();
            return;
        }

        File tmpFile;
        try {
            tmpFile = File.createTempFile(
                    "idCard_" + UUID.randomUUID().toString(),
                    ".jpg",
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        } catch (IOException e) {
            Log.e(TAG, "Could not create temporary file for picture", e);
            Crittercism.logHandledException(e);
            return;
        }

        curIdCardPath = tmpFile.getAbsolutePath();
        picIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpFile));

        startActivityForResult(picIntent, REQUEST_IDCARD_PIC);
    }

    public void saveIdCardFromUI(View v) {
        Bitmap idCard = new PictureAccess(curIdCardPath).get();
        if(idCard == null) {
            Log.w(TAG, "No idCard taken");
            Toast.makeText(this, R.string.id_card_invalid_toast, Toast.LENGTH_LONG).show();
            return;
        }

        UserData.getUser().getIdCard().set(idCard);

        if(UserData.getUser().getIdCard().get() == null)
            Log.d(TAG, "Could not save UD card"); //for debug only
        else {
            Toast.makeText(this, R.string.saved_toast, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK || requestCode != REQUEST_IDCARD_PIC)
            return;

        try {
            Util.rotatePicture(new PictureAccess(curIdCardPath));
        } catch (IOException e) {
            Log.w(TAG, "Could not rotate picture " + curIdCardPath, e);
            curIdCardPath = null;
            Crittercism.logHandledException(e);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(curIdCardPath != null) {
            Bitmap thumb = new PictureAccess(curIdCardPath).get();
            idCardThumb.setImageBitmap(thumb);
            idCardThumb.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
        } else {
            idCardThumb.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CUR_TMP_FILE_KEY, curIdCardPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if(state == null) return;

        curIdCardPath = state.getString(CUR_TMP_FILE_KEY);
    }
}
