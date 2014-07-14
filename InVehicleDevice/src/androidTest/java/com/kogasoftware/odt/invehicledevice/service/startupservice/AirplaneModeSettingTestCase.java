package com.kogasoftware.odt.invehicledevice.service.startupservice;

import java.io.IOException;

import android.content.Context;
import android.os.Build;
import android.test.AndroidTestCase;

public class AirplaneModeSettingTestCase extends AndroidTestCase {
	public void test() throws IOException {
		Context c = getContext();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Boolean enable = AirplaneModeSetting.get(c);
			try {
				// 設定を変更しようとするとIOException
				AirplaneModeSetting.set(c, !enable);
				fail();
			} catch (IOException e) {
			}
			// 設定変更が発生しない場合は例外は発生しない
			AirplaneModeSetting.set(c, enable);
		}
	}
}
