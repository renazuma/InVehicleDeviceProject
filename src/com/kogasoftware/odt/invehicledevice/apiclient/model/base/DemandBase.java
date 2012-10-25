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
 * 予約条件
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class DemandBase extends Model {
	private static final long serialVersionUID = 3364999462603940302L;

	// Columns
	@JsonProperty private Optional<String> arrivalLock = Optional.absent();
	@JsonProperty private Optional<Integer> arrivalPlatformId = Optional.absent();
	@JsonProperty private Optional<Date> arrivalTime = Optional.absent();
	@JsonProperty private Date createdAt = new Date();
	@JsonProperty private Optional<Date> deletedAt = Optional.absent();
	@JsonProperty private Optional<String> departureLock = Optional.absent();
	@JsonProperty private Optional<Integer> departurePlatformId = Optional.absent();
	@JsonProperty private Optional<Date> departureTime = Optional.absent();
	@JsonProperty private Optional<String> deviceName = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private Optional<String> memo = Optional.absent();
	@JsonProperty private Integer passengerCount = 0;
	@JsonProperty private Optional<Boolean> repeat = Optional.absent();
	@JsonProperty private Optional<Integer> serviceProviderId = Optional.absent();
	@JsonProperty private Optional<Integer> stoppageTime = Optional.absent();
	@JsonProperty private Optional<Integer> unitAssignmentId = Optional.absent();
	@JsonProperty private Date updatedAt = new Date();
	@JsonProperty private Integer userId = 0;

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private Optional<Platform> arrivalPlatform = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<Platform> departurePlatform = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private List<User> fellowUsers = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private Optional<Reservation> reservation = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private List<ReservationCandidate> reservationCandidates = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<ReservationUser> reservationUsers = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private Optional<ServiceProvider> serviceProvider = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<UnitAssignment> unitAssignment = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<User> user = Optional.absent();

	public static final String UNDERSCORE = "demand";
	public static final ResponseConverter<Demand> RESPONSE_CONVERTER = getResponseConverter(Demand.class);
	public static final ResponseConverter<List<Demand>> LIST_RESPONSE_CONVERTER = getListResponseConverter(Demand.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static Demand parse(String jsonString) throws IOException {
		return parse(jsonString, Demand.class);
	}

	public static List<Demand> parseList(String jsonString) throws IOException {
		return parseList(jsonString, Demand.class);
	}

	@JsonIgnore
	public Optional<String> getArrivalLock() {
		return wrapNull(arrivalLock);
	}

	@JsonIgnore
	public void setArrivalLock(Optional<String> arrivalLock) {
		refreshUpdatedAt();
		this.arrivalLock = wrapNull(arrivalLock);
	}

	@JsonIgnore
	public void setArrivalLock(String arrivalLock) {
		setArrivalLock(Optional.fromNullable(arrivalLock));
	}

	public void clearArrivalLock() {
		setArrivalLock(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<Integer> getArrivalPlatformId() {
		return wrapNull(arrivalPlatformId);
	}

	@JsonIgnore
	public void setArrivalPlatformId(Optional<Integer> arrivalPlatformId) {
		refreshUpdatedAt();
		this.arrivalPlatformId = wrapNull(arrivalPlatformId);
		for (Platform presentArrivalPlatform : getArrivalPlatform().asSet()) {
			for (Integer presentArrivalPlatformId : getArrivalPlatformId().asSet()) {
				presentArrivalPlatform.setId(presentArrivalPlatformId);
			}
		}
	}

	@JsonIgnore
	public void setArrivalPlatformId(Integer arrivalPlatformId) {
		setArrivalPlatformId(Optional.fromNullable(arrivalPlatformId));
	}

	public void clearArrivalPlatformId() {
		setArrivalPlatformId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Optional<Date> getArrivalTime() {
		return wrapNull(arrivalTime);
	}

	@JsonIgnore
	public void setArrivalTime(Optional<Date> arrivalTime) {
		refreshUpdatedAt();
		this.arrivalTime = wrapNull(arrivalTime);
	}

	@JsonIgnore
	public void setArrivalTime(Date arrivalTime) {
		setArrivalTime(Optional.fromNullable(arrivalTime));
	}

	public void clearArrivalTime() {
		setArrivalTime(Optional.<Date>absent());
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
	public Optional<String> getDepartureLock() {
		return wrapNull(departureLock);
	}

	@JsonIgnore
	public void setDepartureLock(Optional<String> departureLock) {
		refreshUpdatedAt();
		this.departureLock = wrapNull(departureLock);
	}

	@JsonIgnore
	public void setDepartureLock(String departureLock) {
		setDepartureLock(Optional.fromNullable(departureLock));
	}

	public void clearDepartureLock() {
		setDepartureLock(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<Integer> getDeparturePlatformId() {
		return wrapNull(departurePlatformId);
	}

	@JsonIgnore
	public void setDeparturePlatformId(Optional<Integer> departurePlatformId) {
		refreshUpdatedAt();
		this.departurePlatformId = wrapNull(departurePlatformId);
		for (Platform presentDeparturePlatform : getDeparturePlatform().asSet()) {
			for (Integer presentDeparturePlatformId : getDeparturePlatformId().asSet()) {
				presentDeparturePlatform.setId(presentDeparturePlatformId);
			}
		}
	}

	@JsonIgnore
	public void setDeparturePlatformId(Integer departurePlatformId) {
		setDeparturePlatformId(Optional.fromNullable(departurePlatformId));
	}

	public void clearDeparturePlatformId() {
		setDeparturePlatformId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Optional<Date> getDepartureTime() {
		return wrapNull(departureTime);
	}

	@JsonIgnore
	public void setDepartureTime(Optional<Date> departureTime) {
		refreshUpdatedAt();
		this.departureTime = wrapNull(departureTime);
	}

	@JsonIgnore
	public void setDepartureTime(Date departureTime) {
		setDepartureTime(Optional.fromNullable(departureTime));
	}

	public void clearDepartureTime() {
		setDepartureTime(Optional.<Date>absent());
	}

	@JsonIgnore
	public Optional<String> getDeviceName() {
		return wrapNull(deviceName);
	}

	@JsonIgnore
	public void setDeviceName(Optional<String> deviceName) {
		refreshUpdatedAt();
		this.deviceName = wrapNull(deviceName);
	}

	@JsonIgnore
	public void setDeviceName(String deviceName) {
		setDeviceName(Optional.fromNullable(deviceName));
	}

	public void clearDeviceName() {
		setDeviceName(Optional.<String>absent());
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
	public Optional<String> getMemo() {
		return wrapNull(memo);
	}

	@JsonIgnore
	public void setMemo(Optional<String> memo) {
		refreshUpdatedAt();
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
		refreshUpdatedAt();
		this.passengerCount = wrapNull(passengerCount);
	}

	@JsonIgnore
	public Optional<Boolean> getRepeat() {
		return wrapNull(repeat);
	}

	@JsonIgnore
	public void setRepeat(Optional<Boolean> repeat) {
		refreshUpdatedAt();
		this.repeat = wrapNull(repeat);
	}

	@JsonIgnore
	public void setRepeat(Boolean repeat) {
		setRepeat(Optional.fromNullable(repeat));
	}

	public void clearRepeat() {
		setRepeat(Optional.<Boolean>absent());
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
	public Optional<Integer> getStoppageTime() {
		return wrapNull(stoppageTime);
	}

	@JsonIgnore
	public void setStoppageTime(Optional<Integer> stoppageTime) {
		refreshUpdatedAt();
		this.stoppageTime = wrapNull(stoppageTime);
	}

	@JsonIgnore
	public void setStoppageTime(Integer stoppageTime) {
		setStoppageTime(Optional.fromNullable(stoppageTime));
	}

	public void clearStoppageTime() {
		setStoppageTime(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Optional<Integer> getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	@JsonIgnore
	public void setUnitAssignmentId(Optional<Integer> unitAssignmentId) {
		refreshUpdatedAt();
		this.unitAssignmentId = wrapNull(unitAssignmentId);
		for (UnitAssignment presentUnitAssignment : getUnitAssignment().asSet()) {
			for (Integer presentUnitAssignmentId : getUnitAssignmentId().asSet()) {
				presentUnitAssignment.setId(presentUnitAssignmentId);
			}
		}
	}

	@JsonIgnore
	public void setUnitAssignmentId(Integer unitAssignmentId) {
		setUnitAssignmentId(Optional.fromNullable(unitAssignmentId));
	}

	public void clearUnitAssignmentId() {
		setUnitAssignmentId(Optional.<Integer>absent());
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
	public Integer getUserId() {
		return wrapNull(userId);
	}

	@JsonIgnore
	public void setUserId(Integer userId) {
		refreshUpdatedAt();
		this.userId = wrapNull(userId);
		for (User presentUser : getUser().asSet()) {
			presentUser.setId(getUserId());
		}
	}

	@JsonIgnore
	public Optional<Platform> getArrivalPlatform() {
		return wrapNull(arrivalPlatform);
	}

	@JsonIgnore
	public void setArrivalPlatform(Optional<Platform> arrivalPlatform) {
		refreshUpdatedAt();
		this.arrivalPlatform = wrapNull(arrivalPlatform);
		for (Platform presentArrivalPlatform : getArrivalPlatform().asSet()) {
			setArrivalPlatformId(presentArrivalPlatform.getId());
		}
	}

	@JsonIgnore
	public void setArrivalPlatform(Platform arrivalPlatform) {
		setArrivalPlatform(Optional.fromNullable(arrivalPlatform));
	}

	public void clearArrivalPlatform() {
		setArrivalPlatform(Optional.<Platform>absent());
	}

	@JsonIgnore
	public Optional<Platform> getDeparturePlatform() {
		return wrapNull(departurePlatform);
	}

	@JsonIgnore
	public void setDeparturePlatform(Optional<Platform> departurePlatform) {
		refreshUpdatedAt();
		this.departurePlatform = wrapNull(departurePlatform);
		for (Platform presentDeparturePlatform : getDeparturePlatform().asSet()) {
			setDeparturePlatformId(presentDeparturePlatform.getId());
		}
	}

	@JsonIgnore
	public void setDeparturePlatform(Platform departurePlatform) {
		setDeparturePlatform(Optional.fromNullable(departurePlatform));
	}

	public void clearDeparturePlatform() {
		setDeparturePlatform(Optional.<Platform>absent());
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
	public Optional<Reservation> getReservation() {
		return wrapNull(reservation);
	}

	@JsonIgnore
	public void setReservation(Optional<Reservation> reservation) {
		this.reservation = wrapNull(reservation);
	}

	@JsonIgnore
	public void setReservation(Reservation reservation) {
		setReservation(Optional.fromNullable(reservation));
	}

	public void clearReservation() {
		setReservation(Optional.<Reservation>absent());
	}

	@JsonIgnore
	public List<ReservationCandidate> getReservationCandidates() {
		return wrapNull(reservationCandidates);
	}

	@JsonIgnore
	public void setReservationCandidates(Iterable<ReservationCandidate> reservationCandidates) {
		this.reservationCandidates = wrapNull(reservationCandidates);
	}

	public void clearReservationCandidates() {
		setReservationCandidates(new LinkedList<ReservationCandidate>());
	}

	@JsonIgnore
	public List<ReservationUser> getReservationUsers() {
		return wrapNull(reservationUsers);
	}

	@JsonIgnore
	public void setReservationUsers(Iterable<ReservationUser> reservationUsers) {
		this.reservationUsers = wrapNull(reservationUsers);
	}

	public void clearReservationUsers() {
		setReservationUsers(new LinkedList<ReservationUser>());
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
	public Optional<UnitAssignment> getUnitAssignment() {
		return wrapNull(unitAssignment);
	}

	@JsonIgnore
	public void setUnitAssignment(Optional<UnitAssignment> unitAssignment) {
		refreshUpdatedAt();
		this.unitAssignment = wrapNull(unitAssignment);
		for (UnitAssignment presentUnitAssignment : getUnitAssignment().asSet()) {
			setUnitAssignmentId(presentUnitAssignment.getId());
		}
	}

	@JsonIgnore
	public void setUnitAssignment(UnitAssignment unitAssignment) {
		setUnitAssignment(Optional.fromNullable(unitAssignment));
	}

	public void clearUnitAssignment() {
		setUnitAssignment(Optional.<UnitAssignment>absent());
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
	public Demand clone() {
		return super.clone(Demand.class);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(arrivalLock)
			.append(arrivalPlatformId)
			.append(arrivalTime)
			.append(createdAt)
			.append(deletedAt)
			.append(departureLock)
			.append(departurePlatformId)
			.append(departureTime)
			.append(deviceName)
			.append(id)
			.append(memo)
			.append(passengerCount)
			.append(repeat)
			.append(serviceProviderId)
			.append(stoppageTime)
			.append(unitAssignmentId)
			.append(updatedAt)
			.append(userId)
			.append(arrivalPlatform)
			.append(departurePlatform)
			.append(fellowUsers)
			.append(reservation)
			.append(reservationCandidates)
			.append(reservationUsers)
			.append(serviceProvider)
			.append(unitAssignment)
			.append(user)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof DemandBase)) {
			return false;
		}
		DemandBase other = (DemandBase) obj;
		return new EqualsBuilder()
			.append(arrivalLock, other.arrivalLock)
			.append(arrivalPlatformId, other.arrivalPlatformId)
			.append(arrivalTime, other.arrivalTime)
			.append(createdAt, other.createdAt)
			.append(deletedAt, other.deletedAt)
			.append(departureLock, other.departureLock)
			.append(departurePlatformId, other.departurePlatformId)
			.append(departureTime, other.departureTime)
			.append(deviceName, other.deviceName)
			.append(id, other.id)
			.append(memo, other.memo)
			.append(passengerCount, other.passengerCount)
			.append(repeat, other.repeat)
			.append(serviceProviderId, other.serviceProviderId)
			.append(stoppageTime, other.stoppageTime)
			.append(unitAssignmentId, other.unitAssignmentId)
			.append(updatedAt, other.updatedAt)
			.append(userId, other.userId)
			.append(arrivalPlatform, other.arrivalPlatform)
			.append(departurePlatform, other.departurePlatform)
			.append(fellowUsers, other.fellowUsers)
			.append(reservation, other.reservation)
			.append(reservationCandidates, other.reservationCandidates)
			.append(reservationUsers, other.reservationUsers)
			.append(serviceProvider, other.serviceProvider)
			.append(unitAssignment, other.unitAssignment)
			.append(user, other.user)
			.isEquals();
	}
}
