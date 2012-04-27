package com.android.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import java.util.List;

public class GummySettings extends SettingsPreferenceFragment {

    private static final String GENERAL_SETTINGS = "general_settings";
    private static final String SYSTEMUI_TWEAKS = "systemui_tweaks";
    private static final String LOCKSCREEN_SETTINGS = "lockscreen_settings";
    private static final String LOCKSCREEN_STYLES = "lockscreen_styles";
    private static final String LED_SETTINGS = "led_settings";
    private static final String SOFTKEY_SETTINGS = "softkey_settings";
    private static final String PERFORMANCE_SETTINGS = "performance_settings";
    private static final String ABOUT_SETTINGS = "about_settings";
    private static final String POWER_WIDGETS = "power_widgets";

    PreferenceScreen mGeneralSettings;
    PreferenceScreen mSystemUITweaks;
    PreferenceScreen mLockscreenSettings;
    PreferenceScreen mLockscreenStyles;
    PreferenceScreen mLEDSettings;
    PreferenceScreen mSoftkeySettings;
    PreferenceScreen mPerformanceSettings;
    PreferenceScreen mAbout;
    PreferenceScreen mPowerWidgets;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.gummy_settings);

        PreferenceScreen prefs = getPreferenceScreen();

        mGeneralSettings = (PreferenceScreen) findPreference(GENERAL_SETTINGS);
        mSystemUITweaks = (PreferenceScreen) findPreference(SYSTEMUI_TWEAKS);
        mSoftkeySettings = (PreferenceScreen) findPreference(SOFTKEY_SETTINGS);
        mLockscreenSettings = (PreferenceScreen) findPreference(LOCKSCREEN_SETTINGS);
        mLockscreenStyles = (PreferenceScreen) findPreference(LOCKSCREEN_STYLES);
        mLEDSettings = (PreferenceScreen) findPreference(LED_SETTINGS);
        mPerformanceSettings = (PreferenceScreen) findPreference(PERFORMANCE_SETTINGS);
        mAbout = (PreferenceScreen) findPreference(ABOUT_SETTINGS);
        mPowerWidgets = (PreferenceScreen) findPreference(POWER_WIDGETS);

    }
}
