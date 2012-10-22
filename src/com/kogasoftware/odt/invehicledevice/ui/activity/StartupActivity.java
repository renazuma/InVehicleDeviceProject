package com.kogasoftware.odt.invehicledevice.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.service.logservice.LogService;
import com.kogasoftware.odt.invehicledevice.service.startupservice.IStartupService;
import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceService;

public class StartupActivity extends Activity {
	private static final String TAG = StartupActivity.class.getSimpleName();

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			Log.i(TAG, "onServiceConnected");
			IStartupService startupService = IStartupService.Stub
					.asInterface(service);
			try {
				startupService.enable();
			} catch (RemoteException e) {
				Log.w(TAG, e);
			}
			if (!isFinishing()) {
				finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			Log.i(TAG, "onServiceDisconnected");
			if (!isFinishing()) {
				finish();
			}
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(IStartupService.class.getName()),
				serviceConnection, BIND_AUTO_CREATE);
		startService(new Intent(this, StartupService.class));
		startService(new Intent(this, VoiceService.class));
		startService(new Intent(this, LogService.class));
	}

	@Override
	protected void onStop() {
		super.onStop();
		unbindService(serviceConnection);
	}
}
