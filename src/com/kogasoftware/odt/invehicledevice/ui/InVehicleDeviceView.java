package com.kogasoftware.odt.invehicledevice.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ArrivalCheckModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.DepartureCheckModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.MemoModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NavigationModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NotificationModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.PlatformMemoModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ScheduleChangedModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ScheduleModalView;
import com.kogasoftware.odt.invehicledevice.ui.phaseview.DrivePhaseView;
import com.kogasoftware.odt.invehicledevice.ui.phaseview.FinishPhaseView;
import com.kogasoftware.odt.invehicledevice.ui.phaseview.PlatformPhaseView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;

public class InVehicleDeviceView extends FrameLayout implements
		InVehicleDeviceService.OnEnterPhaseListener,
		InVehicleDeviceService.OnAlertUpdatedOperationScheduleListener,
		InVehicleDeviceService.OnAlertVehicleNotificationReceiveListener,
		InVehicleDeviceService.OnChangeSignalStrengthListener {
	private static final String TAG = InVehicleDeviceView.class.getSimpleName();
	private static final int UPDATE_TIME_INTERVAL_MILLIS = 3000;
	private static final int ALERT_SHOW_INTERVAL_MILLIS = 500;
	private static final int PLATFORM_PHASE_COLOR = Color.rgb(0xAA, 0xAA, 0xFF);
	private static final int FINISH_PHASE_COLOR = Color.rgb(0xAA, 0xAA, 0xAA);
	private static final int DRIVE_PHASE_COLOR = Color.rgb(0xAA, 0xFF, 0xAA);
	private final InVehicleDeviceService service;
	private final Button mapButton;
	private final Button scheduleButton;
	private final Button changePhaseButton;
	private final Button platformMemoButton;
	private final ImageView networkStrengthImageView;
	private final ImageView alertImageView;
	private final NavigationModalView navigationModalView;
	private final ScheduleModalView scheduleModalView;
	private final DepartureCheckModalView departureCheckModalView;
	private final ArrivalCheckModalView arrivalCheckModalView;
	private final MemoModalView memoModalView;
	private final PlatformMemoModalView platformMemoModalView;
	private final ScheduleChangedModalView scheduleChangedModalView;
	private final NotificationModalView notificationModalView;
	private final PlatformPhaseView platformPhaseView;
	private final DrivePhaseView drivePhaseView;
	private final FinishPhaseView finishPhaseView;
	private final TextView statusTextView;
	private final TextView presentTimeTextView;
	private final ViewGroup platformMemoButtonLayout;
	
	private final Handler handler = new Handler();

	private final Runnable updateTime = new Runnable() {
		@Override
		public void run() {
			Date now = InVehicleDeviceService.getDate();
			DateFormat f = new SimpleDateFormat(getContext().getString(
					R.string.present_time_format));
			presentTimeTextView.setText(f.format(now));
			handler.postDelayed(this, UPDATE_TIME_INTERVAL_MILLIS);
		}
	};

	private final Runnable alertVehicleNotification = new Runnable() {
		private Integer count = 0;

		@Override
		public void run() {
			if (count > 10) { // TODO 定数
				count = 0;
				alertImageView.setVisibility(View.GONE);
				return;
			}
			count++;
			alertImageView.setVisibility(count % 2 == 0 ? View.VISIBLE
					: View.GONE);
			handler.postDelayed(this, ALERT_SHOW_INTERVAL_MILLIS);
		}
	};

	private final List<View> phaseColoredViews = new LinkedList<View>();

	private InVehicleDeviceView(Context context) {
		super(context);
		throw new RuntimeException("not implemented");
	}

	public InVehicleDeviceView(Context context,
			InVehicleDeviceService localService) {
		super(context);
		this.service = localService;

		LayoutInflater layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.in_vehicle_device, this);

		departureCheckModalView = new DepartureCheckModalView(context, service);
		arrivalCheckModalView = new ArrivalCheckModalView(context, service);
		memoModalView = new MemoModalView(context, service);
		platformMemoModalView = new PlatformMemoModalView(context, service);
		navigationModalView = new NavigationModalView(context, service, platformMemoModalView);
		scheduleModalView = new ScheduleModalView(context, service);
		scheduleChangedModalView = new ScheduleChangedModalView(context,
				service, scheduleModalView);
		notificationModalView = new NotificationModalView(context, service);

		ViewGroup modalViewLayout = (ViewGroup) findViewById(R.id.modal_view_layout);
		modalViewLayout.addView(departureCheckModalView);
		modalViewLayout.addView(arrivalCheckModalView);
		modalViewLayout.addView(memoModalView);
		modalViewLayout.addView(navigationModalView);
		modalViewLayout.addView(platformMemoModalView);
		modalViewLayout.addView(scheduleModalView);
		modalViewLayout.addView(scheduleChangedModalView);
		modalViewLayout.addView(notificationModalView);

		platformPhaseView = new PlatformPhaseView(context, service,
				memoModalView);
		drivePhaseView = new DrivePhaseView(context, service);
		finishPhaseView = new FinishPhaseView(context, service);

		ViewGroup phaseViewLayout = (ViewGroup) findViewById(R.id.phase_view_layout);
		phaseViewLayout.addView(platformPhaseView);
		phaseViewLayout.addView(drivePhaseView);
		phaseViewLayout.addView(finishPhaseView);

		platformMemoButtonLayout = (ViewGroup)findViewById(R.id.platform_memo_button_layout);
		
		presentTimeTextView = (TextView) findViewById(R.id.present_time_text_view);
		statusTextView = (TextView) findViewById(R.id.phase_text_view);
		changePhaseButton = (Button) findViewById(R.id.change_phase_button);
		mapButton = (Button) findViewById(R.id.map_button);
		scheduleButton = (Button) findViewById(R.id.schedule_button);
		networkStrengthImageView = (ImageView) findViewById(R.id.network_strength_image_view);
		alertImageView = (ImageView) findViewById(R.id.alert_image_view);
		platformMemoButton = (Button) findViewById(R.id.platform_memo_button);
		mapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				navigationModalView.show();
			}
		});
		scheduleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				scheduleModalView.show();
			}
		});
		platformMemoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				platformMemoModalView.show();
			}
		});
		for (Integer resourceId : new Integer[] { R.id.icon_layout,
				R.id.operation_phase_layout, R.id.present_time_layout,
				R.id.platform_memo_button_layout, R.id.side_button_view }) {
			View view = findViewById(resourceId);
			if (view != null) {
				phaseColoredViews.add(findViewById(resourceId));
			} else {
				Log.w(TAG, "view != null, resourceId=" + resourceId);
			}
		}

		localService.addOnAlertUpdatedOperationScheduleListener(this);
		localService.addOnAlertVehicleNotificationReceiveListener(this);
		localService.addOnChangeSignalStrengthListener(this);
		localService.addOnEnterPhaseListener(this);
		localService.refreshPhase();

	}

	@Override
	public void onAlertUpdatedOperationSchedule() {
		handler.post(alertVehicleNotification);
	}

	@Override
	public void onAlertVehicleNotificationReceive() {
		handler.post(alertVehicleNotification);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		handler.post(updateTime);
	}

	@Override
	public void onChangeSignalStrength(Integer signalStrengthPercentage) {
		int imageResourceId = R.drawable.network_strength_4;
		if (signalStrengthPercentage.intValue() == 0) {
			imageResourceId = R.drawable.network_strength_0;
		} else if (signalStrengthPercentage.intValue() <= 25) {
			imageResourceId = R.drawable.network_strength_1;
		} else if (signalStrengthPercentage.intValue() <= 50) {
			imageResourceId = R.drawable.network_strength_2;
		} else if (signalStrengthPercentage.intValue() <= 75) {
			imageResourceId = R.drawable.network_strength_3;
		}
		networkStrengthImageView.setImageResource(imageResourceId);
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		handler.removeCallbacks(updateTime);
		handler.removeCallbacks(alertVehicleNotification);
	}

	@Override
	public void onEnterDrivePhase() {
		statusTextView.setText("走行中");
		setPhaseColor(DRIVE_PHASE_COLOR);

		changePhaseButton.setEnabled(true);
		changePhaseButton.setText("到着し\nました");
		changePhaseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				arrivalCheckModalView.show();
			}
		});

		Integer platformMemoVisibility = GONE;
		for (OperationSchedule operationSchedule : service
				.getCurrentOperationSchedule().asSet()) {
			for (Platform platform : operationSchedule.getPlatform().asSet()) {
				if (platform.getMemo().isPresent()) {
					platformMemoVisibility = VISIBLE;	
				}
			}
		}
		platformMemoButtonLayout.setVisibility(platformMemoVisibility);
	}

	@Override
	public void onEnterFinishPhase() {
		statusTextView.setText("");
		changePhaseButton.setEnabled(false);
		setPhaseColor(FINISH_PHASE_COLOR);
	}

	@Override
	public void onEnterPlatformPhase() {
		List<OperationSchedule> operationSchedules = service
				.getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			service.enterFinishPhase();
			return;
		}

		statusTextView.setText("停車中");

		if (operationSchedules.size() > 1) {
			changePhaseButton.setText(" 出発 \n する ");
		} else {
			changePhaseButton.setText(" 確定 \n する ");
		}

		changePhaseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				for (ReservationArrayAdapter adapter : platformPhaseView
						.getReservationArrayAdapter().asSet()) {
					departureCheckModalView.show(adapter);
				}
			}
		});
		changePhaseButton.setEnabled(true);
		setPhaseColor(PLATFORM_PHASE_COLOR); // TODO 定数
	}

	private void setPhaseColor(Integer color) {
		for (View view : phaseColoredViews) {
			view.setBackgroundColor(color);
		}
	}
}
