package com.kogasoftware.odt.invehicledevice.test.unit.ui.phaseview;

import java.util.concurrent.TimeUnit;

import android.view.View;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.Subscriber;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.ui.modalview.DepartureCheckModalView;
import com.kogasoftware.odt.invehicledevice.ui.phaseview.PlatformPhaseView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;

public class PlatformPhaseViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	StatusAccess sa;
	PlatformPhaseView pv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtil.setDataSource(new DummyDataSource());
		sa = new StatusAccess(getActivity());
		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);
		pv = (PlatformPhaseView) inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_platform_phase_view);
		cl.registerEventListener(pv);
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
		pv.setCommonLogic(new CommonLogicLoadCompleteEvent(cl));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void xtestEnterDrivePhaseEventで非表示() throws Exception {
		xtestEnterPlatformPhaseEventで表示();

		cl.postEvent(new EnterDrivePhaseEvent());
		getInstrumentation().waitForIdleSync();

		assertFalse(pv.isShown());
		assertNotSame(pv.getVisibility(), View.VISIBLE);
	}

	public void xtestEnterFinishPhaseEventで非表示() throws Exception {
		xtestEnterPlatformPhaseEventで表示();

		cl.postEvent(new EnterFinishPhaseEvent());
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

		cl.postEvent(new EnterPlatformPhaseEvent());
		getInstrumentation().waitForIdleSync();

		assertTrue(pv.isShown());
		assertEquals(pv.getVisibility(), View.VISIBLE);
	}
}
