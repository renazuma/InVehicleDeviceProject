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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * 運行予定一覧
 */
public class OperationScheduleArrayAdapter
		extends	ArrayAdapter<OperationSchedule> {
	private static final String TAG = OperationScheduleArrayAdapter.class.getSimpleName();
	private static final Integer SELECTED_COLOR = Color.parseColor("#D5E9F6");
	private static final Integer DEPARTED_COLOR = Color.LTGRAY;
	private static final Integer DEFAULT_COLOR = Color.parseColor("#FFFFFF");
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

	static abstract class OnRowTouchListener<T> implements OnTouchListener {
		private final Class<T> rowClass;

		public OnRowTouchListener(Class<T> rowClass) {
			this.rowClass = rowClass;
		}

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			Object tag = view.getTag();
			if (rowClass.isInstance(tag)) {
				return onTouch(view, event, rowClass.cast(tag));
			} else {
				Log.e(TAG, "\"" + view + "\".getTag() (" + tag
						+ ") is not instanceof " + rowClass);
			}
			return false;
		}

		private boolean onTouch(View view, MotionEvent event, T tag) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				view.setBackgroundColor(getSelectedColor(tag));
				return true;
			}

			if (event.getAction() != MotionEvent.ACTION_UP
					&& event.getAction() != MotionEvent.ACTION_CANCEL) {
				return false;
			}

			Boolean result = event.getAction() == MotionEvent.ACTION_CANCEL
					? true
					: onTap(view, event, tag);
			view.setBackgroundColor(getDefaultColor(tag));
			return result;
		}

		protected abstract boolean onTap(View view, MotionEvent event, T tag);

		protected int getDefaultColor(T tag) {
			return DEFAULT_COLOR;
		}

		protected int getSelectedColor(T tag) {
			return SELECTED_COLOR;
		}
	}

	protected final OnTouchListener onOperationScheduleTouchListener = new OnRowTouchListener<OperationSchedule>(
			OperationSchedule.class) {
		@Override
		protected boolean onTap(View view, MotionEvent event,
				final OperationSchedule operationSchedule) {
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
					contentResolver.insert(OperationSchedule.CONTENT.URI,
							values);
				}
			}.start();
			notifyDataSetChanged();
			return false;
		}

		@Override
		protected int getDefaultColor(OperationSchedule operationSchedule) {
			if (operationSchedule.departedAt == null) {
				return DEFAULT_COLOR;
			} else {
				return DEPARTED_COLOR;
			}
		}
	};

	static class PassengerRecordRowTag {
		public final PassengerRecord passengerRecord;
		public final OperationSchedule operationSchedule;
		public final Boolean getOn;

		public PassengerRecordRowTag(PassengerRecord passengerRecord,
				OperationSchedule operationSchedule, Boolean getOn) {
			this.passengerRecord = passengerRecord;
			this.operationSchedule = operationSchedule;
			this.getOn = getOn;
		}
	}

	protected final OnRowTouchListener<PassengerRecordRowTag> onPassengerRecordTouchListener = new OnRowTouchListener<PassengerRecordRowTag>(
			PassengerRecordRowTag.class) {
		@Override
		protected int getDefaultColor(PassengerRecordRowTag passengerRecordRowTag) {
			return getColor(passengerRecordRowTag, false);
		}

		@Override
		protected int getSelectedColor(
				PassengerRecordRowTag passengerRecordRowTag) {
			return getColor(passengerRecordRowTag, true);
		}

		private int getColor(PassengerRecordRowTag passengerRecordRowTag, boolean invert) {
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

		@Override
		protected boolean onTap(View view, MotionEvent event,
				PassengerRecordRowTag passengerRecordRowTag) {
			PassengerRecord passengerRecord = passengerRecordRowTag.passengerRecord;
			OperationSchedule operationSchedule = passengerRecordRowTag.operationSchedule;

			int defaultChargeCnt = ((ArrayList)(((InVehicleDeviceActivity)getContext()).defaultCharges)).size();

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
			final String[] whereArgs = new String[]{passengerRecord.id
					.toString()};
			new Thread() {
				@Override
				public void run() {
					contentResolver.update(PassengerRecord.CONTENT.URI,
							values, where, whereArgs);
				}
			}.start();
			notifyDataSetChanged();
			return false;
		}
	};

	protected final OnClickListener onUserMemoButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Object tag = view.getTag();
			if (!(tag instanceof PassengerRecord)) {
				return;
			}
			PassengerRecord passengerRecord = (PassengerRecord) tag;
			if (fragment.getFragmentManager() == null) { return; }
			Fragments.showModalFragment(fragment.getFragmentManager(),
					PassengerRecordMemoFragment.newInstance(passengerRecord));
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
			convertView.setBackgroundColor(DEFAULT_COLOR);
		}

		OperationSchedule operationSchedule = getItem(position);
		Button mapButton = (Button) convertView.findViewById(R.id.operation_list_map_button);
		mapButton.setTag(operationSchedule);
		mapButton.setOnClickListener(onMapButtonClickListener);
		TextView platformNameView = (TextView) convertView.findViewById(R.id.platform_name);
		TextView platformAddressView = (TextView) convertView.findViewById(R.id.platform_address);
		platformNameView.setText(operationSchedule.name);
		platformAddressView.setText(operationSchedule.address);

		if (StringUtils.isBlank(platformAddressView.getText())) {
			platformAddressView.setText("(住所登録なし)");
		}

		ViewGroup passengerRecordsView = (ViewGroup) convertView.findViewById(R.id.operation_list_passenger_records);
		passengerRecordsView.removeAllViews();
		passengerRecordsView.setVisibility(showPassengerRecords	? View.VISIBLE : View.GONE);

		Long getOffPassengerCount = 0L;
		for (PassengerRecord passengerRecord : passengerRecords) {
			if (operationSchedule.id.equals(passengerRecord.arrivalScheduleId)) {
				if (showPassengerRecords) {
					passengerRecordsView.addView(createPassengerRecordRow(operationSchedule, passengerRecord, false));
				}
				getOffPassengerCount += passengerRecord.passengerCount;
			}
		}

		Long getOnPassengerCount = 0L;
		for (PassengerRecord passengerRecord : passengerRecords) {
			if (operationSchedule.id.equals(passengerRecord.departureScheduleId)) {
				if (showPassengerRecords) {
					passengerRecordsView.addView(createPassengerRecordRow(operationSchedule, passengerRecord, true));
				}
				getOnPassengerCount += passengerRecord.passengerCount;
			}
		}

		TextView getOnPassengerCountTextView = (TextView) convertView.findViewById(R.id.operation_schedule_get_on_passenger_count_text_view);
		getOnPassengerCountTextView.setText("乗" 	+ String.format("%3d", getOnPassengerCount) + "名");
		getOnPassengerCountTextView.setVisibility(getOnPassengerCount > 0 ? View.VISIBLE : View.INVISIBLE);

		TextView getOffPassengerCountTextView = (TextView) convertView.findViewById(R.id.operation_schedule_get_off_passenger_count_text_view);
		getOffPassengerCountTextView.setText("降"
				+ String.format("%3d", getOffPassengerCount) + "名");
		getOffPassengerCountTextView.setVisibility(getOffPassengerCount > 0 ? View.VISIBLE : View.INVISIBLE);

		TextView arrivalEstimateTextView = (TextView) convertView.findViewById(R.id.operation_schedule_arrival_estimate_text_view);
		TextView departureEstimateTextView = (TextView) convertView.findViewById(R.id.operation_schedule_departure_estimate_text_view);

		arrivalEstimateTextView.setText("");
		departureEstimateTextView.setText("");

		arrivalEstimateTextView.setText(operationSchedule.arrivalEstimate.toString(DATE_TIME_FORMATTER) + " 着");

		if (getCount() != position + 1) {
			departureEstimateTextView.setText(operationSchedule.departureEstimate.toString(DATE_TIME_FORMATTER) + " 発");
		}

		TextView checkMarkTextView = (TextView) convertView.findViewById(R.id.check_mark_text_view);
		if (operationSchedule.departedAt == null) {
			convertView.setBackgroundColor(DEFAULT_COLOR);
			checkMarkTextView.setVisibility(View.INVISIBLE);
		} else {
			convertView.setBackgroundColor(DEPARTED_COLOR);
			checkMarkTextView.setVisibility(View.VISIBLE);
		}
		convertView.setTag(operationSchedule);
		convertView.setOnTouchListener(onOperationScheduleTouchListener);
		return convertView;
	}

	private View createPassengerRecordRow(OperationSchedule operationSchedule, PassengerRecord passengerRecord, Boolean getOn) {

		View row = layoutInflater.inflate(R.layout.small_passenger_record_list_row, null);

		// 行のデフォルト背景色
		row.setBackgroundColor(DEFAULT_COLOR);

		// 乗降画像
		ImageView selectMarkImageView = (ImageView) row.findViewById(R.id.select_mark_image_view);
		selectMarkImageView.setImageResource(getOn ? R.drawable.get_on : R.drawable.get_off);

		// ユーザー名
		TextView userNameView = (TextView) row.findViewById(R.id.user_name);
		userNameView.setText(passengerRecord.getDisplayName());

		// 行タッチ時の動作を定義
		PassengerRecordRowTag tag = new PassengerRecordRowTag(passengerRecord, operationSchedule, getOn);
		row.setTag(tag);
		row.setOnTouchListener(onPassengerRecordTouchListener);

		//乗降人数
		TextView countView = (TextView) row.findViewById(R.id.passenger_count_text_view);
		countView.setText(passengerRecord.passengerCount + "名");

		// メモボタン
		Button userMemoButton = (Button) row.findViewById(R.id.user_memo_button);
		userMemoButton.setTag(passengerRecord);
		userMemoButton.setOnClickListener(onUserMemoButtonClickListener);

		// 料金系
		TextView paidChargeView = (TextView) row.findViewById(R.id.paid_charge);
		TextView expectedChargeView = (TextView) row.findViewById(R.id.expected_charge);
		if (!getOn) {
			View passengerCountSpaceView = (View) row.findViewById(R.id.passenger_count_space_view);
			passengerCountSpaceView.setVisibility(View.GONE);
		}

		int defaultChargeCnt = ((ArrayList) (((InVehicleDeviceActivity) getContext()).defaultCharges)).size();
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
		row.setBackgroundColor(onPassengerRecordTouchListener.getDefaultColor(tag));

		// 乗車行に到着乗降場名を追加
		TextView arrivalPlatformView = (TextView) row.findViewById(R.id.user_arrival_platform_name);
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
