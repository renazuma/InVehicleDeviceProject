package com.kogasoftware.odt.invehicledevice.test.unit.logic.empty;

import java.util.concurrent.TimeUnit;

import android.view.View;
import android.view.View.OnClickListener;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyViewOnClickListener;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class EmptyViewOnClickListenerTestCase extends EmptyActivityInstrumentationTestCase2 {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnClick() throws Exception {
		OnClickListener l = new EmptyViewOnClickListener();
		View v = new View(getActivity());
		OnClickListener proxy = (new SimpleTimeLimiter()).newProxy(l,
				OnClickListener.class, 200, TimeUnit.MILLISECONDS);
		proxy.onClick(v);
		// 何もせず即座に制御を返す
	}
}