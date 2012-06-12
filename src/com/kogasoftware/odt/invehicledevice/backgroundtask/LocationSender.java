package com.kogasoftware.odt.invehicledevice.backgroundtask;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;

public class LocationSender implements Runnable {

	private static final String TAG = LocationSender.class.getSimpleName();
	private final CommonLogic commonLogic;

	public LocationSender(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	/**
	 * 現在のServiceUnitStatusLogをサーバーへ送信。
	 * ただしserviceUnitStatusLogLocationEnabledがfalseの場合は送信しない
	 */
	@Override
	public void run() {
		final ServiceUnitStatusLog serviceUnitStatusLog = commonLogic
				.getServiceUnitStatusLog();

		commonLogic.getDataSource().sendServiceUnitStatusLog(
				serviceUnitStatusLog,
				new WebAPICallback<ServiceUnitStatusLog>() {
					@Override
					public void onException(int reqkey, WebAPIException ex) {
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
					}

					@Override
					public void onSucceed(int reqkey, int statusCode,
							ServiceUnitStatusLog result) {
					}
				});
	}
}
