package com.android.settings.service;

import java.io.File;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.settings.Voltage;
import com.android.settings.VoltageControlActivity;
import com.android.settings.util.CMDProcessor;

public class BootService extends Service {

	static final String TAG = "Liberty Settings Service";
	private static final String CUR_GOV = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
	private static final String MAX_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
	private static final String MIN_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
	private final BootService service = this;
	public static SharedPreferences preferences;
	private Thread bootThread;

	@Override
	public void onStart(Intent intent, int startId) {
		preferences = PreferenceManager.getDefaultSharedPreferences(service);
		super.onStart(intent, startId);    
		Log.i(TAG, "Starting set-on-boot");
		bootThread = new Thread() {
			@Override
			public void run() {
				final CMDProcessor cmd = new CMDProcessor();
				if (preferences.getBoolean("cpu_boot", false)){
					final String max = preferences.getString("max_cpu", null);
					final String min = preferences.getString("min_cpu", null);
					final String gov = preferences.getString("gov", null);
					if (max !=null && min != null && gov != null) {
						cmd.su.runWaitFor("busybox echo " + max + " > " + MAX_FREQ);
						cmd.su.runWaitFor("busybox echo " + min + " > " + MIN_FREQ);
						cmd.su.runWaitFor("busybox echo " + gov + " > " + CUR_GOV);
						if (new File("/sys/devices/system/cpu/cpu1").exists()) {
							cmd.su.runWaitFor("busybox echo " + max + " > " + MAX_FREQ.replace("cpu0", "cpu1"));
							cmd.su.runWaitFor("busybox echo " + min + " > " + MIN_FREQ.replace("cpu0", "cpu1"));
							cmd.su.runWaitFor("busybox echo " + gov + " > " + CUR_GOV.replace("cpu0", "cpu1"));
						}
					}
				}		
				if (preferences.getBoolean("free_memory_boot", false)) {
					final String values = preferences.getString("free_memory", null);
					if (!values.equals(null)) {
						cmd.su.runWaitFor("busybox echo " + values + " > /sys/module/lowmemorykiller/parameters/minfree");
					}
				}
				if (preferences.getBoolean(VoltageControlActivity.KEY_APPLY_BOOT, false)) {
					final List<Voltage> volts = VoltageControlActivity.getVolts(preferences);
					final StringBuilder sb = new StringBuilder();
					String logInfo = "Setting Volts: ";
					for (final Voltage volt : volts) {
						sb.append(volt.getSavedMV() + " ");
						logInfo += volt.getFreq() + "=" + volt.getSavedMV() + " ";
					}
					Log.i(TAG, logInfo);
					new CMDProcessor().su.runWaitFor("busybox echo " + sb.toString() + " > " + VoltageControlActivity.MV_TABLE0);
					if (new File(VoltageControlActivity.MV_TABLE1).exists()) {
						new CMDProcessor().su.runWaitFor("busybox echo " + sb.toString() + " > " + VoltageControlActivity.MV_TABLE1);
					}
				}
			}
		};
		bootThread.start();
		// Stop the service
		stopSelf();
	}

	@Override
	public IBinder onBind(final Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
