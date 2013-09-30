package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;

import android.os.Bundle;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher.OnUpdateOperationListener;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundReader;

/**
 * Operation.operationScheduleReceiveSequenceを保存しておき、Fragmentの復帰時にOperationが古い場合
 * 自動でOnUpdateOperationコールバックを発生させる
 */
public abstract class AutoUpdateOperationFragment<S extends Serializable>
		extends ApplicationFragment<S> implements OnUpdateOperationListener {
	private Boolean updateOperationReady = false;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getService().getEventDispatcher().addOnUpdateOperationListener(this);
		updateOperationReady = true;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getService().getLocalStorage().read(
				new BackgroundReader<Optional<Operation>>() {
					@Override
					public Optional<Operation> readInBackground(
							LocalData localData) {
						if (getOperationSchedulesReceiveSequence()
								.equals(localData.operation.operationScheduleReceiveSequence)) {
							return Optional.absent();
						} else {
							return Optional.of(localData.operation);
						}
					}

					@Override
					public void onRead(Optional<Operation> result) {
						for (Operation operation : result.asSet()) {
							if (updateOperationReady) {
								onUpdateOperation(operation);
							}
						}
					}
				});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getService().getEventDispatcher().removeOnUpdateOperationListener(this);
		updateOperationReady = false;
	}

	@Override
	public abstract void onUpdateOperation(Operation operation); // 処理は記述しない

	protected abstract Integer getOperationSchedulesReceiveSequence(); // 現在のシーケンスを取得
}
