package net.tenwame.forgetmypicture;

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
public  abstract class DatabaseAdapter<T> extends BaseAdapter implements AdapterView.OnItemClickListener{
    private final static String TAG = DatabaseAdapter.class.getName();


    private final Dao<T, ?> dao;
    private List<T> data;
    private Map<String, Object> queryArgs = new HashMap<>();
    private int layoutItemId;


    public DatabaseAdapter(Class<T> clazz, int layoutItemId) {
        this.layoutItemId = layoutItemId;
        try {
            this.dao = ForgetMyPictureApp.getHelper().getDao(clazz);
        } catch (SQLException e) { //can't happen, as dao is already initialised at app launch
            throw new RuntimeException(e);

        }
        loadData();
    }

    private void loadData() {
        try {
            if(queryArgs.isEmpty())
                data = dao.queryForAll();
            else
                data = dao.queryForFieldValuesArgs(queryArgs);
        } catch (SQLException e) {
            data = new ArrayList<>(); //getView won't be called
            Log.e(TAG, "loadData: query failed", e);
        }
    }

    public void addFilter(Map<String, Object> filter) {
        if(filter == null) return;

        queryArgs.putAll(filter);
        loadData();
    }

    public void setFilter(Map<String, Object> filter) {
        queryArgs.clear();
        addFilter(filter);
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
    public void notifyDataSetChanged() { // when database is modified
        loadData(); //we reload data before, so error is set when observers are called
        super.notifyDataSetChanged();
    }

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
