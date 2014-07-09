package com.kogasoftware.odt.invehicledevice.service.startupservice;

import java.io.IOException;

import android.content.Context;
import android.os.Build;
import android.test.AndroidTestCase;

public class AirplaneModeSettingTestCase extends AndroidTestCase {
	public void test() throws IOException {
		Context c = getContext();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			// 正しく設定変更が反映される
			AirplaneModeSetting.set(c, false);
			assertFalse(AirplaneModeSetting.get(c));
			AirplaneModeSetting.set(c, true);
			assertTrue(AirplaneModeSetting.get(c));
			AirplaneModeSetting.set(c, false);
			assertFalse(AirplaneModeSetting.get(c));
		} else {
			// 設定変更がある場合IOException
			Boolean enable = AirplaneModeSetting.get(c);
			AirplaneModeSetting.set(c, enable);
			try {
				AirplaneModeSetting.set(c, !enable);
				fail();
			} catch (IOException e) {
			}
		}
	}
}
