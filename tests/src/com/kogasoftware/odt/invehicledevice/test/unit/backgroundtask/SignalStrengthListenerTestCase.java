package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import android.content.Context;
import android.net.ConnectivityManager;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread.SignalStrengthListener;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;

public class SignalStrengthListenerTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	ConnectivityManager cm;
	DummyDataSource dds;
	SignalStrengthListener ssl;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cm = (ConnectivityManager) getActivity().getSystemService(
				Context.CONNECTIVITY_SERVICE);
		dds = new DummyDataSource();
		TestUtil.setDataSource(dds);
		ssl = new SignalStrengthListener(null);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void xtestOnSignalStrengthsChanged() throws Exception {
		// 引数のクラスがnewできないのでstub
		fail("stub!");
	}
}
