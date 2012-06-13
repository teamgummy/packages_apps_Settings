
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

import com.android.settings.R;

import java.util.ArrayList;

public class LockscreenSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {
	
	private boolean isTablet;

    private static final String LOCKSCREEN_BATTERY = "lockscreen_battery";
    private static final String LOCKSCREEN_BEFORE_UNLOCK = "lockscreen_before_unlock";
    private static final String QUICK_PASSWORD_UNLOCK = "quick_password_unlock";
    private static final String VOLUME_WAKE = "volume_wake";
    private static final String VOLUME_SKIP = "volume_skip";
    private static final String LOCKSCREEN_MUSIC_WIDGET = "lockscreen_music_widget";
    private static final String LOCKSCREEN_SMS_CALL_WIDGET = "lockscreen_sms_call_widget";
    
    private PreferenceCategory mCategoryLockSMS;

    private CheckBoxPreference mLockExtra;
    private CheckBoxPreference mLockBattery;
    private CheckBoxPreference mLockBeforeUnlock;
    private CheckBoxPreference mQuickUnlock;
    private CheckBoxPreference mVolumeWake;
    private CheckBoxPreference mVolumeSkip;
    private CheckBoxPreference mLockscreenSmsCallWidget;
    private ListPreference mMusicStyle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lockscreen_settings);
        PreferenceScreen prefSet = getPreferenceScreen();
        
        isTablet = getResources().getBoolean(R.bool.is_a_tablet);
        
        mCategoryLockSMS = (PreferenceCategory) prefSet.findPreference("sms_call_widget");

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

        mLockscreenSmsCallWidget = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_SMS_CALL_WIDGET);
        mLockscreenSmsCallWidget.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.LOCKSCREEN_SMS_CALL_WIDGET, 0) == 1);
        
        if (isTablet) {
        	prefSet.removePreference(mCategoryLockSMS);
        	prefSet.removePreference(mMusicStyle);
            prefSet.removePreference(mLockscreenSmsCallWidget);
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
        } else if (preference == mLockscreenSmsCallWidget) {
            value = mLockscreenSmsCallWidget.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_SMS_CALL_WIDGET, value ? 1 : 0);
            return true;
        }
        return false;
    }    

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mMusicStyle) {
        	Settings.System.putInt(getActivity().getContentResolver(), Settings.System.MUSIC_WIDGET_TYPE, Integer.parseInt((String) newValue));
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
