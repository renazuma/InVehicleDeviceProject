package com.kogasoftware.odt.webapi.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class InVehicleDevice extends Model {
	private static final long serialVersionUID = 4998713565467314809L;

	public InVehicleDevice() {
	}

	public InVehicleDevice(JSONObject jsonObject) throws JSONException, ParseException {
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
		if (getServiceProvider().isPresent()) {
			setServiceProviderId(getServiceProvider().get().getId());
		}
		setServiceUnits(ServiceUnit.parseList(jsonObject, "service_units"));
		setVehicleNotifications(VehicleNotification.parseList(jsonObject, "vehicle_notifications"));
	}

	public static Optional<InVehicleDevice> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<InVehicleDevice>absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<InVehicleDevice> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.<InVehicleDevice>of(new InVehicleDevice(jsonObject));
	}

	public static LinkedList<InVehicleDevice> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<InVehicleDevice>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<InVehicleDevice> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<InVehicleDevice> models = new LinkedList<InVehicleDevice>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new InVehicleDevice(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("authentication_token", toJSON(getAuthenticationToken().orNull()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("login", toJSON(getLogin()));
		jsonObject.put("model_name", toJSON(getModelName()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("type_number", toJSON(getTypeNumber()));
		jsonObject.put("audit_comment", toJSON(getAuditComment().orNull()));
		jsonObject.put("password", toJSON(getPassword().orNull()));
		jsonObject.put("password_confirmation", toJSON(getPasswordConfirmation().orNull()));
		jsonObject.put("remember_me", toJSON(getRememberMe().orNull()));

	   		jsonObject.put("service_provider", toJSON(getServiceProvider()));
	   		if (getServiceProvider().isPresent()) {
				jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
			}

		if (getServiceUnits().size() > 0) {

	   		jsonObject.put("service_units", toJSON(getServiceUnits()));
		}

		if (getVehicleNotifications().size() > 0) {

	   		jsonObject.put("vehicle_notifications", toJSON(getVehicleNotifications()));
		}

		return jsonObject;
	}

	private Optional<String> authenticationToken = Optional.<String>absent();

	public Optional<String> getAuthenticationToken() {
		return wrapNull(authenticationToken);
	}

	public void setAuthenticationToken(Optional<String> authenticationToken) {
		this.authenticationToken = wrapNull(authenticationToken);
	}

	public void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = Optional.fromNullable(authenticationToken);
	}

	public void clearAuthenticationToken() {
		this.authenticationToken = Optional.<String>absent();
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

	private Optional<Integer> serviceProviderId = Optional.<Integer>absent();

	public Optional<Integer> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Optional<Integer> serviceProviderId) {
		this.serviceProviderId = wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Integer serviceProviderId) {
		this.serviceProviderId = Optional.fromNullable(serviceProviderId);
	}

	public void clearServiceProviderId() {
		this.serviceProviderId = Optional.<Integer>absent();
	}

	private String typeNumber = "";

	public String getTypeNumber() {
		return wrapNull(typeNumber);
	}

	public void setTypeNumber(String typeNumber) {
		this.typeNumber = wrapNull(typeNumber);
	}

	private Optional<String> auditComment = Optional.<String>absent();

	public Optional<String> getAuditComment() {
		return wrapNull(auditComment);
	}

	public void setAuditComment(Optional<String> auditComment) {
		this.auditComment = wrapNull(auditComment);
	}

	public void setAuditComment(String auditComment) {
		this.auditComment = Optional.fromNullable(auditComment);
	}

	public void clearAuditComment() {
		this.auditComment = Optional.<String>absent();
	}

	private Optional<String> password = Optional.<String>absent();

	public Optional<String> getPassword() {
		return wrapNull(password);
	}

	public void setPassword(Optional<String> password) {
		this.password = wrapNull(password);
	}

	public void setPassword(String password) {
		this.password = Optional.fromNullable(password);
	}

	public void clearPassword() {
		this.password = Optional.<String>absent();
	}

	private Optional<String> passwordConfirmation = Optional.<String>absent();

	public Optional<String> getPasswordConfirmation() {
		return wrapNull(passwordConfirmation);
	}

	public void setPasswordConfirmation(Optional<String> passwordConfirmation) {
		this.passwordConfirmation = wrapNull(passwordConfirmation);
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = Optional.fromNullable(passwordConfirmation);
	}

	public void clearPasswordConfirmation() {
		this.passwordConfirmation = Optional.<String>absent();
	}

	private Optional<String> rememberMe = Optional.<String>absent();

	public Optional<String> getRememberMe() {
		return wrapNull(rememberMe);
	}

	public void setRememberMe(Optional<String> rememberMe) {
		this.rememberMe = wrapNull(rememberMe);
	}

	public void setRememberMe(String rememberMe) {
		this.rememberMe = Optional.fromNullable(rememberMe);
	}

	public void clearRememberMe() {
		this.rememberMe = Optional.<String>absent();
	}

	private Optional<ServiceProvider> serviceProvider = Optional.<ServiceProvider>absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = Optional.<ServiceProvider>fromNullable(serviceProvider);
	}

	public void clearServiceProvider() {
		this.serviceProvider = Optional.<ServiceProvider>absent();
	}

	private LinkedList<ServiceUnit> serviceUnits = new LinkedList<ServiceUnit>();

	public List<ServiceUnit> getServiceUnits() {
		return new LinkedList<ServiceUnit>(wrapNull(serviceUnits));
	}

	public void setServiceUnits(List<ServiceUnit> serviceUnits) {
		this.serviceUnits = new LinkedList<ServiceUnit>(wrapNull(serviceUnits));
	}

	public void clearServiceUnits() {
		this.serviceUnits = new LinkedList<ServiceUnit>();
	}

	private LinkedList<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();

	public List<VehicleNotification> getVehicleNotifications() {
		return new LinkedList<VehicleNotification>(wrapNull(vehicleNotifications));
	}

	public void setVehicleNotifications(List<VehicleNotification> vehicleNotifications) {
		this.vehicleNotifications = new LinkedList<VehicleNotification>(wrapNull(vehicleNotifications));
	}

	public void clearVehicleNotifications() {
		this.vehicleNotifications = new LinkedList<VehicleNotification>();
	}
}
