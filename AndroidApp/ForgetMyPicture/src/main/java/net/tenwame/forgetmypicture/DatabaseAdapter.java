package net.tenwame.forgetmypicture;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Antoine on 07/04/2016.
 * generic ListView adapter for database items
 */
public abstract class DatabaseAdapter<T> extends BaseAdapter implements AdapterView.OnItemClickListener{
    private final static String TAG = DatabaseAdapter.class.getName();


    private final Dao<T, ?> dao;
    private List<T> data = new ArrayList<>();
    private Map<String, Object> queryArgs = new HashMap<>();
    private int layoutItemId;
    private Util.Filter<T> filter;
    private List<T> queriedItems = new ArrayList<>();


    public DatabaseAdapter(Class<T> clazz, int layoutItemId) {
        this.layoutItemId = layoutItemId;
        try {
            this.dao = ForgetMyPictureApp.getHelper().getDao(clazz);
        } catch (SQLException e) { //can't happen, as dao is already initialised at app launch
            throw new RuntimeException(e);
        }
        //data isn't initialised, because we want the app to notify when loading from db is needed
    }

    public void loadData() {
        try {
            queriedItems = (queryArgs.isEmpty())?
                    dao.queryForAll() :
                    dao.queryForFieldValuesArgs(queryArgs);
        } catch (SQLException e) {
            //data is empty so getView won't be called
            Log.e(TAG, "loadData: query failed", e);
        }
        filter();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void addMatchingArgs(Map<String, Object> args) {
        if(args == null) return;

        queryArgs.putAll(args);
        loadData();
    }

    public void setMatchingArgs(Map<String, Object> args) {
        queryArgs.clear();
        addMatchingArgs(args);
    }

    public void setFilter(Util.Filter<T> filter) {
        this.filter = filter;
        filter();
    }

    private void filter() {
        data.clear();
        for(T item : queriedItems)
            if(filter == null || filter.isAllowed(item))
                data.add(item);
    }

    private Dao.DaoObserver obs = new Dao.DaoObserver() {
        @Override
        public void onChange() {
            loadData();
        }
    };

    public void trackDatabase(boolean track) {
        if(track)
            dao.registerObserver(obs);
        else
            dao.unregisterObserver(obs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(ForgetMyPictureApp.getContext()).inflate(layoutItemId, parent, false);

        setView(convertView, getItem(position));

        return convertView;
    }

    abstract public void setView(View view, T item);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onItemClick(getItem(position));
    }

    abstract public void onItemClick(T item);

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    } //not used

    @Override
    public int getViewTypeCount() {
        return 1;
    }

}
