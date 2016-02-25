package net.tenwame.forgetmypicture.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.UserData;

/**
 * Created by Antoine on 21/02/2016.
 * Activity that asks user information
 */
public class UserSetupActivity extends Activity {
    private static final String TAG = UserSetupActivity.class.getSimpleName();

    private static final int REQUEST_SELFIE_PIC =1;
    private static final int REQUEST_IDCARD_PIC =2;
    private final UserData data = UserData.getInstance();

    private Bitmap idcardBitmap; //TODO: to be removed
    private Bitmap selfieBitmap;

    private EditText nameField;
    private EditText forenameField;
    private EditText emailField;
    private ImageView selfieThumb;
    private ImageView idcardThumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(data.isSet()) {
            Log.e(TAG, "User data is already set");
            finish();
        }
        setContentView(R.layout.activity_user_setup);

        nameField = (EditText) findViewById(R.id.name_field);
        forenameField = (EditText) findViewById(R.id.forename_field);
        emailField = (EditText) findViewById(R.id.email_field);
        selfieThumb = (ImageView) findViewById(R.id.selfie_thumb);
        idcardThumb = (ImageView) findViewById(R.id.idcard_thumb);
    }

    public void saveDataFromUI(View view) {
        data.setupUserData(idcardBitmap, selfieBitmap, nameField.getText().toString(), forenameField.getText().toString(), emailField.getText().toString());
        Toast.makeText(this, R.string.user_setup_save_toast, Toast.LENGTH_SHORT).show();
        finish();
    }



    public void takePictureFromUI(View view) {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(picIntent.resolveActivity(getPackageManager()) == null) {
            Log.e(TAG, "Could not find app to take picture");
            return;
        }
        int requestCode = 0;
        if(view.getId() == R.id.take_selfie_btn)
            requestCode = REQUEST_SELFIE_PIC;
        if(view.getId() == R.id.take_idcard_btn)
            requestCode = REQUEST_IDCARD_PIC;


        startActivityForResult(picIntent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) return;
        Bitmap thumb = (Bitmap) data.getExtras().get("data");

        if(requestCode == REQUEST_SELFIE_PIC) {
            selfieBitmap = thumb;
            selfieThumb.setImageBitmap(thumb);
        }
        if(requestCode == REQUEST_IDCARD_PIC) {
            idcardBitmap = thumb;
            idcardThumb.setImageBitmap(thumb);
        }
    }
}
