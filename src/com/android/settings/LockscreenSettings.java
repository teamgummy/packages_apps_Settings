package com.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import com.android.settings.util.ShortcutPickerHelper;
import com.android.settings.R;

public class LockscreenSettings extends Activity {
    private ShortcutPickerHelper mPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new LockscreenPreferenceFragment()).commit();
    } 

        public class LockscreenPreferenceFragment extends SettingsPreferenceFragment implements ShortcutPickerHelper.OnPickListener {



        private static final String LOCKSCREEN_EXTRA = "lockscreen_extra";
        private static final String LOCKSCREEN_BATTERY = "lockscreen_battery";
        private static final String LOCKSCREEN_CUSTOM_1 = "lockscreen_custom_1";
        private static final String LOCKSCREEN_CUSTOM_2 = "lockscreen_custom_2";

        private CheckBoxPreference mLockExtra;
        private CheckBoxPreference mLockBattery;
        private Preference mCustomApp1;
        private Preference mCustomApp2;

        private Preference mCurrentCustomActivityPreference;
        private String mCurrentCustomActivityString;
        private String mCustomAppText1;
        private String mCustomAppText2;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.lockscreen_settings);
            PreferenceScreen prefSet = getPreferenceScreen();

            mLockExtra = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_EXTRA);
            mLockExtra.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.LOCKSCREEN_EXTRA_ICONS, 0) == 1);
            mLockBattery = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_BATTERY);
            mLockBattery.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.LOCKSCREEN_BATTERY, 0) == 1);

            mCustomApp1 = (Preference) prefSet.findPreference(LOCKSCREEN_CUSTOM_1);
            mCustomApp1.setEnabled(mLockExtra.isChecked());
            mCustomApp2 = (Preference) prefSet.findPreference(LOCKSCREEN_CUSTOM_2);
            mCustomApp2.setEnabled(mLockExtra.isChecked());
            mPicker = new ShortcutPickerHelper(this.getActivity(), this);
            mCustomAppText1 = Settings.System.getString(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_ONE);
            mCustomAppText2 = Settings.System.getString(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_TWO);

        }

        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            boolean value;
            if (preference == mLockBattery) {
                value = mLockBattery.isChecked();
                Settings.System.putInt(getContentResolver(),
                Settings.System.LOCKSCREEN_BATTERY, value ? 1 : 0);
                return true;
            } else if (preference == mLockExtra) {
                value = mLockExtra.isChecked();
                Settings.System.putInt(getContentResolver(),
                Settings.System.LOCKSCREEN_EXTRA_ICONS, value ? 1 : 0);
                updateCustomAppPickers(value);
                return true;
            } else if (preference == mCustomApp1) {
                mCurrentCustomActivityPreference = preference;
                mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_ONE;
                mPicker.pickShortcut();
                return true;
            } else if (preference == mCustomApp2) {
                mCurrentCustomActivityPreference = preference;
                mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_TWO;
                mPicker.pickShortcut();
                return true;
            }
            return false;
        }

        private void updateCustomAppPickers(boolean bool) {
            if (bool){
                mCustomApp1.setEnabled(true);
                mCustomApp2.setEnabled(true);
            } else {
                mCustomApp1.setEnabled(false);
                mCustomApp2.setEnabled(false);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            refreshSettings();
        }

        public void refreshSettings() {
            mCustomApp1.setSummary(mPicker.getFriendlyNameForUri(mCustomAppText1));
            mCustomApp2.setSummary(mPicker.getFriendlyNameForUri(mCustomAppText2));
        }

        @Override 
        public void shortcutPicked(String uri, String friendlyName, boolean isApplication) {
            if (Settings.System.putString(getContentResolver(), mCurrentCustomActivityString, uri)) {
                mCurrentCustomActivityPreference.setSummary(friendlyName);
            }
        }
    }

    @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("ADAM", "Activity Result Triggered!");
        mPicker.onActivityResult(requestCode, resultCode, data);
    }
}