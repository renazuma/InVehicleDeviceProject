package com.kogasoftware.odt.invehicledevice.ui.phaseview;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.backgroundtask.VoiceThread.SpeakEvent;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.PassengerRecords;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;

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
	private final TextView platformName2BeyondTextView;
	private final TextView platformName3BeyondTextView;
	private final TextView platformArrivalTimeTextView;
	private final View driveView1;
	private final View driveView2;
	private final Handler handler = new Handler();

	public DrivePhaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.drive_phase_view);

		nextPlatformNameTextView = (TextView) findViewById(R.id.next_platform_name_text_view);
		nextPlatformNameRubyTextView = (TextView) findViewById(R.id.next_platform_name_ruby_text_view);
		platformName1BeyondTextView = (TextView) findViewById(R.id.platform_name_1_beyond_text_view);
		platformName2BeyondTextView = (TextView) findViewById(R.id.platform_name_2_beyond_text_view);
		platformName3BeyondTextView = (TextView) findViewById(R.id.platform_name_3_beyond_text_view);
		platformArrivalTimeTextView = (TextView) findViewById(R.id.platform_arrival_time_text_view);
		driveView1 = findViewById(R.id.drive_view1);
		driveView2 = findViewById(R.id.drive_view2);

		TypedArray typedArray = getContext().obtainStyledAttributes(
				new int[] { android.R.attr.background });
		Integer backgroundColor = typedArray.getColor(0, Color.WHITE);
		driveView1.setBackgroundColor(backgroundColor); // TODO XMLで指定
		driveView2.setBackgroundColor(backgroundColor); // TODO
	}

	@Override
	public void enterDrivePhase(EnterDrivePhaseEvent event) {
		CommonLogic commonLogic = getCommonLogic();
		List<OperationSchedule> operationSchedules = commonLogic
				.getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			commonLogic.postEvent(new EnterFinishPhaseEvent());
			return;
		}

		OperationSchedule operationSchedule = operationSchedules.get(0);
		TextView totalPassengerCountTextView = (TextView) findViewById(R.id.total_passenger_count_text_view);
		Integer totalPassengerCount = 0;
		for (Reservation reservation : commonLogic.getReservations()) {
			for (PassengerRecord passengerRecord : reservation
					.getPassengerRecord().asSet()) {
				if (PassengerRecords.isRiding(passengerRecord)) {
					totalPassengerCount += passengerRecord.getPassengerCount();
				}
			}
		}
		totalPassengerCountTextView.setText(totalPassengerCount + "名乗車中");
		

		if (!operationSchedule.getPlatform().isPresent()) {
			return; // TODO
		}
		Platform platform = operationSchedule.getPlatform().get();
		nextPlatformNameTextView.setText(platform.getName());
		nextPlatformNameRubyTextView.setText(platform.getNameRuby());

		DateFormat dateFormat = new SimpleDateFormat(getResources().getString(
				R.string.platform_arrival_time_format));

		platformArrivalTimeTextView.setText(dateFormat.format(operationSchedule
				.getArrivalEstimate()));

		platformName1BeyondTextView.setText("");
		if (operationSchedules.size() > 1) {
			for (Platform platform1 : operationSchedules.get(1).getPlatform()
					.asSet()) {
				platformName1BeyondTextView.setText("▼ " + platform1.getName());
			}
		}

		platformName2BeyondTextView.setText("");
		if (operationSchedules.size() > 2) {
			for (Platform platform2 : operationSchedules.get(2).getPlatform()
					.asSet()) {
				platformName2BeyondTextView.setText("▼ " + platform2.getName());
			}
		}

		platformName3BeyondTextView.setText("");
		if (operationSchedules.size() > 3) {
			for (Platform platform3 : operationSchedules.get(3).getPlatform()
					.asSet()) {
				platformName3BeyondTextView.setText("▼ " + platform3.getName());
			}
		}

		commonLogic.postEvent(new SpeakEvent("出発します。次は、"
				+ platform.getNameRuby() + "。" + platform.getNameRuby() + "。"));

		setVisibility(View.VISIBLE);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		handler.post(toggleDrivingView);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		handler.removeCallbacks(toggleDrivingView);
	}
}
