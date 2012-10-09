package com.kogasoftware.odt.invehicledevice.test.unit.ui.activity;

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
import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.provider.SettingsReflection;
import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.ServiceProvider;

public class InVehicleDeviceActivityTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	Solo solo;

	public InVehicleDeviceActivityTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
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

	public void test初回のスケジュール受信に失敗した場合の警告() throws Exception {
		String message = getInstrumentation().getTargetContext().getString(
				R.string.failed_to_connect_operator_tool);
		TestUtil.disableAutoStart(getInstrumentation().getContext());
		TestUtil.clearStatus();
		TestUtil.setDataSource(new EmptyDataSource() {
			@Override
			public int getOperationSchedules(
					WebAPICallback<List<OperationSchedule>> callback) {
				callback.onFailed(0, 500, "");
				return 0;
			}
		});

		solo = new Solo(getInstrumentation(), getActivity());
		assertTrue(solo.waitForText(message));
		while (solo.waitForText(message)) {
		}
	}

	public void test初回のスケジュール受信に失敗した場合の警告_初回でない場合は表示されない() throws Exception {
		String message = getInstrumentation().getTargetContext().getString(
				R.string.failed_to_connect_operator_tool);
		TestUtil.disableAutoStart(getInstrumentation().getContext());
		TestUtil.clearStatus();
		final AtomicBoolean fail = new AtomicBoolean(false);
		TestUtil.setDataSource(new EmptyDataSource() {
			@Override
			public int getServiceProvider(
					WebAPICallback<ServiceProvider> callback) {
				callback.onSucceed(0, 200, new ServiceProvider());
				return 0;
			}

			@Override
			public int getOperationSchedules(
					WebAPICallback<List<OperationSchedule>> callback) {
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
		TestUtil.enableAutoStart(getInstrumentation().getContext());
		Thread.sleep((int) (StartupService.CHECK_DEVICE_INTERVAL_MILLIS));
		TestUtil.assertShow(getInstrumentation().getTargetContext(),
				InVehicleDeviceActivity.class);

		Stopwatch sw = new Stopwatch().start();
		while (solo.waitForText(message)) {
			Thread.sleep(10);
			if (sw.elapsedTime(TimeUnit.SECONDS) > 5) {
				break;
			}
		}
	}

	public void test初回のスケジュール受信に失敗した場合の警告_成功した場合表示されない() throws Exception {
		String message = getInstrumentation().getTargetContext().getString(
				R.string.failed_to_connect_operator_tool);
		TestUtil.disableAutoStart(getInstrumentation().getContext());
		TestUtil.clearStatus();
		TestUtil.setDataSource(new EmptyDataSource() {
			@Override
			public int getOperationSchedules(
					WebAPICallback<List<OperationSchedule>> callback) {
				callback.onSucceed(0, 200, new LinkedList<OperationSchedule>());
				return 0;
			}
		});

		solo = new Solo(getInstrumentation(), getActivity());
		assertFalse(solo.waitForText(message));
	}

	public void testFixUserRotation_0() throws Exception {
		assertFixUserRotation(Surface.ROTATION_0);
	}

	public void testFixUserRotation_90() throws Exception {
		assertFixUserRotation(Surface.ROTATION_90);
	}

	public void testFixUserRotation_180() throws Exception {
		assertFixUserRotation(Surface.ROTATION_180);
	}

	public void testFixUserRotation_270() throws Exception {
		assertFixUserRotation(Surface.ROTATION_270);
	}

	protected Boolean isDefaultLandscape() {
		return true;
	}

	public void assertFixUserRotation(Integer request) throws Exception {
		TestUtil.disableAutoStart(getInstrumentation().getContext());
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
			TestUtil.runOnUiThreadSync(a, new Runnable() {
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
			if (isDefaultLandscape()) {
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
		assertTrue(solo.waitForText(solo.getString(R.string.settings_are_not_initialized)));
	}
}
