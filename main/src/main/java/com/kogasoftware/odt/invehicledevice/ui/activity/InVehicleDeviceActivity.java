package com.kogasoftware.odt.invehicledevice.ui.activity;

import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.provider.SettingsReflection;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.view.ViewReflection;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.view.ViewReflection.OnSystemUiVisibilityChangeListenerReflection;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.ui.BigToast;
import com.kogasoftware.odt.invehicledevice.ui.fragment.InVehicleDeviceFragment;
import com.kogasoftware.odt.invehicledevice.ui.fragment.OperationScheduleChangedAlertFragment;
import com.kogasoftware.odt.invehicledevice.ui.fragment.VehicleNotificationAlertFragment;

public class InVehicleDeviceActivity extends FragmentActivity implements
		EventDispatcher.OnExitListener,
		EventDispatcher.OnOperationScheduleReceiveFailListener,
		EventDispatcher.OnUpdatePhaseListener,
		EventDispatcher.OnAlertVehicleNotificationReceiveListener,
		EventDispatcher.OnMergeOperationSchedulesListener {
	private static final String TAG = InVehicleDeviceActivity.class
			.getSimpleName();
	public static final int ALERT_SHOW_INTERVAL_MILLIS = 500;
	public static final int PAUSE_FINISH_TIMEOUT_MILLIS = 10 * 1000;
	public static final int SYSTEM_UI_VISIBILITY_UPDATE_MILLIS = 2 * 1000;
	public static final String LOADING_DIALOG_FRAGMENT_TAG = "LoadingDialogFragmentTag";
	private final Handler handler = new Handler();

	/**
	 * Androidエミュレーターで、Activity起動後ESCキーを押してホーム画面に戻ると、Activityが見えていないのに
	 * onStopやonDestroyが呼ばれずRunningTaskInfo
	 * .topActivityがこのActivityを返すため、自動再起動ができないことがある。
	 * そのため、onPauseが呼ばれて一定時間が経ったらtaskを移動してfinishするようにした
	 */
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

	/**
	 * InVehicleDeviceServiceとの接続
	 */
	private final ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			if (!(binder instanceof InVehicleDeviceService.LocalBinder)) {
				Log.e(TAG, "!(" + binder
						+ " instanceof InVehicleDeviceService.LocalBinder)");
				return;
			}
			if (isFinishing() || destroyed) {
				Log.w(TAG, "onServiceConnected(" + className + "," + binder
						+ ") but finishing");
				return;
			}
			Log.i(TAG, "onServiceConnected()");
			InVehicleDeviceService service = ((InVehicleDeviceService.LocalBinder) binder)
					.getService();
			service.getEventDispatcher().addOnUpdatePhaseListener(
					InVehicleDeviceActivity.this);
			service.getEventDispatcher()
					.addOnOperationScheduleReceiveFailedListener(
							InVehicleDeviceActivity.this);
			service.getEventDispatcher().addOnExitListener(
					InVehicleDeviceActivity.this);
			service.getEventDispatcher()
					.addOnAlertVehicleNotificationReceiveListener(
							InVehicleDeviceActivity.this);
			service.getEventDispatcher().addOnMergeOperationSchedulesListener(
					InVehicleDeviceActivity.this);
			new OperationScheduleLogic(service).requestUpdatePhase();
			InVehicleDeviceActivity.this.service = Optional.of(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			Log.e(TAG, "onServiceDisconnected(" + className + ")");
			onExit();
		}
	};

	/**
	 * SystemUiVisibilityが変更されたら、元に戻す
	 */
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

	/**
	 * 読み込み中ダイアログ
	 */
	public static class LoadingDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			ProgressDialog dialog = new ProgressDialog(getActivity());
			dialog.setMessage(Html
					.fromHtml(getString(R.string.operation_schedule_receiving_html)));
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					InVehicleDeviceActivity activity = (InVehicleDeviceActivity) getActivity();
					if (!activity.isUiInitialized()) {
						Log.i(TAG, "LoadingDialogFragment.onKey / finish");
						activity.finish();
					} else {
						Log.i(TAG, "LoadingDialogFragment.onKey");
					}
					return false;
				}
			});
			return dialog;
		}
	}

	private Optional<LoadingDialogFragment> loadingDialogFragment = Optional
			.absent();

	private Boolean uiInitialized = false;

	@Deprecated
	public Boolean isUiInitialized() {
		return uiInitialized;
	}

	private Optional<InVehicleDeviceService> service = Optional.absent();
	private View view;
	private Boolean destroyed;

	public Optional<InVehicleDeviceService> getService() {
		return service;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate()");
		destroyed = false;
		
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		loadingDialogFragment = Optional.of(new LoadingDialogFragment());
		for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
			loadingDialogFragment.get().show(fragmentManager,
					LOADING_DIALOG_FRAGMENT_TAG);
		}
		bindService(new Intent(this, InVehicleDeviceService.class),
				serviceConnection, Context.BIND_AUTO_CREATE);
		ViewReflection.setOnSystemUiVisibilityChangeListener(getWindow()
				.getDecorView(), onSystemUiVisibilityChangeListener);

		view = getLayoutInflater().inflate(R.layout.in_vehicle_device_activity,
				null);
		view.setBackgroundColor(Color.WHITE);
		view.setVisibility(View.INVISIBLE);
		setContentView(view);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		destroyed = true;
		dismissLoadingDialogFragment();
		for (InVehicleDeviceService service : getService().asSet()) {
			service.getEventDispatcher().removeOnExitListener(this);
			service.getEventDispatcher()
					.removeOnOperationScheduleReceiveFailedListener(this);
			service.getEventDispatcher()
					.removeOnAlertVehicleNotificationReceiveListener(this);
			service.getEventDispatcher()
					.removeOnMergeOperationSchedulesListener(this);
		}
		unbindService(serviceConnection);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		handler.removeCallbacks(pauseFinishTimeouter);
	}

	@Override
	public void onExit() {
		Log.i(TAG, "onExit()");
		handler.postDelayed(pauseFinishTimeouter, PAUSE_FINISH_TIMEOUT_MILLIS);
		if (!isFinishing()) {
			finish();
		}
	}

	public void initializeUi(Phase phase,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		if (uiInitialized || destroyed || isFinishing()) {
			return;
		}
		Log.i(TAG, "initializeUi()");

		for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.add(R.id.modal_fragment_container,
					InVehicleDeviceFragment.newInstance(phase, operationSchedules,
							passengerRecords));
			fragmentTransaction.commitAllowingStateLoss();
		}

		uiInitialized = true;

		// データが割り当てられる前の状態の部品を表示させないため、表示を遅延させる。
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (destroyed) {
					return;
				}
				Animation animation = AnimationUtils.loadAnimation(
						InVehicleDeviceActivity.this,
						R.anim.show_in_vehicle_device_view);
				view.startAnimation(animation);
				view.setVisibility(View.VISIBLE);
				dismissLoadingDialogFragment();
			}
		});
	}
	
	private void dismissLoadingDialogFragment() {
		for (DialogFragment dialogFragment : loadingDialogFragment.asSet()) {
			try {
				if (dialogFragment != null && dialogFragment.isAdded()) {
					try {
						// ここでNullPointerExceptionが発生することがあるのを無理やりキャッチしてログに出力
						dialogFragment.dismiss();
					} catch (NullPointerException e) {
						Log.e(TAG, e.toString(), e);
					}
				} else {
					Log.e(TAG, "Unexpected state: dialogFragment == null");
				}
			} catch (IllegalStateException e) {
				Log.e(TAG, e.toString(), e);
			}
		}
		loadingDialogFragment = Optional.absent();
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
		for (InVehicleDeviceService service : getService().asSet()) {
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
		for (InVehicleDeviceService service : getService().asSet()) {
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
	public void onOperationScheduleReceiveFail() {
		for (final InVehicleDeviceService service : getService().asSet()) {
			new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... params) {
					return service.isOperationInitialized();
				}

				@Override
				protected void onPostExecute(Boolean operationInitialized) {
					if (!operationInitialized) {
						BigToast.makeText(
								InVehicleDeviceActivity.this,
								getString(R.string.failed_to_connect_operator_tool),
								Toast.LENGTH_LONG).show();
					}
				}
			}.execute();
		}
	}

	@Override
	public void onUpdatePhase(Phase phase,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		initializeUi(phase, operationSchedules, passengerRecords);
	}

	@Override
	public void onAlertVehicleNotificationReceive(
			final List<VehicleNotification> vehicleNotifications) {
		for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.add(R.id.modal_fragment_container,
					VehicleNotificationAlertFragment
							.newInstance(vehicleNotifications));
			fragmentTransaction.commitAllowingStateLoss();
		}		
	}

	@Override
	public void onMergeOperationSchedules(
			List<VehicleNotification> triggerVehicleNotifications) {
		if (triggerVehicleNotifications.isEmpty()) {
			// BigToast.makeText(this, "[debug]: OperationScheduleがマージされた",
			// Toast.LENGTH_LONG).show();
			return;
		}
		for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.add(R.id.modal_fragment_container,
					OperationScheduleChangedAlertFragment
							.newInstance(triggerVehicleNotifications));
			fragmentTransaction.commitAllowingStateLoss();
		}
	}

	public Optional<FragmentManager> getOptionalFragmentManager() {
		return Optional.fromNullable(getSupportFragmentManager());
	}
}
