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
 * 運行予定
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class OperationScheduleBase extends Model {
	private static final long serialVersionUID = 650036520492545179L;

	// Columns
	@JsonProperty private Optional<Date> arrivalEstimate = Optional.absent();
	@JsonProperty private Optional<Date> departureEstimate = Optional.absent();
	@JsonProperty private Integer id = 0;

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private Optional<OperationRecord> operationRecord = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<Platform> platform = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private List<Reservation> reservationsAsArrival = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<Reservation> reservationsAsDeparture = Lists.newLinkedList();

	public static final String UNDERSCORE = "operation_schedule";
	public static final ResponseConverter<UnmergedOperationSchedule> RESPONSE_CONVERTER = getResponseConverter(UnmergedOperationSchedule.class);
	public static final ResponseConverter<List<UnmergedOperationSchedule>> LIST_RESPONSE_CONVERTER = getListResponseConverter(UnmergedOperationSchedule.class);

	public static UnmergedOperationSchedule parse(String jsonString) throws IOException {
		return parse(jsonString, UnmergedOperationSchedule.class);
	}

	public static List<UnmergedOperationSchedule> parseList(String jsonString) throws IOException {
		return parseList(jsonString, UnmergedOperationSchedule.class);
	}

	@JsonIgnore
	public Optional<Date> getArrivalEstimate() {
		return wrapNull(arrivalEstimate);
	}

	@JsonIgnore
	public void setArrivalEstimate(Optional<Date> arrivalEstimate) {
		this.arrivalEstimate = wrapNull(arrivalEstimate);
	}

	@JsonIgnore
	public void setArrivalEstimate(Date arrivalEstimate) {
		setArrivalEstimate(Optional.fromNullable(arrivalEstimate));
	}

	public void clearArrivalEstimate() {
		setArrivalEstimate(Optional.<Date>absent());
	}

	@JsonIgnore
	public Optional<Date> getDepartureEstimate() {
		return wrapNull(departureEstimate);
	}

	@JsonIgnore
	public void setDepartureEstimate(Optional<Date> departureEstimate) {
		this.departureEstimate = wrapNull(departureEstimate);
	}

	@JsonIgnore
	public void setDepartureEstimate(Date departureEstimate) {
		setDepartureEstimate(Optional.fromNullable(departureEstimate));
	}

	public void clearDepartureEstimate() {
		setDepartureEstimate(Optional.<Date>absent());
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
	public Optional<OperationRecord> getOperationRecord() {
		return wrapNull(operationRecord);
	}

	@JsonIgnore
	public void setOperationRecord(Optional<OperationRecord> operationRecord) {
		this.operationRecord = wrapNull(operationRecord);
	}

	@JsonIgnore
	public void setOperationRecord(OperationRecord operationRecord) {
		setOperationRecord(Optional.fromNullable(operationRecord));
	}

	public void clearOperationRecord() {
		setOperationRecord(Optional.<OperationRecord>absent());
	}

	@JsonIgnore
	public Optional<Platform> getPlatform() {
		return wrapNull(platform);
	}

	@JsonIgnore
	public void setPlatform(Optional<Platform> platform) {
		this.platform = wrapNull(platform);
	}

	@JsonIgnore
	public void setPlatform(Platform platform) {
		setPlatform(Optional.fromNullable(platform));
	}

	public void clearPlatform() {
		setPlatform(Optional.<Platform>absent());
	}

	@JsonIgnore
	public List<Reservation> getReservationsAsArrival() {
		return wrapNull(reservationsAsArrival);
	}

	@JsonIgnore
	public void setReservationsAsArrival(Iterable<Reservation> reservationsAsArrival) {
		this.reservationsAsArrival = wrapNull(reservationsAsArrival);
	}

	public void clearReservationsAsArrival() {
		setReservationsAsArrival(new LinkedList<Reservation>());
	}

	@JsonIgnore
	public List<Reservation> getReservationsAsDeparture() {
		return wrapNull(reservationsAsDeparture);
	}

	@JsonIgnore
	public void setReservationsAsDeparture(Iterable<Reservation> reservationsAsDeparture) {
		this.reservationsAsDeparture = wrapNull(reservationsAsDeparture);
	}

	public void clearReservationsAsDeparture() {
		setReservationsAsDeparture(new LinkedList<Reservation>());
	}

	@Override
	public UnmergedOperationSchedule clone() {
		return clone(true);
	}

	@Override
	public UnmergedOperationSchedule clone(Boolean withAssociation) {
		return super.clone(UnmergedOperationSchedule.class, withAssociation);
	}
}
