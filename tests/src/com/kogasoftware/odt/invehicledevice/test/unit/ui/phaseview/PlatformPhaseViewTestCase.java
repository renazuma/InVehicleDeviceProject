package com.kogasoftware.odt.invehicledevice.test.unit.ui.phaseview;

import android.view.View;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.EmptyActivity;
import com.kogasoftware.odt.invehicledevice.ui.modalview.MemoModalView;
import com.kogasoftware.odt.invehicledevice.ui.phaseview.PlatformPhaseView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import static org.mockito.Mockito.*;

public class PlatformPhaseViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	LocalDataSource sa;
	MemoModalView mmv;
	PlatformPhaseView pv;
	InVehicleDeviceService s;
	EmptyActivity a;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtil.setDataSource(new DummyDataSource());
		a = getActivity();
		s = mock(InVehicleDeviceService.class);
		mmv = mock(MemoModalView.class);
		sa = new LocalDataSource(a);
		pv = new PlatformPhaseView(a, s, mmv);
		sa.withWriteLock(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(LocalData status) {
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

	public void xtestEnterDrivePhaseEventで非表示() throws Exception {
		xtestEnterPlatformPhaseEventで表示();

		s.enterDrivePhase();
		getInstrumentation().waitForIdleSync();

		assertFalse(pv.isShown());
		assertNotSame(pv.getVisibility(), View.VISIBLE);
	}

	public void xtestEnterFinishPhaseEventで非表示() throws Exception {
		xtestEnterPlatformPhaseEventで表示();

		s.enterFinishPhase();
		getInstrumentation().waitForIdleSync();

		assertFalse(pv.isShown());
		assertNotSame(pv.getVisibility(), View.VISIBLE);
	}

	public void xtestEnterPlatformPhaseEventで表示() throws Exception {
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				pv.setVisibility(View.GONE);
			}
		});

		s.enterPlatformPhase();
		getInstrumentation().waitForIdleSync();

		assertTrue(pv.isShown());
		assertEquals(pv.getVisibility(), View.VISIBLE);
	}
}
