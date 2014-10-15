package com.kogasoftware.odt.invehicledevice.mockserver;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.Assert;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.joda.time.DateTime;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.OperationRecordJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.OperationScheduleJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.PassengerRecordJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.PlatformJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.ReservationJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.ServiceProviderJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.UserJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.VehicleNotificationJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceProvider;

public class MockServer extends Thread {
	private static final String TAG = MockServer.class.getSimpleName();
	final BasicHttpProcessor httpProcessor;
	final BasicHttpContext httpContext;
	final HttpService httpService;
	final HttpRequestHandlerRegistry registry;
	final ServerSocket serverSocket;
	final Integer port;

	public final String authenticationToken = "token_" + getId() + "_"
			+ RandomStringUtils.randomNumeric(5);

	final AtomicLong platformIdSeq = new AtomicLong();
	final AtomicLong operationScheduleIdSeq = new AtomicLong();
	final AtomicLong passengerRecordIdSeq = new AtomicLong();
	final AtomicLong reservationIdSeq = new AtomicLong();
	final AtomicLong operationRecordIdSeq = new AtomicLong();
	final AtomicLong vehicleNotificationIdSeq = new AtomicLong();
	final AtomicLong serviceProviderIdSeq = new AtomicLong();
	final AtomicLong userIdSeq = new AtomicLong();

	public final List<ServiceProviderJson> serviceProviders = Lists
			.newLinkedList();
	public final List<PlatformJson> platforms = Lists.newLinkedList();
	public final List<OperationScheduleJson> operationSchedules = Lists
			.newLinkedList();
	public final List<PassengerRecordJson> passengerRecords = Lists
			.newLinkedList();
	public final List<OperationRecordJson> operationRecords = Lists
			.newLinkedList();
	public final List<VehicleNotificationJson> vehicleNotifications = Lists
			.newLinkedList();
	public final List<UserJson> users = Lists.newLinkedList();
	public final List<ReservationJson> reservations = Lists.newLinkedList();
	public final List<ServiceUnitStatusLogJson> serviceUnitStatusLogs = Lists
			.newLinkedList();

	public VehicleNotificationJson addVehicleNotification(String body,
			String bodyRuby, Long notificationKind) {
		VehicleNotificationJson vn = new VehicleNotificationJson();
		vn.id = vehicleNotificationIdSeq.incrementAndGet();
		vn.body = body;
		vn.bodyRuby = bodyRuby;
		vn.notificationKind = notificationKind;
		vehicleNotifications.add(vn);
		return vn;
	}

	public PlatformJson addPlatform(String name) {
		PlatformJson p = new PlatformJson();
		p.id = platformIdSeq.incrementAndGet();
		p.name = name;
		p.address = "住所";
		p.latitude = BigDecimal.valueOf(70 + p.id);
		p.longitude = BigDecimal.valueOf(135 - p.id);
		platforms.add(p);
		return p;
	}

	public UserJson addUser(String name) {
		UserJson u = new UserJson();
		u.id = userIdSeq.incrementAndGet();
		Iterator<String> names = Splitter.on(" ").split(name).iterator();
		u.lastName = names.next();
		u.firstName = names.next();
		u.memo = "";
		u.wheelchair = false;
		u.neededCare = false;
		u.handicapped = false;
		users.add(u);
		return u;
	}

	public ServiceProviderJson addServiceProvider(String name) {
		ServiceProviderJson sp = new ServiceProviderJson();
		sp.id = serviceProviderIdSeq.incrementAndGet();
		sp.name = name;
		serviceProviders.add(sp);
		return sp;
	}

	public Pair<OperationScheduleJson, OperationScheduleJson> addOperationSchedule(
			PlatformJson departurePlatform, PlatformJson arrivalPlatform,
			List<UserJson> users, String arrivalEstimate,
			String departureEstimate, Integer minutes) {
		OperationRecordJson dor = new OperationRecordJson();
		OperationRecordJson aor = new OperationRecordJson();
		OperationScheduleJson dos = new OperationScheduleJson();
		OperationScheduleJson aos = new OperationScheduleJson();
		dor.id = operationRecordIdSeq.incrementAndGet();
		aor.id = operationRecordIdSeq.incrementAndGet();
		dos.id = operationScheduleIdSeq.incrementAndGet();
		aos.id = operationScheduleIdSeq.incrementAndGet();
		operationRecords.add(dor);
		operationRecords.add(aor);
		operationSchedules.add(dos);
		operationSchedules.add(aos);
		dos.operationRecord = dor;
		aos.operationRecord = aor;
		dor.operationScheduleId = dos.id;
		aor.operationScheduleId = aos.id;
		dos.platform = departurePlatform;
		aos.platform = arrivalPlatform;
		Integer dayOfYear = DateTime.now().getDayOfYear();
		dos.arrivalEstimate = DateTime.parse("2000-01-01T" + arrivalEstimate)
				.withDayOfYear(dayOfYear);
		dos.departureEstimate = DateTime.parse(
				"2000-01-01T" + departureEstimate).withDayOfYear(dayOfYear);
		aos.arrivalEstimate = dos.arrivalEstimate.plusMinutes(minutes);
		aos.departureEstimate = dos.departureEstimate.plusMinutes(minutes);
		dos.operationDate = dos.departureEstimate.toLocalDate();
		aos.operationDate = dos.operationDate;
		ReservationJson r = new ReservationJson();
		r.id = reservationIdSeq.incrementAndGet();
		reservations.add(r);
		dos.departureReservation = r;
		aos.arrivalReservation = r;
		r.departureScheduleId = dos.id;
		r.arrivalScheduleId = aos.id;
		for (UserJson u : users) {
			PassengerRecordJson pr = new PassengerRecordJson();
			pr.id = passengerRecordIdSeq.incrementAndGet();
			passengerRecords.add(pr);
			pr.reservationId = r.id;
			pr.userId = u.id;
			r.userId = u.id;
			r.fellowUsers.add(u);
			r.passengerRecords.add(pr);
		}
		return Pair.create(dos, aos);
	}

	public MockServer(Integer port) throws IOException {
		this.port = port;
		httpContext = new BasicHttpContext();
		httpProcessor = new BasicHttpProcessor();
		httpProcessor.addInterceptor(new ResponseDate());
		httpProcessor.addInterceptor(new ResponseServer());
		httpProcessor.addInterceptor(new ResponseContent());
		httpProcessor.addInterceptor(new ResponseConnControl());

		registry = new HttpRequestHandlerRegistry();
		registry.register(SignInHandler.PATH, new SignInHandler(this,
				authenticationToken));
		registry.register(VehicleNotificationsHandler.PATH,
				new VehicleNotificationsHandler(this, authenticationToken));
		registry.register(PassengerRecordsHandler.PATH,
				new PassengerRecordsHandler(this, authenticationToken));
		registry.register(OperationRecordsHandler.PATH,
				new OperationRecordsHandler(this, authenticationToken));
		registry.register(OperationSchedulesHandler.PATH,
				new OperationSchedulesHandler(this, authenticationToken));
		registry.register(ServiceUnitStatusLogHandler.PATH,
				new ServiceUnitStatusLogHandler(this, authenticationToken));
		registry.register(ServiceProviderHandler.PATH,
				new ServiceProviderHandler(this, authenticationToken));
		registry.register(NotFoundHandler.PATH, new NotFoundHandler());

		httpService = new HttpService(httpProcessor,
				new DefaultConnectionReuseStrategy(),
				new DefaultHttpResponseFactory());
		httpService.setHandlerResolver(registry);
		serverSocket = new ServerSocket(port, -1,
				Inet4Address.getByName(getHost()));
	}

	public String getHost() {
		if ("goldfish".equals(Build.HARDWARE)) {
			return "10.0.2.15"; // Androidエミュレーター
		} else {
			return "127.0.0.1";
		}
	}

	public String getUrl() {
		return "http://" + getHost() + ":" + port;
	}

	@Override
	public void interrupt() {
		super.interrupt();
		try {
			serverSocket.close();
		} catch (IOException e) {
		}
	}

	@Override
	public void start() {
		super.start();
		Stopwatch stopwatch = new Stopwatch().start();
		while (true) {
			if (stopwatch.elapsed(TimeUnit.SECONDS) > 30) {
				Assert.fail(getClass().getSimpleName() + "を起動できません");
			}
			HttpHead request = new HttpHead();
			try {
				request.setURI(new URI(getUrl()));
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
			HttpClient client = new DefaultHttpClient();
			try {
				client.execute(request);
			} catch (ClientProtocolException e) {
				continue;
			} catch (IOException e) {
				continue;
			} finally {
				client.getConnectionManager().shutdown();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			break;
		}
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			Socket socket;
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				if (!Thread.currentThread().isInterrupted()) {
					Log.e(TAG, "Unexpected exception", e);
				}
				return;
			}
			DefaultHttpServerConnection serverConnection = new PatchReadyHttpServerConnection();
			try {
				serverConnection.bind(socket, new BasicHttpParams());
				httpService.handleRequest(serverConnection, httpContext);
				serverConnection.shutdown();
			} catch (IOException e) {
				Log.e(TAG, "Unexpected exception", e);
			} catch (HttpException e) {
				Log.e(TAG, "Unexpected exception", e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					Log.e(TAG, "Unexpected exception", e);
				}
			}
		}
	}

	public void signIn(SQLiteDatabase database) {
		if (serviceProviders.isEmpty()) {
			addServiceProvider("S市");
		}
		ServiceProviderJson sp = serviceProviders.get(0);
		ContentValues spValues = new ContentValues();
		spValues.put(ServiceProvider.Columns.NAME, sp.name);
		database.insertOrThrow(ServiceProvider.TABLE_NAME, null, spValues);

		ContentValues ivdValues = new ContentValues();
		ivdValues.put(InVehicleDevice.Columns.URL, getUrl());
		ivdValues.put(InVehicleDevice.Columns.AUTHENTICATION_TOKEN,
				authenticationToken);
		ivdValues.put(InVehicleDevice.Columns.LOGIN, "l");
		ivdValues.put(InVehicleDevice.Columns.PASSWORD, "p");
		database.insertOrThrow(InVehicleDevice.TABLE_NAME, null, ivdValues);
	}

	public void signIn(ContentResolver contentResolver) {
		if (serviceProviders.isEmpty()) {
			addServiceProvider("S市");
		}
		ContentValues values = new ContentValues();
		values.put(InVehicleDevice.Columns.URL, getUrl());
		values.put(InVehicleDevice.Columns.LOGIN, "valid_login");
		values.put(InVehicleDevice.Columns.PASSWORD, "valid_password");
		contentResolver.insert(InVehicleDevice.CONTENT.URI, values);
	}
}
