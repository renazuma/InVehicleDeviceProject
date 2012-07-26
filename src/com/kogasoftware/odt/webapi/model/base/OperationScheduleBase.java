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
public abstract class OperationScheduleBase extends Model {
	private static final long serialVersionUID = 763885832946443531L;

	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setArrivalEstimate(parseOptionalDate(jsonObject, "arrival_estimate"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDepartureEstimate(parseOptionalDate(jsonObject, "departure_estimate"));
		setId(parseInteger(jsonObject, "id"));
		setOperationDate(parseOptionalDate(jsonObject, "operation_date"));
		setPlatformId(parseOptionalInteger(jsonObject, "platform_id"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setUnitAssignmentId(parseOptionalInteger(jsonObject, "unit_assignment_id"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setOperationRecord(OperationRecord.parse(jsonObject, "operation_record"));
		setPlatform(Platform.parse(jsonObject, "platform"));
		setReservationsAsArrival(Reservation.parseList(jsonObject, "reservations_as_arrival"));
		setReservationsAsDeparture(Reservation.parseList(jsonObject, "reservations_as_departure"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		setUnitAssignment(UnitAssignment.parse(jsonObject, "unit_assignment"));
	}

	public static Optional<OperationSchedule> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static OperationSchedule parse(JSONObject jsonObject) throws JSONException {
		OperationSchedule model = new OperationSchedule();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<OperationSchedule> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<OperationSchedule>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<OperationSchedule> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<OperationSchedule> models = new LinkedList<OperationSchedule>();
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
		jsonObject.put("arrival_estimate", toJSON(getArrivalEstimate()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt()));
		jsonObject.put("departure_estimate", toJSON(getDepartureEstimate()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("operation_date", toJSON(getOperationDate()));
		jsonObject.put("platform_id", toJSON(getPlatformId()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId()));
		jsonObject.put("unit_assignment_id", toJSON(getUnitAssignmentId()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		if (getOperationRecord().isPresent() && recursive) {
			jsonObject.put("operation_record", getOperationRecord().get().toJSONObject(true, nextDepth));
		}
		if (getPlatform().isPresent()) {
			if (recursive) {
				jsonObject.put("platform", getPlatform().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("platform_id", toJSON(getPlatform().get().getId()));
			}
		}
		if (getReservationsAsArrival().size() > 0 && recursive) {
			jsonObject.put("reservations_as_arrival", toJSON(getReservationsAsArrival(), true, nextDepth));
		}
		if (getReservationsAsDeparture().size() > 0 && recursive) {
			jsonObject.put("reservations_as_departure", toJSON(getReservationsAsDeparture(), true, nextDepth));
		}
		if (getServiceProvider().isPresent()) {
			if (recursive) {
				jsonObject.put("service_provider", getServiceProvider().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
			}
		}
		if (getUnitAssignment().isPresent()) {
			if (recursive) {
				jsonObject.put("unit_assignment", getUnitAssignment().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("unit_assignment_id", toJSON(getUnitAssignment().get().getId()));
			}
		}
		return jsonObject;
	}

	@Override
	public OperationSchedule cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
	}

	private Optional<Date> arrivalEstimate = Optional.absent();

	public Optional<Date> getArrivalEstimate() {
		return wrapNull(arrivalEstimate);
	}

	public void setArrivalEstimate(Optional<Date> arrivalEstimate) {
		this.arrivalEstimate = wrapNull(arrivalEstimate);
	}

	public void setArrivalEstimate(Date arrivalEstimate) {
		this.arrivalEstimate = Optional.fromNullable(arrivalEstimate);
	}

	public void clearArrivalEstimate() {
		this.arrivalEstimate = Optional.absent();
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> deletedAt = Optional.absent();

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
		this.deletedAt = Optional.absent();
	}

	private Optional<Date> departureEstimate = Optional.absent();

	public Optional<Date> getDepartureEstimate() {
		return wrapNull(departureEstimate);
	}

	public void setDepartureEstimate(Optional<Date> departureEstimate) {
		this.departureEstimate = wrapNull(departureEstimate);
	}

	public void setDepartureEstimate(Date departureEstimate) {
		this.departureEstimate = Optional.fromNullable(departureEstimate);
	}

	public void clearDepartureEstimate() {
		this.departureEstimate = Optional.absent();
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private Optional<Date> operationDate = Optional.absent();

	public Optional<Date> getOperationDate() {
		return wrapNull(operationDate);
	}

	public void setOperationDate(Optional<Date> operationDate) {
		this.operationDate = wrapNull(operationDate);
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = Optional.fromNullable(operationDate);
	}

	public void clearOperationDate() {
		this.operationDate = Optional.absent();
	}

	private Optional<Integer> platformId = Optional.absent();

	public Optional<Integer> getPlatformId() {
		return wrapNull(platformId);
	}

	public void setPlatformId(Optional<Integer> platformId) {
		this.platformId = wrapNull(platformId);
	}

	public void setPlatformId(Integer platformId) {
		this.platformId = Optional.fromNullable(platformId);
	}

	public void clearPlatformId() {
		this.platformId = Optional.absent();
	}

	private Optional<Integer> serviceProviderId = Optional.absent();

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
		this.serviceProviderId = Optional.absent();
	}

	private Optional<Integer> unitAssignmentId = Optional.absent();

	public Optional<Integer> getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Optional<Integer> unitAssignmentId) {
		this.unitAssignmentId = wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Integer unitAssignmentId) {
		this.unitAssignmentId = Optional.fromNullable(unitAssignmentId);
	}

	public void clearUnitAssignmentId() {
		this.unitAssignmentId = Optional.absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<OperationRecord> operationRecord = Optional.absent();

	public Optional<OperationRecord> getOperationRecord() {
		return wrapNull(operationRecord);
	}

	public void setOperationRecord(Optional<OperationRecord> operationRecord) {
		this.operationRecord = wrapNull(operationRecord);
	}

	public void setOperationRecord(OperationRecord operationRecord) {
		this.operationRecord = Optional.fromNullable(operationRecord);
	}

	public void clearOperationRecord() {
		this.operationRecord = Optional.absent();
	}

	private Optional<Platform> platform = Optional.absent();

	public Optional<Platform> getPlatform() {
		return wrapNull(platform);
	}

	public void setPlatform(Optional<Platform> platform) {
		this.platform = wrapNull(platform);
	}

	public void setPlatform(Platform platform) {
		this.platform = Optional.fromNullable(platform);
	}

	public void clearPlatform() {
		this.platform = Optional.absent();
	}

	private LinkedList<Reservation> reservationsAsArrival = new LinkedList<Reservation>();

	public List<Reservation> getReservationsAsArrival() {
		return wrapNull(reservationsAsArrival);
	}

	public void setReservationsAsArrival(Iterable<Reservation> reservationsAsArrival) {
		this.reservationsAsArrival = wrapNull(reservationsAsArrival);
	}

	public void clearReservationsAsArrival() {
		this.reservationsAsArrival = new LinkedList<Reservation>();
	}

	private LinkedList<Reservation> reservationsAsDeparture = new LinkedList<Reservation>();

	public List<Reservation> getReservationsAsDeparture() {
		return wrapNull(reservationsAsDeparture);
	}

	public void setReservationsAsDeparture(Iterable<Reservation> reservationsAsDeparture) {
		this.reservationsAsDeparture = wrapNull(reservationsAsDeparture);
	}

	public void clearReservationsAsDeparture() {
		this.reservationsAsDeparture = new LinkedList<Reservation>();
	}

	private Optional<ServiceProvider> serviceProvider = Optional.absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = Optional.fromNullable(serviceProvider);
	}

	public void clearServiceProvider() {
		this.serviceProvider = Optional.absent();
	}

	private Optional<UnitAssignment> unitAssignment = Optional.absent();

	public Optional<UnitAssignment> getUnitAssignment() {
		return wrapNull(unitAssignment);
	}

	public void setUnitAssignment(Optional<UnitAssignment> unitAssignment) {
		this.unitAssignment = wrapNull(unitAssignment);
	}

	public void setUnitAssignment(UnitAssignment unitAssignment) {
		this.unitAssignment = Optional.fromNullable(unitAssignment);
	}

	public void clearUnitAssignment() {
		this.unitAssignment = Optional.absent();
	}
}
