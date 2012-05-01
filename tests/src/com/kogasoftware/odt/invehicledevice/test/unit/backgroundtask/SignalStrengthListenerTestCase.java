package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import android.content.Context;
import android.net.ConnectivityManager;

import com.kogasoftware.odt.invehicledevice.backgroundtask.SignalStrengthListener;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.test.common.MockActivityUnitTestCase;
import com.kogasoftware.odt.invehicledevice.test.common.datasource.DummyDataSource;

public class SignalStrengthListenerTestCase extends MockActivityUnitTestCase {
	CommonLogic cl;
	ConnectivityManager cm;
	DummyDataSource dds;
	SignalStrengthListener ssl;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cm = (ConnectivityManager) getActivity().getSystemService(
				Context.CONNECTIVITY_SERVICE);
		dds = new DummyDataSource();
		DataSourceFactory.setInstance(dds);
		cl = new CommonLogic(getActivity(), getActivityHandler());
		ssl = new SignalStrengthListener(cl, cm);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnSignalStrengthsChanged() throws Exception {
		// 引数のクラスがnewできないのでstub
		fail("stub!");
	}
}
