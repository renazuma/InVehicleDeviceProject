package com.kogasoftware.odt.invehicledevice.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.provider.SettingsReflection;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.view.ViewReflection;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.view.ViewReflection.OnSystemUiVisibilityChangeListenerReflection;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher.OnOperationScheduleReceiveFailedListener;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.BigToast;
import com.kogasoftware.odt.invehicledevice.ui.InVehicleDeviceView;

public class InVehicleDeviceActivity extends Activity implements
		EventDispatcher.OnExitListener,
		OnOperationScheduleReceiveFailedListener {
	private static final String TAG = InVehicleDeviceActivity.class
			.getSimpleName();
	private static final int WAIT_FOR_INITIALIZE_DIALOG_ID = 10;
	private static final int WAIT_FOR_INITIALIZE_MILLIS = 1 * 1000;
	public static final int PAUSE_FINISH_TIMEOUT_MILLIS = 10 * 1000;
	public static final int SYSTEM_UI_VISIBILITY_UPDATE_MILLIS = 2 * 1000;
	private final Handler handler = new Handler();
	// Androidエミュレーターで、Activity起動後ESCキーを押してホーム画面に戻ると、Activityが見えていないのに
	// onStopやonDestroyが呼ばれずRunningTaskInfo.topActivityがこのActivityを返すため、自動再起動ができないことがある。
	// そのため、onPauseが呼ばれて一定時間が経ったらtaskを移動してfinishするようにした
	private final Runnable pauseFinishTimeouter = new Runnable() {
		@Override
		public void run() {
			Log.i(TAG, "pauseFinishTimeouter.run()");
			moveTaskToBack(true);
			if (!isFinishing()) {
				finish();
			}
		}
	};
	private final Runnable waitForInitialize = new Runnable() {
		@Override
		public void run() {
			for (InVehicleDeviceService service : optionalService.asSet()) {
				if (service.isOperationInitialized()) {
					onInitialize(service);
					handler.removeCallbacks(this);
					return;
				}
			}
			handler.postDelayed(this, WAIT_FOR_INITIALIZE_MILLIS);
		}
	};

	private final ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			if (!(binder instanceof InVehicleDeviceService.LocalBinder)) {
				Log.e(TAG, "!(" + binder
						+ " instanceof InVehicleDeviceService.LocalBinder)");
				return;
			}
			InVehicleDeviceService service = ((InVehicleDeviceService.LocalBinder) binder)
					.getService();
			service.getEventDispatcher().addOnOperationScheduleReceiveFailedListener(InVehicleDeviceActivity.this);
			service.getEventDispatcher().addOnExitListener(InVehicleDeviceActivity.this);
			optionalService = Optional.of(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			Log.e(TAG, "onServiceDisconnected(" + className + ")");
			optionalService = Optional.absent();
			onExit();
		}
	};

	private final OnSystemUiVisibilityChangeListenerReflection onSystemUiVisibilityChangeListener = new OnSystemUiVisibilityChangeListenerReflection() {
		@Override
		public void onSystemUiVisibilityChange(int visibility) {
			Log.w(TAG, "onSystemUiVisibilityChange(" + visibility + ")");
			for (final Integer SYSTEM_UI_FLAG_LOW_PROFILE : ViewReflection.SYSTEM_UI_FLAG_LOW_PROFILE
					.asSet()) {
				if (SYSTEM_UI_FLAG_LOW_PROFILE.equals(visibility)) {
					return;
				}
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						ViewReflection.setSystemUiVisibility(getWindow()
								.getDecorView(), SYSTEM_UI_FLAG_LOW_PROFILE);
					}
				}, SYSTEM_UI_VISIBILITY_UPDATE_MILLIS);
			}
		}
	};

	private Boolean uiInitialized = false;
	private Optional<InVehicleDeviceService> optionalService = Optional
			.absent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate()");
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		if (!isFinishing()) {
			showDialog(WAIT_FOR_INITIALIZE_DIALOG_ID);
		}
		bindService(new Intent(this, InVehicleDeviceService.class),
				serviceConnection, Context.BIND_AUTO_CREATE);
		handler.post(waitForInitialize);
		ViewReflection.setOnSystemUiVisibilityChangeListener(getWindow()
				.getDecorView(), onSystemUiVisibilityChangeListener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		for (InVehicleDeviceService service : optionalService.asSet()) {
			service.getEventDispatcher().removeOnExitListener(this);
			service.getEventDispatcher().removeOnOperationScheduleReceiveFailedListener(this);
		}
		optionalService = Optional.absent();
		unbindService(serviceConnection);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		handler.removeCallbacks(waitForInitialize);
		handler.removeCallbacks(pauseFinishTimeouter);
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
					if (!isFinishing()) {
						finish();
					}
				}
			});
			return dialog;
		}

		default:
			return null;
		}
	}

	@Override
	public void onExit() {
		handler.postDelayed(pauseFinishTimeouter, PAUSE_FINISH_TIMEOUT_MILLIS);
		if (!isFinishing()) {
			finish();
		}
	}

	public void onInitialize(final InVehicleDeviceService service) {
		Log.i(TAG, "onInitialize()");
		if (isFinishing()) {
			return;
		}

		final View view = new InVehicleDeviceView(this, service);
		view.setBackgroundColor(Color.WHITE);
		view.setVisibility(View.INVISIBLE);
		setContentView(view);

		// データが割り当てられる前の状態の部品を表示させないため、表示を遅延させる。
		handler.post(new Runnable() {
			@Override
			public void run() {
				Animation animation = AnimationUtils.loadAnimation(
						InVehicleDeviceActivity.this,
						R.anim.show_in_vehicle_device_view);
				view.startAnimation(animation);
				view.setVisibility(View.VISIBLE);
				try {
					removeDialog(WAIT_FOR_INITIALIZE_DIALOG_ID);
				} catch (IllegalArgumentException e) {
					Log.w(TAG, e);
				}
			}
		});
		uiInitialized = true;
	}

	public Boolean isUIInitialized() {
		return uiInitialized;
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i(TAG, "onStart()");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i(TAG, "onStop()");
		if (!isFinishing()) {
			finish();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "onResume()");
		handler.removeCallbacks(pauseFinishTimeouter);
		for (InVehicleDeviceService service : optionalService.asSet()) {
			service.getEventDispatcher().dispatchActivityResumed();
		}
		for (final Integer SYSTEM_UI_FLAG_LOW_PROFILE : ViewReflection.SYSTEM_UI_FLAG_LOW_PROFILE
				.asSet()) {
			ViewReflection.setSystemUiVisibility(getWindow().getDecorView(),
					SYSTEM_UI_FLAG_LOW_PROFILE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, "onPause()");
		handler.postDelayed(pauseFinishTimeouter, PAUSE_FINISH_TIMEOUT_MILLIS);
		for (InVehicleDeviceService service : optionalService.asSet()) {
			service.getEventDispatcher().dispatchActivityPaused();
		}
		fixUserRotation();
	}

	/**
	 * USER_ROTATIONを現在の方向に固定する
	 */
	public void fixUserRotation() {
		for (final String USER_ROTATION : SettingsReflection.SystemReflection.USER_ROTATION
				.asSet()) {
			WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			Integer currentRotation = windowManager.getDefaultDisplay()
					.getRotation();
			ContentResolver contentResolver = getContentResolver();
			try {
				Integer configRotation = Settings.System.getInt(
						contentResolver, USER_ROTATION);
				if (currentRotation.equals(configRotation)) {
					Log.i(TAG, "fixUserRotation() currentRotation.equals("
							+ configRotation + ")");
					return;
				}
				Settings.System.putInt(contentResolver, USER_ROTATION,
						currentRotation);
				Log.i(TAG, "fixUserRotation() updated rotation="
						+ currentRotation);
				Settings.System.putInt(contentResolver,
						Settings.System.ACCELEROMETER_ROTATION, 0);
			} catch (SettingNotFoundException e) {
			}
		}
	}

	@Override
	public void onOperationScheduleReceiveFailed() {
		for (InVehicleDeviceService service : optionalService.asSet()) {
			if (!service.isOperationInitialized()) {
				BigToast.makeText(this,
						getString(R.string.failed_to_connect_operator_tool),
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
