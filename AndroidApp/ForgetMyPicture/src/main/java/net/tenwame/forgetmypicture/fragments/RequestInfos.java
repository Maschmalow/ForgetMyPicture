package net.tenwame.forgetmypicture.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.tenwame.forgetmypicture.DatabaseAdapter;
import net.tenwame.forgetmypicture.ForgetMyPictureApp;
import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.UserData;
import net.tenwame.forgetmypicture.Util;
import net.tenwame.forgetmypicture.activities.IdCardSetup;
import net.tenwame.forgetmypicture.activities.Settings;
import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;
import net.tenwame.forgetmypicture.services.FormFiller;
import net.tenwame.forgetmypicture.services.NetworkService;
import net.tenwame.forgetmypicture.services.Searcher;
import net.tenwame.forgetmypicture.services.ServerInterface;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Antoine on 07/04/2016.
 * Fragment that displays information for one request
 *
 */
public class RequestInfos extends ConventionFragment {
    private static final String TAG = RequestInfos.class.getName();

    private static final String REQUEST_ID_KEY = "REQUEST_ID";
    private static final String SELECTED_KEY = "SELECTED";
    private static final String PAY_DIALOG_KEY = "PAY_DIALOG";
    private static final String AGREEMENT_DIALOG_KEY = "AGREEMENT_DIALOG";

    private Request request;
    private ResultsAdapter adapter = new ResultsAdapter();
    private DataSetObserver loader; //observer that (re)load fragment when data changes
    private Set<String> selected = new HashSet<>();
    private AlertDialog payDialog;
    private AlertDialog userAgreement;

    //auto-retrieved views
    private TextView title;
    private TextView status;
    private TextView stats;
    private TextView keywords;
    private TextView motive;
    private ListView resultsList;
    private TextView empty;


    @Override
    public void setupViews() {
        resultsList.setAdapter(adapter);
        resultsList.setOnItemClickListener(adapter);
        adapter.setFilter(new Util.Filter<Result>() {
            @Override
            public boolean isAllowed(Result candidate) {
                return candidate.getMatch() >= Settings.matchTreshold();
            }
        });
        loader = new DataSetObserver() {
            @Override
            public void onChanged() {
                load();
            }
        };
        adapter.registerDataSetObserver(loader);
        adapter.loadData();
        adapter.trackDatabase(true);

        payDialog = new AlertDialog.Builder(getContext())
                .setMessage(R.string.request_infos_pay_before_check)
                .setPositiveButton(R.string.request_infos_pay_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.setStatus(Request.Status.UNLOCKED);
                        try {
                            ForgetMyPictureApp.getHelper().getRequestDao().update(request);
                        } catch (SQLException e) {
                            return; // TODO: 19/04/2016
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), R.string.request_infos_payed_toast, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton(R.string.cancel, null)
                .setTitle(R.string.request_infos_pay_title)
                .create();

        userAgreement = ForgetMyPictureApp.getAgreementDialog(getContext());
    }


    @Override
    public void load() {
        if(request == null) return;

        Util.setViewVisibleWhen(adapter.getCount() != 0, resultsList);
        Util.setViewVisibleWhen(adapter.getCount() == 0, empty);

        int processed = 0;
        for( Result r : request.getResults())
            if(r.isProcessed())
                processed++;
        int progress = request.getProgress();
        int nbResults = request.getResults().size();

        Resources res = getResources();
        title.setText(res.getString(R.string.request_infos_title, request.getKind().toString().toLowerCase(), request.getId()));
        status.setText(res.getString(R.string.request_infos_status, request.getStatus().getString(res)));
        int estimated = (progress == 0)? request.getMaxProgress()* Searcher.AVG_RESULTS_NB : request.getMaxProgress()/progress *nbResults;
        stats.setText(res.getString(R.string.request_infos_stats, nbResults, estimated, processed));
        keywords.setText(res.getString(R.string.request_infos_keywords, request.getKeywords().toString())); //TODO

        if(request.getStatus() == Request.Status.FINISHED && request.getMotive() != null) {
            motive.setVisibility(View.VISIBLE);
            motive.setText(res.getString(R.string.request_infos_motive, request.getMotive()));
        } else {
            motive.setVisibility(View.GONE);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            adapter.unregisterDataSetObserver(loader);
        } catch (IllegalStateException e) {
            Log.e(TAG, "onStop: could not unregister loader", e);
            Crittercism.logHandledException(e);
        }
        adapter.trackDatabase(false);
    }

    public void fillFormFromUI(View v) {
        if(!request.getStatus().isAfter(Request.Status.UNLOCKED)) {
            payDialog.show();
            return;
        }

        if(!UserData.getUser().isAgreementAccepted()) {
            userAgreement.show();
            return;
        }

        if(UserData.getUser().getIdCard().get() == null) {
            Toast.makeText(getContext(), R.string.request_infos_id_card_toast, Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), IdCardSetup.class));
            return;
        }

        Bundle params = new Bundle(); //we do it manually because Id's are already saved, no need to fetch DB
        params.putInt(FormFiller.EXTRA_REQUEST_ID_KEY, request.getId());
        params.putStringArrayList(FormFiller.EXTRA_RESULTS_KEY, new ArrayList<>(selected));
        NetworkService.execute(FormFiller.class, FormFiller.ACTION_FILL_FORM, params);
        Toast.makeText(getContext(), R.string.request_infos_form_sent, Toast.LENGTH_SHORT).show();
    }

    public void sendEmailFromUI(View v) {
        if(!request.getStatus().isAfter(Request.Status.UNLOCKED)) {
            payDialog.show();
            return;
        }

        if(!UserData.getUser().isAgreementAccepted()) {
            userAgreement.show();
            return;
        }

        Bundle params = new Bundle(); //we do it manually because Id's are already saved, no need to fetch DB
        params.putInt(ServerInterface.EXTRA_REQUEST_ID_KEY, request.getId());
        params.putStringArrayList(ServerInterface.EXTRA_RESULTS_KEY, new ArrayList<>(selected));
        NetworkService.execute(ServerInterface.class, ServerInterface.ACTION_SEND_MAIL, params);
        Toast.makeText(getContext(), R.string.request_infos_email_sent, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(request != null)
            outState.putInt(REQUEST_ID_KEY, request.getId());
        outState.putStringArrayList(SELECTED_KEY, new ArrayList<>(selected));
        outState.putBoolean(PAY_DIALOG_KEY, payDialog.isShowing());
        outState.putBoolean(AGREEMENT_DIALOG_KEY, userAgreement.isShowing());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedState) {
        super.onViewStateRestored(savedState);
        if(savedState == null || !savedState.containsKey(REQUEST_ID_KEY)) return;

        Integer requestId = savedState.getInt(REQUEST_ID_KEY);
        try {
            setRequest(ForgetMyPictureApp.getHelper().getRequestDao().queryForId(requestId));
        } catch (SQLException e) {
            Log.e(TAG, "onViewStateRestored: Could not retrieve request " + requestId, e);
            request = null;
        }

        selected.clear();
        if(savedState.containsKey(SELECTED_KEY)) //noinspection ConstantConditions
            selected.addAll(savedState.getStringArrayList(SELECTED_KEY));
        if(savedState.getBoolean(PAY_DIALOG_KEY, false))
            payDialog.show();
        if(savedState.getBoolean(AGREEMENT_DIALOG_KEY, false))
            userAgreement.show();
    }

    public void setRequest(Request request) {
        this.request = request;
        selected.clear();
        if(request == null) return;
        adapter.setMatchingArgs(Collections.singletonMap("request_id", (Object) request.getId()));
        //no need to call load, adapter will do
    }


    private class ResultsAdapter extends DatabaseAdapter<Result> implements AdapterView.OnItemClickListener {

        public ResultsAdapter() {
            super(Result.class, R.layout.result_item);
        }

        @Override
        public void setView(@NonNull final View itemView, final Result item) {
        final Resources res = getResources();

            String[] match = res.getStringArray(R.array.result_item_match);
            ((TextView) itemView.findViewById(R.id.match)).setText(item.isProcessed()? String.format(match[0], item.getMatch()) : match[1] );

            String host = "#######";
            try {
                if(request.getStatus().isAfter(Request.Status.UNLOCKED))
                    host = new URL(item.getPicRefURL()).getHost();
            } catch (MalformedURLException ignored) { } //already checked in Searcher
            ((TextView) itemView.findViewById(R.id.pic_ref_url)).setText(res.getString(R.string.result_item_pic_ref_url, host));

            ((CheckBox) itemView.findViewById(R.id.selected)).setChecked(selected.contains(item.getId()));
            // TODO: 18/04/2016 Download server-side
            ImageView thumb = (ImageView) itemView.findViewById(R.id.thumb);
            thumb.setImageResource(R.drawable.ic_loading);
            ImageLoader.getInstance().displayImage(item.getPicURL(), thumb);

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(TAG, "onItemClick");
            if(request.getStatus() == Request.Status.FINISHED || !request.getStatus().isAfter(Request.Status.PENDING))
                return;
            if(request.getStatus() == Request.Status.PENDING) {
                payDialog.show();
                return;
            }

            Result item = getItem(position);
            if(selected.contains(item.getId())) {
                ((CheckBox) view.findViewById(R.id.selected)).setChecked(false);
                selected.remove(item.getId());
            } else {
                ((CheckBox) view.findViewById(R.id.selected)).setChecked(true);
                selected.add(item.getId());
            }

        }


    }

}
