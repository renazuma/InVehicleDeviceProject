package com.kogasoftware.odt.invehicledevice.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class OperationScheduleSender extends LogicUser implements Runnable {
	@Override
	public void run() {
		if (!getLogic().isPresent()) {
			return;
		}
		final Logic logic = getLogic().get();
		try {
			send(logic,
					true,
					logic.getStatusAccess().read(
							new Reader<List<OperationSchedule>>() {
								@Override
								public List<OperationSchedule> read(
										Status status) {
									return new LinkedList<OperationSchedule>(
											status.arrivalOperationSchedules);
								}
							}));
			send(logic,
					false,
					logic.getStatusAccess().read(
							new Reader<List<OperationSchedule>>() {
								@Override
								public List<OperationSchedule> read(
										Status status) {
									return new LinkedList<OperationSchedule>(
											status.departureOperationSchedules);
								}
							}));

		} catch (WebAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void send(Logic logic, final Boolean arrival,
			final List<OperationSchedule> operationSchedules)
			throws WebAPIException {
		for (final OperationSchedule operationSchedule : operationSchedules) {
			final AtomicBoolean succeed = new AtomicBoolean(false);
			WebAPICallback<OperationSchedule> callback = new WebAPICallback<OperationSchedule>() {
				@Override
				public void onException(int reqkey, WebAPIException ex) {
				}

				@Override
				public void onFailed(int reqkey, int statusCode, String response) {
				}

				@Override
				public void onSucceed(int reqkey, int statusCode,
						OperationSchedule result) {
					succeed.set(true);
				}
			};
			if (arrival) {
				logic.getDataSource().arrivalOperationSchedule(
						operationSchedule, callback);
			} else {
				logic.getDataSource().departureOperationSchedule(
						operationSchedule, callback);
			}
			if (!succeed.get()) {
				return;
			}

			logic.getStatusAccess().write(new Writer() {
				@Override
				public void write(Status status) {
					if (arrival) {
						status.arrivalOperationSchedules
								.remove(operationSchedule);
					} else {
						status.departureOperationSchedules
								.remove(operationSchedule);
					}
				}
			});
		}
	}
}
