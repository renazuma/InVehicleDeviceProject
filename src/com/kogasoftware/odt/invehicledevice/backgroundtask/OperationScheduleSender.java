package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.kogasoftware.odt.invehicledevice.CommonLogic;
import com.kogasoftware.odt.invehicledevice.Status;
import com.kogasoftware.odt.invehicledevice.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class OperationScheduleSender implements Runnable {
	private final CommonLogic commonLogic;

	public OperationScheduleSender(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	@Override
	public void run() {
		try {
			send(commonLogic,
					true,
					commonLogic.getStatusAccess().read(
							new Reader<List<OperationSchedule>>() {
								@Override
								public List<OperationSchedule> read(
										Status status) {
									return new LinkedList<OperationSchedule>(
											status.sendLists.arrivalOperationSchedules);
								}
							}));
			send(commonLogic,
					false,
					commonLogic.getStatusAccess().read(
							new Reader<List<OperationSchedule>>() {
								@Override
								public List<OperationSchedule> read(
										Status status) {
									return new LinkedList<OperationSchedule>(
											status.sendLists.departureOperationSchedules);
								}
							}));

		} catch (WebAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void send(CommonLogic commonLogic, final Boolean arrival,
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
				commonLogic.getDataSource().arrivalOperationSchedule(
						operationSchedule, callback);
			} else {
				commonLogic.getDataSource().departureOperationSchedule(
						operationSchedule, callback);
			}
			if (!succeed.get()) {
				return;
			}

			commonLogic.getStatusAccess().write(new Writer() {
				@Override
				public void write(Status status) {
					if (arrival) {
						status.sendLists.arrivalOperationSchedules
								.remove(operationSchedule);
					} else {
						status.sendLists.departureOperationSchedules
								.remove(operationSchedule);
					}
				}
			});
		}
	}
}
