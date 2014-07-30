package com.kogasoftware.odt.invehicledevice.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.utils.Fragments;

public class PlatformMemoFragment extends Fragment {
	private static final String OPERATION_SCHEDULE_KEY = "operation_schedule";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		OperationSchedule operationSchedule = (OperationSchedule) getArguments()
				.getSerializable(OPERATION_SCHEDULE_KEY);
		Button closeButton = (Button) view
				.findViewById(R.id.platform_memo_close_button);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragments.hide(PlatformMemoFragment.this);
			}
		});
		TextView platformMemoTextView = (TextView) view
				.findViewById(R.id.platform_memo_text_view);
		platformMemoTextView.setText(operationSchedule.memo);
	}

	public static PlatformMemoFragment newInstance(
			OperationSchedule operationSchedule) {
		PlatformMemoFragment fragment = new PlatformMemoFragment();
		Bundle args = new Bundle();
		args.putSerializable(OPERATION_SCHEDULE_KEY, operationSchedule);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.platform_memo_fragment, container,
				false);
	}
}
