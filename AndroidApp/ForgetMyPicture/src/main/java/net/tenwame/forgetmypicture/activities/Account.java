package net.tenwame.forgetmypicture.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.UserData;
import net.tenwame.forgetmypicture.database.Selfie;
import net.tenwame.forgetmypicture.database.User;

/**
 * Activity that display account information to the user
 */
public class Account extends Activity {


    private TextView empty;
    private LinearLayout contentLayout;

    private TextView name;
    private TextView forename;
    private TextView email;
    private ImageView idCardThumb;
    private LinearLayout selfiesContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        contentLayout = (LinearLayout) findViewById(R.id.content_layout);
        empty = (TextView) findViewById(R.id.empty);
        email = (TextView) findViewById(R.id.email);
        forename = (TextView) findViewById(R.id.forename);
        name = (TextView) findViewById(R.id.name);
        idCardThumb = (ImageView) findViewById(R.id.idcard_thumb);
        selfiesContainer = (LinearLayout) findViewById(R.id.selfies_container);
    }

    @Override
    protected void onResume() {
        super.onResume();

        load();
    }

    private void load() {
        Resources res = getResources();
        User user = UserData.getUser();

        if(UserData.getUser().isValid()) {
            contentLayout.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
            name.setText(res.getString(R.string.account_name, user.getName()));
            forename.setText(getString(R.string.account_forename, user.getForename()));
            email.setText(getString(R.string.account_email, user.getEmail()));
            int width = getResources().getDimensionPixelSize(R.dimen.thumb_max_width);
            int height = getResources().getDimensionPixelSize(R.dimen.thumb_max_height);
            idCardThumb.setImageBitmap(user.getIdCard().get(width, height));
            selfiesContainer.removeAllViews();
            for( Selfie selfie : UserData.getUser().getSelfies())
                addSelfie(selfie);
        } else {
            empty.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
        }
    }

    private void addSelfie(Selfie selfie) {
        ImageView thumbView = new ImageView(this);
        int w = getResources().getDimensionPixelSize(R.dimen.thumb_max_width);
        int h = getResources().getDimensionPixelSize(R.dimen.thumb_max_height);
        thumbView.setAdjustViewBounds(true);
        thumbView.setContentDescription(getResources().getString(R.string.user_setup_selfie_desc));

        Bitmap pic = selfie.getPic().get(w, h);

        thumbView.setImageBitmap(pic);
        selfiesContainer.addView(thumbView);
    }

    public void editAccountFromUI(View v) {
        startActivity(new Intent(this, UserSetup.class));
    }

    public void setIdCardFromUI(View v) {
        Intent intent = new Intent(this, IdCardSetup.class);
        intent.putExtra(IdCardSetup.EXTRA_SETIDCARD_KEY, true);
        startActivity(intent);
    }
}
