package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import android.content.Context;
import android.view.LayoutInflater;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.test.R;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestModalView;

public class ModalViewTestCase extends EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	TestModalView mv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		fail("TestModalViewのinflateにAnimationResourceが必要のため失敗する");

		cl = new CommonLogic(getActivity(), getActivityHandler());

		LayoutInflater li = (LayoutInflater) getInstrumentation().getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mv = (TestModalView) li.inflate(R.layout.test_modal_view, null);
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(mv);
			}
		});
		cl.registerEventListener(mv);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void XtestCommonLogicLoadCompleteEventでCommonLogicを取得() {
		assertNotSame(cl, mv.getCommonLogic());
		cl.postEvent(new CommonLogicLoadCompleteEvent(cl));
		getInstrumentation().waitForIdleSync();
		assertEquals(cl, mv.getCommonLogic());
	}
}
