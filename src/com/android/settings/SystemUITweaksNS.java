package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.text.Spannable;
import android.widget.EditText;

import com.android.settings.R;
import com.android.settings.util.colorpicker.ColorPickerPreference;

public class SystemUITweaksNS extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String HIDE_ALARM = "hide_alarm";
    private static final String PREF_CLOCK_DISPLAY_STYLE = "clock_am_pm";
    private static final String PREF_CLOCK_STYLE = "clock_style";
    private static final String HIDE_BATTERY = "hide_battery";
    private static final String BATTERY_STYLE = "battery_style";
    private static final String BATTERY_BAR = "battery_bar";
    private static final String BATTERY_BAR_COLOR = "battery_bar_color";
    private static final String PREF_CARRIER_TEXT = "carrier_text";

    private CheckBoxPreference mHideAlarm;
    private CheckBoxPreference mHideBatt;
    private CheckBoxPreference mBattBar;
    private ListPreference mAmPmStyle;
    private ListPreference mClockStyle;
    private ListPreference mBatteryStyle;
    private Preference mCarrier;
    private ColorPickerPreference mBattBarColor;

    String mCarrierText = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.systemui_tweaks);
        PreferenceScreen prefSet = getPreferenceScreen();

        mHideAlarm = (CheckBoxPreference) prefSet.findPreference(HIDE_ALARM);
        mHideAlarm.setChecked(Settings.System.getInt(getContentResolver(),
            Settings.System.HIDE_ALARM, 0) == 1);

        mHideBatt = (CheckBoxPreference) prefSet.findPreference(HIDE_BATTERY);
        mHideBatt.setChecked(Settings.System.getInt(getContentResolver(),
            Settings.System.HIDE_BATTERY, 0) == 1);
        mBattBar = (CheckBoxPreference) prefSet.findPreference(BATTERY_BAR);
        mBattBar.setChecked(Settings.System.getInt(getContentResolver(),
            Settings.System.STATUSBAR_BATTERY_BAR, 0) == 1);

        mBattBarColor = (ColorPickerPreference) prefSet.findPreference(BATTERY_BAR_COLOR);
        mBattBarColor.setOnPreferenceChangeListener(this);

        mCarrier = (Preference) prefSet.findPreference(PREF_CARRIER_TEXT);
        updateCarrierText();

        mClockStyle = (ListPreference) prefSet.findPreference(PREF_CLOCK_STYLE);
        mAmPmStyle = (ListPreference) prefSet.findPreference(PREF_CLOCK_DISPLAY_STYLE);
        mBatteryStyle = (ListPreference) prefSet.findPreference(BATTERY_STYLE);

        int styleValue = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_AM_PM, 2);
        mAmPmStyle.setValueIndex(styleValue);
        mAmPmStyle.setOnPreferenceChangeListener(this);

        int clockVal = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CLOCK, 1);
        mClockStyle.setValueIndex(clockVal);
        mClockStyle.setOnPreferenceChangeListener(this);

        int battVal = Settings.System.getInt(getContentResolver(),
                Settings.System.BATTERY_PERCENTAGES, 1);
        mBatteryStyle.setValueIndex(battVal);
        mBatteryStyle.setOnPreferenceChangeListener(this);
    }

    private void updateCarrierText() {
        mCarrierText = Settings.System.getString(getContentResolver(), Settings.System.CUSTOM_CARRIER_TEXT);
        if (mCarrierText == null) {
            mCarrier.setSummary("Upon changing you will need to data wipe to get back stock. Requires reboot.");
        } else {
            mCarrier.setSummary(mCarrierText);
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mHideAlarm) {
            value = mHideAlarm.isChecked();
            Settings.System.putInt(getContentResolver(),
                Settings.System.HIDE_ALARM, value ? 1 : 0);
            return true;
        } else if (preference == mHideBatt) {
            value = mHideBatt.isChecked();
            Settings.System.putInt(getContentResolver(),
                Settings.System.HIDE_BATTERY, value ? 1 : 0);
            return true;
        } else if (preference == mBattBar) {
            value = mBattBar.isChecked();
            Settings.System.putInt(getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR, value ? 1 : 0);
            return true;
        } else if (preference == mCarrier) {
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setTitle("Custom Carrier Text");
            ad.setMessage("Enter new carrier text here");
            final EditText text = new EditText(getActivity());
            text.setText(mCarrierText != null ? mCarrierText : "");
            ad.setView(text);
            ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = ((Spannable) text.getText()).toString();
                    Settings.System.putString(getActivity().getContentResolver(), Settings.System.CUSTOM_CARRIER_TEXT, value);
                    updateCarrierText();
                }
            });
            ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            ad.show();
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
         if (preference == mAmPmStyle) {
            int statusBarAmPm = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                Settings.System.STATUS_BAR_AM_PM, statusBarAmPm);
            return true;
        } else if (preference == mClockStyle) {
            int val = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                Settings.System.STATUS_BAR_CLOCK, val);
            return true;
        } else if (preference == mBatteryStyle) {
            int val = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                Settings.System.BATTERY_PERCENTAGES, val);
            return true;
        } else if (preference == mBattBarColor) {
            String hexColor = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hexColor);
            int color = ColorPickerPreference.convertToColorInt(hexColor);
            Settings.System.putInt(getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_COLOR, color);
            return true;
        }
        return false;
    }
}
