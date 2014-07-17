package com.kogasoftware.odt.invehicledevice.ui.arrayadapter;

import java.util.Collections;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecords;

public class PassengerRecordErrorArrayAdapter
		extends
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
	protected final Long operationScheduleId;
	protected final ContentResolver contentResolver;

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
			if (operationScheduleId.equals(passengerRecord.arrivalScheduleId)) {
				passengerRecord.ignoreGetOffMiss = !passengerRecord.ignoreGetOffMiss;
			} else {
				passengerRecord.ignoreGetOnMiss = !passengerRecord.ignoreGetOnMiss;
			}
			String where = PassengerRecords.Columns._ID + " = ?";
			String[] whereArgs = new String[]{passengerRecord.id.toString()};
			contentResolver.update(PassengerRecords.CONTENT.URI,
					passengerRecord.toContentValues(), where, whereArgs);
			notifyDataSetChanged();
		}
	};

	public PassengerRecordErrorArrayAdapter(Fragment fragment,
			Long operationScheduleId) {
		super(fragment.getActivity(), RESOURCE_ID);
		this.fragmentManager = fragment.getFragmentManager();
		this.contentResolver = fragment.getActivity().getContentResolver();
		this.operationScheduleId = operationScheduleId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(RESOURCE_ID, null);
		}

		PassengerRecord passengerRecord = getItem(position);

		// 無視ボタン
		CheckBox ignoreButton = (CheckBox) convertView
				.findViewById(R.id.passenger_record_error_ignore_button);
		ignoreButton.setTag(passengerRecord);
		ignoreButton.setOnClickListener(onClickIgnoreButtonListener);
		ignoreButton.setTextColor(Color.RED);

		// 行の表示
		String errorMessage = passengerRecord.getDisplayName() + " が";
		if (operationScheduleId.equals(passengerRecord.arrivalScheduleId)
				&& passengerRecord.getOffTime == null) {
			ignoreButton.setChecked(passengerRecord.ignoreGetOffMiss);
			String text = "未降車でよい";
			ignoreButton.setText(text);
			// ignoreButton.setTextOn(text);
			// ignoreButton.setTextOff(text);
			errorMessage += "未降車です";
			if (passengerRecord.ignoreGetOffMiss) {
				ignoreButton
						.setBackgroundResource(R.drawable.ignore_button_pressed);
			} else {
				ignoreButton.setBackgroundResource(R.drawable.ignore_button);
			}
		} else if (operationScheduleId
				.equals(passengerRecord.departureScheduleId)
				&& passengerRecord.getOnTime == null) {
			ignoreButton.setChecked(passengerRecord.ignoreGetOnMiss);
			String text = "未乗車でよい";
			ignoreButton.setText(text);
			// ignoreButton.setTextOn(text);
			// ignoreButton.setTextOff(text);
			errorMessage += "未乗車です";
			if (passengerRecord.ignoreGetOnMiss) {
				ignoreButton
						.setBackgroundResource(R.drawable.ignore_button_pressed);
			} else {
				ignoreButton.setBackgroundResource(R.drawable.ignore_button);
			}
		} else {
			Log.e(TAG, "unexpected PassengerRecord: " + passengerRecord);
		}

		TextView errorMessageTextView = (TextView) convertView
				.findViewById(R.id.passenger_record_error_message);
		errorMessageTextView.setText(errorMessage);
		return convertView;
	}

	public void update(List<PassengerRecord> passengerRecords) {
		clear();
		List<PassengerRecord> sortedPassengerRecord = Lists
				.newArrayList(passengerRecords);
		Collections.sort(sortedPassengerRecord,
				PassengerRecord.DEFAULT_COMPARATOR);
		for (PassengerRecord passengerRecord : sortedPassengerRecord) {
			add(passengerRecord);
		}
		notifyDataSetChanged();
	}

	public Boolean hasError() {
		for (Integer count = 0; count < getCount(); ++count) {
			PassengerRecord passengerRecord = getItem(count);
			if (operationScheduleId.equals(passengerRecord.arrivalScheduleId)
					&& passengerRecord.getOffTime == null
					&& !passengerRecord.ignoreGetOffMiss) {
				return true;
			}
			if (operationScheduleId.equals(passengerRecord.departureScheduleId)
					&& passengerRecord.getOnTime == null
					&& !passengerRecord.ignoreGetOnMiss) {
				return true;
			}
		}
		return false;
	}
}
