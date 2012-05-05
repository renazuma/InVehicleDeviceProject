package com.kogasoftware.odt.invehicledevice.test.unit.ui.phaseview;

import android.view.View;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.phaseview.DrivePhaseView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;

public class DrivePhaseViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	DrivePhaseView pv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = new CommonLogic(getActivity(), getActivityHandler());
		pv = (DrivePhaseView) inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_drive_phase_view);
		cl.registerEventListener(pv);
		cl.getStatusAccess().write(new Writer() { // TODO もっとスマートにする
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
}
