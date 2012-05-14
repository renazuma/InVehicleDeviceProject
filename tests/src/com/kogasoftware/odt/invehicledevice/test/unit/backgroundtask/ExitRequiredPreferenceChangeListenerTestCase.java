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
import com.kogasoftware.odt.invehicledevice.test.util.Subscriber;

public class ExitRequiredPreferenceChangeListenerTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	Subscriber<ExitEvent> s;
	ExitRequiredPreferenceChangeListener erpcl;
	SharedPreferences sp;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		cl = newCommonLogic();
		s = Subscriber.of(ExitEvent.class, cl);
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
		assertTrue(s.cdl.await(3, TimeUnit.SECONDS));
		assertFalse(sp.getBoolean(SharedPreferencesKey.EXIT_REQUIRED, true));
	}

	/**
	 * EXIT_REQUIREDがfalseの場合、なにもしない
	 */
	public void testOnSharedPreferenceChanged2() throws Exception {
		sp.edit().putBoolean(SharedPreferencesKey.EXIT_REQUIRED, false)
				.commit();
		erpcl.onSharedPreferenceChanged(sp, SharedPreferencesKey.EXIT_REQUIRED);
		assertFalse(s.cdl.await(3, TimeUnit.SECONDS));
	}

	/**
	 * 関係ないkeyの場合、EXIT_REQUIREDがtrueでも何もしない
	 */
	public void testOnSharedPreferenceChanged3() throws Exception {
		sp.edit().putBoolean(SharedPreferencesKey.EXIT_REQUIRED, true).commit();
		erpcl.onSharedPreferenceChanged(sp, SharedPreferencesKey.EXIT_REQUIRED
				+ "X");
		assertFalse(s.cdl.await(3, TimeUnit.SECONDS));
	}
}
