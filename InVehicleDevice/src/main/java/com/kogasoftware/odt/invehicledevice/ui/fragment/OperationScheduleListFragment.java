package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		ListView listView = ((FlickUnneededListView) getView().findViewById(
				R.id.operation_schedule_list_view)).getListView();

		OperationScheduleArrayAdapter adapter = new OperationScheduleArrayAdapter(
				getActivity(), getService(),
				getState().getOperation().operationSchedules, getState()
						.getOperation().passengerRecords);
		listView.setAdapter(adapter);

		// 未運行の運行スケジュールまでスクロールする
		Boolean found = false;
		Integer count = adapter.getCount();
		for (Integer i = 0; i < count; ++i) {
			if (!adapter.getItem(i).isDeparted()) {
				listView.setSelectionFromTop(i, 0);
				found = true;
				break;
			}
		}
		if (!found && count >= 1) {
			listView.setSelectionFromTop(count - 1, 0);
		}
	}
}
