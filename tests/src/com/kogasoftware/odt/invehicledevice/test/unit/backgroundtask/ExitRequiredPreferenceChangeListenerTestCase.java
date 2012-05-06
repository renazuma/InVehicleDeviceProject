package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.backgroundtask.ExitRequiredPreferenceChangeListener;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.SharedPreferencesKey;
import com.kogasoftware.odt.invehicledevice.logic.event.ExitEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class ExitRequiredPreferenceChangeListenerTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	static class ExitEventWaiter {
		CountDownLatch cdl = new CountDownLatch(1);

		public Boolean await() throws InterruptedException {
			return cdl.await(5, TimeUnit.SECONDS);
		}

		@Subscribe
		public void exit(ExitEvent e) {
			cdl.countDown();
		}
	}

	CommonLogic cl;
	ExitEventWaiter eew;
	ExitRequiredPreferenceChangeListener erpcl;
	SharedPreferences sp;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		eew = new ExitEventWaiter();
		cl = newCommonLogic();
		cl.registerEventListener(eew);
		erpcl = new ExitRequiredPreferenceChangeListener(cl);
		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

	}

	@Override
	public void tearDown() throws Exception {
		if (cl != null) {
			cl.dispose();
		}
		super.tearDown();
	}

	/**
	 * EXIT_REQUIREDがtrueの場合、ExitEventを発生させる
	 */
	public void testOnSharedPreferenceChanged1() throws Exception {
		sp.edit().putBoolean(SharedPreferencesKey.EXIT_REQUIRED, true).commit();
		erpcl.onSharedPreferenceChanged(sp, SharedPreferencesKey.EXIT_REQUIRED);
		assertTrue(eew.await());
	}

	/**
	 * EXIT_REQUIREDがfalseの場合、なにもしない
	 */
	public void testOnSharedPreferenceChanged2() throws Exception {
		sp.edit().putBoolean(SharedPreferencesKey.EXIT_REQUIRED, false)
				.commit();
		erpcl.onSharedPreferenceChanged(sp, SharedPreferencesKey.EXIT_REQUIRED);
		assertFalse(eew.await());
	}

	/**
	 * 関係ないkeyの場合、EXIT_REQUIREDがtrueでも何もしない
	 */
	public void testOnSharedPreferenceChanged3() throws Exception {
		sp.edit().putBoolean(SharedPreferencesKey.EXIT_REQUIRED, true).commit();
		erpcl.onSharedPreferenceChanged(sp, SharedPreferencesKey.EXIT_REQUIRED
				+ "X");
		assertFalse(eew.await());
	}
}
