
package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.Window;
import android.widget.Toast;
import android.util.Log;

import com.android.settings.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.android.settings.util.colorpicker.ColorPickerView;

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
    private static final String LOCKSCREEN_BACKGROUND = "lockscreen_background";
    private static final int LOCKSCREEN_BACKGROUND_CODE = 1024;
    
    private PreferenceCategory mCategoryLockSMS;

    private Context mContext;

    private CheckBoxPreference mLockExtra;
    private CheckBoxPreference mLockBattery;
    private CheckBoxPreference mLockBeforeUnlock;
    private CheckBoxPreference mQuickUnlock;
    private CheckBoxPreference mVolumeWake;
    private CheckBoxPreference mVolumeSkip;
    private CheckBoxPreference mLockscreenSmsCallWidget;
    private ListPreference mMusicStyle;
    private ListPreference mCustomBackground;

    private Activity mActivity;
    private File wallpaperImage;
    private File wallpaperTemporary;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lockscreen_settings);
        PreferenceScreen prefSet = getPreferenceScreen();
        mActivity = getActivity();
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

        mCustomBackground = (ListPreference) findPreference(LOCKSCREEN_BACKGROUND);
        mCustomBackground.setOnPreferenceChangeListener(this);
        wallpaperImage = new File(mActivity.getFilesDir()+"/lockwallpaper");
        wallpaperTemporary = new File(mActivity.getCacheDir()+"/lockwallpaper.tmp");
        updateCustomBackgroundSummary();
        
        if (isTablet) {
        	prefSet.removePreference(mCategoryLockSMS);
        	prefSet.removePreference(mMusicStyle);
            prefSet.removePreference(mLockscreenSmsCallWidget);
        }
    }

    private void updateCustomBackgroundSummary() {
        int resId;
        String value = Settings.System.getString(getContentResolver(),
                Settings.System.LOCKSCREEN_BACKGROUND);
        if (value == null) {
            resId = R.string.lockscreen_background_default_wallpaper;
            mCustomBackground.setValueIndex(2);
        } else if (value.isEmpty()) {
            resId = R.string.lockscreen_background_custom_image;
            mCustomBackground.setValueIndex(1);
        } else {
            resId = R.string.lockscreen_background_color_fill;
            mCustomBackground.setValueIndex(0);
        }
        mCustomBackground.setSummary(getResources().getString(resId));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCKSCREEN_BACKGROUND_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (wallpaperTemporary.exists()) {
                    wallpaperTemporary.renameTo(wallpaperImage);
                }
                wallpaperImage.setReadOnly();
                Toast.makeText(mActivity, getResources().getString(R.string.
                        lockscreen_background_result_successful), Toast.LENGTH_LONG).show();
                Settings.System.putString(getContentResolver(),
                        Settings.System.LOCKSCREEN_BACKGROUND,"");
                updateCustomBackgroundSummary();
            } else {
                if (wallpaperTemporary.exists()) {
                    wallpaperTemporary.delete();
                }
                Toast.makeText(mActivity, getResources().getString(R.string.
                        lockscreen_background_result_not_successful), Toast.LENGTH_LONG).show();
            }
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
        } else if (preference == mCustomBackground) {
            int indexOf = mCustomBackground.findIndexOfValue(newValue.toString());
            switch (indexOf) {
            //Displays color dialog when user has chosen color fill	
            case 0:
                final ColorPickerView colorView = new ColorPickerView(mActivity);
                int currentColor = Settings.System.getInt(getContentResolver(),
                        Settings.System.LOCKSCREEN_BACKGROUND, -1);
                if (currentColor != -1) {
                    colorView.setColor(currentColor);
                }
                colorView.setAlphaSliderVisible(true);
                new AlertDialog.Builder(mActivity)
                .setTitle(R.string.lockscreen_custom_background_dialog_title)
                .setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_BACKGROUND, colorView.getColor());
                        updateCustomBackgroundSummary();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setView(colorView).show();
                return false;
            //Launches intent for user to select an image/crop it to set as background
            case 1:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("scale", true);
                intent.putExtra("scaleUpIfNeeded", false);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
                int width = mActivity.getWindowManager().getDefaultDisplay().getWidth();
                int height = mActivity.getWindowManager().getDefaultDisplay().getHeight();
                Rect rect = new Rect();
                Window window = mActivity.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rect);
                int statusBarHeight = rect.top;
                int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
                int titleBarHeight = contentViewTop - statusBarHeight;
                boolean isPortrait = getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_PORTRAIT;
                intent.putExtra("aspectX", isPortrait ? width : height - titleBarHeight);
                intent.putExtra("aspectY", isPortrait ? height - titleBarHeight : width);
                try {
                    wallpaperTemporary.createNewFile();
                    wallpaperTemporary.setWritable(true, false);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(wallpaperTemporary));
                    intent.putExtra("return-data", false);
                    startActivityForResult(intent, LOCKSCREEN_BACKGROUND_CODE);
                } catch (IOException e) {
                } catch (ActivityNotFoundException e) {
                }
                return false;
            //Sets background color to default
            case 2:
                Settings.System.putString(getContentResolver(),
                        Settings.System.LOCKSCREEN_BACKGROUND, null);
                updateCustomBackgroundSummary();
                break;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
