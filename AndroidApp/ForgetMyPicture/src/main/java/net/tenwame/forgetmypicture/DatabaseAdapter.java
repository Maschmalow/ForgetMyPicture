package net.tenwame.forgetmypicture;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.crittercism.app.Crittercism;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Antoine on 07/04/2016.
 * generic ListView adapter for database items
 * @param <T> class representing the database table. Must belong to the 'database' package
 */
public abstract class DatabaseAdapter<T> extends BaseAdapter implements AdapterView.OnItemClickListener{
    private final static String TAG = DatabaseAdapter.class.getName();

    private final Dao<T, ?> dao;
    private int layoutItemId;
    private List<T> data = new ArrayList<>();

    // args used to filter from db
    // should be set once on View creation
    private Map<String, Object> queryArgs = new ConcurrentHashMap<>();

    //lightweight filter for dynamic display (does not trigger db query)
    private Util.Filter<T> filter;
    private List<T> queriedItems = Collections.emptyList();


    public DatabaseAdapter(Class<T> tableClass, int layoutItemId) {
        this.layoutItemId = layoutItemId;
        this.dao = ForgetMyPictureApp.getHelper().getDao(tableClass);
        //data isn't initialised, because we want the app to notify when loading from db is needed
    }

    /**
     * reload items from database
     * note: triggers {@Link notifyDataSetChanged}
     */
    public void loadData() {
        try {
            if(queryArgs.isEmpty())
                queriedItems = Collections.synchronizedList(dao.queryForAll());
            else
                queriedItems = Collections.synchronizedList(dao.queryForFieldValuesArgs(queryArgs));
        } catch (SQLException e) { //data is empty so getView won't be called
            Log.e(TAG, "loadData: query failed", e);
            Crittercism.logHandledException(e);
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                filter();
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
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                filter();
            }
        });
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

    /**
     * same as {@Link trackDatabase(Class tableClass, boolean track)}, with the adapter relevant class
     */
    public void trackDatabase(boolean track) {
        if(track)
            dao.registerObserver(obs);
        else
            dao.unregisterObserver(obs);
    }

    /**
     * set whether to reload data each time the given database table is updated
     * @param tableClass the class associated with the table. Must be within 'database' package
     */
    public void trackDatabase(Class<?> tableClass, boolean track) {
        try {
            if(track)
                ForgetMyPictureApp.getHelper().getDao(tableClass).registerObserver(obs);
            else
                ForgetMyPictureApp.getHelper().getDao(tableClass).unregisterObserver(obs);
        } catch (Exception ignored) { } //if class is wrong, we do nothing
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(ForgetMyPictureApp.getContext()).inflate(layoutItemId, parent, false);

        setView(convertView, getItem(position));

        return convertView;
    }

    /**
     * Simplified version of getView, where View recylcling has already been taken care of,
     * and the concerned item is directly available
     * @param view The View representing the {@param item} parameter. All relevant fields should
     *             be set either to the {@param item} corresponding value or a default one, but not left
     *             unchanged.
     */
    abstract public void setView(@NonNull View view, T item);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onItemClick(getItem(position));
    }

    /**
     * Convenience method to be use for setting item's click listeners
     * To use simply set this adapter as the ListView OnItemClickListener
     * @param item the Clicked item
     */
    public void onItemClick(T item) { }

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
