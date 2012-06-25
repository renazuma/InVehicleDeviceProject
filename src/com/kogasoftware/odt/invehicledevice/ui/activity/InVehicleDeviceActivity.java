package com.kogasoftware.odt.invehicledevice.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.WindowManager;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.InVehicleDeviceView;

public class InVehicleDeviceActivity extends Activity implements
		InVehicleDeviceService.OnInitializeListener,
		InVehicleDeviceService.OnExitListener {
	private static final String TAG = InVehicleDeviceActivity.class
			.getSimpleName();

	private static final int WAIT_FOR_INITIALIZE_DIALOG_ID = 10;

	private final ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			InVehicleDeviceService service = ((InVehicleDeviceService.LocalBinder) binder)
					.getService();
			service.addOnInitializeListener(InVehicleDeviceActivity.this);
			service.addOnExitListener(InVehicleDeviceActivity.this);
			optionalService = Optional.of(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			finish();
		}
	};

	private Boolean uiInitialized = false;
	private Optional<InVehicleDeviceService> optionalService = Optional
			.absent();

	protected void enableStrictMode() {
		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyLog().penaltyDeath().build());
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate()");
		// enableStrictMode();
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		getWindow().getDecorView().setBackgroundColor(Color.BLACK);

		bindService(new Intent(this, InVehicleDeviceService.class),
				serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case WAIT_FOR_INITIALIZE_DIALOG_ID: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(Html
					.fromHtml(getString(R.string.operation_schedule_receiving_html)));
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialogInterface) {
					finish();
				}
			});
			return dialog;
		}

		default:
			return null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		unbindService(serviceConnection);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void onExit() {
		finish();
	}

	@Override
	public void onInitialize(InVehicleDeviceService service) {
		Log.w(TAG, "onInitialize");
		try {
			dismissDialog(WAIT_FOR_INITIALIZE_DIALOG_ID);
		} catch (IllegalArgumentException e) {
		}
		if (isFinishing()) {
			return;
		}
		getWindow().getDecorView().setBackgroundColor(Color.WHITE);
		setContentView(new InVehicleDeviceView(this, service));
		uiInitialized = true;
	}
	
	public Boolean isUIInitialized() {
		return uiInitialized;
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			dismissDialog(WAIT_FOR_INITIALIZE_DIALOG_ID);
		} catch (IllegalArgumentException e) {
		}
		if (optionalService.isPresent()) {
			optionalService.get().setActivityPaused();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!isFinishing() && !uiInitialized) {
			showDialog(WAIT_FOR_INITIALIZE_DIALOG_ID);
		}
		if (optionalService.isPresent()) {
			optionalService.get().setActivityResumed();
		}
	}
}
