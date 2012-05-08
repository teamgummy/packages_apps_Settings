
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
    private ListPreference mCustomApp1;
    private ListPreference mCustomApp2;
    private ListPreference mCustomApp3;
    private ListPreference mCustomApp4;
    private ListPreference mCustomApp5;
    private ListPreference mCustomApp6;

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

        mCustomApp1 = (ListPreference) findPreference(LOCKSCREEN_CUSTOM_1);
        mCustomApp1.setOnPreferenceChangeListener(this);
        mCustomApp1.setValue(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_ONE,
            0) + "");
        
        mCustomApp2 = (ListPreference) findPreference(LOCKSCREEN_CUSTOM_2);
        mCustomApp2.setOnPreferenceChangeListener(this);
        mCustomApp2.setValue(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_TWO,
            0) + "");
        
        mCustomApp3 = (ListPreference) findPreference(LOCKSCREEN_CUSTOM_3);
        mCustomApp3.setOnPreferenceChangeListener(this);
        mCustomApp3.setValue(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_THREE,
            0) + "");
        
        mCustomApp4 = (ListPreference) findPreference(LOCKSCREEN_CUSTOM_4);
        mCustomApp4.setOnPreferenceChangeListener(this);
        mCustomApp4.setValue(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_FOUR,
            0) + "");
        
        mCustomApp5 = (ListPreference) findPreference(LOCKSCREEN_CUSTOM_5);
        mCustomApp5.setOnPreferenceChangeListener(this);
        mCustomApp5.setValue(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_FIVE,
            0) + "");
        
        mCustomApp6 = (ListPreference) findPreference(LOCKSCREEN_CUSTOM_6);
        mCustomApp6.setOnPreferenceChangeListener(this);
        mCustomApp6.setValue(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_SIX,
            0) + "");
        
        mPicker = new ShortcutPickerHelper(this, this);

        mSoundCamera = (ListPreference) findPreference(SOUND_OR_CAMERA);
        mSoundCamera.setOnPreferenceChangeListener(this);
        mSoundCamera.setValue(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_FORCE_SOUND_ICON,
            0) + "");

        int lockScreenCurrent = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0);
        whatLock(lockScreenCurrent);
        
        refreshSettings();
        
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
            refreshSettings();
            return true;
        } else if (preference == mCustomApp1) {
        	Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_ONE, Integer.parseInt((String) newValue));
        	if (Integer.parseInt((String) newValue) > 0) {
        		if ((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 5) || 
                		(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 6)) {
                	    mCurrentCustomActivityPreference = preference;
                        mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[0];
                        mPicker.pickShortcut();
                    } else {
                        mCurrentCustomActivityPreference = preference;
                        mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_ONE;
                        mPicker.pickShortcut();
                    }
        	} else {
        		if ((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 5) || 
                		(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 6)) {
        			Settings.System.putString(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[0], null);
        		} else {
        			Settings.System.putString(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_ONE, null);
        		}
        		mCustomApp1.setSummary("Blank");
        	}
            return true;
        } else if (preference == mCustomApp2) {
        	Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_TWO, Integer.parseInt((String) newValue));
        	if (Integer.parseInt((String) newValue) > 0) {
        		 if ((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 5) || 
        	        		(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 6)) {
        	        	    mCurrentCustomActivityPreference = preference;
        	                mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[1];
        	                mPicker.pickShortcut();
        	            } else {
        	                mCurrentCustomActivityPreference = preference;
        	                mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_TWO;
        	                mPicker.pickShortcut();
        	            }
        	} else {
        		if ((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 5) || 
                		(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 6)) {
        			Settings.System.putString(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[1], null);
        		} else {
        			Settings.System.putString(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_TWO, null);
        		}
        		mCustomApp2.setSummary("Blank");
        	}
    	   
            return true;
        } else if (preference == mCustomApp3) {
        	Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_THREE, Integer.parseInt((String) newValue));
        	if (Integer.parseInt((String) newValue) > 0) {
        		if ((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 5) || 
                		(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 6)) {
                	    mCurrentCustomActivityPreference = preference;
                        mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[2];
                        mPicker.pickShortcut();
                    } else {
                	    mCurrentCustomActivityPreference = preference;
                        mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_THREE;
                        mPicker.pickShortcut();
                    }
        	} else {
        		if ((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 5) || 
                		(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 6)) {
        			Settings.System.putString(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[2], null);
        		} else {
        			Settings.System.putString(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_THREE, null);
        		}
        		mCustomApp3.setSummary("Blank");
        	}
    	    
            return true;
        } else if (preference == mCustomApp4) {
        	Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_FOUR, Integer.parseInt((String) newValue));
        	if (Integer.parseInt((String) newValue) > 0) {
        		if ((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 5) || 
                		(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 6)) {
                	    mCurrentCustomActivityPreference = preference;
                        mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[3];
                        mPicker.pickShortcut();
                    } else {
                	    mCurrentCustomActivityPreference = preference;
                        mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_FOUR;
                        mPicker.pickShortcut();
                    }
        	} else {
        		if ((Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 5) || 
                		(Settings.System.getInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 0) == 6)) {
        			Settings.System.putString(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[3], null);
        		} else {
        			Settings.System.putString(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_FOUR, null);
        		}
        		mCustomApp4.setSummary("Blank");
        	}
            return true;
        } else if (preference == mCustomApp5) {
        	Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_FIVE, Integer.parseInt((String) newValue));
        	if (Integer.parseInt((String) newValue) > 0) {
                mCurrentCustomActivityPreference = preference;
                mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_FIVE;
                mPicker.pickShortcut();
        	} else {
        		mCustomApp5.setSummary("Blank");
        		Settings.System.putString(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_FIVE, null);
        	}
            return true;
        } else if (preference == mCustomApp6) {
        	Settings.System.putInt(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_SIX, Integer.parseInt((String) newValue));
        	if (Integer.parseInt((String) newValue) > 0) {
        		mCurrentCustomActivityPreference = preference;
                mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_SIX;
                mPicker.pickShortcut();
        	} else {
        		mCustomApp6.setSummary("Blank");
        		Settings.System.putString(getActivity().getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_SIX, null);
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
                lsApp.add(mCustomApp2);
                lsAppEnable.add(mLockExtra.isChecked());
                lsApp.add(mCustomApp3);
                lsAppEnable.add(mLockExtra.isChecked());
                lsApp.add(mCustomApp4);
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
        	mCustomApp1.setSummary(mPicker.getFriendlyNameForUri(Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[0])));
            mCustomApp2.setSummary(mPicker.getFriendlyNameForUri(Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[1])));
            mCustomApp3.setSummary(mPicker.getFriendlyNameForUri(Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[2])));
            mCustomApp4.setSummary(mPicker.getFriendlyNameForUri(Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[3])));
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
