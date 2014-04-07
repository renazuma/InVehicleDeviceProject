package com.kogasoftware.odt.invehicledevice.ui.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Surface;
import android.view.WindowManager;

import com.google.common.base.Stopwatch;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.EmptyInVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.provider.SettingsReflection;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;
import com.robotium.solo.Solo;

public class InVehicleDeviceActivityTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	Solo solo;

	public InVehicleDeviceActivityTestCase() {
		super(InVehicleDeviceActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getInstrumentation()
						.getTargetContext());
		assertTrue(sp.edit()
				.putBoolean(SharedPreferencesKeys.INITIALIZED, true).commit());
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			if (solo != null) {
				solo.finishOpenedActivities();
			}
		} finally {
			super.tearDown();
		}
	}

	public void testAlertInitialOperationScheduleReceiveFailed()
			throws Exception {
		String message = getInstrumentation().getTargetContext().getString(
				R.string.failed_to_connect_operator_tool);
		TestUtil.clearLocalStorage(getInstrumentation());
		TestUtil.setApiClient(new EmptyInVehicleDeviceApiClient() {
			@Override
			public int getOperationSchedules(
					ApiClientCallback<List<OperationSchedule>> callback) {
				callback.onFailed(0, 500, "");
				return 0;
			}
		});

		solo = new Solo(getInstrumentation(), getActivity());
		assertTrue(solo.waitForText(message, 1, 5000, false, true));
		while (solo.waitForText(message, 1, 5000, false, true)) {
		}
	}

	public void xtestNoAlertSecondOperationScheduleReceiveFailed()
			throws Exception { // TODO: 自動起動テストができるようになったら再実装
		String message = getInstrumentation().getTargetContext().getString(
				R.string.failed_to_connect_operator_tool);
		TestUtil.clearLocalStorage(getInstrumentation());
		final AtomicBoolean fail = new AtomicBoolean(false);
		TestUtil.setApiClient(new EmptyInVehicleDeviceApiClient() {
			@Override
			public int getServiceProvider(
					ApiClientCallback<ServiceProvider> callback) {
				callback.onSucceed(0, 200, new ServiceProvider());
				return 0;
			}

			@Override
			public int getOperationSchedules(
					ApiClientCallback<List<OperationSchedule>> callback) {
				if (fail.get()) {
					callback.onFailed(0, 500, "");
				} else {
					// 初回は成功する
					callback.onSucceed(0, 200,
							new LinkedList<OperationSchedule>());
				}
				return 0;
			}
		});
		InVehicleDeviceActivity a = getActivity();
		solo = new Solo(getInstrumentation(), a);
		assertTrue(TestUtil.waitForStartUI(a));
		a.finish(); // 終了する
		fail.set(true);

		// Activityの再起動を待つ
		Thread.sleep((int) (StartupService.CHECK_DEVICE_INTERVAL_MILLIS));
		TestUtil.assertShow(getInstrumentation().getTargetContext(),
				InVehicleDeviceActivity.class);

		Stopwatch sw = new Stopwatch().start();
		while (solo.waitForText(message)) {
			Thread.sleep(10);
			if (sw.elapsed(TimeUnit.SECONDS) > 5) {
				break;
			}
		}
	}

	public void xtestNoAlertInitialOperationScheduleReceiveSucceed()
			throws Exception {
		String message = getInstrumentation().getTargetContext().getString(
				R.string.failed_to_connect_operator_tool);
		TestUtil.clearLocalStorage(getInstrumentation());
		TestUtil.setApiClient(new EmptyInVehicleDeviceApiClient() {
			@Override
			public int getOperationSchedules(
					ApiClientCallback<List<OperationSchedule>> callback) {
				callback.onSucceed(0, 200, new LinkedList<OperationSchedule>());
				return 0;
			}
		});

		solo = new Solo(getInstrumentation(), getActivity());
		assertFalse(solo.waitForText(message));
	}

	public void testFixUserRotation_0() throws Throwable {
		assertFixUserRotation(Surface.ROTATION_0);
	}

	public void testFixUserRotation_90() throws Throwable {
		assertFixUserRotation(Surface.ROTATION_90);
	}

	public void testFixUserRotation_180() throws Throwable {
		assertFixUserRotation(Surface.ROTATION_180);
	}

	public void testFixUserRotation_270() throws Throwable {
		assertFixUserRotation(Surface.ROTATION_270);
	}

	public void assertFixUserRotation(Integer request) throws Throwable {
		for (String USER_ROTATION : SettingsReflection.SystemReflection.USER_ROTATION
				.asSet()) {
			final ContentResolver cr = getInstrumentation().getTargetContext()
					.getContentResolver();
			assertTrue(Settings.System.putInt(cr, USER_ROTATION, request));
			Thread.sleep(3 * 1000);
			final Activity a = getActivity();
			TestUtil.assertShow(a, a.getClass());
			final AtomicInteger changed = new AtomicInteger(-1);
			Thread.sleep(3 * 1000);
			runTestOnUiThread(new Runnable() {
				@Override
				public void run() {
					changed.set(((WindowManager) a
							.getSystemService(Context.WINDOW_SERVICE))
							.getDefaultDisplay().getOrientation());
				}
			});
			Thread.sleep(3 * 1000);
			a.finish();
			TestUtil.assertHide(a, a.getClass());
			Integer after = Settings.System.getInt(a.getContentResolver(),
					USER_ROTATION);
			assertEquals(changed.get(), after.intValue());
			if (TestUtil.isDefaultLandscape(a)) {
				assertTrue(Surface.ROTATION_0 == changed.get()
						|| Surface.ROTATION_180 == changed.get());
			} else {
				assertTrue(Surface.ROTATION_90 == changed.get()
						|| Surface.ROTATION_270 == changed.get());
			}
		}
	}

	public void testNotInitialized() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getInstrumentation()
						.getTargetContext());
		assertTrue(sp.edit()
				.putBoolean(SharedPreferencesKeys.INITIALIZED, false).commit());
		solo = new Solo(getInstrumentation(), getActivity());
		assertTrue(solo.waitForText(solo
				.getString(R.string.settings_are_not_initialized)));
	}
}
