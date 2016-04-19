package net.tenwame.forgetmypicture.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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


    @Override
        public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if(R.id.settings_btn == item.getItemId())
            startActivity(new Intent(this, Settings.class));
        else if(R.id.account_btn == item.getItemId())
            startActivity(new Intent(this, UserSetup.class));
        else
            return super.onMenuItemSelected(featureId, item);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homepage_action_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }
}
