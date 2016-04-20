package net.tenwame.forgetmypicture.fragments;

import android.content.res.Resources;
import android.database.DataSetObserver;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import net.tenwame.forgetmypicture.DatabaseAdapter;
import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.Util;
import net.tenwame.forgetmypicture.activities.RequestsPanel;
import net.tenwame.forgetmypicture.database.Request;

/**
 * Created by Antoine on 07/04/2016.
 * Fragment displaying user's requests
 */
public class RequestsList extends ConventionFragment {
    private static final String TAG = RequestsList.class.getName();

    private RequestsAdapter adapter = new RequestsAdapter();
    private Dao.DaoObserver notifier = new Dao.DaoObserver() {
        @Override
        public void onChange() {
            adapter.notifyDataSetChanged();
        }
    };
    private DataSetObserver loader = new DataSetObserver() {
        @Override
        public void onChanged() {
            load();
        }
    };

    //auto-retrieved views
    private ListView requestsList;
    private TextView empty;

    @Override
    public void setupViews() {
        requestsList.setAdapter(adapter);
        requestsList.setOnItemClickListener(adapter);
        adapter.registerDataSetObserver(loader);
        adapter.loadData();
        //adapter.trackDatabase(Result.class, true);
    }

    @Override
    public void load() {
        Util.setViewVisibleWhen(adapter.getCount() == 0, requestsList);
        Util.setViewVisibleWhen(adapter.getCount() != 0, empty);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.unregisterDataSetObserver(loader);
        //adapter.trackDatabase(Result.class, false);
    }

    public void setFilterFromUI(/* filter */) {//will be from UI
        //adapter.setMatchingArgs(new HashMap<String, Object>());
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
