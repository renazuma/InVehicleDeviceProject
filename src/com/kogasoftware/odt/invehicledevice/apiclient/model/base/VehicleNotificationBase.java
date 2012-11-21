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
 * 車載器への通知
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class VehicleNotificationBase extends Model {
	private static final long serialVersionUID = 2138620008380953940L;

	// Columns
	@JsonProperty private String body = "";
	@JsonProperty private Optional<String> bodyRuby = Optional.absent();
	@JsonProperty private Date createdAt = new Date();
	@JsonProperty private Optional<Date> eventAt = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private Integer inVehicleDeviceId = 0;
	@JsonProperty private Integer notificationKind = 0;
	@JsonProperty private Optional<Boolean> offline = Optional.absent();
	@JsonProperty private Optional<Integer> operatorId = Optional.absent();
	@JsonProperty private Optional<Date> readAt = Optional.absent();
	@JsonProperty private Optional<Integer> reservationId = Optional.absent();
	@JsonProperty private Optional<Integer> response = Optional.absent();
	@JsonProperty private Date updatedAt = new Date();

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private Optional<InVehicleDevice> inVehicleDevice = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<Operator> operator = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<Reservation> reservation = Optional.absent();

	public static final String UNDERSCORE = "vehicle_notification";
	public static final ResponseConverter<VehicleNotification> RESPONSE_CONVERTER = getResponseConverter(VehicleNotification.class);
	public static final ResponseConverter<List<VehicleNotification>> LIST_RESPONSE_CONVERTER = getListResponseConverter(VehicleNotification.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static VehicleNotification parse(String jsonString) throws IOException {
		return parse(jsonString, VehicleNotification.class);
	}

	public static List<VehicleNotification> parseList(String jsonString) throws IOException {
		return parseList(jsonString, VehicleNotification.class);
	}

	@JsonIgnore
	public String getBody() {
		return wrapNull(body);
	}

	@JsonIgnore
	public void setBody(String body) {
		refreshUpdatedAt();
		this.body = wrapNull(body);
	}

	@JsonIgnore
	public Optional<String> getBodyRuby() {
		return wrapNull(bodyRuby);
	}

	@JsonIgnore
	public void setBodyRuby(Optional<String> bodyRuby) {
		refreshUpdatedAt();
		this.bodyRuby = wrapNull(bodyRuby);
	}

	@JsonIgnore
	public void setBodyRuby(String bodyRuby) {
		setBodyRuby(Optional.fromNullable(bodyRuby));
	}

	public void clearBodyRuby() {
		setBodyRuby(Optional.<String>absent());
	}

	@JsonIgnore
	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	@JsonIgnore
	public void setCreatedAt(Date createdAt) {
		refreshUpdatedAt();
		this.createdAt = wrapNull(createdAt);
	}

	@JsonIgnore
	public Optional<Date> getEventAt() {
		return wrapNull(eventAt);
	}

	@JsonIgnore
	public void setEventAt(Optional<Date> eventAt) {
		refreshUpdatedAt();
		this.eventAt = wrapNull(eventAt);
	}

	@JsonIgnore
	public void setEventAt(Date eventAt) {
		setEventAt(Optional.fromNullable(eventAt));
	}

	public void clearEventAt() {
		setEventAt(Optional.<Date>absent());
	}

	@Override
	@JsonIgnore
	public Integer getId() {
		return wrapNull(id);
	}

	@JsonIgnore
	public void setId(Integer id) {
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	@JsonIgnore
	public Integer getInVehicleDeviceId() {
		return wrapNull(inVehicleDeviceId);
	}

	@JsonIgnore
	public void setInVehicleDeviceId(Integer inVehicleDeviceId) {
		refreshUpdatedAt();
		this.inVehicleDeviceId = wrapNull(inVehicleDeviceId);
		for (InVehicleDevice presentInVehicleDevice : getInVehicleDevice().asSet()) {
			presentInVehicleDevice.setId(getInVehicleDeviceId());
		}
	}

	@JsonIgnore
	public Integer getNotificationKind() {
		return wrapNull(notificationKind);
	}

	@JsonIgnore
	public void setNotificationKind(Integer notificationKind) {
		refreshUpdatedAt();
		this.notificationKind = wrapNull(notificationKind);
	}

	@JsonIgnore
	public Optional<Boolean> getOffline() {
		return wrapNull(offline);
	}

	@JsonIgnore
	public void setOffline(Optional<Boolean> offline) {
		refreshUpdatedAt();
		this.offline = wrapNull(offline);
	}

	@JsonIgnore
	public void setOffline(Boolean offline) {
		setOffline(Optional.fromNullable(offline));
	}

	public void clearOffline() {
		setOffline(Optional.<Boolean>absent());
	}

	@JsonIgnore
	public Optional<Integer> getOperatorId() {
		return wrapNull(operatorId);
	}

	@JsonIgnore
	public void setOperatorId(Optional<Integer> operatorId) {
		refreshUpdatedAt();
		this.operatorId = wrapNull(operatorId);
		for (Operator presentOperator : getOperator().asSet()) {
			for (Integer presentOperatorId : getOperatorId().asSet()) {
				presentOperator.setId(presentOperatorId);
			}
		}
	}

	@JsonIgnore
	public void setOperatorId(Integer operatorId) {
		setOperatorId(Optional.fromNullable(operatorId));
	}

	public void clearOperatorId() {
		setOperatorId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Optional<Date> getReadAt() {
		return wrapNull(readAt);
	}

	@JsonIgnore
	public void setReadAt(Optional<Date> readAt) {
		refreshUpdatedAt();
		this.readAt = wrapNull(readAt);
	}

	@JsonIgnore
	public void setReadAt(Date readAt) {
		setReadAt(Optional.fromNullable(readAt));
	}

	public void clearReadAt() {
		setReadAt(Optional.<Date>absent());
	}

	@JsonIgnore
	public Optional<Integer> getReservationId() {
		return wrapNull(reservationId);
	}

	@JsonIgnore
	public void setReservationId(Optional<Integer> reservationId) {
		refreshUpdatedAt();
		this.reservationId = wrapNull(reservationId);
		for (Reservation presentReservation : getReservation().asSet()) {
			for (Integer presentReservationId : getReservationId().asSet()) {
				presentReservation.setId(presentReservationId);
			}
		}
	}

	@JsonIgnore
	public void setReservationId(Integer reservationId) {
		setReservationId(Optional.fromNullable(reservationId));
	}

	public void clearReservationId() {
		setReservationId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Optional<Integer> getResponse() {
		return wrapNull(response);
	}

	@JsonIgnore
	public void setResponse(Optional<Integer> response) {
		refreshUpdatedAt();
		this.response = wrapNull(response);
	}

	@JsonIgnore
	public void setResponse(Integer response) {
		setResponse(Optional.fromNullable(response));
	}

	public void clearResponse() {
		setResponse(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	@JsonIgnore
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	@JsonIgnore
	public Optional<InVehicleDevice> getInVehicleDevice() {
		return wrapNull(inVehicleDevice);
	}

	@JsonIgnore
	public void setInVehicleDevice(Optional<InVehicleDevice> inVehicleDevice) {
		refreshUpdatedAt();
		this.inVehicleDevice = wrapNull(inVehicleDevice);
		for (InVehicleDevice presentInVehicleDevice : getInVehicleDevice().asSet()) {
			setInVehicleDeviceId(presentInVehicleDevice.getId());
		}
	}

	@JsonIgnore
	public void setInVehicleDevice(InVehicleDevice inVehicleDevice) {
		setInVehicleDevice(Optional.fromNullable(inVehicleDevice));
	}

	public void clearInVehicleDevice() {
		setInVehicleDevice(Optional.<InVehicleDevice>absent());
	}

	@JsonIgnore
	public Optional<Operator> getOperator() {
		return wrapNull(operator);
	}

	@JsonIgnore
	public void setOperator(Optional<Operator> operator) {
		refreshUpdatedAt();
		this.operator = wrapNull(operator);
		for (Operator presentOperator : getOperator().asSet()) {
			setOperatorId(presentOperator.getId());
		}
	}

	@JsonIgnore
	public void setOperator(Operator operator) {
		setOperator(Optional.fromNullable(operator));
	}

	public void clearOperator() {
		setOperator(Optional.<Operator>absent());
	}

	@JsonIgnore
	public Optional<Reservation> getReservation() {
		return wrapNull(reservation);
	}

	@JsonIgnore
	public void setReservation(Optional<Reservation> reservation) {
		refreshUpdatedAt();
		this.reservation = wrapNull(reservation);
		for (Reservation presentReservation : getReservation().asSet()) {
			setReservationId(presentReservation.getId());
		}
	}

	@JsonIgnore
	public void setReservation(Reservation reservation) {
		setReservation(Optional.fromNullable(reservation));
	}

	public void clearReservation() {
		setReservation(Optional.<Reservation>absent());
	}

	@Override
	public VehicleNotification clone() {
		return clone(true);
	}

	@Override
	public VehicleNotification clone(Boolean withAssociation) {
		return super.clone(VehicleNotification.class, withAssociation);
	}
}
