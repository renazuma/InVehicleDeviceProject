package com.kogasoftware.odt.invehicledevice.test.unit.ui.arrayadapter;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Context;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.ReservationArrayAdapter;

public class ReservationArrayAdapterTestCase extends EmptyActivityInstrumentationTestCase2 {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testReservationArrayAdapter_1() throws Exception {
		Context context = new AccountAuthenticatorActivity();
		int resourceId = 0;
		CommonLogic commonLogic = new CommonLogic();

		ReservationArrayAdapter result = new ReservationArrayAdapter(context,
				resourceId, commonLogic);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.RuntimeException: Stub!
		// at android.content.Context.<init>(Context.java:4)
		// at android.content.ContextWrapper.<init>(ContextWrapper.java:5)
		// at
		// android.view.ContextThemeWrapper.<init>(ContextThemeWrapper.java:5)
		// at android.app.Activity.<init>(Activity.java:6)
		// at
		// android.accounts.AccountAuthenticatorActivity.<init>(AccountAuthenticatorActivity.java:5)
		assertNotNull(result);
	}
}