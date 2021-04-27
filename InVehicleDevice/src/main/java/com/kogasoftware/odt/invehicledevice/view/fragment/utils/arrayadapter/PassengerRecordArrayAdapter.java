package com.kogasoftware.odt.invehicledevice.view.fragment.utils.arrayadapter;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.ChargeEditFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.PassengerRecordMemoFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.ViewDisabler;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.WeakHashMap;

/**
 * 乗客一覧
 */
public class PassengerRecordArrayAdapter extends ArrayAdapter<PassengerRecord> {
	private static final String TAG = PassengerRecordArrayAdapter.class.getSimpleName();
	private static final Integer RESOURCE_ID = R.layout.passenger_record_list_row;
	private final LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	private final OperationSchedule operationSchedule;
	private final WeakHashMap<View, Boolean> memoButtons = new WeakHashMap<View, Boolean>();
	private final ContentResolver contentResolver;
	private Boolean memoButtonsVisible = true;
	private Fragment fragment;

	public PassengerRecordArrayAdapter(Fragment fragment, OperationSchedule operationSchedule) {
		super(fragment.getActivity(), RESOURCE_ID);
		this.fragment = fragment;
		this.contentResolver = fragment.getActivity().getContentResolver();
		this.operationSchedule = operationSchedule;
	}

	private final OnClickListener onClickListenerForPassengerRecord = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Object tag = view.getTag();
			if (!(tag instanceof PassengerRecord)) {
				Log.e(TAG, "\"" + view + "\".getTag() (" + tag	+ ") is not instanceof PassengerRecord");
			}
			PassengerRecord passengerRecord = (PassengerRecord) tag;

			int defaultChargeCnt = ((ArrayList)(((InVehicleDeviceActivity)getContext()).defaultCharges)).size();

			// 料金設定ページに遷移するパターン。他のケースと動きが大きく異なるのでこのパターンだけ別扱いにしている。
			// HACK: その他のパターンも整理し直して、シンプルに直すべき。
			if (defaultChargeCnt > 0 && passengerRecord.getOnTime == null) {
				Fragments.showModalFragment(fragment.getFragmentManager(),
								ChargeEditFragment.newInstance(operationSchedule.id, passengerRecord.id));
				return;
			}

			DateTime now = DateTime.now();

			if (operationSchedule.id.equals(passengerRecord.arrivalScheduleId)) {
				passengerRecord.ignoreGetOffMiss = false;
				if (passengerRecord.getOffTime != null) {
					passengerRecord.getOffTime = null;
				} else {
					if (passengerRecord.getOnTime == null) { passengerRecord.getOnTime = now; }
					passengerRecord.getOffTime = now;
				}
			} else if (operationSchedule.id.equals(passengerRecord.departureScheduleId)) {
				passengerRecord.ignoreGetOnMiss = false;
				if (passengerRecord.getOnTime != null) {
					passengerRecord.getOnTime = null;
					passengerRecord.getOffTime = null;
					passengerRecord.paidCharge = null;
				} else {
					passengerRecord.getOnTime = now;
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
			notifyDataSetChanged();
		}
	};

	protected final OnClickListener onClickMemoButtonListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			ViewDisabler.disable(view);
			Object tag = view.getTag();
			if (!(tag instanceof PassengerRecord)) {
				Log.e(TAG, "\"" + view + "\".getTag() (" + tag
						+ ") is not instanceof PassengerRecord");
				return;
			}
			PassengerRecord passengerRecord = (PassengerRecord) tag;
			Fragments.showModalFragment(fragment.getFragmentManager(),
					PassengerRecordMemoFragment.newInstance(passengerRecord));
		}
	};


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(RESOURCE_ID, null);
		}

		PassengerRecord passengerRecord = getItem(position);
		TextView passengerCountTextView = (TextView) convertView.findViewById(R.id.passenger_count_text_view);
		passengerCountTextView.setText(passengerRecord.passengerCount + "名");

		// メモボタン
		View memoButton = convertView.findViewById(R.id.memo_button);
		View memoButtonLayout = convertView.findViewById(R.id.memo_button_layout);
		memoButton.setTag(passengerRecord);
		memoButtonLayout.setTag(passengerRecord);
		memoButton.setOnClickListener(onClickMemoButtonListener);
		memoButtonLayout.setOnClickListener(onClickMemoButtonListener);
		memoButtons.put(memoButton, true);
		if (!passengerRecord.reservationMemo.isEmpty()
				|| !passengerRecord.userMemo.isEmpty()) {
			memoButtonLayout.setVisibility(View.VISIBLE);
		} else {
			memoButtonLayout.setVisibility(View.GONE);
		}


		// 行の表示
		convertView.setTag(passengerRecord);
		convertView.setOnClickListener(onClickListenerForPassengerRecord);

		ImageView selectMarkImageView = (ImageView) convertView.findViewById(R.id.select_mark_image_view);

		// 行のデフォルト色指定
		int color_code = 0;
		if (operationSchedule.id.equals(passengerRecord.arrivalScheduleId)) {
			selectMarkImageView.setImageResource(R.drawable.get_off);
			if (passengerRecord.getOffTime != null) {
			    color_code = ContextCompat.getColor(fragment.getContext(), R.color.selected_get_off_row);
			} else {
				color_code = ContextCompat.getColor(fragment.getContext(), R.color.get_off_row);
			}
		} else if (operationSchedule.id.equals(passengerRecord.departureScheduleId)) {
			selectMarkImageView.setImageResource(R.drawable.get_on);
			if (passengerRecord.getOnTime != null) {
				color_code = ContextCompat.getColor(fragment.getContext(), R.color.selected_get_on_row);
			} else {
				color_code = ContextCompat.getColor(fragment.getContext(), R.color.get_on_row);
			}
		} else {
			Log.e(TAG, "unexpected PassengerRecord: " + passengerRecord);
		}
		convertView.setBackgroundColor(color_code);


		TextView userNameView = (TextView) convertView
				.findViewById(R.id.user_name);
		userNameView.setText(passengerRecord.getDisplayName());

		// 料金表示
		TextView chargeText = (TextView) convertView.findViewById(R.id.charge_edit_text_view);
		if (passengerRecord.paidCharge != null) {
			chargeText.setText(passengerRecord.paidCharge.toString() + "円");
		} else {
			chargeText.setText("");
		}

		TextView expectedChargeText = (TextView) convertView.findViewById(R.id.expected_charge_text_view);
		if (passengerRecord.expectedCharge != null) {
			expectedChargeText.setText(passengerRecord.expectedCharge.toString() + "円");
		} else {
			expectedChargeText.setText("");
		}

		return convertView;
	}

	public void toggleBlink() {
		memoButtonsVisible = !memoButtonsVisible;
		for (Entry<View, Boolean> entry : memoButtons.entrySet()) {
			View memoButton = entry.getKey();
			if (memoButton == null) {
				continue;
			}
			if (memoButtonsVisible) {
				memoButton.setVisibility(View.VISIBLE);
			} else {
				memoButton.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void update(List<PassengerRecord> passengerRecords) {
		clear();
		List<PassengerRecord> sortedPassengerRecords = Lists
				.newArrayList(passengerRecords);
		Collections.sort(sortedPassengerRecords,
				PassengerRecord.DEFAULT_COMPARATOR);
		for (PassengerRecord passengerRecord : sortedPassengerRecords) {
			add(passengerRecord);
		}
		notifyDataSetChanged();
	}
}