package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.invehicledevice.ui.fragment.OperationScheduleListFragment.State;

public class OperationScheduleListFragment extends ApplicationFragment<State> {
	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final Operation operation;

		public State(Operation operation) {
			this.operation = operation;
		}

		public Operation getOperation() {
			return operation;
		}
	}

	public OperationScheduleListFragment() {
		setRemoveOnUpdateOperation(true);
	}

	public static OperationScheduleListFragment newInstance(Operation operation) {
		return newInstance(new OperationScheduleListFragment(), new State(
				operation));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return onCreateViewHelper(inflater, container,
				R.layout.operation_schedule_list_fragment,
				R.id.operation_schedule_list_close_button);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final ListView listView = ((FlickUnneededListView) getView().findViewById(
				R.id.operation_schedule_list_view)).getListView();

		final OperationScheduleArrayAdapter adapter = new OperationScheduleArrayAdapter(
				getActivity(), getService(),
				getState().getOperation().operationSchedules, getState()
						.getOperation().passengerRecords);
		listView.setAdapter(adapter);

		// 未運行の運行スケジュールまでスクロールする
		Boolean found = false;
		Integer count = adapter.getCount();
		for (Integer i = 0; i < count; ++i) {
			if (!adapter.getItem(i).isDeparted()) {
				listView.setSelection(i);
				found = true;
				break;
			}
		}
		if (!found && count >= 1) {
			listView.setSelectionFromTop(count - 1, 0);
		}

		final Button showPassengersButton = (Button) getView().findViewById(
				R.id.operation_schedule_list_show_passengers_button);
		final Button hidePassengersButton = (Button) getView().findViewById(
				R.id.operation_schedule_list_hide_passengers_button);
		final Button closeButton = (Button) getView().findViewById(
				R.id.operation_schedule_list_close_button);
		showPassengersButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
                Integer selection = listView.getFirstVisiblePosition();
				hidePassengersButton.setVisibility(View.VISIBLE);
				showPassengersButton.setVisibility(View.GONE);
				closeButton.setVisibility(View.GONE);
				adapter.showPassengerRecords();
                listView.setSelection(selection);
			}
		});
		hidePassengersButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
                Integer selection = listView.getFirstVisiblePosition();
                hidePassengersButton.setVisibility(View.GONE);
				showPassengersButton.setVisibility(View.VISIBLE);
				closeButton.setVisibility(View.VISIBLE);
				adapter.hidePassengerRecords();
                listView.setSelection(selection);
			}
		});
	}
}
