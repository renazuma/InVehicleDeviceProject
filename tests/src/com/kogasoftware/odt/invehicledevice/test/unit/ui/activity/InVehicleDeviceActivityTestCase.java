package com.kogasoftware.odt.invehicledevice.test.unit.ui.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.test.ActivityInstrumentationTestCase2;

import com.google.common.base.Stopwatch;
import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
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

	public void xtest初回のスケジュール受信に失敗した場合の警告() throws Exception {
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

	public void xtest初回のスケジュール受信に失敗した場合の警告_成功した場合表示されない() throws Exception {
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
}
