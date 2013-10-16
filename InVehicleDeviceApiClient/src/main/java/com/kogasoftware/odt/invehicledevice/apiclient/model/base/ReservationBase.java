package com.kogasoftware.odt.invehicledevice.apiclient.model.base;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.apiclient.ApiClient.ResponseConverter;
import com.kogasoftware.odt.apiclient.ApiClients;
import com.kogasoftware.odt.invehicledevice.apiclient.model.*;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.jsondeserializer.*;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.jsonview.*;

/**
 * 予約
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class ReservationBase extends Model {
	private static final long serialVersionUID = 8844732766756558973L;

	// Columns
	@JsonProperty private Optional<Integer> arrivalScheduleId = Optional.absent();
	@JsonProperty private Optional<Integer> departureScheduleId = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private Optional<String> memo = Optional.absent();
	@JsonProperty private Integer passengerCount = 0;
	@JsonProperty private Optional<Integer> userId = Optional.absent();

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private List<User> fellowUsers = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<PassengerRecord> passengerRecords = Lists.newLinkedList();

	public static final String UNDERSCORE = "reservation";
	public static final ResponseConverter<Reservation> RESPONSE_CONVERTER = getResponseConverter(Reservation.class);
	public static final ResponseConverter<List<Reservation>> LIST_RESPONSE_CONVERTER = getListResponseConverter(Reservation.class);

	public static Reservation parse(String jsonString) throws IOException {
		return parse(jsonString, Reservation.class);
	}

	public static List<Reservation> parseList(String jsonString) throws IOException {
		return parseList(jsonString, Reservation.class);
	}

	@JsonIgnore
	public Optional<Integer> getArrivalScheduleId() {
		return wrapNull(arrivalScheduleId);
	}

	@JsonIgnore
	public void setArrivalScheduleId(Optional<Integer> arrivalScheduleId) {
		this.arrivalScheduleId = wrapNull(arrivalScheduleId);
	}

	@JsonIgnore
	public void setArrivalScheduleId(Integer arrivalScheduleId) {
		setArrivalScheduleId(Optional.fromNullable(arrivalScheduleId));
	}

	public void clearArrivalScheduleId() {
		setArrivalScheduleId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Optional<Integer> getDepartureScheduleId() {
		return wrapNull(departureScheduleId);
	}

	@JsonIgnore
	public void setDepartureScheduleId(Optional<Integer> departureScheduleId) {
		this.departureScheduleId = wrapNull(departureScheduleId);
	}

	@JsonIgnore
	public void setDepartureScheduleId(Integer departureScheduleId) {
		setDepartureScheduleId(Optional.fromNullable(departureScheduleId));
	}

	public void clearDepartureScheduleId() {
		setDepartureScheduleId(Optional.<Integer>absent());
	}

	@Override
	@JsonIgnore
	public Integer getId() {
		return wrapNull(id);
	}

	@JsonIgnore
	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	@JsonIgnore
	public Optional<String> getMemo() {
		return wrapNull(memo);
	}

	@JsonIgnore
	public void setMemo(Optional<String> memo) {
		this.memo = wrapNull(memo);
	}

	@JsonIgnore
	public void setMemo(String memo) {
		setMemo(Optional.fromNullable(memo));
	}

	public void clearMemo() {
		setMemo(Optional.<String>absent());
	}

	@JsonIgnore
	public Integer getPassengerCount() {
		return wrapNull(passengerCount);
	}

	@JsonIgnore
	public void setPassengerCount(Integer passengerCount) {
		this.passengerCount = wrapNull(passengerCount);
	}

	@JsonIgnore
	public Optional<Integer> getUserId() {
		return wrapNull(userId);
	}

	@JsonIgnore
	public void setUserId(Optional<Integer> userId) {
		this.userId = wrapNull(userId);
	}

	@JsonIgnore
	public void setUserId(Integer userId) {
		setUserId(Optional.fromNullable(userId));
	}

	public void clearUserId() {
		setUserId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public List<User> getFellowUsers() {
		return wrapNull(fellowUsers);
	}

	@JsonIgnore
	public void setFellowUsers(Iterable<User> fellowUsers) {
		this.fellowUsers = wrapNull(fellowUsers);
	}

	public void clearFellowUsers() {
		setFellowUsers(new LinkedList<User>());
	}

	@JsonIgnore
	public List<PassengerRecord> getPassengerRecords() {
		return wrapNull(passengerRecords);
	}

	@JsonIgnore
	public void setPassengerRecords(Iterable<PassengerRecord> passengerRecords) {
		this.passengerRecords = wrapNull(passengerRecords);
	}

	public void clearPassengerRecords() {
		setPassengerRecords(new LinkedList<PassengerRecord>());
	}

	@Override
	public Reservation clone() {
		return clone(true);
	}

	@Override
	public Reservation clone(Boolean withAssociation) {
		return super.clone(Reservation.class, withAssociation);
	}
}
