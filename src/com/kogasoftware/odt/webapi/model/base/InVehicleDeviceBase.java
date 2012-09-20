package com.kogasoftware.odt.webapi.model.base;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;
import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.ResponseConverter;
import com.kogasoftware.odt.webapi.model.*;

@SuppressWarnings("unused")
public abstract class InVehicleDeviceBase extends Model {
	private static final long serialVersionUID = 4326828688997805850L;
	public static final ResponseConverter<InVehicleDevice> RESPONSE_CONVERTER = new ResponseConverter<InVehicleDevice>() {
		@Override
		public InVehicleDevice convert(byte[] rawResponse) throws JSONException {
			return parse(WebAPI.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<InVehicleDevice>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<InVehicleDevice>>() {
		@Override
		public List<InVehicleDevice> convert(byte[] rawResponse) throws JSONException {
			return parseList(WebAPI.parseJSONArray(rawResponse));
		}
	};
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setAuthenticationToken(parseOptionalString(jsonObject, "authentication_token"));
		setId(parseInteger(jsonObject, "id"));
		setLogin(parseString(jsonObject, "login"));
		setModelName(parseString(jsonObject, "model_name"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setTypeNumber(parseString(jsonObject, "type_number"));
		setAuditComment(parseOptionalString(jsonObject, "audit_comment"));
		setPassword(parseOptionalString(jsonObject, "password"));
		setPasswordConfirmation(parseOptionalString(jsonObject, "password_confirmation"));
		setRememberMe(parseOptionalString(jsonObject, "remember_me"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		setServiceUnits(ServiceUnit.parseList(jsonObject, "service_units"));
		setVehicleNotifications(VehicleNotification.parseList(jsonObject, "vehicle_notifications"));
	}

	public static Optional<InVehicleDevice> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static InVehicleDevice parse(JSONObject jsonObject) throws JSONException {
		InVehicleDevice model = new InVehicleDevice();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<InVehicleDevice> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<InVehicleDevice>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<InVehicleDevice> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<InVehicleDevice> models = new LinkedList<InVehicleDevice>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(parse(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	protected JSONObject toJSONObject(Boolean recursive, Integer depth) throws JSONException {
		if (depth > MAX_RECURSE_DEPTH) {
			return new JSONObject();
		}
		Integer nextDepth = depth + 1;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("authentication_token", toJSON(getAuthenticationToken()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("login", toJSON(getLogin()));
		jsonObject.put("model_name", toJSON(getModelName()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId()));
		jsonObject.put("type_number", toJSON(getTypeNumber()));
		jsonObject.put("audit_comment", toJSON(getAuditComment()));
		jsonObject.put("password", toJSON(getPassword()));
		jsonObject.put("password_confirmation", toJSON(getPasswordConfirmation()));
		jsonObject.put("remember_me", toJSON(getRememberMe()));
		if (getServiceProvider().isPresent()) {
			if (recursive) {
				jsonObject.put("service_provider", getServiceProvider().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
			}
		}
		if (getServiceUnits().size() > 0 && recursive) {
			jsonObject.put("service_units", toJSON(getServiceUnits(), true, nextDepth));
		}
		if (getVehicleNotifications().size() > 0 && recursive) {
			jsonObject.put("vehicle_notifications", toJSON(getVehicleNotifications(), true, nextDepth));
		}
		return jsonObject;
	}

	@Override
	public InVehicleDevice cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
	}

	private Optional<String> authenticationToken = Optional.absent();

	public Optional<String> getAuthenticationToken() {
		return wrapNull(authenticationToken);
	}

	public void setAuthenticationToken(Optional<String> authenticationToken) {
		this.authenticationToken = wrapNull(authenticationToken);
	}

	public void setAuthenticationToken(String authenticationToken) {
		setAuthenticationToken(Optional.fromNullable(authenticationToken));
	}

	public void clearAuthenticationToken() {
		setAuthenticationToken(Optional.<String>absent());
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private String login = "";

	public String getLogin() {
		return wrapNull(login);
	}

	public void setLogin(String login) {
		this.login = wrapNull(login);
	}

	private String modelName = "";

	public String getModelName() {
		return wrapNull(modelName);
	}

	public void setModelName(String modelName) {
		this.modelName = wrapNull(modelName);
	}

	private Optional<Integer> serviceProviderId = Optional.absent();

	public Optional<Integer> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Optional<Integer> serviceProviderId) {
		this.serviceProviderId = wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Integer serviceProviderId) {
		setServiceProviderId(Optional.fromNullable(serviceProviderId));
	}

	public void clearServiceProviderId() {
		setServiceProviderId(Optional.<Integer>absent());
	}

	private String typeNumber = "";

	public String getTypeNumber() {
		return wrapNull(typeNumber);
	}

	public void setTypeNumber(String typeNumber) {
		this.typeNumber = wrapNull(typeNumber);
	}

	private Optional<String> auditComment = Optional.absent();

	public Optional<String> getAuditComment() {
		return wrapNull(auditComment);
	}

	public void setAuditComment(Optional<String> auditComment) {
		this.auditComment = wrapNull(auditComment);
	}

	public void setAuditComment(String auditComment) {
		setAuditComment(Optional.fromNullable(auditComment));
	}

	public void clearAuditComment() {
		setAuditComment(Optional.<String>absent());
	}

	private Optional<String> password = Optional.absent();

	public Optional<String> getPassword() {
		return wrapNull(password);
	}

	public void setPassword(Optional<String> password) {
		this.password = wrapNull(password);
	}

	public void setPassword(String password) {
		setPassword(Optional.fromNullable(password));
	}

	public void clearPassword() {
		setPassword(Optional.<String>absent());
	}

	private Optional<String> passwordConfirmation = Optional.absent();

	public Optional<String> getPasswordConfirmation() {
		return wrapNull(passwordConfirmation);
	}

	public void setPasswordConfirmation(Optional<String> passwordConfirmation) {
		this.passwordConfirmation = wrapNull(passwordConfirmation);
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		setPasswordConfirmation(Optional.fromNullable(passwordConfirmation));
	}

	public void clearPasswordConfirmation() {
		setPasswordConfirmation(Optional.<String>absent());
	}

	private Optional<String> rememberMe = Optional.absent();

	public Optional<String> getRememberMe() {
		return wrapNull(rememberMe);
	}

	public void setRememberMe(Optional<String> rememberMe) {
		this.rememberMe = wrapNull(rememberMe);
	}

	public void setRememberMe(String rememberMe) {
		setRememberMe(Optional.fromNullable(rememberMe));
	}

	public void clearRememberMe() {
		setRememberMe(Optional.<String>absent());
	}

	private Optional<ServiceProvider> serviceProvider = Optional.<ServiceProvider>absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		setServiceProvider(Optional.fromNullable(serviceProvider));
	}

	public void clearServiceProvider() {
		setServiceProvider(Optional.<ServiceProvider>absent());
	}

	private LinkedList<ServiceUnit> serviceUnits = new LinkedList<ServiceUnit>();

	public List<ServiceUnit> getServiceUnits() {
		return wrapNull(serviceUnits);
	}

	public void setServiceUnits(Iterable<ServiceUnit> serviceUnits) {
		this.serviceUnits = wrapNull(serviceUnits);
	}

	public void clearServiceUnits() {
		setServiceUnits(new LinkedList<ServiceUnit>());
	}

	private LinkedList<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();

	public List<VehicleNotification> getVehicleNotifications() {
		return wrapNull(vehicleNotifications);
	}

	public void setVehicleNotifications(Iterable<VehicleNotification> vehicleNotifications) {
		this.vehicleNotifications = wrapNull(vehicleNotifications);
	}

	public void clearVehicleNotifications() {
		setVehicleNotifications(new LinkedList<VehicleNotification>());
	}
}
