package com.kogasoftware.odt.invehicledevice.map;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.kogasoftware.odt.invehicledevice.R;

public class MainActivity extends MapActivity {

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @Override アクティビティ生成時に呼び出される
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		final NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
		final MapController mapController = navigationView.getMapView()
				.getController();

		// mapOnTouchListener = new MapOnTouchListener(mapView, this);
		Button backButton = (Button) findViewById(R.id.backButton);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				MainActivity.this.finish();
			}
		});

		// final MapController mapController = mapView.getController();
		final Button zoomInButton = (Button) findViewById(R.id.zoomInButton);
		final Button zoomOutButton = (Button) findViewById(R.id.zoomOutButton);

		zoomInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mapController.zoomIn()) {
					zoomInButton.setEnabled(false);
				}
				zoomOutButton.setEnabled(true);
			}
		});

		zoomOutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mapController.zoomOut()) {
					zoomOutButton.setEnabled(false);
				}
				zoomInButton.setEnabled(true);
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
