package net.tenwame.forgetmypicture;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SearchActivity extends Activity {

    private static final String TAG = SearchActivity.class.getCanonicalName();

    private EditText keywordsField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        keywordsField = (EditText) findViewById(R.id.keywords_field);
    }

    public void startSearchFromUI(View view) {
        Log.v(TAG, "Search started from UI");
        String[] keywords = keywordsField.getText().toString().split(" ");
        Searcher.getInstance().startSearch(keywords);


    }


}
