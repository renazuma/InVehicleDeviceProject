package com.kogasoftware.odt.invehicledevice.view.fragment.utils.arrayadapter;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.ChargeEditFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.PassengerRecordMemoFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * 運行予定一覧
 */
public class OperationScheduleArrayAdapter
		extends	ArrayAdapter<OperationSchedule> {
	private static final String TAG = OperationScheduleArrayAdapter.class.getSimpleName();
	private static final Integer SELECTING_COLOR = Color.parseColor("#D5E9F6");
	private static final Integer DEPARTED_COLOR = Color.LTGRAY;
	private static final Integer NOT_YET_DEPARTED_COLOR = Color.parseColor("#FFFFFF");
	private static final Integer RESOURCE_ID = R.layout.operation_list_row;
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");
	private final LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	private final TreeSet<PassengerRecord> passengerRecords = new TreeSet<PassengerRecord>(PassengerRecord.DEFAULT_COMPARATOR);
	private final ContentResolver contentResolver;
	private Boolean showPassengerRecords = false;



	private Fragment fragment;
	public OperationScheduleArrayAdapter(Fragment fragment) {
		super(fragment.getActivity(), RESOURCE_ID, new LinkedList<OperationSchedule>());
		this.fragment = fragment;
		this.contentResolver = fragment.getActivity().getContentResolver();
	}

	protected final OnTouchListener onOperationScheduleTouchListener = new View.OnTouchListener() {
		private View operationScheduleRowView;
		private MotionEvent currentEvent;

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			this.operationScheduleRowView = view;
			this.currentEvent = event;

			Object operationSchedule = operationScheduleRowView.getTag();
			if (OperationSchedule.class.isInstance(operationSchedule)) {
				return onTouch();
			} else {
				Log.e(TAG, "\"" + view + "\".getTag() (" + operationSchedule + ") is not instanceof " + OperationSchedule.class);
				return false;
			}
		}

		private boolean onTouch() {
			if (isSelectingEvent()) {
				operationScheduleRowView.setBackgroundColor(getOperationScheduleRowSelectingColor());
				return true;
			} else if (isNotTargetEvent()) {
				return false;
			} else {
				Boolean result = currentEvent.getAction() == MotionEvent.ACTION_CANCEL ? true : onTap();
				operationScheduleRowView.setBackgroundColor(getOperationScheduleRowNormalColor(operationScheduleRowView));
				operationScheduleRowView.findViewById(R.id.check_mark_text_view)
								.setVisibility(getOperationScheduleRowCheckMarkCode(operationScheduleRowView));

				return result;
			}
		}

		private boolean isSelectingEvent() {
			return currentEvent.getAction() == MotionEvent.ACTION_DOWN;
		}

		private boolean isNotTargetEvent() {
            return (currentEvent.getAction() != MotionEvent.ACTION_UP && currentEvent.getAction() != MotionEvent.ACTION_CANCEL);
		}

		protected boolean onTap() {
			final OperationSchedule operationSchedule = (OperationSchedule)operationScheduleRowView.getTag();

			if (operationSchedule.departedAt == null) {
				DateTime now = DateTime.now();
				operationSchedule.arrivedAt = now;
				operationSchedule.departedAt = now;
			} else {
				operationSchedule.arrivedAt = null;
				operationSchedule.departedAt = null;
			}

			final ContentValues values = operationSchedule.toContentValues();

			new Thread() {
				@Override
				public void run() {
					contentResolver.insert(OperationSchedule.CONTENT.URI, values);
				}
			}.start();

			return false;
		}
	};

	private int getOperationScheduleRowSelectingColor() {
		return SELECTING_COLOR;
	}

	private int getOperationScheduleRowNormalColor(View operationScheduleRowView) {
		OperationSchedule operationSchedule = (OperationSchedule)operationScheduleRowView.getTag();

		if (operationSchedule.departedAt == null) {
			return NOT_YET_DEPARTED_COLOR;
		} else {
			return DEPARTED_COLOR;
		}
	}

	private int getOperationScheduleRowCheckMarkCode(View operationScheduleRowView) {
		OperationSchedule operationSchedule = (OperationSchedule)operationScheduleRowView.getTag();

		if (operationSchedule.departedAt == null) {
			return View.INVISIBLE;
        } else {
			return View.VISIBLE;
		}
	}

	static class PassengerRecordRowTag {
		public final PassengerRecord passengerRecord;
		public final OperationSchedule operationSchedule;
		public final Boolean getOn;

		public PassengerRecordRowTag(PassengerRecord passengerRecord, OperationSchedule operationSchedule, Boolean getOn) {
			this.passengerRecord = passengerRecord;
			this.operationSchedule = operationSchedule;
			this.getOn = getOn;
		}
	}

	protected final OnTouchListener onPassengerRecordTouchListener = new View.OnTouchListener() {
	    private View passengerRecordRowView;
	    private MotionEvent currentEvent;

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			this.passengerRecordRowView = view;
			this.currentEvent = event;

			Object passengerRecordRowTag = view.getTag();
			if (PassengerRecordRowTag.class.isInstance(passengerRecordRowTag)) {
				return onTouch();
			} else {
				Log.e(TAG, "\"" + view + "\".getTag() (" + passengerRecordRowTag + ") is not instanceof " + PassengerRecordRowTag.class);
				return false;
			}
		}

		private boolean onTouch() {
			if (isSelectingEvent()) {
				passengerRecordRowView.setBackgroundColor(getPassengerRecordRowSelectingColor(passengerRecordRowView));
				return true;
			} else if (isNotTargetEvent()) {
				return false;
			} else {
				Boolean result = currentEvent.getAction() == MotionEvent.ACTION_CANCEL ? true : onTap();
				passengerRecordRowView.setBackgroundColor(getPassengerRecordRowNormalColor(passengerRecordRowView));

				return result;
			}
		}

		private boolean isSelectingEvent() {
			return currentEvent.getAction() == MotionEvent.ACTION_DOWN;
		}

		private boolean isNotTargetEvent() {
			return (currentEvent.getAction() != MotionEvent.ACTION_UP && currentEvent.getAction() != MotionEvent.ACTION_CANCEL);
		}


		protected boolean onTap() {
			PassengerRecordRowTag passengerRecordRowTag = (PassengerRecordRowTag)passengerRecordRowView.getTag();

			PassengerRecord passengerRecord = passengerRecordRowTag.passengerRecord;
			OperationSchedule operationSchedule = passengerRecordRowTag.operationSchedule;

			int defaultChargeCnt = (((InVehicleDeviceActivity)getContext()).defaultCharges).size();

			// 料金設定ページに遷移するパターン。他のケースと動きが大きく異なるのでこのパターンだけ別扱いにしている。
			// HACK: その他のパターンも整理し直して、シンプルに直すべき。
			if (defaultChargeCnt > 0 && passengerRecord.getOnTime == null) {
				Fragments.showModalFragment(fragment.getFragmentManager(),
								ChargeEditFragment.newInstance(operationSchedule.id, passengerRecord.id));
				return false;
			}

			DateTime now = DateTime.now();

			if (passengerRecordRowTag.getOn) {
				if (passengerRecord.getOnTime == null) {
					passengerRecord.getOnTime = now;
				} else {
					passengerRecord.getOnTime = null;
					passengerRecord.getOffTime = null;
					passengerRecord.paidCharge = null;
				}
			} else {
				if (passengerRecord.getOffTime == null) {
					if (passengerRecord.getOnTime == null) {
						passengerRecord.getOnTime = now;
					}
					passengerRecord.getOffTime = now;
				} else {
					passengerRecord.getOffTime = null;
				}
			}

			final ContentValues values = passengerRecord.toContentValues();
			final String where = PassengerRecord.Columns._ID + " = ?";
			final String[] whereArgs = new String[]{passengerRecord.id.toString()};

			new Thread() {
				@Override
				public void run() {
					contentResolver.update(PassengerRecord.CONTENT.URI,	values, where, whereArgs);
				}
			}.start();

			return false;
		}
	};

	private int getPassengerRecordRowNormalColor(View passengerRecordRowView) {
		return getPassengerRecordRowColor(passengerRecordRowView, false);
	}

	protected int getPassengerRecordRowSelectingColor(View passengerRecordRowView) {
		return getPassengerRecordRowColor(passengerRecordRowView, true);
	}

	private int getPassengerRecordRowColor(View passengerRecordRowView, boolean invert) {
		PassengerRecordRowTag passengerRecordRowTag = (PassengerRecordRowTag)passengerRecordRowView.getTag();
		PassengerRecord passengerRecord = passengerRecordRowTag.passengerRecord;

		if (passengerRecordRowTag.getOn) {
			if ((passengerRecord.getOnTime != null) ^ invert) {
				return ContextCompat.getColor(fragment.getContext(), R.color.selected_get_on_row);
			} else {
				return ContextCompat.getColor(fragment.getContext(), R.color.get_on_row);
			}
		} else {
			if ((passengerRecord.getOffTime != null) ^ invert) {
				return ContextCompat.getColor(fragment.getContext(), R.color.selected_get_off_row);
			} else {
				return ContextCompat.getColor(fragment.getContext(), R.color.get_off_row);
			}
		}
	}

	protected final OnClickListener onUserMemoButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Object tag = view.getTag();
			if (!(tag instanceof PassengerRecord)) {
				return;
			}
			PassengerRecord passengerRecord = (PassengerRecord) tag;
			if (fragment.getFragmentManager() == null) { return; }
			Fragments.showModalFragment(fragment.getFragmentManager(), PassengerRecordMemoFragment.newInstance(passengerRecord));
		}
	};

	protected final OnClickListener onMapButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Object tag = view.getTag();
			if (!(tag instanceof OperationSchedule)) {
				return;
			}
			final OperationSchedule operationSchedule = (OperationSchedule) tag;
			operationSchedule.startNavigation(getContext());
		}
	};


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(RESOURCE_ID, null);
		} else {
			convertView.setBackgroundColor(NOT_YET_DEPARTED_COLOR);
		}

		setOperationScheduleRowView(position, convertView);
		setPassengerRecordRowViews(convertView, getItem(position));

		return convertView;
	}

	private void setOperationScheduleRowView(int position, View convertView) {
	    OperationSchedule operationSchedule = getItem(position);

		Button mapButton = convertView.findViewById(R.id.operation_list_map_button);
		mapButton.setTag(operationSchedule);
		mapButton.setOnClickListener(onMapButtonClickListener);

		TextView platformNameView = convertView.findViewById(R.id.platform_name);
		TextView platformAddressView = convertView.findViewById(R.id.platform_address);
		platformNameView.setText(operationSchedule.name);
		platformAddressView.setText(operationSchedule.address);

		if (StringUtils.isBlank(platformAddressView.getText())) {
			platformAddressView.setText("(住所登録なし)");
		}

		TextView arrivalEstimateTextView = convertView.findViewById(R.id.operation_schedule_arrival_estimate_text_view);
		TextView departureEstimateTextView = convertView.findViewById(R.id.operation_schedule_departure_estimate_text_view);

		arrivalEstimateTextView.setText("");
		departureEstimateTextView.setText("");

		arrivalEstimateTextView.setText(operationSchedule.arrivalEstimate.toString(DATE_TIME_FORMATTER) + " 着");

		if (getCount() != position + 1) {
			departureEstimateTextView.setText(operationSchedule.departureEstimate.toString(DATE_TIME_FORMATTER) + " 発");
		}

		TextView checkMarkTextView = convertView.findViewById(R.id.check_mark_text_view);
		if (operationSchedule.departedAt == null) {
			convertView.setBackgroundColor(NOT_YET_DEPARTED_COLOR);
			checkMarkTextView.setVisibility(View.INVISIBLE);
		} else {
			convertView.setBackgroundColor(DEPARTED_COLOR);
			checkMarkTextView.setVisibility(View.VISIBLE);
		}

		convertView.setTag(operationSchedule);
		convertView.setOnTouchListener(onOperationScheduleTouchListener);
	}

	private void setPassengerRecordRowViews(View convertView, OperationSchedule operationSchedule) {
		ViewGroup passengerRecordsViewGroup = convertView.findViewById(R.id.operation_list_passenger_records);
		passengerRecordsViewGroup.removeAllViews();
		passengerRecordsViewGroup.setVisibility(showPassengerRecords	? View.VISIBLE : View.GONE);

		Long getOffPassengerCount = 0L;
		Long getOnPassengerCount = 0L;
		for (PassengerRecord passengerRecord : passengerRecords) {
			if (showPassengerRecords) {
				if (operationSchedule.id.equals(passengerRecord.arrivalScheduleId)) {
					passengerRecordsViewGroup.addView(createPassengerRecordRow(operationSchedule, passengerRecord, false));
					getOffPassengerCount += passengerRecord.passengerCount;
				}
				if (operationSchedule.id.equals(passengerRecord.departureScheduleId)) {
					passengerRecordsViewGroup.addView(createPassengerRecordRow(operationSchedule, passengerRecord, true));
					getOnPassengerCount += passengerRecord.passengerCount;
				}
			}
		}

		TextView getOnPassengerCountTextView = convertView.findViewById(R.id.operation_schedule_get_on_passenger_count_text_view);
		getOnPassengerCountTextView.setText("乗" + String.format("%3d", getOnPassengerCount) + "名");
		getOnPassengerCountTextView.setVisibility(getOnPassengerCount > 0 ? View.VISIBLE : View.INVISIBLE);

		TextView getOffPassengerCountTextView = convertView.findViewById(R.id.operation_schedule_get_off_passenger_count_text_view);
		getOffPassengerCountTextView.setText("降" + String.format("%3d", getOffPassengerCount) + "名");
		getOffPassengerCountTextView.setVisibility(getOffPassengerCount > 0 ? View.VISIBLE : View.INVISIBLE);
	}

	private View createPassengerRecordRow(OperationSchedule operationSchedule, PassengerRecord passengerRecord, Boolean getOn) {
		View row = layoutInflater.inflate(R.layout.small_passenger_record_list_row, null);

		// 行のデフォルト背景色
		row.setBackgroundColor(NOT_YET_DEPARTED_COLOR);

		// 乗降画像
		ImageView selectMarkImageView = row.findViewById(R.id.select_mark_image_view);
		selectMarkImageView.setImageResource(getOn ? R.drawable.get_on : R.drawable.get_off);

		// ユーザー名
		TextView userNameView = row.findViewById(R.id.user_name);
		userNameView.setText(passengerRecord.getDisplayName());

		// 行タッチ時の動作を定義
		row.setTag(new PassengerRecordRowTag(passengerRecord, operationSchedule, getOn));
		row.setOnTouchListener(onPassengerRecordTouchListener);

		//乗降人数
		TextView countView = row.findViewById(R.id.passenger_count_text_view);
		countView.setText(passengerRecord.passengerCount + "名");

		// メモボタン
		Button userMemoButton = row.findViewById(R.id.user_memo_button);
		userMemoButton.setTag(passengerRecord);
		userMemoButton.setOnClickListener(onUserMemoButtonClickListener);

		// 料金系
		TextView paidChargeView = row.findViewById(R.id.paid_charge);
		TextView expectedChargeView = row.findViewById(R.id.expected_charge);
		if (!getOn) {
			View passengerCountSpaceView = row.findViewById(R.id.passenger_count_space_view);
			passengerCountSpaceView.setVisibility(View.GONE);
		}

		int defaultChargeCnt = (((InVehicleDeviceActivity) getContext()).defaultCharges).size();
		if (defaultChargeCnt > 0) {
			paidChargeView.setVisibility(View.INVISIBLE);
			expectedChargeView.setVisibility(View.INVISIBLE);
		} else {
			paidChargeView.setVisibility(View.GONE);
			expectedChargeView.setVisibility(View.GONE);
		}

		// 支払料金
		if (passengerRecord.paidCharge != null) {
			paidChargeView.setText(passengerRecord.paidCharge.toString() + "円");
			paidChargeView.setVisibility(View.VISIBLE);
		}

		// 予定料金
		if (getOn) {
			if (passengerRecord.expectedCharge != null) {
				expectedChargeView.setText(passengerRecord.expectedCharge.toString() + "円");
				expectedChargeView.setVisibility(View.VISIBLE);
			}
		} else {
			expectedChargeView.setVisibility(View.GONE);
		}

		// 乗降者行背景色
        row.setBackgroundColor(getPassengerRecordRowNormalColor(row));

		// 乗車行に到着乗降場名を追加
		TextView arrivalPlatformView = row.findViewById(R.id.user_arrival_platform_name);
		arrivalPlatformView.setVisibility(View.GONE);
		if (getOn) {
			for (Integer i = 0; i < getCount(); i++) {
				OperationSchedule arrivalOperationSchedule = getItem(i);
				if (arrivalOperationSchedule.id.equals(passengerRecord.arrivalScheduleId)) {
					arrivalPlatformView.setVisibility(View.VISIBLE);
					arrivalPlatformView.setText("⇨"	+ arrivalOperationSchedule.name);
				}
			}
		}

		return row;
	}

	public void showPassengerRecords() {
		showPassengerRecords = true;
		notifyDataSetChanged();
	}

	public void hidePassengerRecords() {
		showPassengerRecords = false;
		notifyDataSetChanged();
	}

	public void setOperationSchedules(List<OperationSchedule> OperationSchedules) {
		clear();
		addAll(OperationSchedules);
		notifyDataSetChanged();
	}

	public void setPassengerRecords(List<PassengerRecord> newPassengerRecords) {
		passengerRecords.clear();
		passengerRecords.addAll(newPassengerRecords);
		notifyDataSetChanged();
	}
}
