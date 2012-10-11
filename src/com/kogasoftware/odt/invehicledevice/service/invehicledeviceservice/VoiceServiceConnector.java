package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;

import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceService;

public class VoiceServiceConnector implements
		EventDispatcher.OnPauseActivityListener,
		EventDispatcher.OnResumeActivityListener {
	protected final InVehicleDeviceService service;
	protected final List<String> voices = new LinkedList<String>();
	protected Boolean paused = false;

	public VoiceServiceConnector(InVehicleDeviceService service) {
		this.service = service;
	}

	@Override
	public void onPauseActivity() {
		paused = true;
	}

	@Override
	public void onResumeActivity() {
		paused = false;
		send();
	}

	protected void send() {
		Intent intent = new Intent(service, VoiceService.class);
		intent.setAction(VoiceService.ACTION_VOICE);
		intent.putCharSequenceArrayListExtra(VoiceService.MESSAGE_KEY,
				new ArrayList<CharSequence>(voices));
		voices.clear();
		service.startService(intent);
	}

	public void speak(String message) {
		voices.add(message);
		if (paused) {
			return;
		}
		send();
	}
}
