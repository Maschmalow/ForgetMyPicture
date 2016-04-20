package net.tenwame.forgetmypicture.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.fragments.RequestInfos;
import net.tenwame.forgetmypicture.fragments.RequestsList;

public class RequestsPanel extends FragmentActivity {

    private static final String IN_LIST_FRAG_KEY = "IN_LIST_FRAG_KEY";
    private RequestsList requests;
    private RequestInfos infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, requests = new RequestsList())
                .add(android.R.id.content, infos = new RequestInfos())
                .hide(infos)
                .commit();

    }

    public void goToRequestInfo(Request request) {
        getSupportFragmentManager().beginTransaction()
                .hide(requests)
                .show(infos)
                .addToBackStack(null)
                .commit();

        infos.setRequest(request);
    }

    public void fillFormFromUI(View v) {
        infos.fillFormFromUI(v);
    }
    public void sendEmailFromUI(View v) {
        infos.sendEmailFromUI(v);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IN_LIST_FRAG_KEY, requests.isVisible());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState == null) return;

        if(savedInstanceState.getBoolean(IN_LIST_FRAG_KEY, true))
            getSupportFragmentManager().beginTransaction()
                    .hide(requests)
                    .show(infos)
                    .commit();
        else
            getSupportFragmentManager().beginTransaction()
                    .show(requests)
                    .hide(infos)
                    .commit();
    }

}
