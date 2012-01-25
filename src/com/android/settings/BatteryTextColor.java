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

import com.android.settings.R;
import com.android.settings.util.colorpicker.ColorPickerPreference;

public class BatteryTextColor extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new BattTextPreferenceFragment()).commit();
    } 

    public class BattTextPreferenceFragment extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

        private static final String ALLOW_COLOR_CHANGES = "allow_color_changes";
        private static final String BATTERY_TEXT_CHARGE = "battery_text_charge";
        private static final String BATTERY_TEXT_NORMAL = "battery_text_normal";
        private static final String BATTERY_TEXT_LOW = "battery_text_low";

        private CheckBoxPreference mAllowColors;
        private ColorPickerPreference mChargeColor;
        private ColorPickerPreference mNormalColor;
        private ColorPickerPreference mLowColor;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.battery_text_color);
            PreferenceScreen prefSet = getPreferenceScreen();

            mAllowColors = (CheckBoxPreference) prefSet.findPreference(ALLOW_COLOR_CHANGES);
            mAllowColors.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.BATTERY_TEXT_COLOR_ALLOWED, 0) == 1);

            mChargeColor = (ColorPickerPreference) prefSet.findPreference(BATTERY_TEXT_CHARGE);
            mChargeColor.setOnPreferenceChangeListener(this);
            mNormalColor = (ColorPickerPreference) prefSet.findPreference(BATTERY_TEXT_NORMAL);
            mNormalColor.setOnPreferenceChangeListener(this);
            mLowColor = (ColorPickerPreference) prefSet.findPreference(BATTERY_TEXT_LOW);
            mLowColor.setOnPreferenceChangeListener(this);

            updateColorPrefs(mAllowColors.isChecked());
        }

        private void updateColorPrefs(boolean bool){
            if (bool) {
                mChargeColor.setEnabled(true);
                mNormalColor.setEnabled(true);
                mLowColor.setEnabled(true);
            } else {
                mChargeColor.setEnabled(false);
                mNormalColor.setEnabled(false);
                mLowColor.setEnabled(false);
            }
        }

        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            boolean value;
            if (preference == mAllowColors) {
                value = mAllowColors.isChecked();
                Settings.System.putInt(getContentResolver(),
                    Settings.System.BATTERY_TEXT_COLOR_ALLOWED, value ? 1 : 0);
                updateColorPrefs(value);
                return true;
            }
            return false;
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference == mChargeColor) {
                String hexColor = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
                preference.setSummary(hexColor);
                int color = ColorPickerPreference.convertToColorInt(hexColor);
                Settings.System.putInt(getContentResolver(),
                    Settings.System.BATTERY_TEXT_COLOR_CHARGE, color);
                return true;
            } else if (preference == mNormalColor) {
                String hexColor = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
                preference.setSummary(hexColor);
                int color = ColorPickerPreference.convertToColorInt(hexColor);
                Settings.System.putInt(getContentResolver(),
                    Settings.System.BATTERY_TEXT_COLOR_NORMAL, color);
                return true;
            } else if (preference == mLowColor) {
                String hexColor = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
                preference.setSummary(hexColor);
                int color = ColorPickerPreference.convertToColorInt(hexColor);
                Settings.System.putInt(getContentResolver(),
                    Settings.System.BATTERY_TEXT_COLOR_LOW, color);
                return true;
            }
            return false;
        }
    }
}
