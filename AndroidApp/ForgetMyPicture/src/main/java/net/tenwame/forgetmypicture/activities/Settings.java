package net.tenwame.forgetmypicture.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import net.tenwame.forgetmypicture.ForgetMyPictureApp;
import net.tenwame.forgetmypicture.Manager;
import net.tenwame.forgetmypicture.R;

/**
 * Created by Antoine on 16/04/2016.
 * do I need to explain?
 */
public class Settings extends Activity{

    public static final String DATA_ON_WIFI_KEY = "pref_data_on_wifi";
    public static boolean dataOnWifi() {
        return PreferenceManager.getDefaultSharedPreferences(ForgetMyPictureApp.getContext()).getBoolean(DATA_ON_WIFI_KEY, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(DATA_ON_WIFI_KEY.equals(key)) {
                Manager.getInstance().setAlarms();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }


}
