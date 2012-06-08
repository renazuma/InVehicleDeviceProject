package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.logic.event.SpeakEvent;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceService;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class VoiceServiceConnector {
	private final Context context;
	private final List<String> voices = new LinkedList<String>();
	private Boolean paused = false;

	public VoiceServiceConnector(Context context) {
		this.context = context.getApplicationContext();
	}

	@Subscribe
	public void pause(InVehicleDeviceActivity.PausedEvent e) {
		paused = true;
	}

	@Subscribe
	public void resume(InVehicleDeviceActivity.ResumedEvent e) {
		paused = false;
		send();
	}

	private void send() {
		Intent intent = new Intent(context, VoiceService.class);
		intent.setAction(VoiceService.ACTION_VOICE);
		intent.putCharSequenceArrayListExtra(VoiceService.MESSAGE_KEY,
				new ArrayList<CharSequence>(voices));
		voices.clear();
		context.startService(intent);
	}

	@Subscribe
	public void add(SpeakEvent e) {
		voices.add(e.message);
		if (paused) {
			return;
		}
		send();
	}
}
