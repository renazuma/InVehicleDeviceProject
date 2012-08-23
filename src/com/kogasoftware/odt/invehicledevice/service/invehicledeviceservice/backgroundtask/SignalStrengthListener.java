package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

/**
 * onSignalStrengthsChangedを受け取り、現在の電波状況を100分率で表した数値をサービスへ通知
 */
public class SignalStrengthListener extends PhoneStateListener {
	private final InVehicleDeviceService service;

	public SignalStrengthListener(InVehicleDeviceService service) {
		this.service = service;
	}

	/**
	 * SignalStrengthを受け取り、現在の電波状況を100分率で表した数値を取得。数値取得に失敗した場合Optional.absent()
	 * を返す
	 */
	public static Optional<Integer> convertSignalStrengthToPercentage(
			SignalStrength signalStrength) {
		// GSMでない場合は失敗
		if (!signalStrength.isGsm()) {
			return Optional.absent();
		}
		
		// GSMの場合は値を100分率に置き換え
		Integer gsmSignalStrength = signalStrength.getGsmSignalStrength();
		if (gsmSignalStrength.equals(99) || gsmSignalStrength <= 2) {
			return Optional.of(0);
		} else if (gsmSignalStrength <= 4) {
			return Optional.of(25);
		} else if (gsmSignalStrength <= 7) {
			return Optional.of(50);
		} else if (gsmSignalStrength <= 11) {
			return Optional.of(75);
		}
		return Optional.of(100);
	}

	/**
	 * SignalStrenghを受け取り、現在の電波状況を100分率で表した数値への変換に成功した場合サービスに通知
	 */
	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		for (Integer percentage : convertSignalStrengthToPercentage(
				signalStrength).asSet()) {
			service.changeSignalStrength(percentage);
		}
	};
}
