package com.kogasoftware.odt.invehicledevice.mockserver;

import java.io.IOException;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import com.google.common.collect.ComparisonChain;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.OperationScheduleJson;

public class OperationSchedulesHandler extends MockServerRequestHandler {
	public static String PATH = "/in_vehicle_devices/operation_schedules";

	public OperationSchedulesHandler(MockServer server,
			String authenticationToken) {
		super(server, authenticationToken);
	}

	@Override
	protected void handleGet(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		SortedSet<OperationScheduleJson> oss = new TreeSet<OperationScheduleJson>(
				new Comparator<OperationScheduleJson>() {
					@Override
					public int compare(OperationScheduleJson lhs,
							OperationScheduleJson rhs) {
						return ComparisonChain
								.start()
								.compare(lhs.arrivalEstimate.getMillis(),
										rhs.arrivalEstimate.getMillis())
								.compare(lhs.id, rhs.id).result();
					}
				});
		oss.addAll(server.operationSchedules);
		try {
			response.setEntity(new StringEntity(JSON.writeValueAsString(oss),
					"UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.setStatusCode(200);
	}
}
