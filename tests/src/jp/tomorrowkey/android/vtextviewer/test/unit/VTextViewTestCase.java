package jp.tomorrowkey.android.vtextviewer.test.unit;

import jp.tomorrowkey.android.vtextviewer.VTextView;

import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class VTextViewTestCase extends EmptyActivityInstrumentationTestCase2 {
	VTextView vtv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		vtv = (VTextView) inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_vtext_view);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test1() throws Exception {
		vtv.setText("吾輩は猫である。名前はまだ無い。");
		getInstrumentation().waitForIdleSync();
		Thread.sleep(500);
	}

	public void test2() throws Exception {
		fail("stub! / physical test required");
	}
}
