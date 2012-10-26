package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.invehicledevice.ui.fragment.OperationScheduleListFragment.State;

public class OperationScheduleListFragment extends ApplicationFragment<State> {
	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final List<OperationSchedule> operationSchedules;

		public State(List<OperationSchedule> operationSchedules) {
			this.operationSchedules = operationSchedules;
		}

		public List<OperationSchedule> getOperationSchedules() {
			return operationSchedules;
		}
	}

	public OperationScheduleListFragment() {
		super(true);
	}

	public static OperationScheduleListFragment newInstance(
			List<OperationSchedule> operationSchedules) {
		return newInstance(new OperationScheduleListFragment(), new State(
				operationSchedules));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = onCreateViewHelper(inflater, container,
				R.layout.operation_schedule_list_fragment,
				R.id.operation_schedule_list_close_button);
		ListView listView = ((FlickUnneededListView) view
				.findViewById(R.id.operation_schedule_list_view)).getListView();

		OperationScheduleArrayAdapter adapter = new OperationScheduleArrayAdapter(
				getActivity(), getState().getOperationSchedules());
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

		return view;
	}
}
