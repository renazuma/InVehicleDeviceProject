package com.kogasoftware.odt.invehicledevice.test.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.kogasoftware.odt.invehicledevice.test.R;

public class MockActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mock);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void test() {
		setVisible(true);
		Button b = new Button(this);
		FrameLayout l = (FrameLayout) findViewById(android.R.id.content);
		l.addView(b);
		l.setVisibility(View.VISIBLE);
		b.setVisibility(View.VISIBLE);
		Boolean c = l.isShown();
		Boolean e = b.isShown();
		Boolean d = b.isShown();
	}
}
