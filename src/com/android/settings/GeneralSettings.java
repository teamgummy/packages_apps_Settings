
package com.android.settings;

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

import com.android.settings.R;

public class GeneralSettings extends SettingsPreferenceFragment {

    private static final String TURN_DEGREE = "turn_degree";
    private static final String KILL_APP = "kill_app";
    private static final String ENABLE_VOLUME_OPTIONS = "enable_volume_options";
    private static final String BRIGHTNESS_SLIDER = "brightness_slider";
    private static final String UNLINK_VOLUMES = "unlink_volumes";

    private CheckBoxPreference m180Degree;
    private CheckBoxPreference mKillApp;
    private CheckBoxPreference mEnableVolumeOptions;
    private CheckBoxPreference mBrightSlider;
    private CheckBoxPreference mUnlinkVolumes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.general_settings);
        PreferenceScreen prefSet = getPreferenceScreen();

        m180Degree = (CheckBoxPreference) prefSet.findPreference(TURN_DEGREE);
        m180Degree.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION_ANGLES, (1 | 2 | 8)) == (1 | 2 | 4 | 8));

        mKillApp = (CheckBoxPreference) prefSet.findPreference(KILL_APP);
        mKillApp.setChecked(Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.KILL_APP_LONGPRESS_BACK, 0) == 1);

        mEnableVolumeOptions = (CheckBoxPreference) prefSet.findPreference(ENABLE_VOLUME_OPTIONS);
        mEnableVolumeOptions.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.ENABLE_VOLUME_OPTIONS, 0) == 1);

        mBrightSlider = (CheckBoxPreference) prefSet.findPreference(BRIGHTNESS_SLIDER);
        mBrightSlider.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.STATUS_BAR_BRIGHTNESS_TOGGLE, 0) == 1);

        mUnlinkVolumes = (CheckBoxPreference) prefSet.findPreference(UNLINK_VOLUMES);
        mUnlinkVolumes.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.UNLINK_VOLUMES_TOGETHER, 0) == 1);

    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == m180Degree) {
            value = m180Degree.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION_ANGLES, value ? (1 | 2 | 4 | 8)
                            : (1 | 2 | 8));
            return true;
        } else if (preference == mKillApp) {
            value = mKillApp.isChecked();
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.KILL_APP_LONGPRESS_BACK, value ? 1 : 0);
            return true;
        } else if (preference == mEnableVolumeOptions) {
            value = mEnableVolumeOptions.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.ENABLE_VOLUME_OPTIONS, value ? 1 : 0);
            return true;
        } else if (preference == mBrightSlider) {
            value = mBrightSlider.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_BRIGHTNESS_TOGGLE, value ? 1 : 0);
            return true;
        } else if (preference == mUnlinkVolumes) {
            value = mUnlinkVolumes.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.UNLINK_VOLUMES_TOGETHER, value ? 1 : 0);
            return true;
        }
        return false;
    }

}
