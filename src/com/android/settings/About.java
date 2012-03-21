
package com.android.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.util.devcard.DevCard;

public class About extends SettingsPreferenceFragment {
	
	Preference mGummySite;
	Preference mGummySource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_settings);
        
        mGummySite = findPreference("gummy_site");
        mGummySource = findPreference("gummy_source");
        
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mGummySite) {
            gotoUrl("http://www.teamgummy.com");
        } else if (preference == mGummySource) {
        	gotoUrl("http://www.github.com/teamgummy");
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    
    private void gotoUrl(String uri) {
    	Uri page = Uri.parse(uri);
        Intent i = new Intent(Intent.ACTION_VIEW, page);
        getActivity().startActivity(i);
    }
}