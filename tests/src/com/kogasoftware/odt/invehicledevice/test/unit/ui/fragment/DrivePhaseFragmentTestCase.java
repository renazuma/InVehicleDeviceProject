package com.kogasoftware.odt.invehicledevice.test.unit.ui.fragment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.view.View;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;

public class DrivePhaseFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	InVehicleDeviceService s;
	LocalDataSource sa;
	DrivePhaseFragment pv;
	OperationScheduleLogic osl;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		when(s.getEventDispatcher()).thenReturn(new EventDispatcher());
		osl = new OperationScheduleLogic(s);
		sa = new LocalDataSource(getActivity());
		pv = new DrivePhaseFragment(null, s);
		sa.withWriteLock(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(LocalData status) {
				status.phase = Phase.PLATFORM;
				OperationSchedule os1 = new OperationSchedule();
				OperationSchedule os2 = new OperationSchedule();
				os1.setPlatform(new Platform());
				os2.setPlatform(new Platform());
				status.operationSchedules.clear();
				status.operationSchedules.add(os1);
				status.operationSchedules.add(os2);
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

		osl.enterDrivePhase();
		getInstrumentation().waitForIdleSync();

		assertTrue(pv.isShown());
		assertEquals(pv.getVisibility(), View.VISIBLE);
	}

	public void testEnterFinishPhaseで非表示() throws Exception {
		testEnterDrivePhaseEventで表示();

		osl.enterFinishPhase();
		getInstrumentation().waitForIdleSync();

		assertFalse(pv.isShown());
		assertNotSame(pv.getVisibility(), View.VISIBLE);
	}

	public void testEnterPlatformPhaseで非表示() throws Exception {
		testEnterDrivePhaseEventで表示();

		osl.enterPlatformPhase();
		getInstrumentation().waitForIdleSync();

		assertFalse(pv.isShown());
		assertNotSame(pv.getVisibility(), View.VISIBLE);
	}

	public void testOperationScheduleが0個の場合EnterFinishPhaseView発生()
			throws Exception {
		sa.withWriteLock(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(LocalData status) {
				status.operationSchedules.clear();
			}
		});
		final CountDownLatch cdl = new CountDownLatch(1);
		osl.enterDrivePhase();
		assertTrue(cdl.await(10, TimeUnit.SECONDS));
		assertFalse(pv.isShown());
	}

	public void testOperationScheduleが1個の場合EnterFinishPhaseView発生()
			throws Exception {
		sa.withWriteLock(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(LocalData status) {
				OperationSchedule os = new OperationSchedule();
				os.setPlatform(new Platform());
				status.phase = Phase.PLATFORM;
				status.operationSchedules.clear();
				status.operationSchedules.add(os);
			}
		});
		osl.enterDrivePhase();
		assertFalse(pv.isShown());
	}

	public void testOperationScheduleが2個の場合表示される() {
		sa.withWriteLock(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(LocalData status) {
				OperationSchedule os1 = new OperationSchedule();
				OperationSchedule os2 = new OperationSchedule();
				os1.setPlatform(new Platform());
				os2.setPlatform(new Platform());
				status.operationSchedules.clear();
				status.operationSchedules.add(os1);
				status.operationSchedules.add(os2);
			}
		});
		osl.enterDrivePhase();
		getInstrumentation().waitForIdleSync();
		assertTrue(pv.isShown());
	}

	public void xtestOperationScheduleが2個の場合次の駅が1つ表示() {
		sa.withWriteLock(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(LocalData status) {
				OperationSchedule os1 = new OperationSchedule();
				OperationSchedule os2 = new OperationSchedule();
				os1.setPlatform(new Platform());
				os2.setPlatform(new Platform());
				status.operationSchedules.clear();
				status.operationSchedules.add(os1);
				status.operationSchedules.add(os2);
			}
		});
		osl.enterDrivePhase();
		getInstrumentation().waitForIdleSync();
		fail("stub!");
	}

	public void xtestOperationScheduleが2個の場合次の駅が2つ表示() {
		sa.withWriteLock(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(LocalData status) {
				OperationSchedule os1 = new OperationSchedule();
				OperationSchedule os2 = new OperationSchedule();
				OperationSchedule os3 = new OperationSchedule();
				os1.setPlatform(new Platform());
				os2.setPlatform(new Platform());
				os3.setPlatform(new Platform());
				status.operationSchedules.clear();
				status.operationSchedules.add(os1);
				status.operationSchedules.add(os2);
				status.operationSchedules.add(os3);
			}
		});
		osl.enterDrivePhase();
		getInstrumentation().waitForIdleSync();
		fail("stub!");
	}

	public void xtestOperationScheduleが2個の場合次の駅が3つ表示() {
		sa.withWriteLock(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(LocalData status) {
				OperationSchedule os1 = new OperationSchedule();
				OperationSchedule os2 = new OperationSchedule();
				OperationSchedule os3 = new OperationSchedule();
				OperationSchedule os4 = new OperationSchedule();
				os1.setPlatform(new Platform());
				os2.setPlatform(new Platform());
				os3.setPlatform(new Platform());
				os4.setPlatform(new Platform());
				status.operationSchedules.clear();
				status.operationSchedules.add(os1);
				status.operationSchedules.add(os2);
				status.operationSchedules.add(os3);
				status.operationSchedules.add(os4);
			}
		});
		osl.enterDrivePhase();
		getInstrumentation().waitForIdleSync();
		fail("stub!");
	}

	public void xtest表示が一定時間ごとに切り替わる() {
		fail("stub!");
	}
}
