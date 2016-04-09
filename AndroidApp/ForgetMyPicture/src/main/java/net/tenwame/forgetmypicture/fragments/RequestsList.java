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
import net.tenwame.forgetmypicture.database.Result;

import java.util.HashMap;

/**
 * Created by Antoine on 07/04/2016.
 */
public class RequestsList extends ConventionFragment {
    private static final String TAG = RequestsList.class.getName();

    private RequestsAdapter adapter;

    //auto-retrieved views
    private ListView requestsList;
    private TextView empty;

    @Override
    public void setupViews() {
        adapter = new RequestsAdapter();
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
            int processed = 0;
            for( Result r : item.getResults()) {
                if(r.isProcessed())
                    processed++;
            }
            Resources res = getResources();

            TextView status = (TextView) view.findViewById(R.id.status);
            status.setText(item.getStatus().toString());
            if(item.getStatus() == Request.Status.FETCHING) //TODO:
                status.setTextColor(res.getColor(android.R.color.holo_red_light));
            if(item.getStatus() == Request.Status.PROCESSING)
                status.setTextColor(res.getColor(android.R.color.holo_orange_light));
            if(item.getStatus() == Request.Status.PENDING)
                status.setTextColor(res.getColor(android.R.color.holo_green_light));
            if(item.getStatus() == Request.Status.FINISHED)
                status.setTextColor(res.getColor(android.R.color.darker_gray));
            ((TextView) view.findViewById(R.id.kind)).setText(item.getKind().toString());
            ((TextView) view.findViewById(R.id.id)).setText(res.getString(R.string.request_item_id, item.getId()));
            ((TextView) view.findViewById(R.id.results)).setText(res.getString(R.string.request_item_results, item.getResults().size()));
            ((TextView) view.findViewById(R.id.fetched)).setText(res.getString(R.string.request_item_fetched, 100*item.getProgress()/item.getMaxProgress()));
            if(!item.getResults().isEmpty())
                ((TextView) view.findViewById(R.id.processed)).setText(res.getString(R.string.request_item_processed, 100*processed/item.getResults().size()));
            else
                view.findViewById(R.id.processed).setVisibility(View.GONE);
        }

        @Override
        public void onItemClick(Request item) {
            ((RequestsPanel) RequestsList.this.getActivity()).goToRequestInfo(item);
        }
    }



}
