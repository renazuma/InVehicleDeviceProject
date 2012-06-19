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

public class OperationSchedule extends Model {
	private static final long serialVersionUID = 113704077271357260L;

	public OperationSchedule() {
	}

	public OperationSchedule(JSONObject jsonObject) throws JSONException {
		try {
			fillMembers(this, jsonObject);
		} catch (ParseException e) {
			throw new JSONException(e.toString() + "\n" + ExceptionUtils.getStackTrace(e));
		}
	}

	public static void fillMembers(OperationSchedule model, JSONObject jsonObject) throws JSONException, ParseException {
		model.setArrivalEstimate(parseDate(jsonObject, "arrival_estimate"));
		model.setCreatedAt(parseDate(jsonObject, "created_at"));
		model.setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		model.setDepartureEstimate(parseDate(jsonObject, "departure_estimate"));
		model.setId(parseInteger(jsonObject, "id"));
		model.setPlatformId(parseOptionalInteger(jsonObject, "platform_id"));
		model.setRemain(parseInteger(jsonObject, "remain"));
		model.setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		model.setUnitAssignmentId(parseOptionalInteger(jsonObject, "unit_assignment_id"));
		model.setUpdatedAt(parseDate(jsonObject, "updated_at"));
		model.setOperationRecord(OperationRecord.parse(jsonObject, "operation_record"));
		model.setPlatform(Platform.parse(jsonObject, "platform"));
		model.setReservationsAsArrival(Reservation.parseList(jsonObject, "reservations_as_arrival"));
		model.setReservationsAsDeparture(Reservation.parseList(jsonObject, "reservations_as_departure"));
		model.setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		model.setUnitAssignment(UnitAssignment.parse(jsonObject, "unit_assignment"));
	}

	public static Optional<OperationSchedule> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<OperationSchedule> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.of(new OperationSchedule(jsonObject));
	}

	public static LinkedList<OperationSchedule> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<OperationSchedule>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<OperationSchedule> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<OperationSchedule> models = new LinkedList<OperationSchedule>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new OperationSchedule(jsonArray.getJSONObject(i)));
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
		jsonObject.put("arrival_estimate", toJSON(getArrivalEstimate()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("departure_estimate", toJSON(getDepartureEstimate()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("platform_id", toJSON(getPlatformId().orNull()));
		jsonObject.put("remain", toJSON(getRemain()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("unit_assignment_id", toJSON(getUnitAssignmentId().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		if (getOperationRecord().isPresent() && recursive) {
			jsonObject.put("operation_record", getOperationRecord().get().toJSONObject(true, depth));
		}
		if (getPlatform().isPresent()) {
			if (recursive) {
				jsonObject.put("platform", getPlatform().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("platform_id", toJSON(getPlatform().get().getId()));
			}
		}
		if (getReservationsAsArrival().size() > 0 && recursive) {
			jsonObject.put("reservations_as_arrival", toJSON(getReservationsAsArrival(), true, depth));
		}
		if (getReservationsAsDeparture().size() > 0 && recursive) {
			jsonObject.put("reservations_as_departure", toJSON(getReservationsAsDeparture(), true, depth));
		}
		if (getServiceProvider().isPresent()) {
			if (recursive) {
				jsonObject.put("service_provider", getServiceProvider().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
			}
		}
		if (getUnitAssignment().isPresent()) {
			if (recursive) {
				jsonObject.put("unit_assignment", getUnitAssignment().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("unit_assignment_id", toJSON(getUnitAssignment().get().getId()));
			}
		}
		return jsonObject;
	}

	private void writeObject(ObjectOutputStream objectOutputStream)
			throws IOException {
		try {
			objectOutputStream.writeObject(toJSONObject(true).toString());
		} catch (JSONException e) {
			throw new IOException(e);
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
	public OperationSchedule cloneByJSON() throws JSONException {
		return new OperationSchedule(toJSONObject(true));
	}

	private Date arrivalEstimate = new Date();

	public Date getArrivalEstimate() {
		return wrapNull(arrivalEstimate);
	}

	public void setArrivalEstimate(Date arrivalEstimate) {
		this.arrivalEstimate = wrapNull(arrivalEstimate);
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

	private Date departureEstimate = new Date();

	public Date getDepartureEstimate() {
		return wrapNull(departureEstimate);
	}

	public void setDepartureEstimate(Date departureEstimate) {
		this.departureEstimate = wrapNull(departureEstimate);
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
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

	private Integer remain = 0;

	public Integer getRemain() {
		return wrapNull(remain);
	}

	public void setRemain(Integer remain) {
		this.remain = wrapNull(remain);
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
		return new LinkedList<Reservation>(wrapNull(reservationsAsArrival));
	}

	public void setReservationsAsArrival(List<Reservation> reservationsAsArrival) {
		this.reservationsAsArrival = new LinkedList<Reservation>(wrapNull(reservationsAsArrival));
	}

	public void clearReservationsAsArrival() {
		this.reservationsAsArrival = new LinkedList<Reservation>();
	}

	private LinkedList<Reservation> reservationsAsDeparture = new LinkedList<Reservation>();

	public List<Reservation> getReservationsAsDeparture() {
		return new LinkedList<Reservation>(wrapNull(reservationsAsDeparture));
	}

	public void setReservationsAsDeparture(List<Reservation> reservationsAsDeparture) {
		this.reservationsAsDeparture = new LinkedList<Reservation>(wrapNull(reservationsAsDeparture));
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
