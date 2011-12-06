package com.kogasoftware.odt.invehicledevice;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class SpeakAlertThread extends Thread {
	private final TextToSpeech tts;

	public SpeakAlertThread(Context context) {
		tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if (status != TextToSpeech.SUCCESS) {
					return;
				}
				Locale locale = Locale.JAPAN;
				if (tts.isLanguageAvailable(locale) < TextToSpeech.LANG_AVAILABLE) {
					return;
				}
				tts.setLanguage(locale);
			}
		});
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(5000);
				if (tts.isSpeaking()) {
					// 読み上げ中なら止める
					tts.stop();
				}
				// 読み上げ開始
				// tts.speak("こんにちは", TextToSpeech.QUEUE_FLUSH, null);
				tts.speak("Hello Android Launch", TextToSpeech.QUEUE_FLUSH, null);
				
			}
		} catch (InterruptedException e) {
		} finally {
			tts.shutdown();
		}
	}
}

public class InVehicleDeviceActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		WebView webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new WebViewClient());
		webView.setWebChromeClient(new WebChromeClient());
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.loadUrl("file:///android_asset/default.html");

		new SpeakAlertThread(this).start();
	}
}
