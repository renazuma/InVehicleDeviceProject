package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceUnitStatusLogs;
import com.kogasoftware.odt.invehicledevice.ui.BatteryAlerter;
import com.kogasoftware.odt.invehicledevice.ui.BgColorTransitionDrawable;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.utils.ViewDisabler;

public class InformationBarFragment
		extends
			OperationSchedulesAndPassengerRecordsFragment {
	private static final int UPDATE_TIME_INTERVAL_MILLIS = 3000;
	private static final int OPEATION_SCHEDULES_LOADER_ID = 1;
	private static final int PASSENGER_RECORDS_LOADER_ID = 2;

	private BgColorTransitionDrawable bgColorTransitionDrawable;
	private ImageView networkStrengthImageView;
	private TextView presentTimeTextView;
	private Handler handler;

	LoaderCallbacks<Cursor> serviceUnitStatusLogsLoaderCallbacks = new LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String order = ServiceUnitStatusLogs.Columns.CREATED_AT + " DESC";
			return new CursorLoader(getActivity(),
					ServiceUnitStatusLogs.CONTENT.URI, null, null, null, order);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			if (!cursor.moveToFirst()) {
				return;
			}
			Integer signalStrengthPercentage = cursor
					.getInt(cursor
							.getColumnIndexOrThrow(ServiceUnitStatusLogs.Columns.SIGNAL_STRENGTH));
			int imageResourceId = R.drawable.network_strength_4;
			if (signalStrengthPercentage.equals(0)) {
				imageResourceId = R.drawable.network_strength_0;
			} else if (signalStrengthPercentage <= 25) {
				imageResourceId = R.drawable.network_strength_1;
			} else if (signalStrengthPercentage <= 50) {
				imageResourceId = R.drawable.network_strength_2;
			} else if (signalStrengthPercentage <= 75) {
				imageResourceId = R.drawable.network_strength_3;
			}
			networkStrengthImageView.setImageResource(imageResourceId);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	};

	public static InformationBarFragment newInstance() {
		InformationBarFragment fragment = new InformationBarFragment();
		return fragment;
	}

	private final Runnable updateTime = new Runnable() {
		@Override
		public void run() {
			Date now = new Date(DateTimeUtils.currentTimeMillis());
			DateFormat f = new SimpleDateFormat(
					getString(R.string.present_time_format), Locale.US);
			presentTimeTextView.setText(f.format(now));
			handler.postDelayed(this, UPDATE_TIME_INTERVAL_MILLIS);
		}
	};

	/**
	 * バッテリー警告ダイアログ
	 */
	public static class BatteryAlertDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setMessage(Html
					.fromHtml("<big><big>充電ケーブルを接続してください</big></big>"));
			builder.setPositiveButton(
					Html.fromHtml("<big><big>確認</big></big>"), null);
			return builder.create();
		}
	}

	/**
	 * バッテリー状態を監視
	 */
	private Runnable blinkBatteryAlert;

	/**
	 * 背景色を変更
	 */
	private Runnable changeBgColor;

	private final List<PassengerRecord> passengerRecords = Lists
			.newLinkedList();
	private final List<OperationSchedule> operationSchedules = Lists
			.newLinkedList();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		handler = new Handler();
		presentTimeTextView = (TextView) view
				.findViewById(R.id.present_time_text_view);
		networkStrengthImageView = (ImageView) view
				.findViewById(R.id.network_strength_image_view);
		ImageView openLoginImageView = (ImageView) view
				.findViewById(R.id.open_login_image_view);
		openLoginImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity activity = getActivity();
				if (activity instanceof InVehicleDeviceActivity) {
					((InVehicleDeviceActivity) activity).showLoginFragment();
				}
			}
		});
		bgColorTransitionDrawable = new BgColorTransitionDrawable(Color.WHITE,
				0);
		changeBgColor = new Runnable() {
			@Override
			public void run() {
				bgColorTransitionDrawable.changeColor(getPhaseColor());
			}
		};
		View bcwl = view.findViewById(R.id.background_color_workaround_layout);
		bcwl.setBackgroundDrawable(bgColorTransitionDrawable);
		blinkBatteryAlert = new BatteryAlerter(getActivity()
				.getApplicationContext(), handler, (ImageView) getView()
				.findViewById(R.id.battery_alert_image_view),
				getFragmentManager());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.information_bar_fragment, container,
				false);
	}

	@Override
	public void onPause() {
		super.onPause();
		handler.removeCallbacks(updateTime);
		handler.removeCallbacks(blinkBatteryAlert);
		handler.removeCallbacks(changeBgColor);
	}

	@Override
	public void onResume() {
		super.onResume();
		handler.post(updateTime);
		handler.post(blinkBatteryAlert);
	}

	public void updateView() {
		handler.removeCallbacks(changeBgColor);
		handler.postDelayed(changeBgColor, 400);
		View view = getView();
		TextView phaseTextView = (TextView) view
				.findViewById(R.id.phase_text_view);
		Boolean showPlatformMemo = true;
		switch (OperationSchedule
				.getPhase(operationSchedules, passengerRecords)) {
			case DRIVE :
				phaseTextView.setText("運行中");
				break;
			case FINISH :
				showPlatformMemo = false;
				phaseTextView.setText("運行終了");
				break;
			case PLATFORM_GET_OFF :
				phaseTextView.setText("降車中");
				break;
			case PLATFORM_GET_ON :
				phaseTextView.setText("乗車中");
				break;
		}

		final OperationSchedule operationSchedule = OperationSchedule
				.getCurrent(operationSchedules);
		final Button platformMemoButton = (Button) view
				.findViewById(R.id.platform_memo_button);
		platformMemoButton.setVisibility(View.INVISIBLE);
		if (showPlatformMemo && operationSchedule != null
				&& StringUtils.isNotBlank(operationSchedule.memo)) {
			platformMemoButton.setVisibility(View.VISIBLE);
			platformMemoButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewDisabler.disable(v);
					showPlatformMemoFragment(operationSchedule);
				}
			});
		}
	}

	public void showPlatformMemoFragment(OperationSchedule operationSchedule) {
		if (!isAdded()) {
			return;
		}
		getFragmentManager()
				.beginTransaction()
				.add(R.id.modal_fragment_container,
						PlatformMemoFragment.newInstance(operationSchedule))
				.commitAllowingStateLoss();
	}

	private int getPhaseColor() {
		switch (OperationSchedule
				.getPhase(operationSchedules, passengerRecords)) {
			case DRIVE :
				return Color.GREEN;
			case PLATFORM_GET_OFF :
				return Color.BLUE;
			case PLATFORM_GET_ON :
				return Color.BLUE;
			case FINISH :
				return Color.GRAY;
		}
		return Color.WHITE;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getLoaderManager().destroyLoader(PASSENGER_RECORDS_LOADER_ID);
		getLoaderManager().destroyLoader(OPEATION_SCHEDULES_LOADER_ID);
	}

	@Override
	protected void onOperationSchedulesAndPassengerRecordsLoadFinished(
			Phase phase, LinkedList<OperationSchedule> operationSchedules,
			LinkedList<PassengerRecord> passengerRecords) {
		this.operationSchedules.clear();
		this.passengerRecords.clear();
		this.operationSchedules.addAll(operationSchedules);
		this.passengerRecords.addAll(passengerRecords);
		updateView();
	}
}
