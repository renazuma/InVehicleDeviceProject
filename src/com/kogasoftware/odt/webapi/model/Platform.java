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

public class Platform extends Model {
	private static final long serialVersionUID = 4611909959706312425L;

	public Platform() {
	}

	public Platform(JSONObject jsonObject) throws JSONException, ParseException {
		setAddress(parseOptionalString(jsonObject, "address"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDemandAreaId(parseOptionalInteger(jsonObject, "demand_area_id"));
		setEndAt(parseOptionalDate(jsonObject, "end_at"));
		setId(parseInteger(jsonObject, "id"));
		setImage(parseOptionalString(jsonObject, "image"));
		setKeyword(parseOptionalString(jsonObject, "keyword"));
		setLatitude(parseBigDecimal(jsonObject, "latitude"));
		setLongitude(parseBigDecimal(jsonObject, "longitude"));
		setMemo(parseOptionalString(jsonObject, "memo"));
		setName(parseString(jsonObject, "name"));
		setNameRuby(parseString(jsonObject, "name_ruby"));
		setSemiDemandAreaId(parseOptionalInteger(jsonObject, "semi_demand_area_id"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setStartAt(parseOptionalDate(jsonObject, "start_at"));
		setTypeOfDemand(parseOptionalInteger(jsonObject, "type_of_demand"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setDemandsAsArrival(Demand.parseList(jsonObject, "demands_as_arrival"));
		setDemandsAsDeparture(Demand.parseList(jsonObject, "demands_as_departure"));
		setReservationCandidatesAsArrival(ReservationCandidate.parseList(jsonObject, "reservation_candidates_as_arrival"));
		setReservationCandidatesAsDeparture(ReservationCandidate.parseList(jsonObject, "reservation_candidates_as_departure"));
		setReservationsAsArrival(Reservation.parseList(jsonObject, "reservations_as_arrival"));
		setReservationsAsDeparture(Reservation.parseList(jsonObject, "reservations_as_departure"));
	}

	public static Optional<Platform> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<Platform>absent();
		}
		return Optional.<Platform>of(new Platform(jsonObject.getJSONObject(key)));
	}

	public static LinkedList<Platform> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Platform>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
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
	public JSONObject toJSONObject() throws JSONException {
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
		jsonObject.put("semi_demand_area_id", toJSON(getSemiDemandAreaId().orNull()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("start_at", toJSON(getStartAt().orNull()));
		jsonObject.put("type_of_demand", toJSON(getTypeOfDemand().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("demands_as_arrival", toJSON(getDemandsAsArrival()));
		jsonObject.put("demands_as_departure", toJSON(getDemandsAsDeparture()));
		jsonObject.put("reservation_candidates_as_arrival", toJSON(getReservationCandidatesAsArrival()));
		jsonObject.put("reservation_candidates_as_departure", toJSON(getReservationCandidatesAsDeparture()));
		jsonObject.put("reservations_as_arrival", toJSON(getReservationsAsArrival()));
		jsonObject.put("reservations_as_departure", toJSON(getReservationsAsDeparture()));
		return jsonObject;
	}

	private Optional<String> address = Optional.<String>absent();

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
		this.address = Optional.<String>absent();
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> deletedAt = Optional.<Date>absent();

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
		this.deletedAt = Optional.<Date>absent();
	}

	private Optional<Integer> demandAreaId = Optional.<Integer>absent();

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
		this.demandAreaId = Optional.<Integer>absent();
	}

	private Optional<Date> endAt = Optional.<Date>absent();

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
		this.endAt = Optional.<Date>absent();
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private Optional<String> image = Optional.<String>absent();

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
		this.image = Optional.<String>absent();
	}

	private Optional<String> keyword = Optional.<String>absent();

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
		this.keyword = Optional.<String>absent();
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

	private Optional<String> memo = Optional.<String>absent();

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
		this.memo = Optional.<String>absent();
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

	private Optional<Integer> semiDemandAreaId = Optional.<Integer>absent();

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
		this.semiDemandAreaId = Optional.<Integer>absent();
	}

	private Optional<Integer> serviceProviderId = Optional.<Integer>absent();

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
		this.serviceProviderId = Optional.<Integer>absent();
	}

	private Optional<Date> startAt = Optional.<Date>absent();

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
		this.startAt = Optional.<Date>absent();
	}

	private Optional<Integer> typeOfDemand = Optional.<Integer>absent();

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
		this.typeOfDemand = Optional.<Integer>absent();
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
}
