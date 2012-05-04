package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.backgroundtask.ExitRequiredPreferenceChangeListener;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
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

	Activity a;
	CommonLogic cl;
	ExitEventWaiter eew;
	ExitRequiredPreferenceChangeListener erpcl;
	SharedPreferences sp;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		eew = new ExitEventWaiter();
		a = getActivity();
		cl = new CommonLogic(a, getActivityHandler());
		cl.registerEventListener(eew);
		erpcl = new ExitRequiredPreferenceChangeListener(cl);
		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

	}

	/**
	 * EXIT_REQUIRED_KEYがtrueの場合、ExitEventを発生させる
	 */
	public void testOnSharedPreferenceChanged1() throws Exception {
		sp.edit()
				.putBoolean(
						ExitRequiredPreferenceChangeListener.EXIT_REQUIRED_SHARED_PREFERENCE_KEY,
						true).commit();
		erpcl.onSharedPreferenceChanged(
				sp,
				ExitRequiredPreferenceChangeListener.EXIT_REQUIRED_SHARED_PREFERENCE_KEY);
		assertTrue(eew.await());
	}

	/**
	 * EXIT_REQUIRED_KEYがfalseの場合、なにもしない
	 */
	public void testOnSharedPreferenceChanged2() throws Exception {
		sp.edit()
				.putBoolean(
						ExitRequiredPreferenceChangeListener.EXIT_REQUIRED_SHARED_PREFERENCE_KEY,
						false).commit();
		erpcl.onSharedPreferenceChanged(
				sp,
				ExitRequiredPreferenceChangeListener.EXIT_REQUIRED_SHARED_PREFERENCE_KEY);
		assertFalse(eew.await());
	}

	/**
	 * 関係ないkeyの場合、EXIT_REQUIRED_KEYがtrueでも何もしない
	 */
	public void testOnSharedPreferenceChanged3() throws Exception {
		sp.edit()
				.putBoolean(
						ExitRequiredPreferenceChangeListener.EXIT_REQUIRED_SHARED_PREFERENCE_KEY,
						true).commit();
		erpcl.onSharedPreferenceChanged(
				sp,
				ExitRequiredPreferenceChangeListener.EXIT_REQUIRED_SHARED_PREFERENCE_KEY
						+ "X");
		assertFalse(eew.await());
	}
}
