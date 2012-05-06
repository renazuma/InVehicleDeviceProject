package com.kogasoftware.odt.invehicledevice.logic.event;

public class TemperatureChangedEvent {
	public final Float celciusTemperature;

	public TemperatureChangedEvent(Float celciusTemperature) {
		this.celciusTemperature = celciusTemperature;
	}
}
