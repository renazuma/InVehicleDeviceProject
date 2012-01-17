package com.kogasoftware.odt.invehicledevice;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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
