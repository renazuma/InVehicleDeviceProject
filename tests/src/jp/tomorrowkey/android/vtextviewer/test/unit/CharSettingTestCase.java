package jp.tomorrowkey.android.vtextviewer.test.unit;

import junit.framework.*;
import jp.tomorrowkey.android.vtextviewer.CharSetting;

public class CharSettingTestCase extends TestCase {
	public void testCharSetting_1()
		throws Exception {
		String charcter = "";
		float angle = -1.0f;
		float x = -1.0f;
		float y = -1.0f;

		CharSetting result = new CharSetting(charcter, angle, x, y);

		assertNotNull(result);
	}

	public void testGetSetting_1()
		throws Exception {
		String character = "";

		CharSetting result = CharSetting.getSetting(character);

		assertNotNull(result);
	}

	public void testIsPunctuationMark_1()
		throws Exception {
		String s = "";

		boolean result = CharSetting.isPunctuationMark(s);

		assertEquals(false, result);
	}

	protected void setUp()
		throws Exception {
		super.setUp();
	}

	protected void tearDown()
		throws Exception {
		super.tearDown();
	}
}