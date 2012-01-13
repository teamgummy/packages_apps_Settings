package com.android.settings;
import com.android.settings.R;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

public class LockscreenSettings extends SettingsPreferenceFragment {

    private static final String LOCKSCREEN_BATTERY = "lockscreen_battery";

    private CheckBoxPreference mLockBattery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lockscreen_settings);
        PreferenceScreen prefSet = getPreferenceScreen();

        mLockBattery = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_BATTERY);
        mLockBattery.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.LOCKSCREEN_BATTERY, 0) == 1);

    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mLockBattery) {
            value = mLockBattery.isChecked();
            Settings.System.putInt(getContentResolver(),
                Settings.System.LOCKSCREEN_BATTERY, value ? 1 : 0);
            return true;
        }
        return false;
    }
}
