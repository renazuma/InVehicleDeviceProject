package com.kogasoftware.odt.invehicledevice.test.unit.ui.arrayadapter;

import java.util.ArrayList;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Context;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class OperationScheduleArrayAdapterTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOperationScheduleArrayAdapter_1() throws Exception {
		Context context = new AccountAuthenticatorActivity();
		int resourceId = 0;
		ArrayList<OperationSchedule> items = new ArrayList<OperationSchedule>();
		items.add(new OperationSchedule());
		CommonLogic commonLogic = new CommonLogic();

		OperationScheduleArrayAdapter result = new OperationScheduleArrayAdapter(
				context, resourceId, items, commonLogic);

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