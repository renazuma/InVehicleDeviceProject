package com.kogasoftware.odt.invehicledevice.event;

public class SignalStrengthChangedEvent {
	public final Integer signalStrengthPercentage; // 0 - 100

	public SignalStrengthChangedEvent(Integer signalStrengthPercentage) {
		this.signalStrengthPercentage = signalStrengthPercentage;
	}
}
