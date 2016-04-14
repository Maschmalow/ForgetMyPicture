package net.tenwame.forgetmypicture.fragments;

import android.content.res.Resources;
import android.database.DataSetObserver;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import net.tenwame.forgetmypicture.DatabaseAdapter;
import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.activities.RequestsPanel;
import net.tenwame.forgetmypicture.database.Request;

import java.util.HashMap;

/**
 * Created by Antoine on 07/04/2016.
 * Fragment displaying user's requests
 */
public class RequestsList extends ConventionFragment {
    private static final String TAG = RequestsList.class.getName();

    private RequestsAdapter adapter = new RequestsAdapter();

    //auto-retrieved views
    private ListView requestsList;
    private TextView empty;

    @Override
    public void setupViews() {
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                if(adapter.getCount() == 0) {
                    requestsList.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                } else {
                    requestsList.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                }
            }
        });
        requestsList.setAdapter(adapter);
        requestsList.setOnItemClickListener(adapter);
        adapter.trackDatabase(true);
        adapter.notifyDataSetChanged(); // trigger loading and observers

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.trackDatabase(false);
    }

    public void setFilterFromUI(/* filter */) {//will be from UI
        adapter.setFilter(new HashMap<String, Object>());
    }


    private class RequestsAdapter extends DatabaseAdapter<Request> {

        public RequestsAdapter() {
            super(Request.class, R.layout.request_item);
        }

        @Override
        public void setView(View view, Request item) {
            Resources res = getResources();

            TextView status = (TextView) view.findViewById(R.id.status);
            status.setText(item.getStatus().getString(res));
            if(item.getStatus() == Request.Status.FETCHING) //TODO:
                status.setTextColor(res.getColor(R.color.red_light));
            if(item.getStatus() == Request.Status.PROCESSING)
                status.setTextColor(res.getColor(R.color.orange_light));
            if(item.getStatus() == Request.Status.PENDING)
                status.setTextColor(res.getColor(R.color.green_light));
            if(item.getStatus() == Request.Status.FINISHED)
                status.setTextColor(res.getColor(R.color.gray_dark));
            ((TextView) view.findViewById(R.id.id)).setText(res.getString(R.string.request_item_id, item.getKind().getString(res), item.getId()));
            ((TextView) view.findViewById(R.id.results)).setText(res.getString(R.string.request_item_results, item.getResults().size()));
        }

        @Override
        public void onItemClick(Request item) {
            ((RequestsPanel) RequestsList.this.getActivity()).goToRequestInfo(item);
        }
    }



}
