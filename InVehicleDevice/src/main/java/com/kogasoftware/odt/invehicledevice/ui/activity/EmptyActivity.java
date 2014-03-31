package com.kogasoftware.odt.invehicledevice.ui.activity;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

import android.app.Activity;

/**
 * 単体テスト用の空Activity。テスト対象プロジェクトにテスト用Activityは入れたくないが、Viewなどの単体テスト用の
 * Activityをテスト実行プロジェクト内に配置する良い方法が見つからないため妥協する
 * 
 * ActivityUnitTestCaseを使う方法があるが、アニメーションが動かない
 * Robolectric使う方法があるが、Robotiumと二種類のテストフレームワークを使うのはよくないと考える
 */
public class EmptyActivity extends Activity {
	private Optional<InVehicleDeviceService> service = Optional.absent();

	public void setService(InVehicleDeviceService service) {
		this.service = Optional.of(service);
	}

	public InVehicleDeviceService getService() {
		return service.get();
	}
}
