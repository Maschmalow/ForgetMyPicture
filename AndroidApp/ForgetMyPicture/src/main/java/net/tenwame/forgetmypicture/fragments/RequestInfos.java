package net.tenwame.forgetmypicture.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.tenwame.forgetmypicture.DatabaseAdapter;
import net.tenwame.forgetmypicture.ForgetMyPictureApp;
import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.Util;
import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;
import net.tenwame.forgetmypicture.services.FormFiller;
import net.tenwame.forgetmypicture.services.ServerInterface;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Antoine on 07/04/2016.
 * Fragment that displays information for one request
 * TODO: select mode
 */
public class RequestInfos extends ConventionFragment {
    private static final String TAG = RequestInfos.class.getName();
    private static final String REQUEST_ID_KEY = "REQUEST_ID_KEY";

    private Request request;
    private ResultsAdapter adapter = new ResultsAdapter();
    private Set<Result> selected = new HashSet<>();

    //auto-retrieved views
    private TextView title;
    private TextView status;
    private TextView stats;
    private TextView keywords;
    private TextView motive;
    private Button fillForm;
    private Button sendEmail;
    private ListView resultsList;
    private TextView empty;


    @Override
    public void setupViews() {
        resultsList.setAdapter(adapter);
        adapter.setFilter(new Util.Filter<Result>() {
            @Override
            public boolean isAllowed(Result candidate) {
                return candidate.isProcessed();
            }
        });
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                load();
            }
        });
        adapter.trackDatabase(true);
        //resultsList.setOnItemClickListener(adapter);
    }


    @Override
    public void load() {
        if(request == null) return;

        if(adapter.getCount() == 0) {
            resultsList.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            resultsList.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }

        int processed = 0;
        for( Result r : request.getResults())
            if(r.isProcessed())
                processed++;
        int progress = request.getProgress();
        int nbResults = request.getResults().size();

        Resources res = getResources();
        title.setText(res.getString(R.string.request_infos_title, request.getKind().toString().toLowerCase(), request.getId()));
        status.setText(res.getString(R.string.request_infos_status, request.getStatus().getString(res)));
        int estimated = (progress == 0)? request.getMaxProgress()*100 : request.getMaxProgress()/progress *nbResults;
        stats.setText(res.getString(R.string.request_infos_stats, nbResults, estimated, processed, nbResults));
        keywords.setText(res.getString(R.string.request_infos_keywords, request.getKeywords().toString())); //TODO

        if(request.getStatus() == Request.Status.FINISHED && request.getMotive() != null) {
            motive.setVisibility(View.VISIBLE);
            motive.setText(res.getString(R.string.request_infos_motive, request.getMotive()));
        } else {
            motive.setVisibility(View.GONE);
        }

    }

    public void fillFormFromUI(View v) {
        if(request.getStatus() != Request.Status.PAYED) {
            payDialog.show();
            return;
        }

        // TODO: 19/04/2016 status
        FormFiller.execute(request);
        Toast.makeText(getContext(), R.string.request_infos_form_sent, Toast.LENGTH_SHORT).show();
    }

    public void sendEmailFromUI(View v) {
        if(request.getStatus() != Request.Status.PAYED) {
            payDialog.show();
            return;
        }

        // TODO: 19/04/2016 status
        ServerInterface.execute(ServerInterface.ACTION_SEND_MAIL, request);
        Toast.makeText(getContext(), R.string.request_infos_email_sent, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(request != null)
            outState.putInt(REQUEST_ID_KEY, request.getId());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState == null) return;

        Integer requestId = savedInstanceState.getInt(REQUEST_ID_KEY, -1);
        try {
            setRequest(ForgetMyPictureApp.getHelper().getRequestDao().queryForId(requestId));
        } catch (SQLException e) {
            Log.e(TAG, "onViewStateRestored: Could not retrieve request " + requestId, e);
            request = null;
        }

    }

    public void setRequest(Request request) {
        this.request = request;
        if(request == null) return;
        adapter.setMatchingArgs(Collections.singletonMap("request_id", (Object) request.getId()));
        //no need to call load, adapter will do
    }

    private final AlertDialog payDialog = new AlertDialog.Builder(getContext())
            .setMessage(R.string.request_infos_pay_before_check)
            .setPositiveButton(R.string.request_infos_pay_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    request.setStatus(Request.Status.PAYED);
                    try {
                        ForgetMyPictureApp.getHelper().getRequestDao().update(request);
                    } catch (SQLException e) {
                        return; // TODO: 19/04/2016
                    }
                    Toast.makeText(getContext(), R.string.request_infos_payed_toast, Toast.LENGTH_SHORT).show();
                }
            })
            .setNeutralButton(R.string.cancel, null)
            .setTitle(R.string.request_infos_pay_title)
            .create();

    private class ResultsAdapter extends DatabaseAdapter<Result> {

        public ResultsAdapter() {
            super(Result.class, R.layout.result_item);
        }

        @Override
        public void setView(View view, final Result item) {
        final Resources res = getResources();

            ((TextView) view.findViewById(R.id.match)).setText(res.getString(R.string.result_item_match, item.getMatch()));
            String host = "#######";
            try {
                if(request.getStatus() == Request.Status.PAYED || request.getStatus() != Request.Status.FINISHED)
                    host = new URL(item.getPicRefURL()).getHost();
            } catch (MalformedURLException ignored) { } //already checked in Searcher
            ((TextView) view.findViewById(R.id.pic_ref_url)).setText(res.getString(R.string.result_item_pic_ref_url, host));

            if(request.getStatus() == Request.Status.FINISHED)
                view.findViewById(R.id.selected).setVisibility(View.GONE);
            else
                ((CheckBox) view.findViewById(R.id.selected)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(request.getStatus() != Request.Status.PAYED && isChecked) {
                            payDialog.show();
                            buttonView.setChecked(false);
                            return;
                        }

                        Log.d(TAG, "item " + item.getPicURL() + " selected: " + isChecked);
                        if(isChecked)
                            selected.add(item);
                        else
                            selected.remove(item);
                    }
                });



            // TODO: 18/04/2016 Download server-side
            ImageLoader.getInstance().displayImage(item.getPicURL(), (ImageView) view.findViewById(R.id.thumb) );
        }

        @Override
        public void onItemClick(Result item) {
            Log.i(TAG, "onItemClick");
            if(request.getStatus() != Request.Status.PAYED && request.getStatus() != Request.Status.FINISHED) {
                payDialog.show();
                return;
            }


            //show picture
        }

    }

}
