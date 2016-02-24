package net.tenwame.forgetmypicture.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.UserData;

public class HomepageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
    }

    public void startNewSearchFromUI(View v) {
        if(!UserData.getInstance().isSet()) {
            Toast.makeText(this, R.string.homepage_setup_data_toast, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, UserSetupActivity.class));
        } else {
            startActivity(new Intent(this, SearchActivity.class));
        }
    }
}
