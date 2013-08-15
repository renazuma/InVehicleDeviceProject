package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;

import android.os.Bundle;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher.OnUpdateOperationListener;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation;

/**
 * Operation.operationScheduleReceiveSequenceを保存しておき、Fragmentの復帰時にOperationが古い場合
 * 自動でOnUpdateOperationコールバックを発生させる
 */
public abstract class AutoUpdateOperationFragment<S extends Serializable>
		extends ApplicationFragment<S> implements OnUpdateOperationListener {
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getService().getEventDispatcher().addOnUpdateOperationListener(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getService().getEventDispatcher().removeOnUpdateOperationListener(this);
	}

	@Override
	public abstract void onUpdateOperation(Operation operation); // 処理は記述しない
}
