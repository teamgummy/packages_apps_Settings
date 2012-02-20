
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

public class SystemUITweaks extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String HIDE_ALARM = "hide_alarm";
    private static final String PREF_CLOCK_DISPLAY_STYLE = "clock_am_pm";
    private static final String PREF_CLOCK_STYLE = "clock_style";
    private static final String CLOCK_COLOR = "clock_color";
    private static final String SHOW_MENU_BUTTON = "show_menu_button";
    private static final String SHOW_SEARCH_BUTTON = "show_search_button";
    private static final String BATTERY_TEXT = "battery_text";
    private static final String BATTERY_STYLE = "battery_style";
    private static final String BATTERY_BAR = "battery_bar";
    private static final String BATTERY_BAR_COLOR = "battery_bar_color";
    private static final String PREF_CARRIER_TEXT = "carrier_text";
    private static final String BATTERY_TEXT_COLOR = "battery_text_color";
    private static final String LONG_PRESS_HOMEKEY = "long_press_homekey";
    private static final String SOFTKEY_COLOR = "softkey_color";
    private static final String TOGGLE_COLOR = "toggle_color";
    private static final String WIFI_SIGNAL_COLOR = "wifi_signal_color";
    private static final String MOBILE_SIGNAL_COLOR = "mobile_signal_color";

    private CheckBoxPreference mHideAlarm;
    private CheckBoxPreference mShowMenuButton;
    private CheckBoxPreference mShowSearchButton;
    private CheckBoxPreference mLongPressHome;
    private CheckBoxPreference mBattText;
    private CheckBoxPreference mBattBar;
    private ListPreference mAmPmStyle;
    private ListPreference mClockStyle;
    private ListPreference mBatteryStyle;
    private Preference mCarrier;
    private ColorPickerPreference mBattBarColor;
    private ColorPickerPreference mSoftKeyColor;
    private ColorPickerPreference mClockColor;
    private ColorPickerPreference mToggleColor;
    private ColorPickerPreference mWifiSignalColor;
    private ColorPickerPreference mMobileSignalColor;

    PreferenceScreen mBattColor;

    String mCarrierText = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.systemui_tweaks);
        PreferenceScreen prefSet = getPreferenceScreen();

        mHideAlarm = (CheckBoxPreference) prefSet.findPreference(HIDE_ALARM);
        mHideAlarm.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.HIDE_ALARM, 0) == 1);

        mBattText = (CheckBoxPreference) prefSet.findPreference(BATTERY_TEXT);
        mBattText.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.BATTERY_TEXT, 0) == 1);

        mBattBar = (CheckBoxPreference) prefSet.findPreference(BATTERY_BAR);
        mBattBar.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR, 0) == 1);

        mBattColor = (PreferenceScreen) findPreference(BATTERY_TEXT_COLOR);
        mBattColor.setEnabled(mBattText.isChecked());

        mBattBarColor = (ColorPickerPreference) prefSet.findPreference(BATTERY_BAR_COLOR);
        mBattBarColor.setOnPreferenceChangeListener(this);
        mBattBarColor.setEnabled(mBattBar.isChecked());

        mClockColor = (ColorPickerPreference) prefSet.findPreference(CLOCK_COLOR);
        mClockColor.setOnPreferenceChangeListener(this);

        mToggleColor = (ColorPickerPreference) prefSet.findPreference(TOGGLE_COLOR);
        mToggleColor.setOnPreferenceChangeListener(this);

        mLongPressHome = (CheckBoxPreference) prefSet.findPreference(LONG_PRESS_HOMEKEY);
        mLongPressHome.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LONG_PRESS_HOME, 0) == 1);

        mSoftKeyColor = (ColorPickerPreference) prefSet.findPreference(SOFTKEY_COLOR);
        mSoftKeyColor.setOnPreferenceChangeListener(this);

        mWifiSignalColor = (ColorPickerPreference) prefSet.findPreference(WIFI_SIGNAL_COLOR);
        mWifiSignalColor.setOnPreferenceChangeListener(this);

        mMobileSignalColor = (ColorPickerPreference) prefSet.findPreference(MOBILE_SIGNAL_COLOR);
        mMobileSignalColor.setOnPreferenceChangeListener(this);

        mCarrier = (Preference) prefSet.findPreference(PREF_CARRIER_TEXT);
        updateCarrierText();

        mClockStyle = (ListPreference) prefSet.findPreference(PREF_CLOCK_STYLE);
        mAmPmStyle = (ListPreference) prefSet.findPreference(PREF_CLOCK_DISPLAY_STYLE);
        mBatteryStyle = (ListPreference) prefSet.findPreference(BATTERY_STYLE);

        mShowMenuButton = (CheckBoxPreference) prefSet.findPreference(SHOW_MENU_BUTTON);
        mShowMenuButton.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.SHOW_MENU_BUTTON, 0) == 1);

        mShowSearchButton = (CheckBoxPreference) prefSet.findPreference(SHOW_SEARCH_BUTTON);
        mShowSearchButton.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.SHOW_SEARCH_BUTTON, 0) == 1);
        updateLongPressToggle(mShowSearchButton.isChecked());
        updateSearchToggle(mLongPressHome.isChecked());

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
        mCarrierText = Settings.System.getString(getContentResolver(),
                Settings.System.CUSTOM_CARRIER_TEXT);
        if (mCarrierText == null) {
            mCarrier.setSummary("Upon changing you will need to data wipe to get back stock. Requires reboot.");
        } else {
            mCarrier.setSummary(mCarrierText);
        }
    }

    private void updateBatteryTextToggle(boolean bool) {
        if (bool)
            mBattColor.setEnabled(true);
        else
            mBattColor.setEnabled(false);
    }

    private void updateBatteryBarToggle(boolean bool) {
        if (bool)
            mBattBarColor.setEnabled(true);
        else
            mBattBarColor.setEnabled(false);
    }

    private void updateSearchToggle(boolean bool) {
        if (bool)
            mShowSearchButton.setEnabled(false);
        else
            mShowSearchButton.setEnabled(true);
    }

    private void updateLongPressToggle(boolean bool) {
        if (bool)
            mLongPressHome.setEnabled(false);
        else
            mLongPressHome.setEnabled(true);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mHideAlarm) {
            value = mHideAlarm.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.HIDE_ALARM, value ? 1 : 0);
            return true;
        } else if (preference == mShowMenuButton) {
            value = mShowMenuButton.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SHOW_MENU_BUTTON, value ? 1 : 0);
            return true;
        } else if (preference == mShowSearchButton) {
            value = mShowSearchButton.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SHOW_SEARCH_BUTTON, value ? 1 : 0);
            updateLongPressToggle(value);
            return true;
        } else if (preference == mBattText) {
            value = mBattText.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.BATTERY_TEXT, value ? 1 : 0);
            updateBatteryTextToggle(value);
            return true;
        } else if (preference == mBattBar) {
            value = mBattBar.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR, value ? 1 : 0);
            updateBatteryBarToggle(value);
            return true;
        } else if (preference == mLongPressHome) {
            value = mLongPressHome.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LONG_PRESS_HOME, value ? 1 : 0);
            updateSearchToggle(value);
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
                    Settings.System.putString(getActivity().getContentResolver(),
                            Settings.System.CUSTOM_CARRIER_TEXT, value);
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
            String hexColor = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hexColor);
            int color = ColorPickerPreference.convertToColorInt(hexColor);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_COLOR, color);
            return true;
        } else if (preference == mSoftKeyColor) {
            String hexColor = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hexColor);
            int color = ColorPickerPreference.convertToColorInt(hexColor);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SOFT_KEY_COLOR, color);
            return true;
        } else if (preference == mWifiSignalColor) {
            String hexColor = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hexColor);
            int color = ColorPickerPreference.convertToColorInt(hexColor);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.WIFI_SIGNAL_COLOR, color);
            return true;
        } else if (preference == mMobileSignalColor) {
            String hexColor = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hexColor);
            int color = ColorPickerPreference.convertToColorInt(hexColor);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.MOBILE_SIGNAL_COLOR, color);
            return true;
        } else if (preference == mClockColor) {
            String hexColor = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hexColor);
            int color = ColorPickerPreference.convertToColorInt(hexColor);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.CLOCK_COLOR, color);
            return true;
        } else if (preference == mToggleColor) {
            String hexColor = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hexColor);
            int color = ColorPickerPreference.convertToColorInt(hexColor);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NOTIFICATION_TOGGLE_COLOR_BAR, color);
            return true;
        }
        return false;
    }
}
