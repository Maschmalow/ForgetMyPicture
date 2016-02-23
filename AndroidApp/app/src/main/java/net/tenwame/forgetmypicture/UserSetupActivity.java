package net.tenwame.forgetmypicture;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Antoine on 21/02/2016.
 * Activity that asks user information
 */
public class UserSetupActivity extends Activity {

    private static final String TAG = SearchActivity.class.getCanonicalName();
    private final UserData data = UserData.getInstance();

    private EditText nameField;
    private EditText forenameField;
    private EditText emailField;

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
    }

    public void saveDataFromUI(View view) {
        data.setupUserData(null, null, nameField.getText().toString(), forenameField.getText().toString(), emailField.getText().toString());
        Toast.makeText(this, R.string.user_setup_save_toast, Toast.LENGTH_SHORT).show();
        finish();
    }
}
