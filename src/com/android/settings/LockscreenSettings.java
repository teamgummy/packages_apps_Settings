
package com.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import com.android.settings.util.colorpicker.ColorPickerPreference;
import com.android.settings.util.ShortcutPickerHelper;
import com.android.settings.R;

import java.util.ArrayList;

public class LockscreenSettings extends SettingsPreferenceFragment implements
    ShortcutPickerHelper.OnPickListener, OnPreferenceChangeListener {
	
	private boolean isTablet;

    private static final String LOCKSCREEN_BATTERY = "lockscreen_battery";
    private static final String LOCKSCREEN_BEFORE_UNLOCK = "lockscreen_before_unlock";
    private static final String QUICK_PASSWORD_UNLOCK = "quick_password_unlock";
    private static final String VOLUME_WAKE = "volume_wake";
    private static final String VOLUME_SKIP = "volume_skip";
    private static final String LOCKSCREEN_MUSIC_WIDGET = "lockscreen_music_widget";
    private static final String LOCKSCREEN_TEXT = "lockscreen_text";
    private static final String LOCKSCREEN_TEXT_APP = "lockscreen_text_app";
    private static final String LOCKSCREEN_TEXT_COLOR = "lockscreen_text_color";
    
    private PreferenceCategory mCategoryLockSMS;

    private CheckBoxPreference mLockExtra;
    private CheckBoxPreference mLockBattery;
    private CheckBoxPreference mLockBeforeUnlock;
    private CheckBoxPreference mQuickUnlock;
    private CheckBoxPreference mVolumeWake;
    private CheckBoxPreference mVolumeSkip;
    private CheckBoxPreference mLockSMS;
    private ListPreference mMusicStyle;
    private Preference mSMSApp;
    
    private ColorPickerPreference mSMSColor;

    private Preference mCurrentCustomActivityPreference;
    private String mCurrentCustomActivityString;
    
	private ShortcutPickerHelper mPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lockscreen_settings);
        PreferenceScreen prefSet = getPreferenceScreen();
        
        isTablet = getResources().getBoolean(R.bool.is_a_tablet);
        
        mCategoryLockSMS = (PreferenceCategory) prefSet.findPreference("sms_popup");

        mMusicStyle = (ListPreference) findPreference(LOCKSCREEN_MUSIC_WIDGET);
        mMusicStyle.setOnPreferenceChangeListener(this);
        mMusicStyle.setValue(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.MUSIC_WIDGET_TYPE,
            0) + "");

        mLockBattery = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_BATTERY);
        mLockBattery.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_BATTERY, 0) == 1);

        mLockBeforeUnlock = (CheckBoxPreference) prefSet
                .findPreference(LOCKSCREEN_BEFORE_UNLOCK);
        mLockBeforeUnlock.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_BEFORE_UNLOCK, 0) == 1);

        mQuickUnlock = (CheckBoxPreference) prefSet
                .findPreference(QUICK_PASSWORD_UNLOCK);
        mQuickUnlock.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, 0) == 1);

        mVolumeWake = (CheckBoxPreference) prefSet.findPreference(VOLUME_WAKE);
        mVolumeWake.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_WAKE, 0) == 1);
        
        mVolumeSkip = (CheckBoxPreference) prefSet.findPreference(VOLUME_SKIP);
        mVolumeSkip.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.VOLBTN_MUSIC_CONTROLS, 0) == 1);
        
        mLockSMS = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_TEXT);
        mLockSMS.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_SHOW_TEXTS, 0) == 1);
        
        mSMSApp = (Preference) prefSet.findPreference(LOCKSCREEN_TEXT_APP);
        
        mPicker = new ShortcutPickerHelper(this, this);
        
        mSMSColor = (ColorPickerPreference) prefSet.findPreference(LOCKSCREEN_TEXT_COLOR);
        mSMSColor.setOnPreferenceChangeListener(this);
        
        updateSMSApp(mLockSMS.isChecked());
        
        if (isTablet) {
        	prefSet.removePreference(mCategoryLockSMS);
        	prefSet.removePreference(mLockSMS);
        	prefSet.removePreference(mSMSApp);
        	prefSet.removePreference(mSMSColor);
        	prefSet.removePreference(mMusicStyle);
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        boolean value;
        if (preference == mLockBattery) {
            value = mLockBattery.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_BATTERY, value ? 1 : 0);
            return true;
        } else if (preference == mLockBeforeUnlock) {
            value = mLockBeforeUnlock.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_BEFORE_UNLOCK, value ? 1 : 0);
            return true;
        } else if (preference == mQuickUnlock) {
            value = mQuickUnlock.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, value ? 1 : 0);
            return true;
        } else if (preference == mVolumeWake) {
            value = mVolumeWake.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_WAKE, value ? 1 : 0);
            return true;
        } else if (preference == mVolumeSkip) {
            value = mVolumeSkip.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLBTN_MUSIC_CONTROLS, value ? 1 : 0);
            return true;
        } else if (preference == mLockSMS) {
            value = mLockSMS.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_SHOW_TEXTS, value ? 1 : 0);
            updateSMSApp(value);
            return true;
        } else if (preference == mSMSApp) {
        	mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = Settings.System.LOCKSCREEN_SMS_APP;
            mPicker.pickShortcut();
            return true;
        }
        return false;
    }    

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mMusicStyle) {
        	Settings.System.putInt(getActivity().getContentResolver(), Settings.System.MUSIC_WIDGET_TYPE, Integer.parseInt((String) newValue));
            return true;
        } else if (preference == mSMSColor) {
            String hexColor = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hexColor);
            int color = ColorPickerPreference.convertToColorInt(hexColor);
            Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_SMS_COLOR, color);
            return true;
        }
        return false;
    }
    
    private void updateSMSApp(boolean bool) {
    	if (bool) {
    		mSMSApp.setEnabled(true);
    		mSMSColor.setEnabled(true);
    	} else {
    		mSMSApp.setEnabled(false);
    		mSMSColor.setEnabled(false);
    	}
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSettings();
    }

    public void refreshSettings() {
        mSMSApp.setSummary(mPicker.getFriendlyNameForUri(Settings.System.getString(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_SMS_APP)));
    }

    @Override
    public void shortcutPicked(String uri, String friendlyName, boolean isApplication) {
        if (Settings.System.putString(getActivity().getContentResolver(), mCurrentCustomActivityString, uri)) {
            mCurrentCustomActivityPreference.setSummary(friendlyName);
            refreshSettings();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPicker.onActivityResult(requestCode, resultCode, data);
    }
}
