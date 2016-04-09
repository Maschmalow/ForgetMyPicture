package net.tenwame.forgetmypicture.fragments;

import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import net.tenwame.forgetmypicture.DatabaseAdapter;
import net.tenwame.forgetmypicture.ForgetMyPictureApp;
import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

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


    //auto-retrieved views
    private TextView title;
    private TextView status;
    private ListView resultsList;
    private TextView empty;

    public static RequestInfos newInstance(Request request) {
        if(request == null) return new RequestInfos();
        Bundle args = new Bundle();
        args.putInt(REQUEST_ID_KEY, request.getId());
        RequestInfos fragment = new RequestInfos();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setupViews() {
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                if(adapter.getCount() == 0) {
                    resultsList.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                } else {
                    resultsList.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                }
            }
        });
        resultsList.setAdapter(adapter);
        //resultsList.setOnItemClickListener(adapter);
    }


    @Override
    public void load() {
        if(request == null) return;

        Resources res = getResources();
        title.setText(res.getString(R.string.request_infos_title, request.getKind().toString().toLowerCase(), request.getId()));
        status.setText(res.getString(R.string.request_infos_status, request.getStatus().toString()));
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
        adapter.setFilter(null);
        load();
    }

    private class ResultsAdapter extends DatabaseAdapter<Result> {

        public ResultsAdapter() {
            super(Result.class, R.layout.result_item);
        }


        @Override
        public void setView(View view, Result item) {
        Resources res = getResources();

            if(item.isProcessed())
                view.findViewById(R.id.ok_icon).setVisibility(View.VISIBLE);
            else
                view.findViewById(R.id.ok_icon).setVisibility(View.GONE);

            ((TextView) view.findViewById(R.id.pic_url)).setText(res.getString(R.string.result_item_pic_url, item.getPicURL()));
            ((TextView) view.findViewById(R.id.pic_ref_url)).setText(res.getString(R.string.result_item_pic_ref_url, item.getPicRefURL()));

        }

        @Override
        public void onItemClick(Result item) {
            //do we have something to do here?
        }

        @Override
        public void setFilter(Map<String, Object> filter) {
            super.setFilter(filter);
            if(request != null)
                addFilter(Collections.singletonMap("request_id", (Object) request.getId()));
        }
    }
}