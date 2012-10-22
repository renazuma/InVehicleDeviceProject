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
public abstract class UnitAssignmentBase extends Model {
	private static final long serialVersionUID = 3280947584733840345L;
	public static final ResponseConverter<UnitAssignment> RESPONSE_CONVERTER = new ResponseConverter<UnitAssignment>() {
		@Override
		public UnitAssignment convert(byte[] rawResponse) throws JSONException {
			return parse(DefaultApiClient.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<UnitAssignment>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<UnitAssignment>>() {
		@Override
		public List<UnitAssignment> convert(byte[] rawResponse) throws JSONException {
			return parseList(DefaultApiClient.parseJSONArray(rawResponse));
		}
	};
	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setId(parseInteger(jsonObject, "id"));
		setName(parseString(jsonObject, "name"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setWorking(parseBoolean(jsonObject, "working"));
		setOperationSchedules(OperationSchedule.parseList(jsonObject, "operation_schedules"));
		setReservationCandidates(ReservationCandidate.parseList(jsonObject, "reservation_candidates"));
		setReservations(Reservation.parseList(jsonObject, "reservations"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		setServiceUnits(ServiceUnit.parseList(jsonObject, "service_units"));

		setUpdatedAt(parseDate(jsonObject, "updated_at"));
	}

	public static Optional<UnitAssignment> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static UnitAssignment parse(JSONObject jsonObject) throws JSONException {
		UnitAssignment model = new UnitAssignment();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<UnitAssignment> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<UnitAssignment>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<UnitAssignment> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<UnitAssignment> models = new LinkedList<UnitAssignment>();
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
		jsonObject.put("deleted_at", toJSON(getDeletedAt()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("name", toJSON(getName()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("working", toJSON(getWorking()));
		if (getOperationSchedules().size() > 0 && recursive) {
			jsonObject.put("operation_schedules", toJSON(getOperationSchedules(), true, nextDepth));
		}
		if (getReservationCandidates().size() > 0 && recursive) {
			jsonObject.put("reservation_candidates", toJSON(getReservationCandidates(), true, nextDepth));
		}
		if (getReservations().size() > 0 && recursive) {
			jsonObject.put("reservations", toJSON(getReservations(), true, nextDepth));
		}
		if (getServiceProvider().isPresent()) {
			if (recursive) {
				jsonObject.put("service_provider", getServiceProvider().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
			}
		}
		if (getServiceUnits().size() > 0 && recursive) {
			jsonObject.put("service_units", toJSON(getServiceUnits(), true, nextDepth));
		}
		return jsonObject;
	}

	@Override
	public UnitAssignment cloneByJSON() throws JSONException {
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

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	private String name = "";

	public String getName() {
		return wrapNull(name);
	}

	public void setName(String name) {
		refreshUpdatedAt();
		this.name = wrapNull(name);
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

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Boolean working = false;

	public Boolean getWorking() {
		return wrapNull(working);
	}

	public void setWorking(Boolean working) {
		refreshUpdatedAt();
		this.working = wrapNull(working);
	}

	private LinkedList<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();

	public List<OperationSchedule> getOperationSchedules() {
		return wrapNull(operationSchedules);
	}

	public void setOperationSchedules(Iterable<OperationSchedule> operationSchedules) {
		this.operationSchedules = wrapNull(operationSchedules);
	}

	public void clearOperationSchedules() {
		setOperationSchedules(new LinkedList<OperationSchedule>());
	}

	private LinkedList<ReservationCandidate> reservationCandidates = new LinkedList<ReservationCandidate>();

	public List<ReservationCandidate> getReservationCandidates() {
		return wrapNull(reservationCandidates);
	}

	public void setReservationCandidates(Iterable<ReservationCandidate> reservationCandidates) {
		this.reservationCandidates = wrapNull(reservationCandidates);
	}

	public void clearReservationCandidates() {
		setReservationCandidates(new LinkedList<ReservationCandidate>());
	}

	private LinkedList<Reservation> reservations = new LinkedList<Reservation>();

	public List<Reservation> getReservations() {
		return wrapNull(reservations);
	}

	public void setReservations(Iterable<Reservation> reservations) {
		this.reservations = wrapNull(reservations);
	}

	public void clearReservations() {
		setReservations(new LinkedList<Reservation>());
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

	private LinkedList<ServiceUnit> serviceUnits = new LinkedList<ServiceUnit>();

	public List<ServiceUnit> getServiceUnits() {
		return wrapNull(serviceUnits);
	}

	public void setServiceUnits(Iterable<ServiceUnit> serviceUnits) {
		this.serviceUnits = wrapNull(serviceUnits);
	}

	public void clearServiceUnits() {
		setServiceUnits(new LinkedList<ServiceUnit>());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(createdAt)
			.append(deletedAt)
			.append(id)
			.append(name)
			.append(serviceProviderId)
			.append(updatedAt)
			.append(working)
			.append(operationSchedules)
			.append(reservationCandidates)
			.append(reservations)
			.append(serviceProvider)
			.append(serviceUnits)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof UnitAssignmentBase)) {
			return false;
		}
		UnitAssignmentBase other = (UnitAssignmentBase) obj;
		return new EqualsBuilder()
			.append(createdAt, other.createdAt)
			.append(deletedAt, other.deletedAt)
			.append(id, other.id)
			.append(name, other.name)
			.append(serviceProviderId, other.serviceProviderId)
			.append(updatedAt, other.updatedAt)
			.append(working, other.working)
			.append(operationSchedules, other.operationSchedules)
			.append(reservationCandidates, other.reservationCandidates)
			.append(reservations, other.reservations)
			.append(serviceProvider, other.serviceProvider)
			.append(serviceUnits, other.serviceUnits)
			.isEquals();
	}
}
