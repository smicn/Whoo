/* Author: Shaomin (Samuel) Zhang <smicn@foxmail.com>
 *
 * The Android application Whoo is the part of the author's thesis, MS of
 * computer science in 2015. The main purpose is easy and straightforward:
 * to develop an Android application based on OpenCV so that it has the
 * features of face detection and face recognition. OpenCV has supported
 * three face recognition algorithms and this software does not develop new
 * algorithms. However, it really did some careful design and optimizations
 * to make the face recognition easy and friendly to use. Just take pictures
 * to your friends and yourself, and hope you have fun from it.
 *
 * Licensed under the Academic Free License version 2.1
 *
 * Copyright(C)2015  Samuel Zhang <smicn@foxmail.com>
 */
package com.lamar.cs.whoo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private final String TAG = "whoo";
    private Settings settings;
    private Context context;
    private SharedPreferences mSharedPreferences;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);

        context = this;

        settings = Settings.getInstance();

        addPreferencesFromResource(R.xml.preferences);

        mSharedPreferences = getPreferenceScreen().getSharedPreferences();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume()
    {
        super.onResume();

        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        Preference pref = findPreference("prefs_about");
        pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                try {
                    String uriLink = WhooConfig.ABOUT_ME;
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriLink));
                    startActivityForResult(myIntent, 1);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(
                            SettingsActivity.this,
                            "No Web Browser?",
                            Toast.LENGTH_LONG).show();
                }
                return (true);
            }
        });

        pref = findPreference("prefs_version");
        pref.setSummary("Current version: " + WhooConfig.VERSION() +
                        "\nBuild Date: " + WhooConfig.COMPILE_DATE());

        if (WhooConfig.USING_USER_RELEASE_MODE) {
            pref = findPreference("prefs_server_addr");
            //String addrString = mSharedPreferences.getString("prefs_server_addr", "140.158.129.111");
            //pref.setSummary(pref.getSummary() + "\n" + addrString);
            pref.setEnabled(false);

            pref = findPreference("prefs_algorithm");
            //String algorithm = mSharedPreferences.getString("prefs_algorithm", WhooConfig.DEFAULT_ALGORITHM);
            //pref.setSummary(pref.getSummary() + "\nCurrent Algorithm: " + algorithm);
            //pref.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	super.onActivityResult(requestCode, resultCode, data);
		
        if (1 == requestCode) {
            finish();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPause()
    {
        super.onPause();

        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    /*___________________________________________________________________
    |
    | Function: onSharedPreferenceChanged
    |__________________________________________________________________*/
    @SuppressWarnings("deprecation")
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (key.equals("prefs_face_detection")) {

            settings.setFaceDetectionEnabled(sharedPreferences.getBoolean(key, true));

        } else if (key.equals("prefs_server_addr")) {

            settings.setServerAddress(sharedPreferences.getString(key, "140.158.129.111"));
			
        } else if (key.equals("prefs_algorithm")) {

			String no = sharedPreferences.getString(key, "2");
			
            settings.setFaceRecognitionAlgorithmNo(Integer.parseInt(no));

			String algorithm = settings.getFaceRecognitionAlgorithm();

			WFaceRecognizer.getInstance().setAlgorithm(algorithm);

			Log.d(TAG, "setting: user changes algorithm to " + algorithm);
        }
    }
}