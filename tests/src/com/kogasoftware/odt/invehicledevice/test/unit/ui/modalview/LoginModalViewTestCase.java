package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Context;
import android.util.AttributeSet;

import com.kogasoftware.odt.invehicledevice.test.common.MockActivityUnitTestCase;
import com.kogasoftware.odt.invehicledevice.ui.modalview.LoginModalView;

public class LoginModalViewTestCase extends MockActivityUnitTestCase {
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLoginModalView_1() throws Exception {
		Context context = new AccountAuthenticatorActivity();
		AttributeSet attrs = null;

		LoginModalView result = new LoginModalView(context, attrs);

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