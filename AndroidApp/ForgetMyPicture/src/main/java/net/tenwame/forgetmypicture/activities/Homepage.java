package net.tenwame.forgetmypicture.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.tenwame.forgetmypicture.R;

public class Homepage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
    }

    public void startNewSearchFromUI(View v) {
        startActivity(new Intent(this, NewRequest.class));
    }

    public void goToRequestsFromUI(View v) {
        startActivity(new Intent(this, RequestsPanel.class));
    }

    public void goToDataFromUI(View view) {
        startActivity(new Intent(this, UserSetup.class));
    }
}
