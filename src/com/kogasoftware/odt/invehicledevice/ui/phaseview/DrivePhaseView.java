package com.kogasoftware.odt.invehicledevice.ui.phaseview;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Platform;

public class DrivePhaseView extends PhaseView {
	private static final int TOGGLE_DRIVING_VIEW_INTERVAL = 5000;

	private final Runnable toggleDrivingView = new Runnable() {
		@Override
		public void run() {
			if (driveView2.getVisibility() == View.VISIBLE) {
				driveView2.setVisibility(View.GONE);
			} else {
				driveView2.setVisibility(View.VISIBLE);
			}
			handler.postDelayed(this, TOGGLE_DRIVING_VIEW_INTERVAL);
		}
	};
	private final TextView nextPlatformNameTextView;
	private final TextView nextPlatformNameRubyTextView;
	private final TextView platformName1BeyondTextView;
	private final TextView platformArrivalTimeTextView;
	private final TextView platformArrivalTimeTextView2;
	private final View driveView1;
	private final View driveView2;
	private final Handler handler = new Handler();

	public DrivePhaseView(Context context, InVehicleDeviceService service) {
		super(context, service);
		setContentView(R.layout.drive_phase_view);

		nextPlatformNameTextView = (TextView) findViewById(R.id.next_platform_name_text_view);
		nextPlatformNameRubyTextView = (TextView) findViewById(R.id.next_platform_name_ruby_text_view);
		platformName1BeyondTextView = (TextView) findViewById(R.id.platform_name_1_beyond_text_view);
		platformArrivalTimeTextView = (TextView) findViewById(R.id.platform_arrival_time_text_view);
		platformArrivalTimeTextView2 = (TextView) findViewById(R.id.platform_arrival_time_text_view2);
		driveView1 = findViewById(R.id.drive_view1);
		driveView2 = findViewById(R.id.drive_view2);

		TypedArray typedArray = getContext().obtainStyledAttributes(
				new int[] { android.R.attr.background });
		Integer backgroundColor = typedArray.getColor(0, Color.WHITE);
		driveView1.setBackgroundColor(backgroundColor); // TODO XMLで指定
		driveView2.setBackgroundColor(backgroundColor); // TODO
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		// handler.post(toggleDrivingView);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		handler.removeCallbacks(toggleDrivingView);
	}

	@Override
	public void onEnterDrivePhase() {
		List<OperationSchedule> operationSchedules = service
				.getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			service.enterFinishPhase();
			return;
		}

		OperationSchedule operationSchedule = operationSchedules.get(0);
		TextView totalPassengerCountTextView = (TextView) findViewById(R.id.total_passenger_count_text_view);
		Integer totalPassengerCount = 0;
		for (PassengerRecord passengerRecord : service.getPassengerRecords()) {
			if (passengerRecord.isRiding()) {
				totalPassengerCount += passengerRecord.getPassengerCount();
			}
		}
		totalPassengerCountTextView.setText(totalPassengerCount + "名乗車中");

		if (!operationSchedule.getPlatform().isPresent()) {
			return; // TODO
		}
		Platform platform = operationSchedule.getPlatform().get();
		nextPlatformNameTextView.setText(platform.getName());
		nextPlatformNameRubyTextView.setText(platform.getNameRuby());

		platformArrivalTimeTextView.setText("");
		platformArrivalTimeTextView2.setText("");
		DateFormat dateFormat = new SimpleDateFormat(getResources().getString(
				R.string.platform_arrival_time_format));
		for (Date arrivalEstimate : operationSchedule.getArrivalEstimate()
				.asSet()) {
			String text = "  " + dateFormat.format(arrivalEstimate);
			platformArrivalTimeTextView.setText(text);
			platformArrivalTimeTextView2.setText(text);
		}

		platformName1BeyondTextView.setText("");
		if (operationSchedules.size() > 1) {
			for (Platform platform1 : operationSchedules.get(1).getPlatform()
					.asSet()) {
				platformName1BeyondTextView.setText("▼ " + platform1.getName());
			}
		}

		service.speak("出発します。次は、" + platform.getNameRuby() + "。"
				+ platform.getNameRuby() + "。");
		setVisibility(View.VISIBLE);
	}
}
