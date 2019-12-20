package com.kogasoftware.odt.invehicledevice.view.fragment.modal;

import org.joda.time.DateTime;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;

/**
 * 到着チェック画面
 */
public class ArrivalCheckFragment extends Fragment {
	private static final String OPERATION_SCHEDULE_KEY = "operation_schedule";

	public static Fragment newInstance(OperationSchedule operationSchedule) {
		ArrivalCheckFragment fragment = new ArrivalCheckFragment();
		Bundle args = new Bundle();
		args.putSerializable(OPERATION_SCHEDULE_KEY, operationSchedule);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		final ContentResolver contentResolver = getActivity()
				.getContentResolver();
		final OperationSchedule operationSchedule = (OperationSchedule) getArguments()
				.getSerializable(OPERATION_SCHEDULE_KEY);
		TextView commentTextView = (TextView) view
				.findViewById(R.id.arrival_check_comment_text_view);
		Button closeButton = (Button) view
				.findViewById(R.id.arrival_check_close_button);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragments.hide(ArrivalCheckFragment.this);
			}
		});
		Button arrivalButton = (Button) view.findViewById(R.id.arrival_button);
		arrivalButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Fragments.hide(ArrivalCheckFragment.this);
				new Thread() {
					@Override
					public void run() {
						operationSchedule.arrivedAt = DateTime.now();
						ContentValues values = operationSchedule
								.toContentValues();
						contentResolver.insert(OperationSchedule.CONTENT.URI,
								values);
					};
				}.start();
			}
		});
		commentTextView.setText(operationSchedule.name);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.arrival_check_fragment, container,
				false);
	}
}
