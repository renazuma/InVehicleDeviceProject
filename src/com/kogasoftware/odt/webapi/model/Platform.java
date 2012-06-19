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

public class Platform extends Model {
	private static final long serialVersionUID = 777744408342965993L;

	public Platform() {
	}

	public Platform(JSONObject jsonObject) throws JSONException {
		try {
			fillMembers(this, jsonObject);
		} catch (ParseException e) {
			throw new JSONException(e.toString() + "\n" + ExceptionUtils.getStackTrace(e));
		}
	}

	public static void fillMembers(Platform model, JSONObject jsonObject) throws JSONException, ParseException {
		model.setAddress(parseOptionalString(jsonObject, "address"));
		model.setCreatedAt(parseDate(jsonObject, "created_at"));
		model.setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		model.setDemandAreaId(parseOptionalInteger(jsonObject, "demand_area_id"));
		model.setEndAt(parseOptionalDate(jsonObject, "end_at"));
		model.setId(parseInteger(jsonObject, "id"));
		model.setImage(parseOptionalString(jsonObject, "image"));
		model.setKeyword(parseOptionalString(jsonObject, "keyword"));
		model.setLatitude(parseBigDecimal(jsonObject, "latitude"));
		model.setLongitude(parseBigDecimal(jsonObject, "longitude"));
		model.setMemo(parseOptionalString(jsonObject, "memo"));
		model.setName(parseString(jsonObject, "name"));
		model.setNameRuby(parseString(jsonObject, "name_ruby"));
		model.setPlatformCategoryId(parseOptionalInteger(jsonObject, "platform_category_id"));
		model.setReportingRegionId(parseInteger(jsonObject, "reporting_region_id"));
		model.setSemiDemandAreaId(parseOptionalInteger(jsonObject, "semi_demand_area_id"));
		model.setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		model.setStartAt(parseOptionalDate(jsonObject, "start_at"));
		model.setTypeOfDemand(parseOptionalInteger(jsonObject, "type_of_demand"));
		model.setTypeOfPlatform(parseInteger(jsonObject, "type_of_platform"));
		model.setUpdatedAt(parseDate(jsonObject, "updated_at"));
		model.setDemandsAsArrival(Demand.parseList(jsonObject, "demands_as_arrival"));
		model.setDemandsAsDeparture(Demand.parseList(jsonObject, "demands_as_departure"));
		model.setOperationSchedules(OperationSchedule.parseList(jsonObject, "operation_schedules"));
		model.setReservationCandidatesAsArrival(ReservationCandidate.parseList(jsonObject, "reservation_candidates_as_arrival"));
		model.setReservationCandidatesAsDeparture(ReservationCandidate.parseList(jsonObject, "reservation_candidates_as_departure"));
		model.setReservationsAsArrival(Reservation.parseList(jsonObject, "reservations_as_arrival"));
		model.setReservationsAsDeparture(Reservation.parseList(jsonObject, "reservations_as_departure"));
		model.setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
	}

	public static Optional<Platform> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<Platform> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.of(new Platform(jsonObject));
	}

	public static LinkedList<Platform> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Platform>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<Platform> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<Platform> models = new LinkedList<Platform>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new Platform(jsonArray.getJSONObject(i)));
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
		jsonObject.put("address", toJSON(getAddress().orNull()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("demand_area_id", toJSON(getDemandAreaId().orNull()));
		jsonObject.put("end_at", toJSON(getEndAt().orNull()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("image", toJSON(getImage().orNull()));
		jsonObject.put("keyword", toJSON(getKeyword().orNull()));
		jsonObject.put("latitude", toJSON(getLatitude()));
		jsonObject.put("longitude", toJSON(getLongitude()));
		jsonObject.put("memo", toJSON(getMemo().orNull()));
		jsonObject.put("name", toJSON(getName()));
		jsonObject.put("name_ruby", toJSON(getNameRuby()));
		jsonObject.put("platform_category_id", toJSON(getPlatformCategoryId().orNull()));
		jsonObject.put("reporting_region_id", toJSON(getReportingRegionId()));
		jsonObject.put("semi_demand_area_id", toJSON(getSemiDemandAreaId().orNull()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("start_at", toJSON(getStartAt().orNull()));
		jsonObject.put("type_of_demand", toJSON(getTypeOfDemand().orNull()));
		jsonObject.put("type_of_platform", toJSON(getTypeOfPlatform()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		if (getDemandsAsArrival().size() > 0 && recursive) {
			jsonObject.put("demands_as_arrival", toJSON(getDemandsAsArrival(), true, depth));
		}
		if (getDemandsAsDeparture().size() > 0 && recursive) {
			jsonObject.put("demands_as_departure", toJSON(getDemandsAsDeparture(), true, depth));
		}
		if (getOperationSchedules().size() > 0 && recursive) {
			jsonObject.put("operation_schedules", toJSON(getOperationSchedules(), true, depth));
		}
		if (getReservationCandidatesAsArrival().size() > 0 && recursive) {
			jsonObject.put("reservation_candidates_as_arrival", toJSON(getReservationCandidatesAsArrival(), true, depth));
		}
		if (getReservationCandidatesAsDeparture().size() > 0 && recursive) {
			jsonObject.put("reservation_candidates_as_departure", toJSON(getReservationCandidatesAsDeparture(), true, depth));
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
		return jsonObject;
	}

	private void writeObject(ObjectOutputStream objectOutputStream)
			throws IOException {
		try {
			objectOutputStream.writeObject(toJSONObject(true).toString());
		} catch (JSONException e) {
			throw new IOException(e.toString() + "\n" + ExceptionUtils.getStackTrace(e));
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
	public Platform cloneByJSON() throws JSONException {
		return new Platform(toJSONObject(true));
	}

	private Optional<String> address = Optional.absent();

	public Optional<String> getAddress() {
		return wrapNull(address);
	}

	public void setAddress(Optional<String> address) {
		this.address = wrapNull(address);
	}

	public void setAddress(String address) {
		this.address = Optional.fromNullable(address);
	}

	public void clearAddress() {
		this.address = Optional.absent();
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

	private Optional<Integer> demandAreaId = Optional.absent();

	public Optional<Integer> getDemandAreaId() {
		return wrapNull(demandAreaId);
	}

	public void setDemandAreaId(Optional<Integer> demandAreaId) {
		this.demandAreaId = wrapNull(demandAreaId);
	}

	public void setDemandAreaId(Integer demandAreaId) {
		this.demandAreaId = Optional.fromNullable(demandAreaId);
	}

	public void clearDemandAreaId() {
		this.demandAreaId = Optional.absent();
	}

	private Optional<Date> endAt = Optional.absent();

	public Optional<Date> getEndAt() {
		return wrapNull(endAt);
	}

	public void setEndAt(Optional<Date> endAt) {
		this.endAt = wrapNull(endAt);
	}

	public void setEndAt(Date endAt) {
		this.endAt = Optional.fromNullable(endAt);
	}

	public void clearEndAt() {
		this.endAt = Optional.absent();
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private Optional<String> image = Optional.absent();

	public Optional<String> getImage() {
		return wrapNull(image);
	}

	public void setImage(Optional<String> image) {
		this.image = wrapNull(image);
	}

	public void setImage(String image) {
		this.image = Optional.fromNullable(image);
	}

	public void clearImage() {
		this.image = Optional.absent();
	}

	private Optional<String> keyword = Optional.absent();

	public Optional<String> getKeyword() {
		return wrapNull(keyword);
	}

	public void setKeyword(Optional<String> keyword) {
		this.keyword = wrapNull(keyword);
	}

	public void setKeyword(String keyword) {
		this.keyword = Optional.fromNullable(keyword);
	}

	public void clearKeyword() {
		this.keyword = Optional.absent();
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

	private Optional<String> memo = Optional.absent();

	public Optional<String> getMemo() {
		return wrapNull(memo);
	}

	public void setMemo(Optional<String> memo) {
		this.memo = wrapNull(memo);
	}

	public void setMemo(String memo) {
		this.memo = Optional.fromNullable(memo);
	}

	public void clearMemo() {
		this.memo = Optional.absent();
	}

	private String name = "";

	public String getName() {
		return wrapNull(name);
	}

	public void setName(String name) {
		this.name = wrapNull(name);
	}

	private String nameRuby = "";

	public String getNameRuby() {
		return wrapNull(nameRuby);
	}

	public void setNameRuby(String nameRuby) {
		this.nameRuby = wrapNull(nameRuby);
	}

	private Optional<Integer> platformCategoryId = Optional.absent();

	public Optional<Integer> getPlatformCategoryId() {
		return wrapNull(platformCategoryId);
	}

	public void setPlatformCategoryId(Optional<Integer> platformCategoryId) {
		this.platformCategoryId = wrapNull(platformCategoryId);
	}

	public void setPlatformCategoryId(Integer platformCategoryId) {
		this.platformCategoryId = Optional.fromNullable(platformCategoryId);
	}

	public void clearPlatformCategoryId() {
		this.platformCategoryId = Optional.absent();
	}

	private Integer reportingRegionId = 0;

	public Integer getReportingRegionId() {
		return wrapNull(reportingRegionId);
	}

	public void setReportingRegionId(Integer reportingRegionId) {
		this.reportingRegionId = wrapNull(reportingRegionId);
	}

	private Optional<Integer> semiDemandAreaId = Optional.absent();

	public Optional<Integer> getSemiDemandAreaId() {
		return wrapNull(semiDemandAreaId);
	}

	public void setSemiDemandAreaId(Optional<Integer> semiDemandAreaId) {
		this.semiDemandAreaId = wrapNull(semiDemandAreaId);
	}

	public void setSemiDemandAreaId(Integer semiDemandAreaId) {
		this.semiDemandAreaId = Optional.fromNullable(semiDemandAreaId);
	}

	public void clearSemiDemandAreaId() {
		this.semiDemandAreaId = Optional.absent();
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

	private Optional<Date> startAt = Optional.absent();

	public Optional<Date> getStartAt() {
		return wrapNull(startAt);
	}

	public void setStartAt(Optional<Date> startAt) {
		this.startAt = wrapNull(startAt);
	}

	public void setStartAt(Date startAt) {
		this.startAt = Optional.fromNullable(startAt);
	}

	public void clearStartAt() {
		this.startAt = Optional.absent();
	}

	private Optional<Integer> typeOfDemand = Optional.absent();

	public Optional<Integer> getTypeOfDemand() {
		return wrapNull(typeOfDemand);
	}

	public void setTypeOfDemand(Optional<Integer> typeOfDemand) {
		this.typeOfDemand = wrapNull(typeOfDemand);
	}

	public void setTypeOfDemand(Integer typeOfDemand) {
		this.typeOfDemand = Optional.fromNullable(typeOfDemand);
	}

	public void clearTypeOfDemand() {
		this.typeOfDemand = Optional.absent();
	}

	private Integer typeOfPlatform = 0;

	public Integer getTypeOfPlatform() {
		return wrapNull(typeOfPlatform);
	}

	public void setTypeOfPlatform(Integer typeOfPlatform) {
		this.typeOfPlatform = wrapNull(typeOfPlatform);
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private LinkedList<Demand> demandsAsArrival = new LinkedList<Demand>();

	public List<Demand> getDemandsAsArrival() {
		return new LinkedList<Demand>(wrapNull(demandsAsArrival));
	}

	public void setDemandsAsArrival(List<Demand> demandsAsArrival) {
		this.demandsAsArrival = new LinkedList<Demand>(wrapNull(demandsAsArrival));
	}

	public void clearDemandsAsArrival() {
		this.demandsAsArrival = new LinkedList<Demand>();
	}

	private LinkedList<Demand> demandsAsDeparture = new LinkedList<Demand>();

	public List<Demand> getDemandsAsDeparture() {
		return new LinkedList<Demand>(wrapNull(demandsAsDeparture));
	}

	public void setDemandsAsDeparture(List<Demand> demandsAsDeparture) {
		this.demandsAsDeparture = new LinkedList<Demand>(wrapNull(demandsAsDeparture));
	}

	public void clearDemandsAsDeparture() {
		this.demandsAsDeparture = new LinkedList<Demand>();
	}

	private LinkedList<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();

	public List<OperationSchedule> getOperationSchedules() {
		return new LinkedList<OperationSchedule>(wrapNull(operationSchedules));
	}

	public void setOperationSchedules(List<OperationSchedule> operationSchedules) {
		this.operationSchedules = new LinkedList<OperationSchedule>(wrapNull(operationSchedules));
	}

	public void clearOperationSchedules() {
		this.operationSchedules = new LinkedList<OperationSchedule>();
	}

	private LinkedList<ReservationCandidate> reservationCandidatesAsArrival = new LinkedList<ReservationCandidate>();

	public List<ReservationCandidate> getReservationCandidatesAsArrival() {
		return new LinkedList<ReservationCandidate>(wrapNull(reservationCandidatesAsArrival));
	}

	public void setReservationCandidatesAsArrival(List<ReservationCandidate> reservationCandidatesAsArrival) {
		this.reservationCandidatesAsArrival = new LinkedList<ReservationCandidate>(wrapNull(reservationCandidatesAsArrival));
	}

	public void clearReservationCandidatesAsArrival() {
		this.reservationCandidatesAsArrival = new LinkedList<ReservationCandidate>();
	}

	private LinkedList<ReservationCandidate> reservationCandidatesAsDeparture = new LinkedList<ReservationCandidate>();

	public List<ReservationCandidate> getReservationCandidatesAsDeparture() {
		return new LinkedList<ReservationCandidate>(wrapNull(reservationCandidatesAsDeparture));
	}

	public void setReservationCandidatesAsDeparture(List<ReservationCandidate> reservationCandidatesAsDeparture) {
		this.reservationCandidatesAsDeparture = new LinkedList<ReservationCandidate>(wrapNull(reservationCandidatesAsDeparture));
	}

	public void clearReservationCandidatesAsDeparture() {
		this.reservationCandidatesAsDeparture = new LinkedList<ReservationCandidate>();
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
}
