package net.tenwame.forgetmypicture.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crittercism.app.Crittercism;

import net.tenwame.forgetmypicture.ForgetMyPictureApp;
import net.tenwame.forgetmypicture.PictureAccess;
import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.UserData;
import net.tenwame.forgetmypicture.Util;
import net.tenwame.forgetmypicture.database.Request;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Activity that let the user add his ID card.
 */
public class IdCardSetup extends Activity {
    private static final String TAG = IdCardSetup.class.getSimpleName();

    public static final String CUR_TMP_FILE_KEY = "CUR_TMP_FILE";
    public static final String CUR_REQUEST_KEY = "CUR_REQUEST";
    public static final String EXTRA_REQUEST_KEY = ForgetMyPictureApp.getName() + ".request";
    public static final String EXTRA_SETIDCARD_KEY = ForgetMyPictureApp.getName() + ".idcard";
    private static final int REQUEST_IDCARD_PIC = 1;

    private boolean setIdCard;
    private String curIdCardPath;
    private LinearLayout idCardContent;
    private ImageView idCardThumb;
    private Button takeIdcard;

    private Request request;
    private LinearLayout motiveContent;
    private EditText motiveText;

    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_card_setup);

        try {
            request = ForgetMyPictureApp.getHelper().getRequestDao().queryForId(
                        getIntent().getIntExtra(EXTRA_REQUEST_KEY, -1));
        } catch (SQLException e) {
            request = null; //no request
        }
        setIdCard = getIntent().getBooleanExtra(EXTRA_SETIDCARD_KEY, false);

        idCardThumb = (ImageView) findViewById(R.id.idcard_thumb);
        saveButton = (Button) findViewById(R.id.save_btn);
        takeIdcard = (Button) findViewById(R.id.take_idcard);
        idCardContent = (LinearLayout) findViewById(R.id.idcard_content);

        motiveContent = (LinearLayout) findViewById(R.id.motive_content);
        motiveText = (EditText) findViewById(R.id.motive_text);

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

    public void save(View v) {
        if(curIdCardPath == null && request == null) return;

        if(curIdCardPath != null) {
            Bitmap idCard = new PictureAccess(curIdCardPath).get();
            if( idCard == null ) {
                Log.w(TAG, "No idCard taken");
                Toast.makeText(this, R.string.id_card_invalid_toast, Toast.LENGTH_LONG).show();
                return;
            }

            UserData.getUser().getIdCard().set(idCard);
            idCard.recycle();

            if(! UserData.getUser().getIdCard().getFile().exists() )
                Log.d(TAG, "Could not save ID card"); //for debug only
        }

        if(request != null ) {
            request.setMotive(motiveText.getText().toString());
            try {
                ForgetMyPictureApp.getHelper().getRequestDao().update(request);
            } catch (SQLException e) {
                Log.d(TAG, "Could not save motive");
                return;
            }

        }

        Toast.makeText(this, R.string.saved_toast, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();

    }


    @Override
    protected void onResume() {
        super.onResume();

        load();
    }

    private void load() {
        Resources res = getResources();

        Util.setViewVisibleWhen(request != null, motiveContent);
        Util.setViewVisibleWhen(setIdCard, idCardContent);

        if(curIdCardPath != null) {
            int w = res.getDimensionPixelSize(R.dimen.thumb_max_height);
            int h = res.getDimensionPixelSize(R.dimen.thumb_max_width);
            Bitmap thumb = new PictureAccess(curIdCardPath).get();
            idCardThumb.setImageBitmap(thumb);
            idCardThumb.setVisibility(View.VISIBLE);
            takeIdcard.setVisibility(View.GONE);
        } else {
            idCardThumb.setVisibility(View.GONE);
            takeIdcard.setVisibility(View.VISIBLE);
        }


    }

    public void setRequest(Request request) {
        this.request = request;
        load();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CUR_TMP_FILE_KEY, curIdCardPath);
        if(request != null)
            outState.putInt(CUR_REQUEST_KEY, request.getId());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if(state == null) return;

        curIdCardPath = state.getString(CUR_TMP_FILE_KEY);
        if(state.containsKey(CUR_REQUEST_KEY))
            try {
                request = ForgetMyPictureApp.getHelper().getRequestDao().queryForId(state.getInt(CUR_REQUEST_KEY));
            } catch (SQLException ignored) {}
    }
}
