
package com.android.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.util.CMDProcessor;
import com.android.settings.util.Helpers;

import java.io.File;

public class GeneralSettings extends SettingsPreferenceFragment {
	
    private boolean isTablet;

    private static final String TURN_DEGREE = "turn_degree";
    private static final String KILL_APP = "kill_app";
    private static final String ENABLE_VOLUME_OPTIONS = "enable_volume_options";
    private static final String BRIGHTNESS_SLIDER = "brightness_slider";
    private static final String UNLINK_VOLUMES = "unlink_volumes";
    private static final String BOOT_SOUND = "boot_sound";
    private static final String BOOT_ANIM = "boot_anim";

    private CheckBoxPreference m180Degree;
    private CheckBoxPreference mKillApp;
    private CheckBoxPreference mEnableVolumeOptions;
    private CheckBoxPreference mBrightSlider;
    private CheckBoxPreference mUnlinkVolumes;
    private CheckBoxPreference mBootSound;
    private CheckBoxPreference mBootAnim;
    
    private boolean isTurnedOn;
    private boolean doesItEvenExist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.general_settings);
        PreferenceScreen prefSet = getPreferenceScreen();
        
        isTablet = getResources().getBoolean(R.bool.is_a_tablet);

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

        mBootSound = (CheckBoxPreference) prefSet.findPreference(BOOT_SOUND);
        isTurnedOn = isItChecked(BOOT_SOUND);
        mBootSound.setChecked(isTurnedOn);
        doesItEvenExist = doesItExist(BOOT_SOUND);
        mBootSound.setEnabled(doesItEvenExist);
        
        mBootAnim = (CheckBoxPreference) prefSet.findPreference(BOOT_ANIM);
        isTurnedOn = isItChecked(BOOT_ANIM);
        mBootAnim.setChecked(isTurnedOn);
        doesItEvenExist = doesItExist(BOOT_ANIM);
        mBootAnim.setEnabled(doesItEvenExist);
        
        if (isTablet) {
        	prefSet.removePreference(m180Degree);
        	prefSet.removePreference(mEnableVolumeOptions);
        	prefSet.removePreference(mBrightSlider);
        	prefSet.removePreference(mUnlinkVolumes);
        }
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
        } else if (preference == mBootSound) {
            value = mBootSound.isChecked();
            Helpers.getMount("rw");
            if (!value && new File("/system/media/boot_audio.mp3").exists()) {
            	new CMDProcessor().su.runWaitFor("busybox mv /system/media/boot_audio.mp3 /system/media/boot_audio.old");
            } else {
            	new CMDProcessor().su.runWaitFor("busybox mv /system/media/boot_audio.old /system/media/boot_audio.mp3");
            }
            Helpers.getMount("ro");
            return true;
        } else if (preference == mBootAnim) {
            value = mBootAnim.isChecked();
            Helpers.getMount("rw");
            if (!value && new File("/system/media/bootanimation.zip").exists()) {
            	new CMDProcessor().su.runWaitFor("busybox mv /system/media/bootanimation.zip /system/media/bootanimation.old");
            } else {
            	new CMDProcessor().su.runWaitFor("busybox mv /system/media/bootanimation.old /system/media/bootanimation.zip");
            }
            Helpers.getMount("ro");
            return true;
        }
        return false;
    }
    
    private boolean isItChecked(String supDog) {
    	if (supDog == BOOT_SOUND) {
    		if (new File("/system/media/boot_audio.mp3").exists()) {
    			return true;
    		} else if (!new File("/system/media/boot_audio.mp3").exists()) {
    			return false;
    		}
    	} else if (supDog == BOOT_ANIM) {
    		if (new File("/system/media/bootanimation.zip").exists()) {
    			return true;
    		} else if (!new File("/system/media/bootanimation.zip").exists()) {
    			return false;
    		}
    	}
    	return false;
    }
    
    private boolean doesItExist (String youMadBro) {
    	if (youMadBro == BOOT_SOUND) {
    		if (new File("/system/media/boot_audio.mp3").exists() || new File("/system/media/boot_audio.old").exists()) {
    			mBootSound.setSummary(getString(R.string.boot_sound_summary));
    			return true;
    		} else {
    			mBootSound.setSummary("You do not have a boot_audio.mp3 in the system/media folder");
    			return false;
    		}
    	} else if (youMadBro == BOOT_ANIM) {
    		if (new File("/system/media/bootanimation.zip").exists() || new File("/system/media/bootanimation.old").exists()) {
    			mBootAnim.setSummary(getString(R.string.boot_anim_summary));
    			return true;
    		} else {
    			mBootAnim.setSummary("You do not have a bootanimation.zip in the system/media folder");
    			return false;
    		}
    	}
    	return false;
    }
}
