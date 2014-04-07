package com.kogasoftware.odt.invehicledevice.ui.arrayadapter;

import java.util.List;
import java.util.Map.Entry;
import java.util.Collections;
import java.util.WeakHashMap;

import android.app.FragmentManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.PassengerRecordLogic;
import com.kogasoftware.odt.invehicledevice.ui.ViewDisabler;
import com.kogasoftware.odt.invehicledevice.ui.fragment.ApplicationFragment;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PassengerRecordMemoFragment;

public class PassengerRecordArrayAdapter extends ArrayAdapter<PassengerRecord> {
	private static final String TAG = PassengerRecordArrayAdapter.class
			.getSimpleName();
	protected static final Integer RESOURCE_ID = R.layout.passenger_record_list_row;
	protected final FragmentManager fragmentManager;
	protected final LayoutInflater layoutInflater = (LayoutInflater) getContext()
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	protected final OperationSchedule operationSchedule;
	protected final InVehicleDeviceService service;
	protected final PassengerRecordLogic passengerRecordLogic;
	protected final OperationScheduleLogic operationScheduleLogic;
	protected final WeakHashMap<View, Boolean> memoButtons = new WeakHashMap<View, Boolean>();
	protected Boolean memoButtonsVisible = true;
	protected final OnTouchListener onTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			Object tag = view.getTag();
			if (!(tag instanceof PassengerRecord)) {
				Log.e(TAG, "\"" + view + "\".getTag() (" + tag
						+ ") is not instanceof PassengerRecord");
				return false;
			}
			
			PassengerRecord passengerRecord = (PassengerRecord) tag;
			// 色を反転する
			Boolean invertColor = event.getAction() == MotionEvent.ACTION_DOWN;
			// 色を元に戻す
			Boolean restoreColor = event.getAction() == MotionEvent.ACTION_CANCEL;
			// 乗降を実行する
			Boolean execute = event.getAction() == MotionEvent.ACTION_UP;

			if (invertColor || restoreColor) {
				if (operationSchedule.isGetOffScheduled(passengerRecord)) {
					if (passengerRecord.getGetOffTime().isPresent()
							^ invertColor) {
						view.setBackgroundColor(PassengerRecordLogic.SELECTED_GET_OFF_COLOR);
					} else {
						view.setBackgroundColor(PassengerRecordLogic.GET_OFF_COLOR);
					}
				} else if (operationSchedule.isGetOnScheduled(passengerRecord)) {
					if (passengerRecord.getGetOnTime().isPresent()
							^ invertColor) {
						view.setBackgroundColor(PassengerRecordLogic.SELECTED_GET_ON_COLOR);
					} else {
						view.setBackgroundColor(PassengerRecordLogic.GET_ON_COLOR);
					}
				}
			} else if (execute) {
				if (operationSchedule.isGetOffScheduled(passengerRecord)) {
					passengerRecord.setIgnoreGetOffMiss(false);
					if (passengerRecord.getGetOffTime().isPresent()) {
						passengerRecordLogic.cancelGetOff(operationSchedule,
								passengerRecord);
					} else {
						passengerRecordLogic.getOff(operationSchedule,
								passengerRecord);
					}
				} else if (operationSchedule.isGetOnScheduled(passengerRecord)) {
					passengerRecord.setIgnoreGetOnMiss(false);
					if (passengerRecord.getGetOnTime().isPresent()) {
						passengerRecordLogic.cancelGetOn(operationSchedule,
								passengerRecord);
					} else {
						passengerRecordLogic.getOn(operationSchedule,
								passengerRecord);
					}
				}
				notifyDataSetChanged();
			}
			return true;
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
			ApplicationFragment
					.setCustomAnimation(fragmentManager.beginTransaction())
					.add(R.id.modal_fragment_container,
							PassengerRecordMemoFragment
									.newInstance(passengerRecord)).commit();
		}
	};

	public PassengerRecordArrayAdapter(Context context,
			InVehicleDeviceService service, FragmentManager fragmentManager,
			OperationSchedule operationSchedule,
			List<PassengerRecord> passengerRecords) {
		super(context, RESOURCE_ID);
		this.service = service;
		this.fragmentManager = fragmentManager;
		this.operationSchedule = operationSchedule;
		passengerRecordLogic = new PassengerRecordLogic(service);
		operationScheduleLogic = new OperationScheduleLogic(service);
		
		List<PassengerRecord> sortedPassengerRecords = Lists
				.newArrayList(passengerRecords);
		Collections.sort(sortedPassengerRecords,
				PassengerRecord.DEFAULT_COMPARATOR);
		Log.i(TAG, "PassengerRecords:");
		for (PassengerRecord passengerRecord : sortedPassengerRecords) {
			Log.i(TAG, "id=" + passengerRecord.getId() +
					" representative=" + passengerRecord.isRepresentative() +
					" getOnTime=" + passengerRecord.getGetOnTime() +
					" getOffTime=" + passengerRecord.getGetOffTime());
			add(passengerRecord);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(RESOURCE_ID, null);
		}

		// 予約取得
		PassengerRecord passengerRecord = getItem(position);
		if (!passengerRecord.getReservation().isPresent()) {
			Log.e(TAG, "passengerRecord (" + passengerRecord
					+ ") has no Reservation");
			return convertView;
		}
		Reservation reservation = passengerRecord.getReservation().get();

		// ユーザー取得
		if (!passengerRecord.getUser().isPresent()) {
			Log.e(TAG, "passengerRecord (" + passengerRecord + ") has no User");
			return convertView;
		}
		User user = passengerRecord.getUser().get();

		// 人数表示
		TextView passengerCountTextView = (TextView) convertView
				.findViewById(R.id.passenger_count_text_view);
		passengerCountTextView.setText(passengerRecord
				.getScheduledPassengerCount() + "名");

		// メモボタン
		View memoButton = convertView.findViewById(R.id.memo_button);
		View memoButtonLayout = convertView
				.findViewById(R.id.memo_button_layout);
		memoButton.setTag(passengerRecord);
		memoButtonLayout.setTag(passengerRecord);
		memoButton.setOnClickListener(onClickMemoButtonListener);
		memoButtonLayout.setOnClickListener(onClickMemoButtonListener);
		memoButtons.put(memoButton, true);
		if (!reservation.getMemo().or("").isEmpty()
				|| !user.getNotes().isEmpty()) {
			memoButtonLayout.setVisibility(View.VISIBLE);
		} else {
			memoButtonLayout.setVisibility(View.GONE);
		}

		// 行の表示
		convertView.setTag(passengerRecord);
		convertView.setOnTouchListener(onTouchListener);

		ImageView selectMarkImageView = (ImageView) convertView
				.findViewById(R.id.select_mark_image_view);

		if (operationSchedule.isGetOffScheduled(passengerRecord)) {
			selectMarkImageView.setImageResource(R.drawable.get_off);
			if (passengerRecord.getGetOffTime().isPresent()) {
				convertView.setBackgroundColor(PassengerRecordLogic.SELECTED_GET_OFF_COLOR);
			} else {
				convertView.setBackgroundColor(PassengerRecordLogic.GET_OFF_COLOR);
			}
		} else if (operationSchedule.isGetOnScheduled(passengerRecord)) {
			selectMarkImageView.setImageResource(R.drawable.get_on);
			if (passengerRecord.getGetOnTime().isPresent()) {
				convertView.setBackgroundColor(PassengerRecordLogic.SELECTED_GET_ON_COLOR);
			} else {
				convertView.setBackgroundColor(PassengerRecordLogic.GET_ON_COLOR);
			}
		} else {
			Log.e(TAG, "unexpected PassengerRecord: " + passengerRecord);
		}

		TextView userNameView = (TextView) convertView
				.findViewById(R.id.user_name);
		userNameView.setText(passengerRecord.getDisplayName());

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

	public void updatePassengerRecord(PassengerRecord passengerRecord) {
		Boolean updated = false;
		for (int i = 0; i < getCount(); i++) {
			PassengerRecord oldPassengerRecord = getItem(i);
			if (oldPassengerRecord.getId().equals(passengerRecord.getId())) {
				remove(oldPassengerRecord);
				insert(passengerRecord, i);
				updated = true;
			}
		}
		if (updated) {
			notifyDataSetChanged();
		}
	}
}
