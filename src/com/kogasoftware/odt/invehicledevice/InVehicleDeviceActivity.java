package com.kogasoftware.odt.invehicledevice;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class InVehicleDeviceActivity extends Activity {
	Thread speakAlertThread = new Thread();
	private final BlockingQueue<String> texts = new LinkedBlockingQueue<String>();

	private Button changeStatusButton = null;
	private TextView statusTextView = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invehicledevice);

		changeStatusButton = (Button) findViewById(R.id.changeStatusButton);
		statusTextView = (TextView) findViewById(R.id.statusTextView);

		changeStatusButton.setOnClickListener(new OnClickListener() {
			private Integer status = 0;

			@Override
			public void onClick(View view) {
				if (status.equals(0)) {
					statusTextView.setText("停車中");
					changeStatusButton.setText("出発");
					status = 1;
				} else {
					statusTextView.setText("走行中");
					changeStatusButton.setText("到着");
					status = 0;
				}
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
