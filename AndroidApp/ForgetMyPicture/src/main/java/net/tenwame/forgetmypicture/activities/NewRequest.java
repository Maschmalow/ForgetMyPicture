package net.tenwame.forgetmypicture.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.tenwame.forgetmypicture.Manager;
import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.UserData;

import java.util.Arrays;

public class NewRequest extends Activity {
    private static final String TAG = NewRequest.class.getCanonicalName();

    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_USER_SETUP = 1;


    private LinearLayout selectOriginalPic;
    private TextView originalPicPath;
    private EditText keywordsField;

    private Bitmap originalPic = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!UserData.getUser().isValid()) {
            Toast.makeText(this, R.string.search_setup_data_toast, Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(this, UserSetup.class), REQUEST_USER_SETUP);
        }

        setContentView(R.layout.activity_new_request);

        originalPicPath = (TextView) findViewById(R.id.original_pic_path);
        selectOriginalPic = (LinearLayout) findViewById(R.id.select_original_pic);
        keywordsField = (EditText) findViewById(R.id.keywords_field);
        keywordsField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    startSearchFromUI(v);
                    return true;
                }
                return false;
            }
        });
    }

    public void startSearchFromUI(View view) {
        Log.v(TAG, "Request started from UI");
        String[] keywords = keywordsField.getText().toString().split(" ");

        if(Manager.getInstance().startNewRequest(Arrays.asList(keywords), originalPic) == null) {
            Toast.makeText(this, R.string.search_failed_toast, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.search_started_toast, Toast.LENGTH_SHORT).show();
        }

        finish();
    }


    public void onSearchKindCheckedFromUI(View view) {
        CheckBox box = (CheckBox) view;
        if(box.isChecked())
            selectOriginalPic.setVisibility(View.VISIBLE);
        else {
            selectOriginalPic.setVisibility(View.GONE);
            originalPic = null;
            originalPicPath.setText(R.string.search_no_picture_selected);
        }
    }

    public void takeOriginalPicFromUI(View view) {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(data.getData(),
                    filePathColumn, null, null, null);
            if(cursor == null) {
                Toast.makeText(this, "Could not fetch selected picture", Toast.LENGTH_LONG).show();
                return;
            }
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            originalPic = BitmapFactory.decodeFile(picturePath);
            originalPicPath.setText(picturePath);
        }
        if(requestCode == REQUEST_USER_SETUP && resultCode != RESULT_OK) {
            finish();
        }

    }
}
