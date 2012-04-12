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
	private static final long serialVersionUID = 1318749105397980260L;

	public InVehicleDevice() {
	}

	public InVehicleDevice(JSONObject jsonObject) throws JSONException, ParseException {
		setAuthenticationToken(parseOptionalString(jsonObject, "authentication_token"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setCurrentSignInAt(parseOptionalDate(jsonObject, "current_sign_in_at"));
		setCurrentSignInIp(parseOptionalString(jsonObject, "current_sign_in_ip"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setEncryptedPassword(parseString(jsonObject, "encrypted_password"));
		setId(parseInteger(jsonObject, "id"));
		setLastSignInAt(parseOptionalDate(jsonObject, "last_sign_in_at"));
		setLastSignInIp(parseOptionalString(jsonObject, "last_sign_in_ip"));
		setLogin(parseString(jsonObject, "login"));
		setModelName(parseString(jsonObject, "model_name"));
		setRememberCreatedAt(parseOptionalDate(jsonObject, "remember_created_at"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setSignInCount(parseOptionalInteger(jsonObject, "sign_in_count"));
		setTypeNumber(parseString(jsonObject, "type_number"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setServiceUnits(ServiceUnit.parseList(jsonObject, "service_units"));
		setVehicleNotifications(VehicleNotification.parseList(jsonObject, "vehicle_notifications"));
	}

	public static Optional<InVehicleDevice> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<InVehicleDevice>absent();
		}
		return Optional.<InVehicleDevice>of(new InVehicleDevice(jsonObject.getJSONObject(key)));
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
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("current_sign_in_at", toJSON(getCurrentSignInAt().orNull()));
		jsonObject.put("current_sign_in_ip", toJSON(getCurrentSignInIp().orNull()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("encrypted_password", toJSON(getEncryptedPassword()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("last_sign_in_at", toJSON(getLastSignInAt().orNull()));
		jsonObject.put("last_sign_in_ip", toJSON(getLastSignInIp().orNull()));
		jsonObject.put("login", toJSON(getLogin()));
		jsonObject.put("model_name", toJSON(getModelName()));
		jsonObject.put("remember_created_at", toJSON(getRememberCreatedAt().orNull()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("sign_in_count", toJSON(getSignInCount().orNull()));
		jsonObject.put("type_number", toJSON(getTypeNumber()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("service_units", toJSON(getServiceUnits()));
		jsonObject.put("vehicle_notifications", toJSON(getVehicleNotifications()));
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

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> currentSignInAt = Optional.<Date>absent();

	public Optional<Date> getCurrentSignInAt() {
		return wrapNull(currentSignInAt);
	}

	public void setCurrentSignInAt(Optional<Date> currentSignInAt) {
		this.currentSignInAt = wrapNull(currentSignInAt);
	}

	public void setCurrentSignInAt(Date currentSignInAt) {
		this.currentSignInAt = Optional.fromNullable(currentSignInAt);
	}

	public void clearCurrentSignInAt() {
		this.currentSignInAt = Optional.<Date>absent();
	}

	private Optional<String> currentSignInIp = Optional.<String>absent();

	public Optional<String> getCurrentSignInIp() {
		return wrapNull(currentSignInIp);
	}

	public void setCurrentSignInIp(Optional<String> currentSignInIp) {
		this.currentSignInIp = wrapNull(currentSignInIp);
	}

	public void setCurrentSignInIp(String currentSignInIp) {
		this.currentSignInIp = Optional.fromNullable(currentSignInIp);
	}

	public void clearCurrentSignInIp() {
		this.currentSignInIp = Optional.<String>absent();
	}

	private Optional<Date> deletedAt = Optional.<Date>absent();

	public Optional<Date> getDeletedAt() {
		return wrapNull(deletedAt);
	}

	public void setDeletedAt(Optional<Date> deletedAt) {
		this.deletedAt = wrapNull(deletedAt);
	}

	public void setDeletedAt(Date deletedAt) {
		this.deletedAt = Optional.fromNullable(deletedAt);
	}

	public void clearDeletedAt() {
		this.deletedAt = Optional.<Date>absent();
	}

	private String encryptedPassword = "";

	public String getEncryptedPassword() {
		return wrapNull(encryptedPassword);
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = wrapNull(encryptedPassword);
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private Optional<Date> lastSignInAt = Optional.<Date>absent();

	public Optional<Date> getLastSignInAt() {
		return wrapNull(lastSignInAt);
	}

	public void setLastSignInAt(Optional<Date> lastSignInAt) {
		this.lastSignInAt = wrapNull(lastSignInAt);
	}

	public void setLastSignInAt(Date lastSignInAt) {
		this.lastSignInAt = Optional.fromNullable(lastSignInAt);
	}

	public void clearLastSignInAt() {
		this.lastSignInAt = Optional.<Date>absent();
	}

	private Optional<String> lastSignInIp = Optional.<String>absent();

	public Optional<String> getLastSignInIp() {
		return wrapNull(lastSignInIp);
	}

	public void setLastSignInIp(Optional<String> lastSignInIp) {
		this.lastSignInIp = wrapNull(lastSignInIp);
	}

	public void setLastSignInIp(String lastSignInIp) {
		this.lastSignInIp = Optional.fromNullable(lastSignInIp);
	}

	public void clearLastSignInIp() {
		this.lastSignInIp = Optional.<String>absent();
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

	private Optional<Date> rememberCreatedAt = Optional.<Date>absent();

	public Optional<Date> getRememberCreatedAt() {
		return wrapNull(rememberCreatedAt);
	}

	public void setRememberCreatedAt(Optional<Date> rememberCreatedAt) {
		this.rememberCreatedAt = wrapNull(rememberCreatedAt);
	}

	public void setRememberCreatedAt(Date rememberCreatedAt) {
		this.rememberCreatedAt = Optional.fromNullable(rememberCreatedAt);
	}

	public void clearRememberCreatedAt() {
		this.rememberCreatedAt = Optional.<Date>absent();
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

	private Optional<Integer> signInCount = Optional.<Integer>absent();

	public Optional<Integer> getSignInCount() {
		return wrapNull(signInCount);
	}

	public void setSignInCount(Optional<Integer> signInCount) {
		this.signInCount = wrapNull(signInCount);
	}

	public void setSignInCount(Integer signInCount) {
		this.signInCount = Optional.fromNullable(signInCount);
	}

	public void clearSignInCount() {
		this.signInCount = Optional.<Integer>absent();
	}

	private String typeNumber = "";

	public String getTypeNumber() {
		return wrapNull(typeNumber);
	}

	public void setTypeNumber(String typeNumber) {
		this.typeNumber = wrapNull(typeNumber);
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
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
