package com.kogasoftware.odt.invehicledevice.phaseview;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import jp.tomorrowkey.android.vtextviewer.VTextView;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.Logic;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;

public class DrivePhaseView extends PhaseView {
	private static final int TOGGLE_DRIVING_VIEW_INTERVAL = 5000;

	private final Runnable toggleDrivingView = new Runnable() {
		@Override
		public void run() {
			if (drivingView2Layout.getVisibility() == View.VISIBLE) {
				drivingView2Layout.setVisibility(View.GONE);
			} else {
				drivingView2Layout.setVisibility(View.VISIBLE);
			}
			handler.postDelayed(this, TOGGLE_DRIVING_VIEW_INTERVAL);
		}
	};
	private final TextView nextPlatformNameTextView;
	private final TextView nextPlatformNameRubyTextView;
	private final VTextView platformName1BeyondTextView;
	private final VTextView platformName2BeyondTextView;
	private final VTextView platformName3BeyondTextView;
	private final TextView platformArrivalTimeTextView;
	private final View drivingView1Layout;
	private final View drivingView2Layout;
	private final Handler handler = new Handler();

	public DrivePhaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.drive_phase_view);

		nextPlatformNameTextView = (TextView) findViewById(R.id.next_platform_name_text_view);
		nextPlatformNameRubyTextView = (TextView) findViewById(R.id.next_platform_name_ruby_text_view);
		platformName1BeyondTextView = (VTextView) findViewById(R.id.platform_name_1_beyond_text_view);
		platformName2BeyondTextView = (VTextView) findViewById(R.id.platform_name_2_beyond_text_view);
		platformName3BeyondTextView = (VTextView) findViewById(R.id.platform_name_3_beyond_text_view);
		platformArrivalTimeTextView = (TextView) findViewById(R.id.platform_arrival_time_text_view);
		drivingView1Layout = findViewById(R.id.driving_view1);
		drivingView2Layout = findViewById(R.id.driving_view2);

		TypedArray typedArray = getContext().obtainStyledAttributes(
				new int[] { android.R.attr.background });
		Integer backgroundColor = typedArray.getColor(0, Color.WHITE);
		drivingView1Layout.setBackgroundColor(backgroundColor); // TODO XMLで指定
		drivingView2Layout.setBackgroundColor(backgroundColor); // TODO
	}

	@Override
	@Subscribe
	public void enterDrivePhase(EnterDrivePhaseEvent event) {
		Logic logic = getLogic();
		List<OperationSchedule> operationSchedules = logic
				.getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			logic.enterFinishPhase();
			return;
		}

		OperationSchedule operationSchedule = operationSchedules.get(0);
		TextView totalPassengerCountTextView = (TextView) findViewById(R.id.total_passenger_count_text_view);
		Integer totalPassengerCount = 0;
		for (Reservation reservation : logic.getRidingReservations()) {
			totalPassengerCount += reservation.getPassengerCount();
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

		platformName1BeyondTextView.setText(platform.getName());
		platformArrivalTimeTextView.setText(dateFormat.format(operationSchedule
				.getArrivalEstimate()));

		platformName2BeyondTextView.setText("");
		if (operationSchedules.size() > 1) {
			Optional<Platform> optionalPlatform = operationSchedules.get(1)
					.getPlatform();
			if (optionalPlatform.isPresent()) {
				platformName2BeyondTextView.setText(optionalPlatform.get()
						.getName());
			}
		}

		platformName3BeyondTextView.setText("");
		if (operationSchedules.size() > 2) {
			Optional<Platform> optionalPlatform = operationSchedules.get(2)
					.getPlatform();
			if (optionalPlatform.isPresent()) {
				platformName3BeyondTextView.setText(optionalPlatform.get()
						.getName());
			}
		}

		logic.speak("出発します。次は、" + platform.getNameRuby() + "。"
				+ platform.getNameRuby() + "。");

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