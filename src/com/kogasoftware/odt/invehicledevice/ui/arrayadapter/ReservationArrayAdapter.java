package com.kogasoftware.odt.invehicledevice.ui.arrayadapter;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic.PayTiming;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.VoidReader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.ui.modalview.MemoModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ReturnPathModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class ReservationArrayAdapter extends ArrayAdapter<PassengerRecord> {
	public static enum ItemType {
		RIDING_AND_NO_GET_OFF, FUTURE_GET_ON, MISSED,
	}

	private static final String TAG = ReservationArrayAdapter.class
			.getSimpleName();

	private final LayoutInflater layoutInflater = (LayoutInflater) getContext()
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	private static final Integer RESOURCE_ID = R.layout.reservation_list_row;
	private final CommonLogic commonLogic;
	private final List<PassengerRecord> unhandledPassengerRecords = new LinkedList<PassengerRecord>();
	private final List<PassengerRecord> ridingPassengerRecords = new LinkedList<PassengerRecord>();
	private final List<OperationSchedule> remainingOperationSchedules = new LinkedList<OperationSchedule>();
	private final OperationSchedule operationSchedule;
	private final EnumSet<ItemType> visibleItemTypes = EnumSet
			.noneOf(ItemType.class);
	private final EnumSet<PayTiming> payTiming;
	private final Boolean isLastOperationSchedule;

	public ReservationArrayAdapter(Context context, CommonLogic commonLogic) {
		super(context, RESOURCE_ID);
		this.commonLogic = commonLogic;
		payTiming = commonLogic.getPayTiming();
		remainingOperationSchedules.addAll(commonLogic
				.getRemainingOperationSchedules());
		isLastOperationSchedule = (remainingOperationSchedules.size() <= 1);
		if (remainingOperationSchedules.isEmpty()) {
			operationSchedule = new OperationSchedule();
			return;
		}
		operationSchedule = remainingOperationSchedules.get(0);
		commonLogic.getStatusAccess().read(new VoidReader() {
			@Override
			public void read(Status status) {
				unhandledPassengerRecords
						.addAll(status.unhandledPassengerRecords);
				ridingPassengerRecords.addAll(status.ridingPassengerRecords);
			}
		});

		if (isLastOperationSchedule) {
			unhandledPassengerRecords.clear();
			visibleItemTypes.add(ItemType.RIDING_AND_NO_GET_OFF);
		}
		updateDataSet();
	}

	private void addPassengerRecordItem(PassengerRecord passengerRecord) {
		if (!passengerRecord.getReservation().isPresent()) {
			return;
		}
		if (isGetOn(passengerRecord)) {
			add(passengerRecord);
			return;
		}
		if (isGetOff(passengerRecord)) {
			add(passengerRecord);
			return;
		}
		if (visibleItemTypes.contains(ItemType.FUTURE_GET_ON)
				&& isFutureGetOn(passengerRecord)) {
			add(passengerRecord);
			return;
		}
		if (visibleItemTypes.contains(ItemType.RIDING_AND_NO_GET_OFF)
				&& isRidingAndNotGetOff(passengerRecord)) {
			add(passengerRecord);
			return;
		}
		if (visibleItemTypes.contains(ItemType.MISSED)
				&& isMissed(passengerRecord)) {
			add(passengerRecord);
			return;
		}
	}

	public void addUnexpectedReservation() {
		for (PassengerRecord passengerRecord : commonLogic
				.getUnhandledPassengerRecords()) {
			if (!unhandledPassengerRecords.contains(passengerRecord)) {
				unhandledPassengerRecords.add(passengerRecord);
				selectPassengerRecord(passengerRecord);
			}
		}
		updateDataSet();
	}

	public List<PassengerRecord> getNoGettingOffPassengerRecords() {
		List<PassengerRecord> passengerRecords = new LinkedList<PassengerRecord>();
		for (PassengerRecord passengerRecord : ridingPassengerRecords) {
			if (isGetOff(passengerRecord)
					&& !isSelectedPassengerRecord(passengerRecord)) {
				passengerRecords.add(passengerRecord);
			}
		}
		return passengerRecords;
	}

	public List<PassengerRecord> getNoGettingOnPassengerRecords() {
		List<PassengerRecord> passengerRecords = new LinkedList<PassengerRecord>();
		for (PassengerRecord passengerRecord : unhandledPassengerRecords) {
			if (isGetOn(passengerRecord)
					&& !isSelectedPassengerRecord(passengerRecord)) {
				passengerRecords.add(passengerRecord);
			}
		}
		return passengerRecords;
	}

	public List<PassengerRecord> getNoPaymentReservations() {
		List<PassengerRecord> passengerRecords = new LinkedList<PassengerRecord>();
		if (!payTiming.contains(PayTiming.GET_OFF)
				&& payTiming.contains(PayTiming.GET_ON)) {
			return passengerRecords;
		}
		for (PassengerRecord passengerRecord : getSelectedGetOffPassengerRecords()) {
			if (!passengerRecord.getPayment().isPresent()) {
				passengerRecords.add(passengerRecord);
			}
		}
		return passengerRecords;
	}

	public List<PassengerRecord> getSelectedGetOffPassengerRecords() {
		List<PassengerRecord> passengerRecords = new LinkedList<PassengerRecord>();
		for (PassengerRecord passengerRecord : ridingPassengerRecords) {
			if (isSelectedPassengerRecord(passengerRecord)) {
				passengerRecords.add(passengerRecord);
			}
		}
		return passengerRecords;
	}

	public List<PassengerRecord> getSelectedGetOnPassengerRecords() {
		List<PassengerRecord> passengerRecords = new LinkedList<PassengerRecord>();
		for (PassengerRecord passengerRecord : unhandledPassengerRecords) {
			if (isSelectedPassengerRecord(passengerRecord)) {
				passengerRecords.add(passengerRecord);
			}
		}
		return passengerRecords;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView != null ? convertView : layoutInflater.inflate(
				RESOURCE_ID, null);
		final PassengerRecord passengerRecord = getItem(position);
		if (!passengerRecord.getReservation().isPresent()) {
			return view;
		}
		final Reservation reservation = passengerRecord.getReservation().get();

		// 人数変更UI
		Spinner spinner = (Spinner) view
				.findViewById(R.id.change_passenger_count_spinner);
		List<String> passengerCounts = new LinkedList<String>();
		Integer max = reservation.getPassengerCount() + 10;
		for (Integer i = 1; i <= max; ++i) {
			passengerCounts.add(i + "名");
		}
		spinner.setAdapter(new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_list_item_1, passengerCounts));
		try {
			Integer count = passengerRecord.getPassengerCount() - 1;
			spinner.setSelection(count);
		} catch (RuntimeException e) {
			Log.w(TAG, e);
		}
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				passengerRecord.setPassengerCount(position + 1);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		// メモボタン
		Button memoButton = (Button) view.findViewById(R.id.memo_button);
		if (reservation.getMemo().isPresent()) {
			memoButton.setVisibility(View.VISIBLE);
			memoButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					commonLogic.postEvent(new MemoModalView.ShowEvent(
							reservation));
				}
			});
		} else {
			memoButton.setVisibility(View.GONE);
		}

		// 復路ボタン
		Button returnPathButton = (Button) view
				.findViewById(R.id.return_path_button);
		returnPathButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				commonLogic.postEvent(new ReturnPathModalView.ShowEvent(
						reservation));
			}
		});

		// 支払いボタン
		final ToggleButton paidButton = (ToggleButton) view
				.findViewById(R.id.paid_button);
		if (reservation.getPayment().equals(0)) {
			paidButton.setVisibility(View.GONE);
		} else if (payTiming.contains(PayTiming.GET_OFF)
				&& payTiming.contains(PayTiming.GET_ON)) {
			paidButton.setVisibility(View.VISIBLE);
		} else if (payTiming.contains(PayTiming.GET_OFF)
				&& ridingPassengerRecords.contains(passengerRecord)) {
			paidButton.setVisibility(View.VISIBLE);
		} else {
			paidButton.setVisibility(View.GONE);
		}

		if (passengerRecord.getPayment().isPresent()) {
			paidButton.setChecked(true);
		} else {
			paidButton.setChecked(false);
		}
		paidButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					passengerRecord.setPayment(reservation.getPayment());
				} else {
					passengerRecord.clearPayment();
				}
				notifyDataSetChanged();
			}
		});

		// 行の表示
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isSelectedPassengerRecord(passengerRecord)) {
					unselectPassengerRecord(passengerRecord);
					if (paidButton.isShown()) {
						paidButton.setChecked(false);
					}
				} else {
					selectPassengerRecord(passengerRecord);
					if (paidButton.isShown()) {
						paidButton.setChecked(true);
					}
				}
				notifyDataSetChanged();
			}
		});
		TextView userNameView = (TextView) view.findViewById(R.id.user_name);
		if (reservation.getUser().isPresent()) {
			User user = reservation.getUser().get();
			userNameView.setText(user.getLastName() + " " + user.getFirstName()
					+ " 様");
		} else {
			userNameView.setText("ID:" + reservation.getUserId() + " 様");
		}

		String text = "";
		if (isFutureGetOn(passengerRecord) || isMissed(passengerRecord)) {
			text += "[*乗]";
		} else if (isGetOn(passengerRecord)) {
			text += "[乗]";
		} else if (isRidingAndNotGetOff(passengerRecord)) {
			text += "[*降]";
		} else {
			text += "[降]";
		}

		if (commonLogic.isUnexpectedPassengerRecord(passengerRecord)) {
			text += " 飛び乗り ";
		} else {
			text += " 予約番号 " + reservation.getId();
		}
		TextView reservationIdView = (TextView) view
				.findViewById(R.id.reservation_id);
		reservationIdView.setText(text);

		if (isSelectedPassengerRecord(passengerRecord)) {
			view.setBackgroundColor(Color.CYAN); // TODO テーマ
		} else {
			view.setBackgroundColor(Color.TRANSPARENT);
		}
		return view;
	}

	public void hide(ItemType itemType) {
		visibleItemTypes.remove(itemType);
		updateDataSet();
	}

	private Boolean isFutureGetOn(PassengerRecord passengerRecord) {
		if (!passengerRecord.getReservation().isPresent()) {
			return false;
		}
		Reservation reservation = passengerRecord.getReservation().get();
		if (remainingOperationSchedules.size() <= 1) {
			return false;
		}
		for (OperationSchedule operationSchedule : remainingOperationSchedules
				.subList(1, remainingOperationSchedules.size())) {
			if (reservation.getDepartureScheduleId().isPresent()
					&& operationSchedule.getId().equals(
							reservation.getDepartureScheduleId().get())) {
				return true;
			}
		}
		return false;
	}

	private Boolean isGetOff(PassengerRecord passengerRecord) {
		if (!ridingPassengerRecords.contains(passengerRecord)) {
			return false;
		}
		if (!passengerRecord.getReservation().isPresent()
				|| !passengerRecord.getReservation().get()
						.getArrivalScheduleId().isPresent()) {
			return false;
		}
		return operationSchedule.getId().equals(
				passengerRecord.getReservation().get().getArrivalScheduleId()
						.get());
	}

	private Boolean isGetOn(PassengerRecord passengerRecord) {
		if (!unhandledPassengerRecords.contains(passengerRecord)) {
			return false;
		}
		if (!passengerRecord.getReservation().isPresent()
				|| !passengerRecord.getReservation().get()
						.getDepartureScheduleId().isPresent()) {
			return false;
		}
		return operationSchedule.getId().equals(
				passengerRecord.getReservation().get().getDepartureScheduleId()
						.get());
	}

	private Boolean isMissed(PassengerRecord passengerRecord) {
		if (!passengerRecord.getReservation().isPresent()) {
			return false;
		}
		if (ridingPassengerRecords.contains(passengerRecord)) {
			return false;
		}
		Reservation reservation = passengerRecord.getReservation().get();
		for (OperationSchedule operationSchedule : remainingOperationSchedules) {
			if (reservation.getDepartureScheduleId().isPresent()
					&& operationSchedule.getId().equals(
							reservation.getDepartureScheduleId().get())) {
				return false;
			}
		}
		return true;
	}

	private Boolean isRidingAndNotGetOff(PassengerRecord passengerRecord) {
		return ridingPassengerRecords.contains(passengerRecord)
				&& !isGetOff(passengerRecord);
	}

	public Boolean isSelectedPassengerRecord(
			final PassengerRecord passengerRecord) {
		return commonLogic.getStatusAccess().read(new Reader<Boolean>() {
			@Override
			public Boolean read(Status status) {
				return status.selectedPassengerRecords
						.contains(passengerRecord);
			}
		});
	}

	public void selectPassengerRecord(final PassengerRecord passengerRecord) {
		commonLogic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.selectedPassengerRecords.add(passengerRecord);
			}
		});
	}

	public void show(ItemType itemType) {
		visibleItemTypes.add(itemType);
		updateDataSet();
	}

	public void unselectPassengerRecord(final PassengerRecord passengerRecord) {
		commonLogic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.selectedPassengerRecords.remove(passengerRecord);
			}
		});
	}

	private void updateDataSet() {
		clear();
		for (PassengerRecord passengerRecord : unhandledPassengerRecords) {
			addPassengerRecordItem(passengerRecord);
		}
		for (PassengerRecord passengerRecord : ridingPassengerRecords) {
			addPassengerRecordItem(passengerRecord);
		}
		notifyDataSetChanged();
	}
}
