package com.kogasoftware.odt.invehicledevice.ui.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.contentprovider.task.SignInErrorBroadcastIntent;
import com.kogasoftware.odt.invehicledevice.service.startupservice.AirplaneModeOnBroadcastIntent;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceService;
import com.kogasoftware.odt.invehicledevice.ui.fragment.NormalVehicleNotificationFragment;
import com.kogasoftware.odt.invehicledevice.ui.fragment.OperationListFragment;
import com.kogasoftware.odt.invehicledevice.ui.fragment.OrderedOperationFragment;
import com.kogasoftware.odt.invehicledevice.ui.fragment.ScheduleVehicleNotificationFragment;
import com.kogasoftware.odt.invehicledevice.ui.fragment.SignInFragment;
import com.kogasoftware.odt.invehicledevice.ui.fragment.VehicleNotificationAlertFragment;
import com.kogasoftware.odt.invehicledevice.utils.Fragments;

/**
 * 全体の大枠。サインイン前はSignInFragmentを表示し、サインイン後は、自治体に依存して「運行予定一覧画面」か「順番に運行を進める画面」を表示する
 */
public class InVehicleDeviceActivity extends Activity {
	public static final Integer VEHICLE_NOTIFICATION_ALERT_DELAY_MILLIS = 5000;
	private static final Integer IN_VEHICLE_DEVICE_LOADER_ID = 1;
	private static final Integer SERVICE_PROVIDER_LOADER_ID = 2;
	private static final Integer NORMAL_VEHICLE_NOTIFICATION_LOADER_ID = 3;
	private static final Integer SCHEDULE_VEHICLE_NOTIFICATION_LOADER_ID = 4;
	private static final String SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_TAG = InVehicleDeviceActivity.class
			+ "/" + ScheduleVehicleNotificationFragment.class;
	private static final String SIGN_IN_FRAGMENT_TAG = InVehicleDeviceActivity.class
			+ "/" + SignInFragment.class;
	private static final String ORDERED_OPERATION_FRAGMENT_TAG = InVehicleDeviceActivity.class
			+ "/" + OrderedOperationFragment.class;
	private static final String VEHICLE_NOTIFICATION_FRAGMENT_TAG = InVehicleDeviceActivity.class
			+ "/" + NormalVehicleNotificationFragment.class + "/%d";
	public static final String OPERATION_LIST_FRAGMENT_TAG = InVehicleDeviceActivity.class
			+ "/" + OperationListFragment.class;
	private static final String VEHICLE_NOTIFICATION_ALERT_FRAGMENT_TAG = InVehicleDeviceActivity.class
			+ "/" + VehicleNotificationAlertFragment.class;
	private static final String AIRPLANE_MODE_ALERT_DIALOG_FRAGMENT_TAG = InVehicleDeviceActivity.class
			+ "/" + AirplaneModeAlertDialogFragment.class;

	public static class AirplaneModeAlertDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setMessage(Html
					.fromHtml("<big><big>機内モードをOFFにしてください</big></big>"));
			builder.setPositiveButton(
					Html.fromHtml("<big><big>確認</big></big>"), null);
			return builder.create();
		}
	}

	private LoaderManager loaderManager;
	private Boolean destroyed = true;
	private Handler handler;
	private ServiceProvider serviceProvider;

	private final BroadcastReceiver signInErrorReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					showLoginFragment();
				}
			});
		}
	};

	private final BroadcastReceiver airplaneModeOnReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					showAirplaneModeAlertDialogFragment();
				}
			});
		}
	};

	private final LoaderCallbacks<Cursor> normalVehicleNotificationLoaderCallbacks = new LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String where = VehicleNotification.Columns.NOTIFICATION_KIND
					+ " = " + VehicleNotification.NotificationKind.NORMAL
					+ " AND " + VehicleNotification.Columns.RESPONSE
					+ " IS NULL";
			return new CursorLoader(InVehicleDeviceActivity.this,
					VehicleNotification.CONTENT.URI, null, where, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (data.getCount() == 0) {
				return;
			}
			final List<VehicleNotification> vehicleNotifications = VehicleNotification
					.getAll(data);
			handler.post(new Runnable() {
				@Override
				public void run() {
					showVehicleNotificationAlertFragment("管理者から連絡があります");
				}
			});
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					showNormalVehicleNotificationsFragment(vehicleNotifications);
				}
			}, VEHICLE_NOTIFICATION_ALERT_DELAY_MILLIS);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	};

	private final LoaderCallbacks<Cursor> scheduleVehicleNotificationLoaderCallbacks = new LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new CursorLoader(
					InVehicleDeviceActivity.this,
					VehicleNotification.CONTENT.URI,
					null,
					VehicleNotification.WHERE_SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_CONTENT,
					null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			if (cursor.getCount() == 0) {
				return;
			}
			handler.post(new Runnable() {
				@Override
				public void run() {
					showVehicleNotificationAlertFragment("運行予定が変更されました");
				}
			});
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					showScheduleVehicleNotificationsFragment();
				}
			}, VEHICLE_NOTIFICATION_ALERT_DELAY_MILLIS);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	};

	private final LoaderCallbacks<Cursor> inVehicleDeviceLoaderCallbacks = new LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new CursorLoader(InVehicleDeviceActivity.this,
					InVehicleDevice.CONTENT.URI, null, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (data.moveToFirst()) {
				return;
			}
			handler.post(new Runnable() {
				@Override
				public void run() {
					showLoginFragment();
				}
			});
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	};

	private final LoaderCallbacks<Cursor> serviceProviderLoaderCallbacks = new LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new CursorLoader(InVehicleDeviceActivity.this,
					ServiceProvider.CONTENT.URI, null, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			Runnable showOperationListFragmentTask = new Runnable() {
				@Override
				public void run() {
					showOperationListFragment();
				}
			};
			Runnable hideOperationListFragmentTask = new Runnable() {
				@Override
				public void run() {
					hideOperationListFragment();
				}
			};
			Runnable showOrderedOperationFragmentTask = new Runnable() {
				@Override
				public void run() {
					showOrderedOperationFragment();
				}
			};
			Runnable hideOrderedOperationFragmentTask = new Runnable() {
				@Override
				public void run() {
					hideOrderedOperationFragment();
				}
			};
			if (cursor.moveToFirst()) {
				serviceProvider = new ServiceProvider(cursor);
				if (serviceProvider.operationListOnly) {
					handler.post(showOperationListFragmentTask);
				} else {
					handler.post(showOrderedOperationFragmentTask);
				}
			} else {
				serviceProvider = null;
				handler.post(hideOrderedOperationFragmentTask);
				handler.post(hideOperationListFragmentTask);
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	};

	private void showOperationListFragment() {
		if (destroyed) {
			return;
		}
		if (getFragmentManager().findFragmentByTag(OPERATION_LIST_FRAGMENT_TAG) != null) {
			return;
		}
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.add(R.id.modal_fragment_container,
				OperationListFragment.newInstance(false),
				OPERATION_LIST_FRAGMENT_TAG);
		fragmentTransaction.commitAllowingStateLoss();
	}

	private void hideOperationListFragment() {
		if (destroyed) {
			return;
		}
		Fragment fragment = getFragmentManager().findFragmentByTag(
				OPERATION_LIST_FRAGMENT_TAG);
		if (fragment == null) {
			return;
		}
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.remove(fragment);
		fragmentTransaction.commitAllowingStateLoss();
	}

	private void showOrderedOperationFragment() {
		if (destroyed) {
			return;
		}
		if (getFragmentManager().findFragmentByTag(
				ORDERED_OPERATION_FRAGMENT_TAG) != null) {
			return;
		}
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.add(R.id.modal_fragment_container,
				OrderedOperationFragment.newInstance(),
				ORDERED_OPERATION_FRAGMENT_TAG);
		fragmentTransaction.commitAllowingStateLoss();
	}

	private void hideOrderedOperationFragment() {
		if (destroyed) {
			return;
		}
		Fragment fragment = getFragmentManager().findFragmentByTag(
				ORDERED_OPERATION_FRAGMENT_TAG);
		if (fragment == null) {
			return;
		}
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.remove(fragment);
		fragmentTransaction.commitAllowingStateLoss();
	}

	public void showLoginFragment() {
		if (destroyed) {
			return;
		}
		if (getFragmentManager().findFragmentByTag(SIGN_IN_FRAGMENT_TAG) != null) {
			return;
		}
		Fragments.showModalFragment(getFragmentManager(),
				SignInFragment.newInstance(), SIGN_IN_FRAGMENT_TAG);
	}

	private void showNormalVehicleNotificationsFragment(
			List<VehicleNotification> VehicleNotifications) {
		if (destroyed) {
			return;
		}
		for (final VehicleNotification vehicleNotification : VehicleNotifications) {
			final String tag = String.format(VEHICLE_NOTIFICATION_FRAGMENT_TAG,
					vehicleNotification.id);
			if (getFragmentManager().findFragmentByTag(tag) != null) {
				return;
			}
			Fragments.showModalFragment(getFragmentManager(),
					NormalVehicleNotificationFragment
							.newInstance(vehicleNotification), tag);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		destroyed = false;
		super.onCreate(savedInstanceState);
		handler = new Handler();
		setContentView(R.layout.in_vehicle_device_activity);
		loaderManager = getLoaderManager();
		registerReceiver(signInErrorReceiver, new IntentFilter(
				SignInErrorBroadcastIntent.ACTION));
		registerReceiver(airplaneModeOnReceiver, new IntentFilter(
				AirplaneModeOnBroadcastIntent.ACTION));
		loaderManager.initLoader(IN_VEHICLE_DEVICE_LOADER_ID, null,
				inVehicleDeviceLoaderCallbacks);
		loaderManager.initLoader(SERVICE_PROVIDER_LOADER_ID, null,
				serviceProviderLoaderCallbacks);
		loaderManager.initLoader(NORMAL_VEHICLE_NOTIFICATION_LOADER_ID, null,
				normalVehicleNotificationLoaderCallbacks);
		loaderManager.initLoader(SCHEDULE_VEHICLE_NOTIFICATION_LOADER_ID, null,
				scheduleVehicleNotificationLoaderCallbacks);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		loaderManager.destroyLoader(IN_VEHICLE_DEVICE_LOADER_ID);
		loaderManager.destroyLoader(SERVICE_PROVIDER_LOADER_ID);
		loaderManager.destroyLoader(NORMAL_VEHICLE_NOTIFICATION_LOADER_ID);
		loaderManager.destroyLoader(SCHEDULE_VEHICLE_NOTIFICATION_LOADER_ID);
		unregisterReceiver(signInErrorReceiver);
		unregisterReceiver(airplaneModeOnReceiver);
		destroyed = true;
	}

	private void showScheduleVehicleNotificationsFragment() {
		if (destroyed
				|| serviceProvider == null
				|| getFragmentManager().findFragmentByTag(
						SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_TAG) != null) {
			return;
		}
		Fragments.showModalFragment(getFragmentManager(),
				ScheduleVehicleNotificationFragment
						.newInstance(!serviceProvider.operationListOnly),
				SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_TAG);
	}

	private void showVehicleNotificationAlertFragment(String message) {
		if (destroyed
				|| serviceProvider == null
				|| getFragmentManager().findFragmentByTag(
						VEHICLE_NOTIFICATION_ALERT_FRAGMENT_TAG) != null) {
			return;
		}
		VoiceService.speak(this, message);
		Fragments.showModalFragment(getFragmentManager(),
				VehicleNotificationAlertFragment.newInstance(),
				VEHICLE_NOTIFICATION_ALERT_FRAGMENT_TAG);
	}

	private void showAirplaneModeAlertDialogFragment() {
		if (destroyed) {
			return;
		}
		FragmentManager fragmentManager = getFragmentManager();
		if (fragmentManager
				.findFragmentByTag(AIRPLANE_MODE_ALERT_DIALOG_FRAGMENT_TAG) == null) {
			AirplaneModeAlertDialogFragment airplaneModeAlertDialogFragment = new AirplaneModeAlertDialogFragment();
			airplaneModeAlertDialogFragment.show(fragmentManager,
					AIRPLANE_MODE_ALERT_DIALOG_FRAGMENT_TAG);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		VoiceService.enable(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		VoiceService.disable(this);
	}
}
