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
import com.kogasoftware.odt.webapi.WebAPI;

public class ServiceUnitStatusLog extends Model {
	private static final long serialVersionUID = 4948261639769588725L;
	public static final String JSON_NAME = "service_unit_status_log";
	public static final String CONTROLLER_NAME = "service_unit_status_logs";

	public static class URL {
		public static final String ROOT = "/" + CONTROLLER_NAME;
	}

	public ServiceUnitStatusLog() {
	}

	public ServiceUnitStatusLog(JSONObject jsonObject) throws JSONException, ParseException {
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setId(parseLong(jsonObject, "id"));
		setLatitude(parseBigDecimal(jsonObject, "latitude"));
		setLongitude(parseBigDecimal(jsonObject, "longitude"));
		setOrientation(parseOptionalLong(jsonObject, "orientation"));
		setServiceUnitId(parseOptionalLong(jsonObject, "service_unit_id"));
		setStatus(parseOptionalLong(jsonObject, "status"));
		setTemperature(parseOptionalLong(jsonObject, "temperature"));
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

	public static List<ServiceUnitStatusLog> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ServiceUnitStatusLog>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		List<ServiceUnitStatusLog> models = new LinkedList<ServiceUnitStatusLog>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new ServiceUnitStatusLog(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	public static class ResponseConverter implements
			WebAPI.ResponseConverter<ServiceUnitStatusLog> {
		@Override
		public ServiceUnitStatusLog convert(byte[] rawResponse) throws JSONException, ParseException {
			return new ServiceUnitStatusLog(new JSONObject(new String(rawResponse)));
		}
	}

	public static class ListResponseConverter implements
			WebAPI.ResponseConverter<List<ServiceUnitStatusLog>> {
		@Override
		public List<ServiceUnitStatusLog> convert(byte[] rawResponse) throws JSONException,
				ParseException {
			JSONArray array = new JSONArray(new String(rawResponse));
			List<ServiceUnitStatusLog> models = new LinkedList<ServiceUnitStatusLog>();
			for (Integer i = 0; i < array.length(); ++i) {
				if (array.isNull(i)) {
					continue;
				}
				JSONObject object = array.getJSONObject(i);
				models.add(new ServiceUnitStatusLog(object));
			}
			return models;
		}
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

	private Long id = 0L;

	public Long getId() {
		return wrapNull(id);
	}

	public void setId(Long id) {
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

	private Optional<Long> orientation = Optional.<Long>absent();

	public Optional<Long> getOrientation() {
		return wrapNull(orientation);
	}

	public void setOrientation(Optional<Long> orientation) {
		this.orientation = wrapNull(orientation);
	}

	public void setOrientation(Long orientation) {
		this.orientation = Optional.fromNullable(orientation);
	}

	public void clearOrientation() {
		this.orientation = Optional.<Long>absent();
	}

	private Optional<Long> serviceUnitId = Optional.<Long>absent();

	public Optional<Long> getServiceUnitId() {
		return wrapNull(serviceUnitId);
	}

	public void setServiceUnitId(Optional<Long> serviceUnitId) {
		this.serviceUnitId = wrapNull(serviceUnitId);
	}

	public void setServiceUnitId(Long serviceUnitId) {
		this.serviceUnitId = Optional.fromNullable(serviceUnitId);
	}

	public void clearServiceUnitId() {
		this.serviceUnitId = Optional.<Long>absent();
	}

	private Optional<Long> status = Optional.<Long>absent();

	public Optional<Long> getStatus() {
		return wrapNull(status);
	}

	public void setStatus(Optional<Long> status) {
		this.status = wrapNull(status);
	}

	public void setStatus(Long status) {
		this.status = Optional.fromNullable(status);
	}

	public void clearStatus() {
		this.status = Optional.<Long>absent();
	}

	private Optional<Long> temperature = Optional.<Long>absent();

	public Optional<Long> getTemperature() {
		return wrapNull(temperature);
	}

	public void setTemperature(Optional<Long> temperature) {
		this.temperature = wrapNull(temperature);
	}

	public void setTemperature(Long temperature) {
		this.temperature = Optional.fromNullable(temperature);
	}

	public void clearTemperature() {
		this.temperature = Optional.<Long>absent();
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
