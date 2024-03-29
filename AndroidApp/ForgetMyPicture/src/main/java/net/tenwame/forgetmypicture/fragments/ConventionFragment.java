package net.tenwame.forgetmypicture.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.tenwame.forgetmypicture.R;
import net.tenwame.forgetmypicture.Util;

import java.lang.reflect.Field;

/**
 * Created by Antoine on 27/09/2015.
 * utility Fragment that rely on naming conventions to initialize its elements
 * i.e. MyFragment has layout id fragment_my_fragment,
 * if it has a View field name myView, its resId is my_view.
 */
public class ConventionFragment extends Fragment {
    private static final String TAG = ConventionFragment.class.getSimpleName();

    protected View root;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(getLayoutId(), container, false);

        findFieldsViews();

        setupViews();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        load();
    }

    @Override
    public void onStop() {
        super.onStart();
        unload();
    }


    public View findViewById(int id) {
        if(root == null) return null;
        return root.findViewById(id);
    }

    private int getLayoutId() {
        String name = "fragment" + Util.camelToSnakeCase(this.getClass().getSimpleName());
        try {
            Field layoutField = R.layout.class.getDeclaredField(name);
            return layoutField.getInt(layoutField);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    //fill all the child's Views, assuming that the View id is the same as it's field name
    // i.e. private ViewGroup itemMenu = (ViewGroup) findViewById(R.id.item_menu);
    private void findFieldsViews() {
        Class childClass = this.getClass();
        for( Field f : childClass.getDeclaredFields()) {
            if( View.class.isAssignableFrom(f.getType()) &&  //if the field is a subclass from View=
                    root != null ) {  //optimisation

                f.setAccessible(true);
                try {
                    Field idField = R.id.class.getDeclaredField(Util.camelToSnakeCase(f.getName()));
                    f.set(this, root.findViewById(idField.getInt(idField)));
                    if(f.get(this) == null)
                        Log.w(TAG, "R.id." + f.getName() + " couldn't be found in Fragment.");
                } catch (Exception e) {
                    Log.w(TAG, "field " + f.getName() + " is not accessible," +
                            " or id R.id." + Util.camelToSnakeCase(f.getName()) + " does not exist.");
                }
            }
        }
    }





    /**
     * Method to override that does the static initiation of Views, just after inflation
     * i.e. things that could be done in XML, but needs to be done programmatically
     * attach listeners, animators, parameters that needs to be retrieved, etc
     * Called during onCreateView
     */
    public void setupViews() {
    }

    /**
     * Method to override that does the context-dependent initiation of Views
     * called when the Fragment is shown to the user (and between onStart() and onResume() )
     */
    public void load() {
    }

    /**
     * Method to override that does a light cleaning of the Fragment
     * called when the Fragment is left (and between onStop() and onDestroyView() )
     * typically called when the fragment visibility is affecting other UI components as well
     */
    public void unload() {
    }


}
