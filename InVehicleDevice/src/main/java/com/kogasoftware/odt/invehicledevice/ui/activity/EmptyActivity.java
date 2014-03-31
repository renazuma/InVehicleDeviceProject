package com.kogasoftware.odt.invehicledevice.ui.activity;

import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * 単体テスト用の空Activity。テスト対象プロジェクトにテスト用Activityは入れたくないが、Viewなどの単体テスト用の
 * Activityをテスト実行プロジェクト内に配置する良い方法が見つからないため妥協する
 * 
 * ActivityUnitTestCaseを使う方法があるが、アニメーションが動かない
 * Robolectric使う方法があるが、Robotiumと二種類のテストフレームワークを使うのはよくないと考える
 */
public class EmptyActivity extends Activity {
	private Boolean beforeSaveInstanceState = false;

	@Override
	protected void onResume() {
		super.onResume();
		beforeSaveInstanceState = true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		beforeSaveInstanceState = false;
	}
	
	public Boolean isBeforeSaveInstanceState() {
		return beforeSaveInstanceState;
	}

	private static final String TAG = EmptyActivity.class.getSimpleName();
	public static final AtomicBoolean USE_SAVED_INSTANCE_STATE = new AtomicBoolean(true);
	private Optional<InVehicleDeviceService> service = Optional.absent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		if (USE_SAVED_INSTANCE_STATE.get()) {
			super.onCreate(savedInstanceState);
		} else {
			super.onCreate(null);
			if (savedInstanceState != null) {
				Log.i(TAG, "savedInstanceState ignored: " + savedInstanceState);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
	}

	public void setService(InVehicleDeviceService service) {
		this.service = Optional.of(service);
	}

	public InVehicleDeviceService getService() {
		return service.get();
	}
}
