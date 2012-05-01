package jp.tomorrowkey.android.vtextviewer.test.unit;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.test.common.MockActivityUnitTestCase;

public class VTextViewTestCase extends MockActivityUnitTestCase {
	Solo solo;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void test1() throws Exception {
		fail("stub! / physical test required");
	}
}
