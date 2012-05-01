package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.test.R;
import com.kogasoftware.odt.invehicledevice.test.common.MockActivityUnitTestCase;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ConfigModalView;

public class ConfigModalViewTestCase extends MockActivityUnitTestCase {
	CommonLogic cl;
	ConfigModalView cmv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = new CommonLogic(getActivity(), getActivityHandler());

		AttributeSet attributeSet = Xml.asAttributeSet(getActivity()
				.getResources().getXml(R.style.default_modal_view_style));
		cmv = new ConfigModalView(getActivity(), attributeSet);
		cmv.setCommonLogic(new CommonLogicLoadCompleteEvent(cl));
		cl.getEventBus().register(cmv);
		getActivity().setContentView(cmv);
		getActivity().setVisible(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cl.dispose();
	}

	public void testShowEvent() throws InterruptedException {
		cmv.setVisibility(View.GONE);
		cl.getEventBus().post(new ConfigModalView.ShowEvent());
		loop(500);
		cmv.setVisibility(View.VISIBLE);
		assertEquals(cmv.getVisibility(), View.VISIBLE);
		assertTrue(cmv.isShown());
	}

	public void test中止ボタンを押すとStopCheckModalView_ShowEvent通知() throws Exception {
		fail("stub!");
	}

	public void test停止ボタンを押すとPauseModalView_ShowEvent通知() throws Exception {
		fail("stub!");
	}

	public void test戻るボタンを押すと消える() throws Exception {
		fail("stub!");
	}
}