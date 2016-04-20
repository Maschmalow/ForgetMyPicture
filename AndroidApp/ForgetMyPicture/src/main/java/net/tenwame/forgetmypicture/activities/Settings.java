package net.tenwame.forgetmypicture.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import net.tenwame.forgetmypicture.ForgetMyPictureApp;
import net.tenwame.forgetmypicture.Manager;
import net.tenwame.forgetmypicture.R;

/**
 * Created by Antoine on 16/04/2016.
 * do I need to explain?
 */
public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String WIPE_KEY = "pref_wipe";
    public static final String DATA_ON_WIFI_KEY = "pref_data_on_wifi";
    public static boolean dataOnWifi() {
        return PreferenceManager.getDefaultSharedPreferences(ForgetMyPictureApp.getContext()).getBoolean(DATA_ON_WIFI_KEY, false);
    }

    public static final String OFFLINE_MODE_KEY = "pref_offline_mode";
    public static boolean offlineMode() {
        return PreferenceManager.getDefaultSharedPreferences(ForgetMyPictureApp.getContext()).getBoolean(OFFLINE_MODE_KEY, false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }
        else { //noinspection deprecation
            addPreferencesFromResource(R.xml.preferences);
            findPreference(WIPE_KEY).setOnPreferenceClickListener(wipeListener);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(DATA_ON_WIFI_KEY.equals(key)) {
            Manager.getInstance().setAlarms();
        }
    }

    private Preference.OnPreferenceClickListener wipeListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if(preference.hasKey() && WIPE_KEY.equals(preference.getKey())) {
                new AlertDialog.Builder(Settings.this)
                        .setCancelable(true)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.settings_wipe_btn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ForgetMyPictureApp.getHelper().wipeDatabase();
                                // TODO: 18/04/2016 wipe from server
                                Toast.makeText(Settings.this, R.string.settings_wipe_toast, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setMessage(R.string.settings_wipe_warning)
                        .setTitle(R.string.ask_continue)
                        .create().show();
                return true;
            }
            return false;
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)//noinspection deprecation
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)//noinspection deprecation
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            findPreference(WIPE_KEY).setOnPreferenceClickListener(((Settings) getActivity()).wipeListener);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener((Settings) getActivity());
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener((Settings) getActivity());
        }
    }

}
