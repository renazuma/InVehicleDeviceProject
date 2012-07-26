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
import com.kogasoftware.odt.webapi.model.*;

@SuppressWarnings("unused")
public abstract class OperationRecordBase extends Model {
	private static final long serialVersionUID = 4608510169436397882L;

	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
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
		setServiceUnit(ServiceUnit.parse(jsonObject, "service_unit"));
	}

	public static Optional<OperationRecord> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static OperationRecord parse(JSONObject jsonObject) throws JSONException {
		OperationRecord model = new OperationRecord();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<OperationRecord> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<OperationRecord>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<OperationRecord> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<OperationRecord> models = new LinkedList<OperationRecord>();
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
		jsonObject.put("arrived_at", toJSON(getArrivedAt()));
		jsonObject.put("arrived_at_offline", toJSON(getArrivedAtOffline()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("departed_at", toJSON(getDepartedAt()));
		jsonObject.put("departed_at_offline", toJSON(getDepartedAtOffline()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("operation_schedule_id", toJSON(getOperationScheduleId()));
		jsonObject.put("service_unit_id", toJSON(getServiceUnitId()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		if (getOperationSchedule().isPresent()) {
			if (recursive) {
				jsonObject.put("operation_schedule", getOperationSchedule().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("operation_schedule_id", toJSON(getOperationSchedule().get().getId()));
			}
		}
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
	public OperationRecord cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
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
