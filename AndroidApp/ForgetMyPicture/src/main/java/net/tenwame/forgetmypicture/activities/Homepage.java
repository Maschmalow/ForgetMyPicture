package net.tenwame.forgetmypicture.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.UserData;

public class Homepage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
    }

    public void startNewSearchFromUI(View v) {
        if(!UserData.getUser().isValid()) {
            Toast.makeText(this, R.string.homepage_setup_data_toast, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, UserSetup.class));
        } else {
            startActivity(new Intent(this, NewRequest.class));
        }
    }

    public void goToRequestsFromUI(View v) {
        startActivity(new Intent(this, RequestsPanel.class));
    }
}
