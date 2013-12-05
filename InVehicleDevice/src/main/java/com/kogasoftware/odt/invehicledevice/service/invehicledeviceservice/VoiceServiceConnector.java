package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Intent;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceService;

public class VoiceServiceConnector implements
		EventDispatcher.OnPauseActivityListener,
		EventDispatcher.OnResumeActivityListener {
	private static final String TAG = VoiceServiceConnector.class.getSimpleName();
	protected final InVehicleDeviceService service;
	protected final BlockingQueue<String> voices = new LinkedBlockingQueue<String>();
	protected Boolean paused = false;
	protected String lastMessage = ""; // 同じメッセージが複数連続で発生することがあるので、診断用

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
		ArrayList<CharSequence> sendVoices = new ArrayList<CharSequence>();
		if (voices.drainTo(sendVoices) == 0) {
			return;
		}
		Intent intent = new Intent(service, VoiceService.class);
		intent.setAction(VoiceService.ACTION_VOICE);
		intent.putCharSequenceArrayListExtra(VoiceService.MESSAGE_KEY,
				sendVoices);
		service.startService(intent);
	}

	public void speak(String message) {
		if (message.equals(lastMessage)) {
			Log.w(TAG, new Exception("Duplicated speak request: " + message));
		}
		lastMessage = message;
		voices.add(message);
		if (paused) {
			return;
		}
		send();
	}
}
