package net.tenwame.forgetmypicture.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.UserData;
import net.tenwame.forgetmypicture.database.User;

public class Account extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Resources res = getResources();
        User user = UserData.getUser();

        ((TextView) findViewById(R.id.name)).setText(res.getString(R.string.account_name, user.getName()));
        ((TextView) findViewById(R.id.forename)).setText(getString(R.string.account_forename, user.getForename()));
        ((TextView) findViewById(R.id.email)).setText(getString(R.string.account_email, user.getEmail()));
        ((ImageView) findViewById(R.id.idcard_thumb)).setImageBitmap(user.getIdCard().get());
    }



    public void editAccountFromUI(View v) {
        startActivity(new Intent(this, UserSetup.class));
    }

    public void setIdCardFromUI(View v) {
        startActivity(new Intent(this, IdCardSetup.class));
    }
}
