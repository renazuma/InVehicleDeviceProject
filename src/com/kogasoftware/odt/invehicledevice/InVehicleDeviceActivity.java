package com.kogasoftware.odt.invehicledevice;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.gimite.jatts.JapaneseTextToSpeech;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

class SpeakAlertThread2 extends Thread {
	private final JapaneseTextToSpeech tts;
	private final BlockingQueue<String> texts;

	public SpeakAlertThread2(Context context, BlockingQueue<String> texts) {
		this.texts = texts;
		tts = new JapaneseTextToSpeech(context, null);
	}

	@Override
	public void run() {
		try {
			while (true) {
				String text = texts.take();
				while (tts.isSpeaking()) {
					// QUEUE_ADD未対応のため、自前で待つ
					Thread.sleep(200);
				}
				HashMap<String, String> myHashAlarm = new HashMap<String, String>();
				myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
						String.valueOf(AudioManager.STREAM_ALARM));
				tts.speak(text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
			}
		} catch (InterruptedException e) {
		}
	}
}

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
				// if (tts.isSpeaking()) {
				// tts.stop();
				// }
				HashMap<String, String> myHashAlarm = new HashMap();
				myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
						String.valueOf(AudioManager.STREAM_ALARM));
				tts.speak(text, TextToSpeech.QUEUE_ADD, myHashAlarm);
			}
		} catch (InterruptedException e) {
		} finally {
			tts.shutdown();
		}
	}
}

class WebViewSpeaker {
	private final BlockingQueue<String> texts;

	public WebViewSpeaker(BlockingQueue<String> texts) {
		this.texts = texts;
	}

	public void speak(String text) {
		texts.add(text);
	}
}

class WebViewMapLauncher {
	private final Context context;

	public WebViewMapLauncher(Context context) {
		this.context = context;
	}

	public void launch() {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}
}

public class InVehicleDeviceActivity extends Activity {
	Thread speakAlertThread = new Thread();
	private final BlockingQueue<String> texts = new LinkedBlockingQueue<String>();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invehicledevice);

		WebView webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new WebViewClient());
		webView.setWebChromeClient(new WebChromeClient());
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new WebViewSpeaker(texts), "speaker");
		webView.addJavascriptInterface(new WebViewMapLauncher(this),
				"mapLauncher");
		webView.loadUrl("file:///android_asset/default.html");

		speakAlertThread = new SpeakAlertThread2(this, texts);
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
