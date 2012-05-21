package com.kogasoftware.odt.webapi.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class OperationRecord extends Model {
	private static final long serialVersionUID = 6439856943165817552L;

	public OperationRecord() {
	}

	public OperationRecord(JSONObject jsonObject) throws JSONException, ParseException {
		setArrivedAt(parseOptionalDate(jsonObject, "arrived_at"));
		setArrivedAtOffline(parseOptionalBoolean(jsonObject, "arrived_at_offline"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDepartedAt(parseOptionalDate(jsonObject, "departed_at"));
		setDepartedAtOffline(parseOptionalBoolean(jsonObject, "departed_at_offline"));
		setId(parseInteger(jsonObject, "id"));
		setOperationScheduleId(parseOptionalInteger(jsonObject, "operation_schedule_id"));
		setServiceUnitId(parseOptionalInteger(jsonObject, "service_unit_id"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setOperationSchedule(OperationSchedule.parse(jsonObject, "operation_schedule"));
		if (getOperationSchedule().isPresent()) {
			setOperationScheduleId(getOperationSchedule().get().getId());
		}
		setServiceUnit(ServiceUnit.parse(jsonObject, "service_unit"));
		if (getServiceUnit().isPresent()) {
			setServiceUnitId(getServiceUnit().get().getId());
		}
	}

	public static Optional<OperationRecord> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<OperationRecord> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.of(new OperationRecord(jsonObject));
	}

	public static LinkedList<OperationRecord> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<OperationRecord>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<OperationRecord> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<OperationRecord> models = new LinkedList<OperationRecord>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new OperationRecord(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("arrived_at", toJSON(getArrivedAt().orNull()));
		jsonObject.put("arrived_at_offline", toJSON(getArrivedAtOffline().orNull()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("departed_at", toJSON(getDepartedAt().orNull()));
		jsonObject.put("departed_at_offline", toJSON(getDepartedAtOffline().orNull()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("operation_schedule_id", toJSON(getOperationScheduleId().orNull()));
		jsonObject.put("service_unit_id", toJSON(getServiceUnitId().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));

		if (getOperationSchedule().isPresent()) {
			jsonObject.put("operation_schedule_id", toJSON(getOperationSchedule().get().getId()));
		}

		if (getServiceUnit().isPresent()) {
			jsonObject.put("service_unit_id", toJSON(getServiceUnit().get().getId()));
		}
		return jsonObject;
	}

	@Override
	public OperationRecord clone() {
		return SerializationUtils.clone(this);
	}

	private Optional<Date> arrivedAt = Optional.absent();

	public Optional<Date> getArrivedAt() {
		return wrapNull(arrivedAt);
	}

	public void setArrivedAt(Optional<Date> arrivedAt) {
		this.arrivedAt = wrapNull(arrivedAt);
	}

	public void setArrivedAt(Date arrivedAt) {
		this.arrivedAt = Optional.fromNullable(arrivedAt);
	}

	public void clearArrivedAt() {
		this.arrivedAt = Optional.absent();
	}

	private Optional<Boolean> arrivedAtOffline = Optional.absent();

	public Optional<Boolean> getArrivedAtOffline() {
		return wrapNull(arrivedAtOffline);
	}

	public void setArrivedAtOffline(Optional<Boolean> arrivedAtOffline) {
		this.arrivedAtOffline = wrapNull(arrivedAtOffline);
	}

	public void setArrivedAtOffline(Boolean arrivedAtOffline) {
		this.arrivedAtOffline = Optional.fromNullable(arrivedAtOffline);
	}

	public void clearArrivedAtOffline() {
		this.arrivedAtOffline = Optional.absent();
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> departedAt = Optional.absent();

	public Optional<Date> getDepartedAt() {
		return wrapNull(departedAt);
	}

	public void setDepartedAt(Optional<Date> departedAt) {
		this.departedAt = wrapNull(departedAt);
	}

	public void setDepartedAt(Date departedAt) {
		this.departedAt = Optional.fromNullable(departedAt);
	}

	public void clearDepartedAt() {
		this.departedAt = Optional.absent();
	}

	private Optional<Boolean> departedAtOffline = Optional.absent();

	public Optional<Boolean> getDepartedAtOffline() {
		return wrapNull(departedAtOffline);
	}

	public void setDepartedAtOffline(Optional<Boolean> departedAtOffline) {
		this.departedAtOffline = wrapNull(departedAtOffline);
	}

	public void setDepartedAtOffline(Boolean departedAtOffline) {
		this.departedAtOffline = Optional.fromNullable(departedAtOffline);
	}

	public void clearDepartedAtOffline() {
		this.departedAtOffline = Optional.absent();
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private Optional<Integer> operationScheduleId = Optional.absent();

	public Optional<Integer> getOperationScheduleId() {
		return wrapNull(operationScheduleId);
	}

	public void setOperationScheduleId(Optional<Integer> operationScheduleId) {
		this.operationScheduleId = wrapNull(operationScheduleId);
	}

	public void setOperationScheduleId(Integer operationScheduleId) {
		this.operationScheduleId = Optional.fromNullable(operationScheduleId);
	}

	public void clearOperationScheduleId() {
		this.operationScheduleId = Optional.absent();
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

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<OperationSchedule> operationSchedule = Optional.absent();

	public Optional<OperationSchedule> getOperationSchedule() {
		return wrapNull(operationSchedule);
	}

	public void setOperationSchedule(Optional<OperationSchedule> operationSchedule) {
		this.operationSchedule = wrapNull(operationSchedule);
	}

	public void setOperationSchedule(OperationSchedule operationSchedule) {
		this.operationSchedule = Optional.fromNullable(operationSchedule);
	}

	public void clearOperationSchedule() {
		this.operationSchedule = Optional.absent();
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
