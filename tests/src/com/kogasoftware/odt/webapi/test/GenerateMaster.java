package com.kogasoftware.odt.webapi.test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import android.util.Log;

import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Operator;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.ServiceProvider;
import com.kogasoftware.odt.webapi.model.User;

public class GenerateMaster {
	private WebTestAPI api;
	private boolean succeed;
	private ServiceProvider serviceProvider;
	private Operator operator;
	private InVehicleDevice inVehicleDevice;

	public GenerateMaster() {
		api = new WebTestAPI();
	}
	
	public WebTestAPI getTestAPI() {
		return api;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public InVehicleDevice getInVehicleDevice() {
		return inVehicleDevice;
	}
	
	public boolean cleanDatabase() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		succeed = false;
		api.cleanDatabase(new WebAPICallback<Void>() {
			@Override
			public void onSucceed(int reqkey, int statusCode, Void result) {
				succeed = true;
				latch.countDown();
			}
			
			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
				latch.countDown();
			}
			
			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
		});

		latch.await();
		
		return succeed;
	}
	
	public ServiceProvider createServiceProvider() throws Exception {
		SyncCall<ServiceProvider> sc = new SyncCall<ServiceProvider>() {
			@Override
			public int run() throws Exception {
				ServiceProvider obj = new ServiceProvider();
				obj.setName("もぎ市");
				
				return api.createServiceProvider(obj, this);
			}
		};
		
		this.serviceProvider = sc.getResult();
		
		return this.serviceProvider;
	}

	public Operator createOperator() throws Exception {
		// オブジェクトをひとつ生成
		SyncCall<Operator> sc = new SyncCall<Operator>() {
			@Override
			public int run() throws Exception {
				Operator obj = new Operator();
				obj.setLogin("operator1");
				obj.setPassword("pass");
				obj.setPasswordConfirmation("pass");
				obj.setFirstName("もぎ");
				obj.setLastName("もぎぞう");
				obj.setServiceProvider(serviceProvider);
				
				return api.createOperator(obj, this);
			}
		};
		
		this.operator = sc.getResult();
		return this.operator;
	}
	
	public InVehicleDevice createInVehicleDevice() throws Exception {
		SyncCall<InVehicleDevice> c = new SyncCall<InVehicleDevice>() {
			@Override
			public int run() throws Exception {
				InVehicleDevice ivd = new InVehicleDevice();
				ivd.setLogin("ivd1");
				ivd.setPassword("ivdpass");
				ivd.setPasswordConfirmation("ivdpass");
				ivd.setModelName("モデル名");
				ivd.setTypeNumber("車種");
				ivd.setServiceProvider(serviceProvider);
				
				Log.d("GenerateMaster", "send:" + ivd.toJSONObject().toString());
				return api.createInVehicleDevice(ivd, this);
			}
		};
		
		this.inVehicleDevice = c.getResult();
		Log.d("GenerateMaster", "recv:" + this.inVehicleDevice.toJSONObject().toString());
		Log.d("GenerateMaster", "token:" + this.inVehicleDevice.getAuthenticationToken().orNull());
		return this.inVehicleDevice;
	}

	public Platform createPlatform(final String name, final String nameRuby) throws Exception {
		SyncCall<Platform> sc = new SyncCall<Platform>() {
			@Override
			public int run() throws Exception {
				Platform obj = new Platform();
				obj.setName(name);
				obj.setNameRuby(nameRuby);
				obj.setServiceProvider(serviceProvider);

				return api.createPlatform(obj, this);
			}
		};

		return sc.getResult();		
	}

	public User createUser(final String login, final String firstName, final String lastName) throws Exception {
		SyncCall<User> sc = new SyncCall<User>() {
			@Override
			public int run() throws Exception {
				User obj = new User();
				obj.setLogin(login);
				obj.setFirstName(firstName);
				obj.setLastName(lastName);
				obj.setFirstNameRuby("よみ");
				obj.setLastNameRuby("よみ");
				obj.setBirthday(new Date());
				obj.setAddress("住所");
				obj.setTelephoneNumber("000");
				obj.setServiceProvider(serviceProvider);

				return api.createUser(obj, this);
			}
		};

		return sc.getResult();		
	}
	
}
