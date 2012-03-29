package com.kogasoftware.odt.webapi.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class ServiceUnitStatusLog extends Model {
	private static final long serialVersionUID = 8602413632451689935L;

	public ServiceUnitStatusLog() {
	}

	public ServiceUnitStatusLog(JSONObject jsonObject) throws JSONException, ParseException {
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setId(parseInteger(jsonObject, "id"));
		setLatitude(parseBigDecimal(jsonObject, "latitude"));
		setLongitude(parseBigDecimal(jsonObject, "longitude"));
		setOrientation(parseOptionalInteger(jsonObject, "orientation"));
		setServiceUnitId(parseOptionalInteger(jsonObject, "service_unit_id"));
		setStatus(parseOptionalInteger(jsonObject, "status"));
		setTemperature(parseOptionalInteger(jsonObject, "temperature"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setServiceUnit(ServiceUnit.parse(jsonObject, "service_unit"));
		if (getServiceUnit().isPresent()) {
			setServiceUnitId(getServiceUnit().get().getId());
		}
	}

	public static Optional<ServiceUnitStatusLog> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<ServiceUnitStatusLog>absent();
		}
		return Optional.<ServiceUnitStatusLog>of(new ServiceUnitStatusLog(jsonObject.getJSONObject(key)));
	}

	public static LinkedList<ServiceUnitStatusLog> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ServiceUnitStatusLog>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
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
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("latitude", toJSON(getLatitude()));
		jsonObject.put("longitude", toJSON(getLongitude()));
		jsonObject.put("orientation", toJSON(getOrientation().orNull()));
		jsonObject.put("service_unit_id", toJSON(getServiceUnitId().orNull()));
		jsonObject.put("status", toJSON(getStatus().orNull()));
		jsonObject.put("temperature", toJSON(getTemperature().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("service_unit", toJSON(getServiceUnit()));
		if (getServiceUnit().isPresent()) {
			jsonObject.put("service_unit_id", toJSON(getServiceUnit().get().getId()));
		}
		return jsonObject;
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

	private Optional<Integer> orientation = Optional.<Integer>absent();

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
		this.orientation = Optional.<Integer>absent();
	}

	private Optional<Integer> serviceUnitId = Optional.<Integer>absent();

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
		this.serviceUnitId = Optional.<Integer>absent();
	}

	private Optional<Integer> status = Optional.<Integer>absent();

	public Optional<Integer> getStatus() {
		return wrapNull(status);
	}

	public void setStatus(Optional<Integer> status) {
		this.status = wrapNull(status);
	}

	public void setStatus(Integer status) {
		this.status = Optional.fromNullable(status);
	}

	public void clearStatus() {
		this.status = Optional.<Integer>absent();
	}

	private Optional<Integer> temperature = Optional.<Integer>absent();

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
		this.temperature = Optional.<Integer>absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<ServiceUnit> serviceUnit = Optional.<ServiceUnit>absent();

	public Optional<ServiceUnit> getServiceUnit() {
		return wrapNull(serviceUnit);
	}

	public void setServiceUnit(Optional<ServiceUnit> serviceUnit) {
		this.serviceUnit = wrapNull(serviceUnit);
	}

	public void setServiceUnit(ServiceUnit serviceUnit) {
		this.serviceUnit = Optional.<ServiceUnit>fromNullable(serviceUnit);
	}

	public void clearServiceUnit() {
		this.serviceUnit = Optional.<ServiceUnit>absent();
	}
}
