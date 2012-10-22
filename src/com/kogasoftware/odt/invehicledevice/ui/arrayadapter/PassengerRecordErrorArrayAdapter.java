package com.kogasoftware.odt.invehicledevice.ui.arrayadapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.PassengerRecordLogic;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordArrayAdapter.ItemType;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.User;

public class PassengerRecordErrorArrayAdapter extends
		ArrayAdapter<PassengerRecord> {

	public static interface OnPassengerRecordChangeListener {
		void onPassengerRecordChange(PassengerRecordErrorArrayAdapter adapter);
	}

	private static final String TAG = PassengerRecordErrorArrayAdapter.class
			.getSimpleName();
	protected static final Integer RESOURCE_ID = R.layout.passenger_record_error_list_row;
	protected final FragmentManager fragmentManager;
	protected final LayoutInflater layoutInflater = (LayoutInflater) getContext()
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	protected final OperationSchedule operationSchedule;
	protected final InVehicleDeviceService service;
	protected final PassengerRecordLogic passengerRecordLogic;
	protected final OperationScheduleLogic operationScheduleLogic;
	protected final OnPassengerRecordChangeListener onPassengerRecordChangeListener;

	protected final OnClickListener onClickIgnoreButtonListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Object tag = view.getTag();
			if (!(tag instanceof PassengerRecord)) {
				Log.e(TAG, "\"" + view + "\".getTag() (" + tag
						+ ") is not instanceof PassengerRecord");
				return;
			}
			PassengerRecord passengerRecord = (PassengerRecord) tag;
			if (operationSchedule.isGetOffScheduled(passengerRecord)) {
				passengerRecordLogic.setIgnoreGetOffMiss(passengerRecord,
						!passengerRecord.getIgnoreGetOffMiss());
			} else {
				passengerRecordLogic.setIgnoreGetOnMiss(passengerRecord,
						!passengerRecord.getIgnoreGetOnMiss());
			}
			onPassengerRecordChangeListener
					.onPassengerRecordChange(PassengerRecordErrorArrayAdapter.this);
			notifyDataSetChanged();
		}
	};

	public PassengerRecordErrorArrayAdapter(Context context,
			InVehicleDeviceService service, FragmentManager fragmentManager,
			OperationSchedule operationSchedule,
			List<PassengerRecord> passengerRecords,
			OnPassengerRecordChangeListener onPassengerRecordChangeListener) {
		super(context, RESOURCE_ID, passengerRecords);
		this.service = service;
		this.fragmentManager = fragmentManager;
		this.operationSchedule = operationSchedule;
		this.onPassengerRecordChangeListener = onPassengerRecordChangeListener;

		passengerRecordLogic = new PassengerRecordLogic(service);
		operationScheduleLogic = new OperationScheduleLogic(service);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(RESOURCE_ID, null);
		}

		PassengerRecord passengerRecord = getItem(position);

		// ユーザー取得
		if (!passengerRecord.getUser().isPresent()) {
			Log.e(TAG, "passengerRecord (" + passengerRecord + ") has no User");
			return convertView;
		}
		User user = passengerRecord.getUser().get();

		// 無視ボタン
		CheckBox ignoreButton = (CheckBox) convertView
				.findViewById(R.id.passenger_record_error_ignore_button);
		ignoreButton.setTag(passengerRecord);
		ignoreButton.setOnClickListener(onClickIgnoreButtonListener);
		ignoreButton.setTextColor(Color.RED);

		// 行の表示
		String errorMessage = user.getLastName() + " " + user.getFirstName()
				+ " 様が";
		if (operationSchedule.isGetOffScheduled(passengerRecord)
				&& !passengerRecord.getGetOffTime().isPresent()) {
			ignoreButton.setChecked(passengerRecord.getIgnoreGetOffMiss());
			String text = "未降車でよい";
			ignoreButton.setText(text);
			// ignoreButton.setTextOn(text);
			// ignoreButton.setTextOff(text);
			errorMessage += "未降車です";
			if (passengerRecord.getIgnoreGetOffMiss()) {
				ignoreButton.setBackgroundColor(Color.CYAN);
			} else {
				ignoreButton.setBackgroundColor(Color.LTGRAY);
			}
		} else if (operationSchedule.isGetOnScheduled(passengerRecord)
				&& !passengerRecord.getGetOnTime().isPresent()) {
			ignoreButton.setChecked(passengerRecord.getIgnoreGetOnMiss());
			String text = "未乗車でよい";
			ignoreButton.setText(text);
			// ignoreButton.setTextOn(text);
			// ignoreButton.setTextOff(text);
			errorMessage += "未乗車です";
			if (passengerRecord.getIgnoreGetOnMiss()) {
				ignoreButton.setBackgroundColor(Color.CYAN);
			} else {
				ignoreButton.setBackgroundColor(Color.LTGRAY);
			}
		} else {
			Log.e(TAG, "unexpected PassengerRecord: " + passengerRecord);
		}

		TextView errorMessageTextView = (TextView) convertView
				.findViewById(R.id.passenger_record_error_message);
		errorMessageTextView.setText(errorMessage);
		return convertView;
	}

	@Deprecated
	public void show(ItemType ridingAndNoGetOff) {
	}

	@Deprecated
	public void hide(ItemType ridingAndNoGetOff) {
	}

	public Boolean hasError() {
		for (Integer count = 0; count < getCount(); ++count) {
			PassengerRecord passengerRecord = getItem(count);
			if (operationSchedule.isGetOffScheduled(passengerRecord)
					&& !passengerRecord.getGetOffTime().isPresent()
					&& !passengerRecord.getIgnoreGetOffMiss()) {
				return true;
			}
			if (operationSchedule.isGetOnScheduled(passengerRecord)
					&& !passengerRecord.getGetOnTime().isPresent()
					&& !passengerRecord.getIgnoreGetOnMiss()) {
				return true;
			}
		}
		return false;
	}
}
