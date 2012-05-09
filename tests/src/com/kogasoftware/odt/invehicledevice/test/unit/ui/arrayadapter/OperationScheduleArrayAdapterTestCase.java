package com.kogasoftware.odt.invehicledevice.test.unit.ui.arrayadapter;

import java.util.ArrayList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;

public class OperationScheduleArrayAdapterTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	OperationScheduleArrayAdapter osaa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = newCommonLogic();
		osaa = new OperationScheduleArrayAdapter(getInstrumentation()
				.getTargetContext(), new ArrayList<OperationSchedule>(), cl);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void testOperationScheduleのPlatformが表示される() throws Exception {
		String platformName0 = "上野駅前";
		String platformName1 = "御徒町駅前";
		List<OperationSchedule> oss = new ArrayList<OperationSchedule>();
		{
			OperationSchedule os = new OperationSchedule();
			Platform p = new Platform();
			p.setName(platformName0);
			os.setPlatform(p);
			oss.add(os);
		}
		{
			OperationSchedule os = new OperationSchedule();
			Platform p = new Platform();
			p.setName(platformName1);
			os.setPlatform(p);
			oss.add(os);
		}
		
		osaa = new OperationScheduleArrayAdapter(getInstrumentation()
				.getTargetContext(), oss, cl);
		
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(osaa.getView(0, null, null));
			}
		});
		assertTrue(solo.searchText(platformName0));
		
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(osaa.getView(1, null, null));
			}
		});
		assertTrue(solo.searchText(platformName1));
	}

	public void xtestOperationScheduleが変更されたら変更後の表示になる() {
		fail("stub!");
	}

	public void xtest最後のOperationScheduleの出発時刻は表示されない() {
		fail("stub!");
	}

	public void xtest最初のOperationScheduleの到着時刻は表示されない() {
		fail("stub!");
	}

	public void xtest終了したOperationScheduleは表示色が変更される() {
		fail("stub!");
	}
}
