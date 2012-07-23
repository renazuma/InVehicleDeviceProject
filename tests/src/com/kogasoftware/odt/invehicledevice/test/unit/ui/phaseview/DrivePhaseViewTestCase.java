package com.kogasoftware.odt.invehicledevice.test.unit.ui.phaseview;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.view.View;

import com.google.common.base.Function;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.phaseview.DrivePhaseView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import static org.mockito.Mockito.*;

public class DrivePhaseViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	InVehicleDeviceService s;
	LocalDataSource sa;
	DrivePhaseView pv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		sa = new LocalDataSource(getActivity());
		pv = (DrivePhaseView) inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_drive_phase_view);
		sa.withWriteLock(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(LocalData status) {
				status.phase = Phase.PLATFORM;
				OperationSchedule os1 = new OperationSchedule();
				OperationSchedule os2 = new OperationSchedule();
				os1.setPlatform(new Platform());
				os2.setPlatform(new Platform());
				status.remainingOperationSchedules.clear();
				status.remainingOperationSchedules.add(os1);
				status.remainingOperationSchedules.add(os2);
			}
		});
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEnterDrivePhaseEventで表示() throws Exception {
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				pv.setVisibility(View.GONE);
			}
		});

		cl.postEvent(new EnterDrivePhaseEvent());
		getInstrumentation().waitForIdleSync();

		assertTrue(pv.isShown());
		assertEquals(pv.getVisibility(), View.VISIBLE);
	}

	public void testEnterFinishPhaseEventで非表示() throws Exception {
		testEnterDrivePhaseEventで表示();

		cl.postEvent(new EnterFinishPhaseEvent());
		getInstrumentation().waitForIdleSync();

		assertFalse(pv.isShown());
		assertNotSame(pv.getVisibility(), View.VISIBLE);
	}

	public void testEnterPlatformPhaseEventで非表示() throws Exception {
		testEnterDrivePhaseEventで表示();

		cl.postEvent(new EnterPlatformPhaseEvent());
		getInstrumentation().waitForIdleSync();

		assertFalse(pv.isShown());
		assertNotSame(pv.getVisibility(), View.VISIBLE);
	}

	public void testOperationScheduleが0個の場合EnterFinishPhaseView発生()
			throws Exception {
		sa.write(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(Status status) {
				status.remainingOperationSchedules.clear();
			}
		});
		final CountDownLatch cdl = new CountDownLatch(1);
		cl.registerEventListener(new Function<EnterFinishPhaseEvent, Void>() {
			@Subscribe
			@Override
			public Void apply(EnterFinishPhaseEvent e) {
				cdl.countDown();
				return null;
			}
		});
		cl.postEvent(new EnterDrivePhaseEvent());
		assertTrue(cdl.await(10, TimeUnit.SECONDS));
		assertFalse(pv.isShown());
	}

	public void testOperationScheduleが1個の場合EnterFinishPhaseView発生()
			throws Exception {
		sa.write(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(Status status) {
				OperationSchedule os = new OperationSchedule();
				os.setPlatform(new Platform());
				status.phase = Phase.PLATFORM;
				status.remainingOperationSchedules.clear();
				status.remainingOperationSchedules.add(os);
			}
		});
		final CountDownLatch cdl = new CountDownLatch(1);
		cl.registerEventListener(new Function<EnterFinishPhaseEvent, Void>() {
			@Subscribe
			@Override
			public Void apply(EnterFinishPhaseEvent e) {
				cdl.countDown();
				return null;
			}
		});
		cl.postEvent(new EnterDrivePhaseEvent());
		assertTrue(cdl.await(10, TimeUnit.SECONDS));
		assertFalse(pv.isShown());
	}

	public void testOperationScheduleが2個の場合表示される() {
		sa.write(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(Status status) {
				OperationSchedule os1 = new OperationSchedule();
				OperationSchedule os2 = new OperationSchedule();
				os1.setPlatform(new Platform());
				os2.setPlatform(new Platform());
				status.remainingOperationSchedules.clear();
				status.remainingOperationSchedules.add(os1);
				status.remainingOperationSchedules.add(os2);
			}
		});
		cl.postEvent(new EnterDrivePhaseEvent());
		getInstrumentation().waitForIdleSync();
		assertTrue(pv.isShown());
	}

	public void xtestOperationScheduleが2個の場合次の駅が1つ表示() {
		sa.write(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(Status status) {
				OperationSchedule os1 = new OperationSchedule();
				OperationSchedule os2 = new OperationSchedule();
				os1.setPlatform(new Platform());
				os2.setPlatform(new Platform());
				status.remainingOperationSchedules.clear();
				status.remainingOperationSchedules.add(os1);
				status.remainingOperationSchedules.add(os2);
			}
		});
		cl.postEvent(new EnterDrivePhaseEvent());
		getInstrumentation().waitForIdleSync();
		fail("stub!");
	}

	public void xtestOperationScheduleが2個の場合次の駅が2つ表示() {
		sa.write(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(Status status) {
				OperationSchedule os1 = new OperationSchedule();
				OperationSchedule os2 = new OperationSchedule();
				OperationSchedule os3 = new OperationSchedule();
				os1.setPlatform(new Platform());
				os2.setPlatform(new Platform());
				os3.setPlatform(new Platform());
				status.remainingOperationSchedules.clear();
				status.remainingOperationSchedules.add(os1);
				status.remainingOperationSchedules.add(os2);
				status.remainingOperationSchedules.add(os3);
			}
		});
		cl.postEvent(new EnterDrivePhaseEvent());
		getInstrumentation().waitForIdleSync();
		fail("stub!");
	}

	public void xtestOperationScheduleが2個の場合次の駅が3つ表示() {
		sa.write(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(Status status) {
				OperationSchedule os1 = new OperationSchedule();
				OperationSchedule os2 = new OperationSchedule();
				OperationSchedule os3 = new OperationSchedule();
				OperationSchedule os4 = new OperationSchedule();
				os1.setPlatform(new Platform());
				os2.setPlatform(new Platform());
				os3.setPlatform(new Platform());
				os4.setPlatform(new Platform());
				status.remainingOperationSchedules.clear();
				status.remainingOperationSchedules.add(os1);
				status.remainingOperationSchedules.add(os2);
				status.remainingOperationSchedules.add(os3);
				status.remainingOperationSchedules.add(os4);
			}
		});
		cl.postEvent(new EnterDrivePhaseEvent());
		getInstrumentation().waitForIdleSync();
		fail("stub!");
	}

	public void xtest表示が一定時間ごとに切り替わる() {
		fail("stub!");
	}
}
