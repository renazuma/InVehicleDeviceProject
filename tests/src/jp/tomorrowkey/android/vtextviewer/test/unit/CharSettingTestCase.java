package jp.tomorrowkey.android.vtextviewer.test.unit;

import jp.tomorrowkey.android.vtextviewer.CharSetting;
import junit.framework.TestCase;

public class CharSettingTestCase extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCharSetting_1() throws Exception {
		String charcter = "";
		float angle = -1.0f;
		float x = -1.0f;
		float y = -1.0f;

		CharSetting result = new CharSetting(charcter, angle, x, y);

		assertNotNull(result);
	}

	public void testGetSetting_1() throws Exception {
		String character = "";

		CharSetting result = CharSetting.getSetting(character);

		assertNotNull(result);
	}
}