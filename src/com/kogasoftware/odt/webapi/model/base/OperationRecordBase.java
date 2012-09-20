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
public abstract class OperationRecordBase extends Model {
	private static final long serialVersionUID = 4832882434228285278L;
	public static final ResponseConverter<OperationRecord> RESPONSE_CONVERTER = new ResponseConverter<OperationRecord>() {
		@Override
		public OperationRecord convert(byte[] rawResponse) throws JSONException {
			return parse(WebAPI.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<OperationRecord>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<OperationRecord>>() {
		@Override
		public List<OperationRecord> convert(byte[] rawResponse) throws JSONException {
			return parseList(WebAPI.parseJSONArray(rawResponse));
		}
	};
	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}
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
		setOperationSchedule(OperationSchedule.parse(jsonObject, "operation_schedule"));
		setServiceUnit(ServiceUnit.parse(jsonObject, "service_unit"));

		setUpdatedAt(parseDate(jsonObject, "updated_at"));
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
		refreshUpdatedAt();
		this.arrivedAt = wrapNull(arrivedAt);
	}

	public void setArrivedAt(Date arrivedAt) {
		setArrivedAt(Optional.fromNullable(arrivedAt));
	}

	public void clearArrivedAt() {
		setArrivedAt(Optional.<Date>absent());
	}

	private Optional<Boolean> arrivedAtOffline = Optional.absent();

	public Optional<Boolean> getArrivedAtOffline() {
		return wrapNull(arrivedAtOffline);
	}

	public void setArrivedAtOffline(Optional<Boolean> arrivedAtOffline) {
		refreshUpdatedAt();
		this.arrivedAtOffline = wrapNull(arrivedAtOffline);
	}

	public void setArrivedAtOffline(Boolean arrivedAtOffline) {
		setArrivedAtOffline(Optional.fromNullable(arrivedAtOffline));
	}

	public void clearArrivedAtOffline() {
		setArrivedAtOffline(Optional.<Boolean>absent());
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		refreshUpdatedAt();
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> departedAt = Optional.absent();

	public Optional<Date> getDepartedAt() {
		return wrapNull(departedAt);
	}

	public void setDepartedAt(Optional<Date> departedAt) {
		refreshUpdatedAt();
		this.departedAt = wrapNull(departedAt);
	}

	public void setDepartedAt(Date departedAt) {
		setDepartedAt(Optional.fromNullable(departedAt));
	}

	public void clearDepartedAt() {
		setDepartedAt(Optional.<Date>absent());
	}

	private Optional<Boolean> departedAtOffline = Optional.absent();

	public Optional<Boolean> getDepartedAtOffline() {
		return wrapNull(departedAtOffline);
	}

	public void setDepartedAtOffline(Optional<Boolean> departedAtOffline) {
		refreshUpdatedAt();
		this.departedAtOffline = wrapNull(departedAtOffline);
	}

	public void setDepartedAtOffline(Boolean departedAtOffline) {
		setDepartedAtOffline(Optional.fromNullable(departedAtOffline));
	}

	public void clearDepartedAtOffline() {
		setDepartedAtOffline(Optional.<Boolean>absent());
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	private Optional<Integer> operationScheduleId = Optional.absent();

	public Optional<Integer> getOperationScheduleId() {
		return wrapNull(operationScheduleId);
	}

	public void setOperationScheduleId(Optional<Integer> operationScheduleId) {
		refreshUpdatedAt();
		this.operationScheduleId = wrapNull(operationScheduleId);
	}

	public void setOperationScheduleId(Integer operationScheduleId) {
		setOperationScheduleId(Optional.fromNullable(operationScheduleId));
	}

	public void clearOperationScheduleId() {
		setOperationScheduleId(Optional.<Integer>absent());
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

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<OperationSchedule> operationSchedule = Optional.<OperationSchedule>absent();

	public Optional<OperationSchedule> getOperationSchedule() {
		return wrapNull(operationSchedule);
	}

	public void setOperationSchedule(Optional<OperationSchedule> operationSchedule) {
		this.operationSchedule = wrapNull(operationSchedule);
	}

	public void setOperationSchedule(OperationSchedule operationSchedule) {
		setOperationSchedule(Optional.fromNullable(operationSchedule));
	}

	public void clearOperationSchedule() {
		setOperationSchedule(Optional.<OperationSchedule>absent());
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
}
