package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher.OnUpdatePassengerRecordListener;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundReader;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.invehicledevice.ui.fragment.OperationScheduleListFragment.State;

public class OperationScheduleListFragment extends ApplicationFragment<State>
		implements OnUpdatePassengerRecordListener {
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

	private ListView listView;

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
		listView = ((FlickUnneededListView) getView().findViewById(
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
		getService().getEventDispatcher().addOnUpdatePassengerRecordListener(
				this);
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

	@Override
	public void onResume() {
		super.onResume();
		StringBuilder message = new StringBuilder("OperationSchedule: ");
		for (OperationScheduleArrayAdapter adapter : getOperationScheduleArrayAdapter()
				.asSet()) {
			for (Integer i = 0; i < adapter.getCount(); i++) {
				if (!i.equals(0)) {
					message.append(",");
				}
				OperationSchedule item = adapter.getItem(i);
				message.append(item.getPlatform().or(new Platform()).getName());
			}
		}
	}

	public Optional<OperationScheduleArrayAdapter> getOperationScheduleArrayAdapter() {
		Object obj = listView.getAdapter();
		if (obj instanceof OperationScheduleArrayAdapter) {
			return Optional.of((OperationScheduleArrayAdapter) obj);
		}
		return Optional.absent();
	}

	@Override
	public void onUpdatePassengerRecord(PassengerRecord passengerRecord) {
		if (!isAdded()) {
			return;
		}
		for (OperationScheduleArrayAdapter adapter : getOperationScheduleArrayAdapter()
				.asSet()) {
			adapter.updatePassengerRecord(passengerRecord);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getService().getEventDispatcher()
				.removeOnUpdatePassengerRecordListener(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		final InVehicleDeviceService service = getService();
		for (OperationScheduleArrayAdapter adapter : getOperationScheduleArrayAdapter()
				.asSet()) {
			if (!adapter.isOperationScheduleArrivalDepartureChanged()) {
				return;
			}
			getService().getLocalStorage().read(
					new BackgroundReader<Operation>() {
						@Override
						public Operation readInBackground(
								LocalData localData) {
							return localData.operation;
						}

						@Override
						public void onRead(Operation operation) {
					service.getEventDispatcher()
									.dispatchUpdateOperation(operation);
						}
					});
		}
	}
}
