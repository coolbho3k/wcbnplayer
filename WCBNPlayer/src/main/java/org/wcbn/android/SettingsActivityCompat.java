package org.wcbn.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import org.wcbn.android.station.Station;

/**
 * For API versions < 11 without PreferenceFragment.
 */
public class SettingsActivityCompat extends PreferenceActivity {

    private Station mStation = Utils.getStation();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        Preference versionPreference = findPreference("version");

        assert versionPreference != null;
        if(versionPreference.getSummary() == null) {
            try {
                PackageInfo pInfo = getPackageManager()
                        .getPackageInfo(getPackageName(), 0);
                versionPreference.setSummary(pInfo.versionName);
            }
            catch(PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        Preference qualityPreference = findPreference("quality");

        assert qualityPreference != null;
        qualityPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Integer i = Integer.parseInt((String) newValue);

                preference.setSummary(getResources()
                        .getStringArray(R.array.quality_desc)[i]);
                preference.setTitle(getResources()
                        .getStringArray(R.array.quality_pref)[i]);

                resetService();

                return true;
            }
        });

        Preference albumArtPreference = findPreference("grab_album_art");
        assert albumArtPreference != null;
        albumArtPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                resetService();
                return true;
            }
        });

        Preference websitePreference = findPreference("website");
        assert websitePreference != null;
        websitePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                startActivity(new Intent()
                        .setAction(Intent.ACTION_VIEW)
                        .setData(Uri.parse(getString(mStation.getWebsite()))));

                return false;
            }
        });

        Preference numberPreference = findPreference("request_number");
        assert numberPreference != null;
        numberPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                startActivity(new Intent()
                        .setAction(Intent.ACTION_DIAL)
                        .setData(Uri.parse("tel:"+getString(mStation.getNumber()))));

                return false;
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Integer i = Integer.parseInt(prefs.getString("quality", "1"));
        qualityPreference.setSummary(getResources()
                .getStringArray(R.array.quality_desc)[i]);
        qualityPreference.setTitle(getResources()
                .getStringArray(R.array.quality_pref)[i]);
    }

    public void resetService() {
            stopService(new Intent(this, StreamService.class));
            startService(new Intent(this, StreamService.class));
    }
}
