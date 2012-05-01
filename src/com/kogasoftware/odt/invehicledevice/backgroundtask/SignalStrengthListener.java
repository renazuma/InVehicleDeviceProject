package com.kogasoftware.odt.invehicledevice.backgroundtask;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.SignalStrengthChangedEvent;

public class SignalStrengthListener extends PhoneStateListener {
	private final CommonLogic commonLogic;
	private final ConnectivityManager connectivityManager;

	public SignalStrengthListener(CommonLogic commonLogic,
			ConnectivityManager connectivityManager) {
		this.commonLogic = commonLogic;
		this.connectivityManager = connectivityManager;
	}

	private Integer getSignalStrengthPercentage(SignalStrength signalStrength) {
		NetworkInfo networkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (!networkInfo.isAvailable()) {
			return 0;
		}
		if (signalStrength.isGsm()) {
			Integer value = signalStrength.getGsmSignalStrength();
			if (value == 99 || value <= 2) {
				return 0;
			} else if (value <= 4) {
				return 25;
			} else if (value <= 7) {
				return 50;
			} else if (value <= 11) {
				return 75;
			}
			return 100;
		}
		return 0;
	}

	/**
	 * SignalStrengthから電波状況を判断しSignalStrengthChangedEventを送信する
	 */
	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		commonLogic.getEventBus().post(
				new SignalStrengthChangedEvent(
						getSignalStrengthPercentage(signalStrength)));
	};
}
