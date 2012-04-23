package com.kogasoftware.odt.invehicledevice.logic;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

import com.kogasoftware.odt.invehicledevice.event.SignalStrengthChangedEvent;

public class SignalStrengthListener extends PhoneStateListener {
	private final Logic logic;
	private final ConnectivityManager connectivityManager;

	public SignalStrengthListener(Logic logic,
			ConnectivityManager connectivityManager) {
		this.logic = logic;
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

	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		logic.getEventBus().post(
				new SignalStrengthChangedEvent(
						getSignalStrengthPercentage(signalStrength)));
	};
}
