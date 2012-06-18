package com.android.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MinfreeValues extends PreferenceActivity {

        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.minfree_values);

                InitD activity = InitD.whatActivity();
                activity.mPrefs.registerOnSharedPreferenceChangeListener(
                                activity.mOnSharedPreferenceChangeListener);
        }

}
