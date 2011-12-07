package com.kogasoftware.odt.invehicledevice;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

class SpeakAlertThread extends Thread {
	private final TextToSpeech tts;
	private final String text;

	public SpeakAlertThread(Context context, String text) {
		this.text = text;
		tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				try {
					if (status != TextToSpeech.SUCCESS) {
						return;
					}
					Locale locale = Locale.JAPAN;
					if (tts.isLanguageAvailable(locale) < TextToSpeech.LANG_AVAILABLE) {
						return;
					}
					tts.setLanguage(locale);
				} finally {
				}
			}
		});
	}

	@Override
	public void run() {
		try {
			//Thread.sleep(2000);
			// for (int i = 0; i < 10; ++i) {
			if (tts.isSpeaking()) {
				// 読み上げ中なら止める
				tts.stop();
			}
			// 読み上げ開始
			// tts.speak("こんにちは", TextToSpeech.QUEUE_FLUSH, null);
			tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
			// }
			// } catch (InterruptedException e) {
		} catch (IllegalMonitorStateException e) {
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

		new SpeakAlertThread(this, "Hello, World !").start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		openOptionsMenu();
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.string.sample1, 0, R.string.sample1);
		menu.add(0, R.string.sample2, 0, R.string.sample2);
		menu.add(0, R.string.sample3, 0, R.string.sample3);
		menu.add(0, R.string.sample4, 0, R.string.sample4);
		menu.add(0, R.string.sample5, 0, R.string.sample5);
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
			Toast.makeText(this, getString(item.getItemId()), Toast.LENGTH_LONG)
					.show();
			new SpeakAlertThread(this, getString(item.getItemId())).start();
			break;
		default:
			break;
		}
		return false;
	}
}
