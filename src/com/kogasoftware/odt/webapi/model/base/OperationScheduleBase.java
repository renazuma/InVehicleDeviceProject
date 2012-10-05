package com.kogasoftware.odt.webapi.model.base;

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
import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.ResponseConverter;
import com.kogasoftware.odt.webapi.model.*;

@SuppressWarnings("unused")
public abstract class OperationScheduleBase extends Model {
	private static final long serialVersionUID = 8204934758626763019L;
	public static final ResponseConverter<OperationSchedule> RESPONSE_CONVERTER = new ResponseConverter<OperationSchedule>() {
		@Override
		public OperationSchedule convert(byte[] rawResponse) throws JSONException {
			return parse(WebAPI.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<OperationSchedule>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<OperationSchedule>>() {
		@Override
		public List<OperationSchedule> convert(byte[] rawResponse) throws JSONException {
			return parseList(WebAPI.parseJSONArray(rawResponse));
		}
	};
	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setArrivalEstimate(parseOptionalDate(jsonObject, "arrival_estimate"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDepartureEstimate(parseOptionalDate(jsonObject, "departure_estimate"));
		setId(parseInteger(jsonObject, "id"));
		setOperationDate(parseOptionalDate(jsonObject, "operation_date"));
		setPassengerCountChange(parseInteger(jsonObject, "passenger_count_change"));
		setPlatformId(parseOptionalInteger(jsonObject, "platform_id"));
		setRemain(parseInteger(jsonObject, "remain"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setUnitAssignmentId(parseOptionalInteger(jsonObject, "unit_assignment_id"));
		setOperationRecord(OperationRecord.parse(jsonObject, "operation_record"));
		setPlatform(Platform.parse(jsonObject, "platform"));
		setReservationsAsArrival(Reservation.parseList(jsonObject, "reservations_as_arrival"));
		setReservationsAsDeparture(Reservation.parseList(jsonObject, "reservations_as_departure"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		setUnitAssignment(UnitAssignment.parse(jsonObject, "unit_assignment"));

		setUpdatedAt(parseDate(jsonObject, "updated_at"));
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
		jsonObject.put("passenger_count_change", toJSON(getPassengerCountChange()));
		jsonObject.put("platform_id", toJSON(getPlatformId()));
		jsonObject.put("remain", toJSON(getRemain()));
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
		refreshUpdatedAt();
		this.arrivalEstimate = wrapNull(arrivalEstimate);
	}

	public void setArrivalEstimate(Date arrivalEstimate) {
		setArrivalEstimate(Optional.fromNullable(arrivalEstimate));
	}

	public void clearArrivalEstimate() {
		setArrivalEstimate(Optional.<Date>absent());
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		refreshUpdatedAt();
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> deletedAt = Optional.absent();

	public Optional<Date> getDeletedAt() {
		return wrapNull(deletedAt);
	}

	public void setDeletedAt(Optional<Date> deletedAt) {
		refreshUpdatedAt();
		this.deletedAt = wrapNull(deletedAt);
	}

	public void setDeletedAt(Date deletedAt) {
		setDeletedAt(Optional.fromNullable(deletedAt));
	}

	public void clearDeletedAt() {
		setDeletedAt(Optional.<Date>absent());
	}

	private Optional<Date> departureEstimate = Optional.absent();

	public Optional<Date> getDepartureEstimate() {
		return wrapNull(departureEstimate);
	}

	public void setDepartureEstimate(Optional<Date> departureEstimate) {
		refreshUpdatedAt();
		this.departureEstimate = wrapNull(departureEstimate);
	}

	public void setDepartureEstimate(Date departureEstimate) {
		setDepartureEstimate(Optional.fromNullable(departureEstimate));
	}

	public void clearDepartureEstimate() {
		setDepartureEstimate(Optional.<Date>absent());
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	private Optional<Date> operationDate = Optional.absent();

	public Optional<Date> getOperationDate() {
		return wrapNull(operationDate);
	}

	public void setOperationDate(Optional<Date> operationDate) {
		refreshUpdatedAt();
		this.operationDate = wrapNull(operationDate);
	}

	public void setOperationDate(Date operationDate) {
		setOperationDate(Optional.fromNullable(operationDate));
	}

	public void clearOperationDate() {
		setOperationDate(Optional.<Date>absent());
	}

	private Integer passengerCountChange = 0;

	public Integer getPassengerCountChange() {
		return wrapNull(passengerCountChange);
	}

	public void setPassengerCountChange(Integer passengerCountChange) {
		refreshUpdatedAt();
		this.passengerCountChange = wrapNull(passengerCountChange);
	}

	private Optional<Integer> platformId = Optional.absent();

	public Optional<Integer> getPlatformId() {
		return wrapNull(platformId);
	}

	public void setPlatformId(Optional<Integer> platformId) {
		refreshUpdatedAt();
		this.platformId = wrapNull(platformId);
	}

	public void setPlatformId(Integer platformId) {
		setPlatformId(Optional.fromNullable(platformId));
	}

	public void clearPlatformId() {
		setPlatformId(Optional.<Integer>absent());
	}

	private Integer remain = 0;

	public Integer getRemain() {
		return wrapNull(remain);
	}

	public void setRemain(Integer remain) {
		refreshUpdatedAt();
		this.remain = wrapNull(remain);
	}

	private Optional<Integer> serviceProviderId = Optional.absent();

	public Optional<Integer> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Optional<Integer> serviceProviderId) {
		refreshUpdatedAt();
		this.serviceProviderId = wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Integer serviceProviderId) {
		setServiceProviderId(Optional.fromNullable(serviceProviderId));
	}

	public void clearServiceProviderId() {
		setServiceProviderId(Optional.<Integer>absent());
	}

	private Optional<Integer> unitAssignmentId = Optional.absent();

	public Optional<Integer> getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Optional<Integer> unitAssignmentId) {
		refreshUpdatedAt();
		this.unitAssignmentId = wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Integer unitAssignmentId) {
		setUnitAssignmentId(Optional.fromNullable(unitAssignmentId));
	}

	public void clearUnitAssignmentId() {
		setUnitAssignmentId(Optional.<Integer>absent());
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<OperationRecord> operationRecord = Optional.<OperationRecord>absent();

	public Optional<OperationRecord> getOperationRecord() {
		return wrapNull(operationRecord);
	}

	public void setOperationRecord(Optional<OperationRecord> operationRecord) {
		this.operationRecord = wrapNull(operationRecord);
	}

	public void setOperationRecord(OperationRecord operationRecord) {
		setOperationRecord(Optional.fromNullable(operationRecord));
	}

	public void clearOperationRecord() {
		setOperationRecord(Optional.<OperationRecord>absent());
	}

	private Optional<Platform> platform = Optional.<Platform>absent();

	public Optional<Platform> getPlatform() {
		return wrapNull(platform);
	}

	public void setPlatform(Optional<Platform> platform) {
		this.platform = wrapNull(platform);
	}

	public void setPlatform(Platform platform) {
		setPlatform(Optional.fromNullable(platform));
	}

	public void clearPlatform() {
		setPlatform(Optional.<Platform>absent());
	}

	private LinkedList<Reservation> reservationsAsArrival = new LinkedList<Reservation>();

	public List<Reservation> getReservationsAsArrival() {
		return wrapNull(reservationsAsArrival);
	}

	public void setReservationsAsArrival(Iterable<Reservation> reservationsAsArrival) {
		this.reservationsAsArrival = wrapNull(reservationsAsArrival);
	}

	public void clearReservationsAsArrival() {
		setReservationsAsArrival(new LinkedList<Reservation>());
	}

	private LinkedList<Reservation> reservationsAsDeparture = new LinkedList<Reservation>();

	public List<Reservation> getReservationsAsDeparture() {
		return wrapNull(reservationsAsDeparture);
	}

	public void setReservationsAsDeparture(Iterable<Reservation> reservationsAsDeparture) {
		this.reservationsAsDeparture = wrapNull(reservationsAsDeparture);
	}

	public void clearReservationsAsDeparture() {
		setReservationsAsDeparture(new LinkedList<Reservation>());
	}

	private Optional<ServiceProvider> serviceProvider = Optional.<ServiceProvider>absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		setServiceProvider(Optional.fromNullable(serviceProvider));
	}

	public void clearServiceProvider() {
		setServiceProvider(Optional.<ServiceProvider>absent());
	}

	private Optional<UnitAssignment> unitAssignment = Optional.<UnitAssignment>absent();

	public Optional<UnitAssignment> getUnitAssignment() {
		return wrapNull(unitAssignment);
	}

	public void setUnitAssignment(Optional<UnitAssignment> unitAssignment) {
		this.unitAssignment = wrapNull(unitAssignment);
	}

	public void setUnitAssignment(UnitAssignment unitAssignment) {
		setUnitAssignment(Optional.fromNullable(unitAssignment));
	}

	public void clearUnitAssignment() {
		setUnitAssignment(Optional.<UnitAssignment>absent());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(arrivalEstimate)
			.append(createdAt)
			.append(deletedAt)
			.append(departureEstimate)
			.append(id)
			.append(operationDate)
			.append(passengerCountChange)
			.append(platformId)
			.append(remain)
			.append(serviceProviderId)
			.append(unitAssignmentId)
			.append(updatedAt)
			.append(operationRecord)
			.append(platform)
			.append(reservationsAsArrival)
			.append(reservationsAsDeparture)
			.append(serviceProvider)
			.append(unitAssignment)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof OperationScheduleBase)) {
			return false;
		}
		OperationScheduleBase other = (OperationScheduleBase) obj;
		return new EqualsBuilder()
			.append(arrivalEstimate, other.arrivalEstimate)
			.append(createdAt, other.createdAt)
			.append(deletedAt, other.deletedAt)
			.append(departureEstimate, other.departureEstimate)
			.append(id, other.id)
			.append(operationDate, other.operationDate)
			.append(passengerCountChange, other.passengerCountChange)
			.append(platformId, other.platformId)
			.append(remain, other.remain)
			.append(serviceProviderId, other.serviceProviderId)
			.append(unitAssignmentId, other.unitAssignmentId)
			.append(updatedAt, other.updatedAt)
			.append(operationRecord, other.operationRecord)
			.append(platform, other.platform)
			.append(reservationsAsArrival, other.reservationsAsArrival)
			.append(reservationsAsDeparture, other.reservationsAsDeparture)
			.append(serviceProvider, other.serviceProvider)
			.append(unitAssignment, other.unitAssignment)
			.isEquals();
	}
}
