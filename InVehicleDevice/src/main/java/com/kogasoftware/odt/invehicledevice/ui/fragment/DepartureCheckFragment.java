package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.util.LinkedList;

import org.joda.time.DateTime;

import android.content.ContentResolver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedules;
import com.kogasoftware.odt.invehicledevice.utils.FragmentUtils;

public class DepartureCheckFragment
		extends
			OperationSchedulesAndPassengerRecordsFragment {
	private static final String TAG = DepartureCheckFragment.class
			.getSimpleName();
	private static final String OPERATION_SCHEDULE_ID_KEY = "operation_schedule_id";

	public static DepartureCheckFragment newInstance(Long operationScheduleId) {
		DepartureCheckFragment fragment = new DepartureCheckFragment();
		Bundle args = new Bundle();
		args.putLong(OPERATION_SCHEDULE_ID_KEY, operationScheduleId);
		fragment.setArguments(args);
		return fragment;
	}

	private ContentResolver contentResolver;
	private Long operationScheduleId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.departure_check_fragment, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		contentResolver = getActivity().getContentResolver();
		Bundle args = getArguments();
		operationScheduleId = args.getLong(OPERATION_SCHEDULE_ID_KEY);
	}

	@Override
	protected void onOperationSchedulesAndPassengerRecordsLoadFinished(
			Phase phase, LinkedList<OperationSchedule> operationSchedules,
			LinkedList<PassengerRecord> passengerRecords) {
		final OperationSchedule operationSchedule = OperationSchedule.getById(
				operationSchedules, operationScheduleId);
		if (operationSchedule == null) {
			FragmentUtils.hide(this);
			return;
		}
		View view = getView();
		Button departureButton = (Button) view
				.findViewById(R.id.departure_button);
		Button closeButton = (Button) view
				.findViewById(R.id.departure_check_close_button);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentUtils.hide(DepartureCheckFragment.this);
			}
		});
		if (phase == Phase.PLATFORM_GET_OFF) {
			closeButton.setText("降車一覧に戻る");
		} else {
			closeButton.setText("乗車一覧に戻る");
		}
		if (OperationSchedule.getCurrentOffset(operationSchedules, 1) == null) {
			departureButton.setText("確定する");
		} else {
			departureButton.setText("出発する");
		}
		departureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				operationSchedule.departedAt = DateTime.now();
				FragmentUtils.hide(DepartureCheckFragment.this);
				new Thread() {
					@Override
					public void run() {
						contentResolver.insert(OperationSchedules.CONTENT.URI,
								operationSchedule.toContentValues());
					}
				}.start();
			}
		});
	}
}
