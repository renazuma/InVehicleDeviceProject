package com.kogasoftware.odt.invehicledevice.preference;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.util.EntityUtils;

import android.os.Build;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kogasoftware.odt.invehicledevice.apiclient.model.InVehicleDevice;

public class MockServer extends Thread {
	private final BasicHttpProcessor httpProcessor;
	private final BasicHttpContext httpContext;
	private final HttpService httpService;
	private final HttpRequestHandlerRegistry registry;
	private final CountDownLatch waitForBind = new CountDownLatch(1);
	private final ServerSocket serverSocket;

	public class SignInHandler implements HttpRequestHandler {
		@Override
		public void handle(HttpRequest request, HttpResponse response,
				HttpContext context) throws HttpException, IOException {
			if (!(request instanceof HttpEntityEnclosingRequest)
					|| !request.getRequestLine().getMethod().equals("POST")) {
				throw new IOException("no POST request");
			}
			String requestEntity = EntityUtils
					.toString(((HttpEntityEnclosingRequest) request)
							.getEntity());
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(requestEntity);
			JsonNode loginNode = jsonNode.path(InVehicleDevice.UNDERSCORE)
					.path("login");
			JsonNode passwordNode = jsonNode.path(InVehicleDevice.UNDERSCORE)
					.path("password");
			if (loginNode.isTextual()
					&& loginNode.asText().equals("valid_login")
					&& passwordNode.isTextual()
					&& passwordNode.asText().equals("valid_password")) {
				// OK
				response.setStatusCode(201);
				response.setEntity(new StringEntity(
						"{\"authentication_token\":\"token\"}"));
			} else {
				// NG
				response.setStatusCode(401);
				response.setEntity(new StringEntity("{}"));
			}
		}
	}

	public void startAndWaitForBind() throws InterruptedException {
		start();
		waitForBind.await();
	}

	public MockServer() throws IOException {
		httpContext = new BasicHttpContext();
		httpProcessor = new BasicHttpProcessor();
		httpProcessor.addInterceptor(new ResponseDate());
		httpProcessor.addInterceptor(new ResponseServer());
		httpProcessor.addInterceptor(new ResponseContent());
		httpProcessor.addInterceptor(new ResponseConnControl());
		registry = new HttpRequestHandlerRegistry();
		registry.register("/in_vehicle_devices/sign_in.json",
				new SignInHandler());
		httpService = new HttpService(httpProcessor,
				new DefaultConnectionReuseStrategy(),
				new DefaultHttpResponseFactory());
		httpService.setHandlerResolver(registry);
		serverSocket = new ServerSocket(12345, -1,
				Inet4Address.getByName(getLocalServerHost()));
	}

	public static String getLocalServerHost() {
		if ("goldfish".equals(Build.HARDWARE)) {
			// Androidエミュレーター
			return "10.0.2.15";
		} else {
			return "127.0.0.1";
		}
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
	public void run() {
		try {
			waitForBind.countDown();
			while (!Thread.currentThread().isInterrupted()) {
				Socket socket = serverSocket.accept();
				try {
					DefaultHttpServerConnection serverConnection = new DefaultHttpServerConnection();
					serverConnection.bind(socket, new BasicHttpParams());
					httpService.handleRequest(serverConnection, httpContext);
					serverConnection.shutdown();
				} catch (HttpException e) {
				} finally {
					socket.close();
				}
			}
		} catch (IOException e) {
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
			}
		}
	}
}
