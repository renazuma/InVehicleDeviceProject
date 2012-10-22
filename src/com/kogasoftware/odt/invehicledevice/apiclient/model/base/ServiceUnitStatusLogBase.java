package com.kogasoftware.odt.invehicledevice.apiclient.model.base;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;
import com.kogasoftware.odt.apiclient.DefaultApiClient;
import com.kogasoftware.odt.apiclient.ApiClient.ResponseConverter;
import com.kogasoftware.odt.invehicledevice.apiclient.model.*;

@SuppressWarnings("unused")
public abstract class ServiceUnitStatusLogBase extends Model {
	private static final long serialVersionUID = 4166838134573111029L;
	public static final ResponseConverter<ServiceUnitStatusLog> RESPONSE_CONVERTER = new ResponseConverter<ServiceUnitStatusLog>() {
		@Override
		public ServiceUnitStatusLog convert(byte[] rawResponse) throws JSONException {
			return parse(DefaultApiClient.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<ServiceUnitStatusLog>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<ServiceUnitStatusLog>>() {
		@Override
		public List<ServiceUnitStatusLog> convert(byte[] rawResponse) throws JSONException {
			return parseList(DefaultApiClient.parseJSONArray(rawResponse));
		}
	};
	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setId(parseInteger(jsonObject, "id"));
		setLatitude(parseBigDecimal(jsonObject, "latitude"));
		setLongitude(parseBigDecimal(jsonObject, "longitude"));
		setOffline(parseOptionalBoolean(jsonObject, "offline"));
		setOfflineTime(parseOptionalDate(jsonObject, "offline_time"));
		setOrientation(parseOptionalInteger(jsonObject, "orientation"));
		setServiceUnitId(parseOptionalInteger(jsonObject, "service_unit_id"));
		setTemperature(parseOptionalInteger(jsonObject, "temperature"));
		setServiceUnit(ServiceUnit.parse(jsonObject, "service_unit"));

		setUpdatedAt(parseDate(jsonObject, "updated_at"));
	}

	public static Optional<ServiceUnitStatusLog> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static ServiceUnitStatusLog parse(JSONObject jsonObject) throws JSONException {
		ServiceUnitStatusLog model = new ServiceUnitStatusLog();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<ServiceUnitStatusLog> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ServiceUnitStatusLog>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<ServiceUnitStatusLog> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<ServiceUnitStatusLog> models = new LinkedList<ServiceUnitStatusLog>();
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
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("latitude", toJSON(getLatitude()));
		jsonObject.put("longitude", toJSON(getLongitude()));
		jsonObject.put("offline", toJSON(getOffline()));
		jsonObject.put("offline_time", toJSON(getOfflineTime()));
		jsonObject.put("orientation", toJSON(getOrientation()));
		jsonObject.put("service_unit_id", toJSON(getServiceUnitId()));
		jsonObject.put("temperature", toJSON(getTemperature()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		if (getServiceUnit().isPresent()) {
			if (recursive) {
				jsonObject.put("service_unit", getServiceUnit().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("service_unit_id", toJSON(getServiceUnit().get().getId()));
			}
		}
		return jsonObject;
	}

	@Override
	public ServiceUnitStatusLog cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		refreshUpdatedAt();
		this.createdAt = wrapNull(createdAt);
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	private BigDecimal latitude = BigDecimal.ZERO;

	public BigDecimal getLatitude() {
		return wrapNull(latitude);
	}

	public void setLatitude(BigDecimal latitude) {
		refreshUpdatedAt();
		this.latitude = wrapNull(latitude);
	}

	private BigDecimal longitude = BigDecimal.ZERO;

	public BigDecimal getLongitude() {
		return wrapNull(longitude);
	}

	public void setLongitude(BigDecimal longitude) {
		refreshUpdatedAt();
		this.longitude = wrapNull(longitude);
	}

	private Optional<Boolean> offline = Optional.absent();

	public Optional<Boolean> getOffline() {
		return wrapNull(offline);
	}

	public void setOffline(Optional<Boolean> offline) {
		refreshUpdatedAt();
		this.offline = wrapNull(offline);
	}

	public void setOffline(Boolean offline) {
		setOffline(Optional.fromNullable(offline));
	}

	public void clearOffline() {
		setOffline(Optional.<Boolean>absent());
	}

	private Optional<Date> offlineTime = Optional.absent();

	public Optional<Date> getOfflineTime() {
		return wrapNull(offlineTime);
	}

	public void setOfflineTime(Optional<Date> offlineTime) {
		refreshUpdatedAt();
		this.offlineTime = wrapNull(offlineTime);
	}

	public void setOfflineTime(Date offlineTime) {
		setOfflineTime(Optional.fromNullable(offlineTime));
	}

	public void clearOfflineTime() {
		setOfflineTime(Optional.<Date>absent());
	}

	private Optional<Integer> orientation = Optional.absent();

	public Optional<Integer> getOrientation() {
		return wrapNull(orientation);
	}

	public void setOrientation(Optional<Integer> orientation) {
		refreshUpdatedAt();
		this.orientation = wrapNull(orientation);
	}

	public void setOrientation(Integer orientation) {
		setOrientation(Optional.fromNullable(orientation));
	}

	public void clearOrientation() {
		setOrientation(Optional.<Integer>absent());
	}

	private Optional<Integer> serviceUnitId = Optional.absent();

	public Optional<Integer> getServiceUnitId() {
		return wrapNull(serviceUnitId);
	}

	public void setServiceUnitId(Optional<Integer> serviceUnitId) {
		refreshUpdatedAt();
		this.serviceUnitId = wrapNull(serviceUnitId);
	}

	public void setServiceUnitId(Integer serviceUnitId) {
		setServiceUnitId(Optional.fromNullable(serviceUnitId));
	}

	public void clearServiceUnitId() {
		setServiceUnitId(Optional.<Integer>absent());
	}

	private Optional<Integer> temperature = Optional.absent();

	public Optional<Integer> getTemperature() {
		return wrapNull(temperature);
	}

	public void setTemperature(Optional<Integer> temperature) {
		refreshUpdatedAt();
		this.temperature = wrapNull(temperature);
	}

	public void setTemperature(Integer temperature) {
		setTemperature(Optional.fromNullable(temperature));
	}

	public void clearTemperature() {
		setTemperature(Optional.<Integer>absent());
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
		setServiceUnit(Optional.fromNullable(serviceUnit));
	}

	public void clearServiceUnit() {
		setServiceUnit(Optional.<ServiceUnit>absent());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(createdAt)
			.append(id)
			.append(latitude)
			.append(longitude)
			.append(offline)
			.append(offlineTime)
			.append(orientation)
			.append(serviceUnitId)
			.append(temperature)
			.append(updatedAt)
			.append(serviceUnit)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof ServiceUnitStatusLogBase)) {
			return false;
		}
		ServiceUnitStatusLogBase other = (ServiceUnitStatusLogBase) obj;
		return new EqualsBuilder()
			.append(createdAt, other.createdAt)
			.append(id, other.id)
			.append(latitude, other.latitude)
			.append(longitude, other.longitude)
			.append(offline, other.offline)
			.append(offlineTime, other.offlineTime)
			.append(orientation, other.orientation)
			.append(serviceUnitId, other.serviceUnitId)
			.append(temperature, other.temperature)
			.append(updatedAt, other.updatedAt)
			.append(serviceUnit, other.serviceUnit)
			.isEquals();
	}
}
