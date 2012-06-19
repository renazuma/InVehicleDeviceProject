package com.kogasoftware.odt.webapi.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class ServiceUnitStatusLog extends Model {
	private static final long serialVersionUID = 5130444674534893698L;

	public ServiceUnitStatusLog() {
	}

	public ServiceUnitStatusLog(JSONObject jsonObject) throws JSONException {
		try {
			fillMembers(this, jsonObject);
		} catch (ParseException e) {
			throw new JSONException(e.toString() + "\n" + ExceptionUtils.getStackTrace(e));
		}
	}

	public static void fillMembers(ServiceUnitStatusLog model, JSONObject jsonObject) throws JSONException, ParseException {
		model.setCreatedAt(parseDate(jsonObject, "created_at"));
		model.setId(parseInteger(jsonObject, "id"));
		model.setLatitude(parseBigDecimal(jsonObject, "latitude"));
		model.setLongitude(parseBigDecimal(jsonObject, "longitude"));
		model.setOffline(parseOptionalBoolean(jsonObject, "offline"));
		model.setOfflineTime(parseOptionalDate(jsonObject, "offline_time"));
		model.setOrientation(parseOptionalInteger(jsonObject, "orientation"));
		model.setServiceUnitId(parseOptionalInteger(jsonObject, "service_unit_id"));
		model.setTemperature(parseOptionalInteger(jsonObject, "temperature"));
		model.setUpdatedAt(parseDate(jsonObject, "updated_at"));
		model.setServiceUnit(ServiceUnit.parse(jsonObject, "service_unit"));
	}

	public static Optional<ServiceUnitStatusLog> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<ServiceUnitStatusLog> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.of(new ServiceUnitStatusLog(jsonObject));
	}

	public static LinkedList<ServiceUnitStatusLog> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ServiceUnitStatusLog>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<ServiceUnitStatusLog> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<ServiceUnitStatusLog> models = new LinkedList<ServiceUnitStatusLog>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new ServiceUnitStatusLog(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	protected JSONObject toJSONObject(Boolean recursive, Integer depth) throws JSONException {
		depth++;
		if (depth > MAX_RECURSE_DEPTH) {
			return new JSONObject();
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("latitude", toJSON(getLatitude()));
		jsonObject.put("longitude", toJSON(getLongitude()));
		jsonObject.put("offline", toJSON(getOffline().orNull()));
		jsonObject.put("offline_time", toJSON(getOfflineTime().orNull()));
		jsonObject.put("orientation", toJSON(getOrientation().orNull()));
		jsonObject.put("service_unit_id", toJSON(getServiceUnitId().orNull()));
		jsonObject.put("temperature", toJSON(getTemperature().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		if (getServiceUnit().isPresent()) {
			if (recursive) {
				jsonObject.put("service_unit", getServiceUnit().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("service_unit_id", toJSON(getServiceUnit().get().getId()));
			}
		}
		return jsonObject;
	}

	private void writeObject(ObjectOutputStream objectOutputStream)
			throws IOException {
		try {
			objectOutputStream.writeObject(toJSONObject(true).toString());
		} catch (JSONException e) {
			throw new IOException(e.toString() + "\n" + ExceptionUtils.getStackTrace(e));
		}
	}

	private void readObject(ObjectInputStream objectInputStream)
		throws IOException, ClassNotFoundException {
		Object object = objectInputStream.readObject();
		if (!(object instanceof String)) {
			return;
		}
		String jsonString = (String) object;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			fillMembers(this, jsonObject);
		} catch (JSONException e) {
			throw new IOException(e);
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public ServiceUnitStatusLog cloneByJSON() throws JSONException {
		return new ServiceUnitStatusLog(toJSONObject(true));
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private BigDecimal latitude = BigDecimal.ZERO;

	public BigDecimal getLatitude() {
		return wrapNull(latitude);
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = wrapNull(latitude);
	}

	private BigDecimal longitude = BigDecimal.ZERO;

	public BigDecimal getLongitude() {
		return wrapNull(longitude);
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = wrapNull(longitude);
	}

	private Optional<Boolean> offline = Optional.absent();

	public Optional<Boolean> getOffline() {
		return wrapNull(offline);
	}

	public void setOffline(Optional<Boolean> offline) {
		this.offline = wrapNull(offline);
	}

	public void setOffline(Boolean offline) {
		this.offline = Optional.fromNullable(offline);
	}

	public void clearOffline() {
		this.offline = Optional.absent();
	}

	private Optional<Date> offlineTime = Optional.absent();

	public Optional<Date> getOfflineTime() {
		return wrapNull(offlineTime);
	}

	public void setOfflineTime(Optional<Date> offlineTime) {
		this.offlineTime = wrapNull(offlineTime);
	}

	public void setOfflineTime(Date offlineTime) {
		this.offlineTime = Optional.fromNullable(offlineTime);
	}

	public void clearOfflineTime() {
		this.offlineTime = Optional.absent();
	}

	private Optional<Integer> orientation = Optional.absent();

	public Optional<Integer> getOrientation() {
		return wrapNull(orientation);
	}

	public void setOrientation(Optional<Integer> orientation) {
		this.orientation = wrapNull(orientation);
	}

	public void setOrientation(Integer orientation) {
		this.orientation = Optional.fromNullable(orientation);
	}

	public void clearOrientation() {
		this.orientation = Optional.absent();
	}

	private Optional<Integer> serviceUnitId = Optional.absent();

	public Optional<Integer> getServiceUnitId() {
		return wrapNull(serviceUnitId);
	}

	public void setServiceUnitId(Optional<Integer> serviceUnitId) {
		this.serviceUnitId = wrapNull(serviceUnitId);
	}

	public void setServiceUnitId(Integer serviceUnitId) {
		this.serviceUnitId = Optional.fromNullable(serviceUnitId);
	}

	public void clearServiceUnitId() {
		this.serviceUnitId = Optional.absent();
	}

	private Optional<Integer> temperature = Optional.absent();

	public Optional<Integer> getTemperature() {
		return wrapNull(temperature);
	}

	public void setTemperature(Optional<Integer> temperature) {
		this.temperature = wrapNull(temperature);
	}

	public void setTemperature(Integer temperature) {
		this.temperature = Optional.fromNullable(temperature);
	}

	public void clearTemperature() {
		this.temperature = Optional.absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<ServiceUnit> serviceUnit = Optional.absent();

	public Optional<ServiceUnit> getServiceUnit() {
		return wrapNull(serviceUnit);
	}

	public void setServiceUnit(Optional<ServiceUnit> serviceUnit) {
		this.serviceUnit = wrapNull(serviceUnit);
	}

	public void setServiceUnit(ServiceUnit serviceUnit) {
		this.serviceUnit = Optional.fromNullable(serviceUnit);
	}

	public void clearServiceUnit() {
		this.serviceUnit = Optional.absent();
	}
}
