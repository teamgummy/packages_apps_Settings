
package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

public class LockscreenStyles extends SettingsPreferenceFragment implements
    ShortcutPickerHelper.OnPickListener, OnPreferenceChangeListener {
	
	private boolean isTablet;
	
    private static final String GENERAL_CATEGORY = "general_category";
    private static final String UNLOCK_CATEGORY = "unlock_category";
    private static final String CUSTOM_APP= "custom_app";
    private static final String LOCKSCREEN_EXTRA = "lockscreen_extra";
    private static final String LOCKSCREEN_CUSTOM_1 = "lockscreen_custom_1";
    private static final String LOCKSCREEN_CUSTOM_2 = "lockscreen_custom_2";
    private static final String LOCKSCREEN_CUSTOM_3 = "lockscreen_custom_3";
    private static final String LOCKSCREEN_CUSTOM_4 = "lockscreen_custom_4";
    private static final String LOCKSCREEN_CUSTOM_5 = "lockscreen_custom_5";
    private static final String LOCKSCREEN_CUSTOM_6 = "lockscreen_custom_6";
    private static final String SOUND_OR_CAMERA = "sound_or_camera";
    private static final String LOCKSCREEN_STYLES = "lockscreen_styles";
    private static final String ROTARY_ARROWS = "rotary_arrows";
    private static final String ROTARY_DOWN = "rotary_down";

    private PreferenceCategory mCategoryGeneral;
    private PreferenceCategory mCategoryCustom;

    private CheckBoxPreference mLockExtra;
    private CheckBoxPreference mRotaryArrows;
    private CheckBoxPreference mRotaryDown;
    private ListPreference mSoundCamera;
    private ListPreference mLockStyle;
    private Preference mCustomApp1;
    private Preference mCustomApp2;
    private Preference mCustomApp3;
    private Preference mCustomApp4;
    private Preference mCustomApp5;
    private Preference mCustomApp6;

    private Preference mCurrentCustomActivityPreference;
    private String mCurrentCustomActivityString;

    private int mWhichApp = -1;
    private int mMaxRingCustomApps = Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES.length;
    
	private ShortcutPickerHelper mPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lockscreen_styles);
        PreferenceScreen prefSet = getPreferenceScreen();
        
        isTablet = getResources().getBoolean(R.bool.is_a_tablet);

        mCategoryGeneral = (PreferenceCategory) prefSet.findPreference(GENERAL_CATEGORY);
        mCategoryCustom = (PreferenceCategory) prefSet.findPreference(CUSTOM_APP);

        mLockStyle = (ListPreference) findPreference(LOCKSCREEN_STYLES);
        mLockStyle.setOnPreferenceChangeListener(this);
        mLockStyle.setValue(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE,
            0) + "");
        
        mRotaryArrows = (CheckBoxPreference) prefSet.findPreference(ROTARY_ARROWS);
        mRotaryArrows.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_HIDE_ARROWS, 0) == 1);

        mRotaryDown = (CheckBoxPreference) prefSet.findPreference(ROTARY_DOWN);
        mRotaryDown.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_ROTARY_UNLOCK_DOWN, 0) == 1);

        mLockExtra = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_EXTRA);
        mLockExtra.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_EXTRA_ICONS, 0) == 1);

        mCustomApp1 = (Preference) prefSet.findPreference(LOCKSCREEN_CUSTOM_1);
        mCustomApp2 = (Preference) prefSet.findPreference(LOCKSCREEN_CUSTOM_2);
        mCustomApp3 = (Preference) prefSet.findPreference(LOCKSCREEN_CUSTOM_3);
        mCustomApp4 = (Preference) prefSet.findPreference(LOCKSCREEN_CUSTOM_4);
        mCustomApp5 = (Preference) prefSet.findPreference(LOCKSCREEN_CUSTOM_5);
        mCustomApp6 = (Preference) prefSet.findPreference(LOCKSCREEN_CUSTOM_6);
        
        mPicker = new ShortcutPickerHelper(this, this);

        mSoundCamera = (ListPreference) findPreference(SOUND_OR_CAMERA);
        mSoundCamera.setOnPreferenceChangeListener(this);
        mSoundCamera.setValue(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_FORCE_SOUND_ICON,
            0) + "");

        int lockScreenCurrent = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0);
        whatLock(lockScreenCurrent);
        
        if (isTablet) {
        	mLockStyle.setEnabled(false);
        	mLockStyle.setSummary("There is only the ICS style for tablets at this time");
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        boolean value;
        if (preference == mLockExtra) {
            value = mLockExtra.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_EXTRA_ICONS, value ? 1 : 0);
            if(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 0 || Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 1 
            		|| Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 5)
                updateCustomAppPickers(value);
            return true;
        } else if (preference == mRotaryArrows) {
            value = mRotaryArrows.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_HIDE_ARROWS, value ? 1 : 0);
            return true;
        } else if (preference == mRotaryDown) {
            value = mRotaryDown.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_ROTARY_UNLOCK_DOWN, value ? 1 : 0);
            return true;
        } else if (preference == mCustomApp1) {
                if ((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 5) || 
                		(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 6)) {
                    final String[] items = getCustomRingAppItems();

                    if (items.length == 0) {
                        mWhichApp = 0;
                        mPicker.pickShortcut();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                        builder.setTitle(R.string.pref_lockscreen_ring_custom_apps_dialog_title_set);
                        builder.setItems(items, new Dialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mWhichApp = which;
                                mPicker.pickShortcut();
                            }
                        });
                        if (items.length < mMaxRingCustomApps) {
                            builder.setPositiveButton(R.string.pref_lockscreen_ring_custom_apps_dialog_add,
                                new Dialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mWhichApp = items.length;
                                    mPicker.pickShortcut();
                                }
                            });
                        }
                        builder.setNeutralButton(R.string.pref_lockscreen_ring_custom_apps_dialog_remove,
                        new Dialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(R.string.pref_lockscreen_ring_custom_apps_dialog_title_unset);
                                builder.setItems(items, new Dialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Settings.System.putString(getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[which], null);
                                    
                                        for (int q = which + 1; q < mMaxRingCustomApps; q++) {
                                            Settings.System.putString(getContentResolver(),
                                            Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[q - 1],
                                            Settings.System.getString(getContentResolver(),
                                            Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[q]));
                                             Settings.System.putString(getContentResolver(),
                                            Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[q], null);
                                         }
                                         mCustomApp1.setSummary(getCustomRingAppSummary());
                                     }
                                });
                                builder.setNegativeButton(R.string.pref_lockscreen_ring_custom_apps_dialog_cancel,
                                new Dialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.setCancelable(true);
                                builder.create().show();
                            }
                        });
                        builder.setNegativeButton(R.string.pref_lockscreen_ring_custom_apps_dialog_cancel,
                            new Dialog.OnClickListener() {
                	        @Override
                	        public void onClick(DialogInterface dialog, int which) {
                		        dialog.dismiss();
                	        }
                        });
                        builder.setCancelable(true);
                        builder.create().show();
                    }
                } else {
                    mCurrentCustomActivityPreference = preference;
                    mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_ONE;
                    mPicker.pickShortcut();
                }
            return true;
        } else if (preference == mCustomApp2) {
            mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_TWO;
            mPicker.pickShortcut();
            return true;
        } else if (preference == mCustomApp3) {
            mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_THREE;
            mPicker.pickShortcut();
            return true;
        } else if (preference == mCustomApp4) {
            mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_FOUR;
            mPicker.pickShortcut();
            return true;
        } else if (preference == mCustomApp5) {
            mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_FIVE;
            mPicker.pickShortcut();
            return true;
        } else if (preference == mCustomApp6) {
            mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_SIX;
            mPicker.pickShortcut();
            return true;
        }
        return false;
    }    

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSoundCamera) {
            Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_FORCE_SOUND_ICON, Integer.parseInt((String) newValue));
            return true;
        } else if (preference == mLockStyle) {
            Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, Integer.parseInt((String) newValue));
            try {
                whatLock(Settings.System.getInt(getContentResolver(), Settings.System.LOCKSCREEN_TYPE));
            } catch (SettingNotFoundException e) {
            }
            return true;
        }
        return false;
    }

    private void updateCustomAppPickers(boolean bool) {
        if (bool) {
            mCustomApp1.setEnabled(true);
            mCustomApp2.setEnabled(true);
            mCustomApp3.setEnabled(true);
            mCustomApp4.setEnabled(true);
            mCustomApp5.setEnabled(true);
            mCustomApp6.setEnabled(true);
        } else {
            mCustomApp1.setEnabled(false);
            mCustomApp2.setEnabled(false);
            mCustomApp3.setEnabled(false);
            mCustomApp4.setEnabled(false);
            mCustomApp5.setEnabled(false);
            mCustomApp6.setEnabled(false);
        }
    }

    private void whatLock(int lock) {
        ArrayList<Preference> lsGen = new ArrayList<Preference>();
        ArrayList<Boolean> lsGenEnable = new ArrayList<Boolean>();
        ArrayList<Preference> lsApp = new ArrayList<Preference>();
        ArrayList<Boolean> lsAppEnable = new ArrayList<Boolean>();

        PreferenceScreen prefSet = getPreferenceScreen();
        prefSet.removeAll();
        prefSet.addPreference(mCategoryGeneral);
        prefSet.addPreference(mCategoryCustom);

        mCategoryGeneral.setTitle(mLockStyle.getEntries()[mLockStyle.
            findIndexOfValue("" + (Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0)))] + " " + "Settings");
        mCategoryCustom.setTitle("Custom Apps");
        
        switch (lock) {
            case 0:
                lsGen.add(mLockStyle);
                lsGenEnable.add(true);
                lsGen.add(mLockExtra);
                lsGenEnable.add(true);
                lsApp.add(mSoundCamera);
                lsAppEnable.add(true);
                lsApp.add(mCustomApp1);
                lsAppEnable.add(mLockExtra.isChecked());
                lsApp.add(mCustomApp2);
                lsAppEnable.add(mLockExtra.isChecked());
                lsApp.add(mCustomApp3);
                lsAppEnable.add(mLockExtra.isChecked());
                if (isTablet) {
                	lsApp.add(mCustomApp4);
                    lsAppEnable.add(mLockExtra.isChecked());
                    lsApp.add(mCustomApp5);
                    lsAppEnable.add(mLockExtra.isChecked());
                    lsApp.add(mCustomApp6);
                    lsAppEnable.add(mLockExtra.isChecked());
                }
                break;
            case 1:
                lsGen.add(mLockStyle);
                lsGenEnable.add(true);
                lsGen.add(mLockExtra);
                lsGenEnable.add(true);               
                lsApp.add(mCustomApp1);
                lsAppEnable.add(mLockExtra.isChecked());
                lsApp.add(mSoundCamera);
                lsAppEnable.add(true);
                break;
            case 2:
                lsGen.add(mLockStyle);
                lsGenEnable.add(true);
                lsGen.add(mRotaryArrows);
                lsGenEnable.add(true);
                lsApp.add(mSoundCamera);
                lsAppEnable.add(true);
                break;
            case 3:
                lsGen.add(mLockStyle);
                lsGenEnable.add(true);
                lsGen.add(mRotaryArrows);
                lsGenEnable.add(true);
                lsGen.add(mRotaryDown);
                lsGenEnable.add(true);
                lsApp.add(mCustomApp1);
                lsAppEnable.add(true);
                lsApp.add(mSoundCamera);
                lsAppEnable.add(true);
                break;
            case 4:
                lsGen.add(mLockStyle);
                lsGenEnable.add(true);
                break;
            case 5:
                lsGen.add(mLockStyle);
                lsGenEnable.add(true);
                lsGen.add(mLockExtra);
                lsGenEnable.add(true);
                lsApp.add(mCustomApp1);
                lsAppEnable.add(mLockExtra.isChecked());
                lsApp.add(mSoundCamera);
                lsAppEnable.add(true);
                break;
            case 6:
                lsGen.add(mLockStyle);
                lsGenEnable.add(true);
                lsApp.add(mCustomApp1);
                lsAppEnable.add(true);
                break;
        }

        mCategoryGeneral.removeAll();
        for (int q = 0; q < lsGen.size(); q++) {
            Preference pref = lsGen.get(q);
            boolean enabled = lsGenEnable.get(q);

            mCategoryGeneral.addPreference(pref);
            pref.setEnabled(enabled);
            if (!enabled && pref instanceof CheckBoxPreference) {
                ((CheckBoxPreference) pref).setChecked(false);
            }
        }

        mCategoryCustom.removeAll();
        for (int q = 0; q < lsApp.size(); q++) {
            Preference pref = lsApp.get(q);
            boolean enabled = lsAppEnable.get(q);

            mCategoryCustom.addPreference(pref);
            pref.setEnabled(enabled);
            if (!enabled && pref instanceof CheckBoxPreference) {
                ((CheckBoxPreference) pref).setChecked(false);
            }
        }

        if (lsApp.size() < 1)
            prefSet.removePreference(mCategoryCustom);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSettings();
    }

    public void refreshSettings() {
        if (Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 5 || 
                Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 6) {
        	mCustomApp1.setSummary(getCustomRingAppSummary());
        } else {
            mCustomApp1.setSummary(mPicker.getFriendlyNameForUri(Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_ONE)));
            mCustomApp2.setSummary(mPicker.getFriendlyNameForUri(Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_TWO)));
            mCustomApp3.setSummary(mPicker.getFriendlyNameForUri(Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_THREE)));
            mCustomApp4.setSummary(mPicker.getFriendlyNameForUri(Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_FOUR)));
            mCustomApp5.setSummary(mPicker.getFriendlyNameForUri(Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_FIVE)));
            mCustomApp6.setSummary(mPicker.getFriendlyNameForUri(Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_SIX)));
        }
    }

    @Override
    public void shortcutPicked(String uri, String friendlyName, boolean isApplication) {
        if (mWhichApp == -1) {
            if (Settings.System.putString(getActivity().getContentResolver(), mCurrentCustomActivityString, uri)) {
                mCurrentCustomActivityPreference.setSummary(friendlyName);
                refreshSettings();
            }
        } else {
            Settings.System.putString(getContentResolver(),
                Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[mWhichApp], uri);
            mCustomApp1.setSummary(getCustomRingAppSummary());
            mWhichApp = -1;
        }
    }

    private String getCustomRingAppSummary() {
        String summary = "";
        String[] items = getCustomRingAppItems();

        for (int q = 0; q < items.length; q++) {
            if (q != 0) {
                summary += ", ";
            }
            summary += items[q];
        }

        return summary;
    }

    private String[] getCustomRingAppItems() {
        ArrayList<String> items = new ArrayList<String>();
        for (int q = 0; q < mMaxRingCustomApps; q++) {
            String uri = Settings.System.getString(getContentResolver(),
                Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[q]);
            if (uri != null) {
                items.add(mPicker.getFriendlyNameForUri(uri));
            }
        }
        return items.toArray(new String[0]);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPicker.onActivityResult(requestCode, resultCode, data);
    }
}
