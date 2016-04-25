package net.tenwame.forgetmypicture.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.fragments.RequestInfos;
import net.tenwame.forgetmypicture.fragments.RequestsList;

/**
 * Base Activity that holds both the Request list ({@Link RequestList}) Fragment and
 * the Request-specific Fragment ({@Link RequestInfos})
 */
public class RequestsPanel extends FragmentActivity {

    private static final String IN_LIST_FRAG_KEY = "IN_LIST_FRAG_KEY";
    private RequestsList requests;
    private RequestInfos infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requests = addFragment(RequestsList.class);
        infos = addFragment(RequestInfos.class);

        getSupportFragmentManager().beginTransaction()
                .show(requests)
                .hide(infos)
                .commit();
    }

    protected <T extends Fragment> T addFragment( Class<T> clss ) {
        String tag = clss.getSimpleName();
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentByTag( tag );
        if ( f == null ) {
            f = Fragment.instantiate( this, clss.getName() );
            fm.beginTransaction()
                    .add( android.R.id.content, f, tag )
                    .commit();
        }
        return (T) f;
    }

    public void goToResultList() {
        getSupportFragmentManager().beginTransaction()
                .show(requests)
                .hide(infos)
                .addToBackStack(null)
                .commit();
    }

    public void goToRequestInfo(Request request) {
        getSupportFragmentManager().beginTransaction()
                .hide(requests)
                .show(infos)
                .addToBackStack(null)
                .commit();

        if(request != null) //the request is not actually set to null, it is just kept unchanged
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
            goToResultList();
        else
            goToRequestInfo(null);
    }

}
