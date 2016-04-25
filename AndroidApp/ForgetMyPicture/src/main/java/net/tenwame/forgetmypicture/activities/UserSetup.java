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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import net.tenwame.forgetmypicture.DatabaseHelper;
import net.tenwame.forgetmypicture.PictureAccess;
import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.UserData;
import net.tenwame.forgetmypicture.Util;
import net.tenwame.forgetmypicture.services.ServerInterface;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Antoine on 21/02/2016.
 * Activity that asks user information
 */
public class UserSetup extends Activity {
    private static final String TAG = UserSetup.class.getSimpleName();

    public static final String CUR_TMP_FILE_KEY = "CUR_TMP_FILE";
    public static final String SELFIES_PATH_KEY = "SELFIES_PATH";
    private static final int REQUEST_SELFIE_PIC =1;

    private ArrayList<String> selfiesPath = new ArrayList<>();

    private EditText nameField;
    private EditText forenameField;
    private EditText emailField;
    private LinearLayout thumbHolder;

    private String curTmpFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setup);

        nameField = (EditText) findViewById(R.id.name_field);
        forenameField = (EditText) findViewById(R.id.forename_field);
        emailField = (EditText) findViewById(R.id.email_field);
        thumbHolder = (LinearLayout) findViewById(R.id.thumb_holder);
    }

    @Override
    protected void onResume() {
        super.onResume();

        thumbHolder.removeAllViews();
        for(String curSelfiePath : selfiesPath) {
            addSelfieThumb(curSelfiePath);
        }
    }

    public void saveDataFromUI(View view) {

        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        try {
            UserData.getUser().setup(emailField.getText().toString(), nameField.getText().toString(), forenameField.getText().toString(), selfiesPath);
            if(!UserData.getUser().isValid()) {
                Toast.makeText(this, R.string.user_setup_invalid_toast, Toast.LENGTH_SHORT).show();
                return;
            }
            helper.getUserDao().update(UserData.getUser());
        } catch (SQLException e) {
            Log.e(TAG, "Could not save user data", e);
            Toast.makeText(this, R.string.user_setup_save_failed_toast, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        ServerInterface.execute(ServerInterface.ACTION_REGISTER);
        Toast.makeText(this, R.string.saved_toast, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }



    public void takePictureFromUI(View view) {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(picIntent.resolveActivity(getPackageManager()) == null) {
            Log.e(TAG, "Could not find app to take picture");
            Toast.makeText(this, "Could not find app to take picture", Toast.LENGTH_SHORT).show();
            return;
        }

        File tmpFile;
        try {
            tmpFile = File.createTempFile(
                    "selfie_" + UUID.randomUUID().toString(),
                    ".jpg",
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        } catch (IOException e) {
            Log.e(TAG, "Could not create temporary file for picture", e);
            Crittercism.logHandledException(e);
            return;
        }

        curTmpFilePath = tmpFile.getAbsolutePath();
        picIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpFile));

        startActivityForResult(picIntent, REQUEST_SELFIE_PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK || requestCode != REQUEST_SELFIE_PIC)
            return;

        try {
            Util.rotatePicture(new PictureAccess(curTmpFilePath));
        } catch (IOException e) {
            Log.w(TAG, "Could not rotate picture " + curTmpFilePath, e);
            Crittercism.logHandledException(e);
        }
        selfiesPath.add(curTmpFilePath);
        curTmpFilePath = null;

    }

    private void addSelfieThumb(String selfiePath) {
        ImageView thumbView = new ImageView(this);
        int w = getResources().getDimensionPixelSize(R.dimen.thumb_max_width);
        int h = getResources().getDimensionPixelSize(R.dimen.thumb_max_height);
        thumbView.setAdjustViewBounds(true);
        thumbView.setMaxWidth(w);
        thumbView.setMaxHeight(h);
        thumbView.setContentDescription(getResources().getString(R.string.user_setup_selfie_desc));

        Bitmap pic = new PictureAccess(selfiePath).get(w, h);

        thumbView.setImageBitmap(pic);
        thumbHolder.addView(thumbView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CUR_TMP_FILE_KEY, curTmpFilePath);
        outState.putStringArrayList(SELFIES_PATH_KEY, selfiesPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if(state == null) return;

        curTmpFilePath = state.getString(CUR_TMP_FILE_KEY);
        selfiesPath = state.getStringArrayList(SELFIES_PATH_KEY);
    }
}
