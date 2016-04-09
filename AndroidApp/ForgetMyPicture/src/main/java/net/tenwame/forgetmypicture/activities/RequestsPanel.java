package net.tenwame.forgetmypicture.activities;

import android.app.Activity;
import android.os.Bundle;

import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.fragments.RequestInfos;
import net.tenwame.forgetmypicture.fragments.RequestsList;

public class RequestsPanel extends Activity {

    private RequestsList requests;
    private RequestInfos infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_panel);

        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, requests = new RequestsList())
                .add(R.id.fragment_container, infos = new RequestInfos())
                .hide(infos)
                .commit();

    }

    public void goToRequestInfo(Request request) {
        getFragmentManager().beginTransaction()
                .hide(requests)
                .show(infos)
                .addToBackStack(null)
                .commit();

        infos.setRequest(request);
    }

}
