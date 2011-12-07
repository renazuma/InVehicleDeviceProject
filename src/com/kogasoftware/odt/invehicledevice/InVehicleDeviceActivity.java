package com.kogasoftware.odt.invehicledevice;

import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

class SpeakAlertThread extends Thread {
	private final TextToSpeech tts;
	private final BlockingQueue<String> texts;

	public SpeakAlertThread(Context context, BlockingQueue<String> texts) {
		this.texts = texts;
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
				String text = texts.take();
				if (tts.isSpeaking()) {
					tts.stop();
				}
				tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
			}
		} catch (InterruptedException e) {
		} finally {
			tts.shutdown();
		}
	}
}

public class InVehicleDeviceActivity extends Activity {
	Thread speakAlertThread = new Thread();
	private final BlockingQueue<String> texts = new LinkedBlockingQueue();
	
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

		speakAlertThread = new SpeakAlertThread(this, texts);
		speakAlertThread.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		speakAlertThread.interrupt();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.string.sample1, 0, R.string.sample1);
		menu.add(0, R.string.sample2, 0, R.string.sample2);
		menu.add(0, R.string.sample3, 0, R.string.sample3);
		menu.add(0, R.string.sample4, 0, R.string.sample4);
		menu.add(0, R.string.sample5, 0, R.string.sample5);
		menu.add(0, R.string.sample6, 0, R.string.sample6);
		menu.add(0, R.string.sample7, 0, R.string.sample7);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.string.sample1:
		case R.string.sample2:
		case R.string.sample3:
		case R.string.sample4:
		case R.string.sample5:
		case R.string.sample6:
		case R.string.sample7:
			Toast.makeText(this, getString(item.getItemId()), Toast.LENGTH_LONG)
					.show();
			texts.add(getString(item.getItemId()));
			break;
		default:
			break;
		}
		return false;
	}
}
