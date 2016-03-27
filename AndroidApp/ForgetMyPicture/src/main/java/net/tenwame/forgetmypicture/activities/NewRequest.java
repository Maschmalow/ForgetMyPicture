package net.tenwame.forgetmypicture.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.tenwame.forgetmypicture.Manager;
import net.tenwame.forgetmypicture.R;

import java.util.Arrays;

public class NewRequest extends Activity {

    private static final String TAG = NewRequest.class.getCanonicalName();

    private EditText keywordsField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);

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
        if(Manager.getInstance().startNewRequest(Arrays.asList(keywords)) == null) {
            Toast.makeText(this, R.string.search_failed_toast, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.search_started_toast, Toast.LENGTH_SHORT).show();
        }

        finish();
    }


}
