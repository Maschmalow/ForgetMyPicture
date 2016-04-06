package net.tenwame.forgetmypicture.activities;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import net.tenwame.forgetmypicture.ForgetMyPictureApp;
import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestsPanel extends Activity {

    private RequestsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_panel);

        adapter = new RequestsAdapter();
        ((ListView) findViewById(R.id.results_list)).setAdapter(adapter);
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void setFilter(RequestsAdapter.Filter filter) {
        adapter.setFilter(filter);
    }

    static class RequestsAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{

        private Dao<Request, Integer> dao = ForgetMyPictureApp.getHelper().getRequestDao();
        private List<Request> data;
        private Map<String, Object> queryArgs = new HashMap<>(3);
        private boolean error;


        public RequestsAdapter() {
            this(null);
        }

        public RequestsAdapter(Filter filter) {
            setFilter(filter);
            loadData();
            registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    loadData();
                }
            });
        }

        private void loadData() {
            try {
                if(queryArgs.isEmpty())
                    data = dao.queryForAll();
                else
                    data = dao.queryForFieldValuesArgs(queryArgs);
                error = false;
            } catch (SQLException e) {
                data = new ArrayList<>();
                error = true;
            }
        }

        public void setFilter(Filter filter) {
            queryArgs.clear();
            if(filter == null) return;

            if(filter.id != null)
                queryArgs.put("id", filter.id);
            if(filter.status != null)
                queryArgs.put("id", filter.status.toString());
            if(filter.kind != null)
                queryArgs.put("id", filter.kind.toString());

            loadData();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Context context = ForgetMyPictureApp.getContext();
            if(error) {
                TextView errMsg = new TextView(context);
                errMsg.setText(R.string.request_panel_error_requests);
                return errMsg;
            }
            if(v == null)
                v = LayoutInflater.from(context).inflate(R.layout.request_item, parent, false);

            Request request = getItem(position);
            int processed = 0;
            for( Result r : request.getResults()) {
                if(r.isProcessed())
                    processed++;
            }
            Resources res = context.getResources();

            TextView status = (TextView) v.findViewById(R.id.status);
            status.setText(request.getStatus().toString());
            if(request.getStatus() == Request.Status.FETCHING)
                status.setTextColor(res.getColor(android.R.color.holo_red_light));
            if(request.getStatus() == Request.Status.PROCESSING)
                status.setTextColor(res.getColor(android.R.color.holo_orange_light));
            if(request.getStatus() == Request.Status.PENDING)
                status.setTextColor(res.getColor(android.R.color.holo_green_light));
            if(request.getStatus() == Request.Status.FINISHED)
                status.setTextColor(res.getColor(android.R.color.darker_gray));
            ((TextView) v.findViewById(R.id.kind)).setText(request.getKind().toString());
            ((TextView) v.findViewById(R.id.id)).setText(res.getString(R.string.request_panel_id, request.getId()));
            ((TextView) v.findViewById(R.id.results)).setText(res.getString(R.string.request_panel_results, request.getResults().size()));
            ((TextView) v.findViewById(R.id.fetched)).setText(res.getString(R.string.request_panel_fetched, 100*request.getProgress()/request.getMaxProgress()));
            if(!request.getResults().isEmpty())
                ((TextView) v.findViewById(R.id.processed)).setText(res.getString(R.string.request_panel_processed, 100*processed/request.getResults().size()));
            else
                v.findViewById(R.id.processed).setVisibility(View.GONE);

            return v;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Request getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return data.get(position).getId();
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }

        public class Filter {

            public Filter(Request.Status status, Request.Kind kind, Integer id) {
                this.status = status;
                this.kind = kind;
                this.id = id;
            }

            private Request.Status status;
            private Request.Kind kind;
            private Integer id;
        }

    }
}
