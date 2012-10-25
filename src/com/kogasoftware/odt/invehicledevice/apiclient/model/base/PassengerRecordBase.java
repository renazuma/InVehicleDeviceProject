package com.kogasoftware.odt.invehicledevice.apiclient.model.base;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
 * 乗車記録
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class PassengerRecordBase extends Model {
	private static final long serialVersionUID = 9013969978927692724L;

	// Columns
	@JsonProperty private Optional<Integer> age = Optional.absent();
	@JsonProperty private Optional<Integer> arrivalOperationScheduleId = Optional.absent();
	@JsonProperty private Date createdAt = new Date();
	@JsonProperty private Optional<Date> deletedAt = Optional.absent();
	@JsonProperty private Optional<Integer> departureOperationScheduleId = Optional.absent();
	@JsonProperty private Optional<Date> getOffTime = Optional.absent();
	@JsonProperty private Optional<Boolean> getOffTimeOffline = Optional.absent();
	@JsonProperty private Optional<Date> getOnTime = Optional.absent();
	@JsonProperty private Optional<Boolean> getOnTimeOffline = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private Integer passengerCount = 0;
	@JsonProperty private Optional<Integer> payment = Optional.absent();
	@JsonProperty private Optional<Integer> reservationId = Optional.absent();
	@JsonProperty private Optional<Integer> serviceProviderId = Optional.absent();
	@JsonProperty private Integer status = 0;
	@JsonProperty private Date updatedAt = new Date();
	@JsonProperty private Optional<Integer> userId = Optional.absent();

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private Optional<OperationSchedule> arrivalOperationSchedule = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<OperationSchedule> departureOperationSchedule = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<Reservation> reservation = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<ServiceProvider> serviceProvider = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<User> user = Optional.absent();

	public static final String UNDERSCORE = "passenger_record";
	public static final ResponseConverter<PassengerRecord> RESPONSE_CONVERTER = getResponseConverter(PassengerRecord.class);
	public static final ResponseConverter<List<PassengerRecord>> LIST_RESPONSE_CONVERTER = getListResponseConverter(PassengerRecord.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static PassengerRecord parse(String jsonString) throws IOException {
		return parse(jsonString, PassengerRecord.class);
	}

	public static List<PassengerRecord> parseList(String jsonString) throws IOException {
		return parseList(jsonString, PassengerRecord.class);
	}

	@JsonIgnore
	public Optional<Integer> getAge() {
		return wrapNull(age);
	}

	@JsonIgnore
	public void setAge(Optional<Integer> age) {
		refreshUpdatedAt();
		this.age = wrapNull(age);
	}

	@JsonIgnore
	public void setAge(Integer age) {
		setAge(Optional.fromNullable(age));
	}

	public void clearAge() {
		setAge(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Optional<Integer> getArrivalOperationScheduleId() {
		return wrapNull(arrivalOperationScheduleId);
	}

	@JsonIgnore
	public void setArrivalOperationScheduleId(Optional<Integer> arrivalOperationScheduleId) {
		refreshUpdatedAt();
		this.arrivalOperationScheduleId = wrapNull(arrivalOperationScheduleId);
		for (OperationSchedule presentArrivalOperationSchedule : getArrivalOperationSchedule().asSet()) {
			for (Integer presentArrivalOperationScheduleId : getArrivalOperationScheduleId().asSet()) {
				presentArrivalOperationSchedule.setId(presentArrivalOperationScheduleId);
			}
		}
	}

	@JsonIgnore
	public void setArrivalOperationScheduleId(Integer arrivalOperationScheduleId) {
		setArrivalOperationScheduleId(Optional.fromNullable(arrivalOperationScheduleId));
	}

	public void clearArrivalOperationScheduleId() {
		setArrivalOperationScheduleId(Optional.<Integer>absent());
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
	public Optional<Date> getDeletedAt() {
		return wrapNull(deletedAt);
	}

	@JsonIgnore
	public void setDeletedAt(Optional<Date> deletedAt) {
		refreshUpdatedAt();
		this.deletedAt = wrapNull(deletedAt);
	}

	@JsonIgnore
	public void setDeletedAt(Date deletedAt) {
		setDeletedAt(Optional.fromNullable(deletedAt));
	}

	public void clearDeletedAt() {
		setDeletedAt(Optional.<Date>absent());
	}

	@JsonIgnore
	public Optional<Integer> getDepartureOperationScheduleId() {
		return wrapNull(departureOperationScheduleId);
	}

	@JsonIgnore
	public void setDepartureOperationScheduleId(Optional<Integer> departureOperationScheduleId) {
		refreshUpdatedAt();
		this.departureOperationScheduleId = wrapNull(departureOperationScheduleId);
		for (OperationSchedule presentDepartureOperationSchedule : getDepartureOperationSchedule().asSet()) {
			for (Integer presentDepartureOperationScheduleId : getDepartureOperationScheduleId().asSet()) {
				presentDepartureOperationSchedule.setId(presentDepartureOperationScheduleId);
			}
		}
	}

	@JsonIgnore
	public void setDepartureOperationScheduleId(Integer departureOperationScheduleId) {
		setDepartureOperationScheduleId(Optional.fromNullable(departureOperationScheduleId));
	}

	public void clearDepartureOperationScheduleId() {
		setDepartureOperationScheduleId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Optional<Date> getGetOffTime() {
		return wrapNull(getOffTime);
	}

	@JsonIgnore
	public void setGetOffTime(Optional<Date> getOffTime) {
		refreshUpdatedAt();
		this.getOffTime = wrapNull(getOffTime);
	}

	@JsonIgnore
	public void setGetOffTime(Date getOffTime) {
		setGetOffTime(Optional.fromNullable(getOffTime));
	}

	public void clearGetOffTime() {
		setGetOffTime(Optional.<Date>absent());
	}

	@JsonIgnore
	public Optional<Boolean> getGetOffTimeOffline() {
		return wrapNull(getOffTimeOffline);
	}

	@JsonIgnore
	public void setGetOffTimeOffline(Optional<Boolean> getOffTimeOffline) {
		refreshUpdatedAt();
		this.getOffTimeOffline = wrapNull(getOffTimeOffline);
	}

	@JsonIgnore
	public void setGetOffTimeOffline(Boolean getOffTimeOffline) {
		setGetOffTimeOffline(Optional.fromNullable(getOffTimeOffline));
	}

	public void clearGetOffTimeOffline() {
		setGetOffTimeOffline(Optional.<Boolean>absent());
	}

	@JsonIgnore
	public Optional<Date> getGetOnTime() {
		return wrapNull(getOnTime);
	}

	@JsonIgnore
	public void setGetOnTime(Optional<Date> getOnTime) {
		refreshUpdatedAt();
		this.getOnTime = wrapNull(getOnTime);
	}

	@JsonIgnore
	public void setGetOnTime(Date getOnTime) {
		setGetOnTime(Optional.fromNullable(getOnTime));
	}

	public void clearGetOnTime() {
		setGetOnTime(Optional.<Date>absent());
	}

	@JsonIgnore
	public Optional<Boolean> getGetOnTimeOffline() {
		return wrapNull(getOnTimeOffline);
	}

	@JsonIgnore
	public void setGetOnTimeOffline(Optional<Boolean> getOnTimeOffline) {
		refreshUpdatedAt();
		this.getOnTimeOffline = wrapNull(getOnTimeOffline);
	}

	@JsonIgnore
	public void setGetOnTimeOffline(Boolean getOnTimeOffline) {
		setGetOnTimeOffline(Optional.fromNullable(getOnTimeOffline));
	}

	public void clearGetOnTimeOffline() {
		setGetOnTimeOffline(Optional.<Boolean>absent());
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
	public Integer getPassengerCount() {
		return wrapNull(passengerCount);
	}

	@JsonIgnore
	public void setPassengerCount(Integer passengerCount) {
		refreshUpdatedAt();
		this.passengerCount = wrapNull(passengerCount);
	}

	@JsonIgnore
	public Optional<Integer> getPayment() {
		return wrapNull(payment);
	}

	@JsonIgnore
	public void setPayment(Optional<Integer> payment) {
		refreshUpdatedAt();
		this.payment = wrapNull(payment);
	}

	@JsonIgnore
	public void setPayment(Integer payment) {
		setPayment(Optional.fromNullable(payment));
	}

	public void clearPayment() {
		setPayment(Optional.<Integer>absent());
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
	public Optional<Integer> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	@JsonIgnore
	public void setServiceProviderId(Optional<Integer> serviceProviderId) {
		refreshUpdatedAt();
		this.serviceProviderId = wrapNull(serviceProviderId);
		for (ServiceProvider presentServiceProvider : getServiceProvider().asSet()) {
			for (Integer presentServiceProviderId : getServiceProviderId().asSet()) {
				presentServiceProvider.setId(presentServiceProviderId);
			}
		}
	}

	@JsonIgnore
	public void setServiceProviderId(Integer serviceProviderId) {
		setServiceProviderId(Optional.fromNullable(serviceProviderId));
	}

	public void clearServiceProviderId() {
		setServiceProviderId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Integer getStatus() {
		return wrapNull(status);
	}

	@JsonIgnore
	public void setStatus(Integer status) {
		refreshUpdatedAt();
		this.status = wrapNull(status);
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
	public Optional<Integer> getUserId() {
		return wrapNull(userId);
	}

	@JsonIgnore
	public void setUserId(Optional<Integer> userId) {
		refreshUpdatedAt();
		this.userId = wrapNull(userId);
		for (User presentUser : getUser().asSet()) {
			for (Integer presentUserId : getUserId().asSet()) {
				presentUser.setId(presentUserId);
			}
		}
	}

	@JsonIgnore
	public void setUserId(Integer userId) {
		setUserId(Optional.fromNullable(userId));
	}

	public void clearUserId() {
		setUserId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Optional<OperationSchedule> getArrivalOperationSchedule() {
		return wrapNull(arrivalOperationSchedule);
	}

	@JsonIgnore
	public void setArrivalOperationSchedule(Optional<OperationSchedule> arrivalOperationSchedule) {
		refreshUpdatedAt();
		this.arrivalOperationSchedule = wrapNull(arrivalOperationSchedule);
		for (OperationSchedule presentArrivalOperationSchedule : getArrivalOperationSchedule().asSet()) {
			setArrivalOperationScheduleId(presentArrivalOperationSchedule.getId());
		}
	}

	@JsonIgnore
	public void setArrivalOperationSchedule(OperationSchedule arrivalOperationSchedule) {
		setArrivalOperationSchedule(Optional.fromNullable(arrivalOperationSchedule));
	}

	public void clearArrivalOperationSchedule() {
		setArrivalOperationSchedule(Optional.<OperationSchedule>absent());
	}

	@JsonIgnore
	public Optional<OperationSchedule> getDepartureOperationSchedule() {
		return wrapNull(departureOperationSchedule);
	}

	@JsonIgnore
	public void setDepartureOperationSchedule(Optional<OperationSchedule> departureOperationSchedule) {
		refreshUpdatedAt();
		this.departureOperationSchedule = wrapNull(departureOperationSchedule);
		for (OperationSchedule presentDepartureOperationSchedule : getDepartureOperationSchedule().asSet()) {
			setDepartureOperationScheduleId(presentDepartureOperationSchedule.getId());
		}
	}

	@JsonIgnore
	public void setDepartureOperationSchedule(OperationSchedule departureOperationSchedule) {
		setDepartureOperationSchedule(Optional.fromNullable(departureOperationSchedule));
	}

	public void clearDepartureOperationSchedule() {
		setDepartureOperationSchedule(Optional.<OperationSchedule>absent());
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

	@JsonIgnore
	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	@JsonIgnore
	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		refreshUpdatedAt();
		this.serviceProvider = wrapNull(serviceProvider);
		for (ServiceProvider presentServiceProvider : getServiceProvider().asSet()) {
			setServiceProviderId(presentServiceProvider.getId());
		}
	}

	@JsonIgnore
	public void setServiceProvider(ServiceProvider serviceProvider) {
		setServiceProvider(Optional.fromNullable(serviceProvider));
	}

	public void clearServiceProvider() {
		setServiceProvider(Optional.<ServiceProvider>absent());
	}

	@JsonIgnore
	public Optional<User> getUser() {
		return wrapNull(user);
	}

	@JsonIgnore
	public void setUser(Optional<User> user) {
		refreshUpdatedAt();
		this.user = wrapNull(user);
		for (User presentUser : getUser().asSet()) {
			setUserId(presentUser.getId());
		}
	}

	@JsonIgnore
	public void setUser(User user) {
		setUser(Optional.fromNullable(user));
	}

	public void clearUser() {
		setUser(Optional.<User>absent());
	}

	@Override
	public PassengerRecord clone() {
		return super.clone(PassengerRecord.class);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(age)
			.append(arrivalOperationScheduleId)
			.append(createdAt)
			.append(deletedAt)
			.append(departureOperationScheduleId)
			.append(getOffTime)
			.append(getOffTimeOffline)
			.append(getOnTime)
			.append(getOnTimeOffline)
			.append(id)
			.append(passengerCount)
			.append(payment)
			.append(reservationId)
			.append(serviceProviderId)
			.append(status)
			.append(updatedAt)
			.append(userId)
			.append(arrivalOperationSchedule)
			.append(departureOperationSchedule)
			.append(reservation)
			.append(serviceProvider)
			.append(user)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof PassengerRecordBase)) {
			return false;
		}
		PassengerRecordBase other = (PassengerRecordBase) obj;
		return new EqualsBuilder()
			.append(age, other.age)
			.append(arrivalOperationScheduleId, other.arrivalOperationScheduleId)
			.append(createdAt, other.createdAt)
			.append(deletedAt, other.deletedAt)
			.append(departureOperationScheduleId, other.departureOperationScheduleId)
			.append(getOffTime, other.getOffTime)
			.append(getOffTimeOffline, other.getOffTimeOffline)
			.append(getOnTime, other.getOnTime)
			.append(getOnTimeOffline, other.getOnTimeOffline)
			.append(id, other.id)
			.append(passengerCount, other.passengerCount)
			.append(payment, other.payment)
			.append(reservationId, other.reservationId)
			.append(serviceProviderId, other.serviceProviderId)
			.append(status, other.status)
			.append(updatedAt, other.updatedAt)
			.append(userId, other.userId)
			.append(arrivalOperationSchedule, other.arrivalOperationSchedule)
			.append(departureOperationSchedule, other.departureOperationSchedule)
			.append(reservation, other.reservation)
			.append(serviceProvider, other.serviceProvider)
			.append(user, other.user)
			.isEquals();
	}
}
