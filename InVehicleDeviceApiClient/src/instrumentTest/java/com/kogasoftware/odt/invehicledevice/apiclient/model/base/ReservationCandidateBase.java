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
 * 予約候補
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class ReservationCandidateBase extends Model {
	private static final long serialVersionUID = 2816755569411134842L;

	// Columns
	@JsonProperty private Optional<Double> accuracy = Optional.absent();
	@JsonProperty private Optional<String> arrivalLock = Optional.absent();
	@JsonProperty private Integer arrivalPlatformId = 0;
	@JsonProperty private Date arrivalTime = DEFAULT_DATE_TIME;
	@JsonProperty private Optional<String> characteristic = Optional.absent();
	@JsonProperty private Date createdAt = new Date();
	@JsonProperty private Optional<Date> deletedAt = Optional.absent();
	@JsonProperty private Optional<Integer> demandId = Optional.absent();
	@JsonProperty private Optional<String> departureLock = Optional.absent();
	@JsonProperty private Integer departurePlatformId = 0;
	@JsonProperty private Date departureTime = DEFAULT_DATE_TIME;
	@JsonProperty private Optional<String> deviceName = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private Integer passengerCount = 0;
	@JsonProperty private Optional<Integer> serviceProviderId = Optional.absent();
	@JsonProperty private Optional<Integer> stoppageTime = Optional.absent();
	@JsonProperty private Optional<Integer> unitAssignmentId = Optional.absent();
	@JsonProperty private Date updatedAt = new Date();
	@JsonProperty private Optional<Integer> userId = Optional.absent();

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private Optional<Platform> arrivalPlatform = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<Demand> demand = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<Platform> departurePlatform = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private List<User> fellowUsers = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<ReservationUser> reservationUsers = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private Optional<ServiceProvider> serviceProvider = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<UnitAssignment> unitAssignment = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<User> user = Optional.absent();

	public static final String UNDERSCORE = "reservation_candidate";
	public static final ResponseConverter<ReservationCandidate> RESPONSE_CONVERTER = getResponseConverter(ReservationCandidate.class);
	public static final ResponseConverter<List<ReservationCandidate>> LIST_RESPONSE_CONVERTER = getListResponseConverter(ReservationCandidate.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static ReservationCandidate parse(String jsonString) throws IOException {
		return parse(jsonString, ReservationCandidate.class);
	}

	public static List<ReservationCandidate> parseList(String jsonString) throws IOException {
		return parseList(jsonString, ReservationCandidate.class);
	}

	@JsonIgnore
	public Optional<Double> getAccuracy() {
		return wrapNull(accuracy);
	}

	@JsonIgnore
	public void setAccuracy(Optional<Double> accuracy) {
		refreshUpdatedAt();
		this.accuracy = wrapNull(accuracy);
	}

	@JsonIgnore
	public void setAccuracy(Double accuracy) {
		setAccuracy(Optional.fromNullable(accuracy));
	}

	public void clearAccuracy() {
		setAccuracy(Optional.<Double>absent());
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
	public Integer getArrivalPlatformId() {
		return wrapNull(arrivalPlatformId);
	}

	@JsonIgnore
	public void setArrivalPlatformId(Integer arrivalPlatformId) {
		refreshUpdatedAt();
		this.arrivalPlatformId = wrapNull(arrivalPlatformId);
		for (Platform presentArrivalPlatform : getArrivalPlatform().asSet()) {
			presentArrivalPlatform.setId(getArrivalPlatformId());
		}
	}

	@JsonIgnore
	public Date getArrivalTime() {
		return wrapNull(arrivalTime);
	}

	@JsonIgnore
	public void setArrivalTime(Date arrivalTime) {
		refreshUpdatedAt();
		this.arrivalTime = wrapNull(arrivalTime);
	}

	@JsonIgnore
	public Optional<String> getCharacteristic() {
		return wrapNull(characteristic);
	}

	@JsonIgnore
	public void setCharacteristic(Optional<String> characteristic) {
		refreshUpdatedAt();
		this.characteristic = wrapNull(characteristic);
	}

	@JsonIgnore
	public void setCharacteristic(String characteristic) {
		setCharacteristic(Optional.fromNullable(characteristic));
	}

	public void clearCharacteristic() {
		setCharacteristic(Optional.<String>absent());
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
	public Optional<Integer> getDemandId() {
		return wrapNull(demandId);
	}

	@JsonIgnore
	public void setDemandId(Optional<Integer> demandId) {
		refreshUpdatedAt();
		this.demandId = wrapNull(demandId);
		for (Demand presentDemand : getDemand().asSet()) {
			for (Integer presentDemandId : getDemandId().asSet()) {
				presentDemand.setId(presentDemandId);
			}
		}
	}

	@JsonIgnore
	public void setDemandId(Integer demandId) {
		setDemandId(Optional.fromNullable(demandId));
	}

	public void clearDemandId() {
		setDemandId(Optional.<Integer>absent());
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
	public Integer getDeparturePlatformId() {
		return wrapNull(departurePlatformId);
	}

	@JsonIgnore
	public void setDeparturePlatformId(Integer departurePlatformId) {
		refreshUpdatedAt();
		this.departurePlatformId = wrapNull(departurePlatformId);
		for (Platform presentDeparturePlatform : getDeparturePlatform().asSet()) {
			presentDeparturePlatform.setId(getDeparturePlatformId());
		}
	}

	@JsonIgnore
	public Date getDepartureTime() {
		return wrapNull(departureTime);
	}

	@JsonIgnore
	public void setDepartureTime(Date departureTime) {
		refreshUpdatedAt();
		this.departureTime = wrapNull(departureTime);
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
	public Integer getPassengerCount() {
		return wrapNull(passengerCount);
	}

	@JsonIgnore
	public void setPassengerCount(Integer passengerCount) {
		refreshUpdatedAt();
		this.passengerCount = wrapNull(passengerCount);
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
	public Optional<Demand> getDemand() {
		return wrapNull(demand);
	}

	@JsonIgnore
	public void setDemand(Optional<Demand> demand) {
		refreshUpdatedAt();
		this.demand = wrapNull(demand);
		for (Demand presentDemand : getDemand().asSet()) {
			setDemandId(presentDemand.getId());
		}
	}

	@JsonIgnore
	public void setDemand(Demand demand) {
		setDemand(Optional.fromNullable(demand));
	}

	public void clearDemand() {
		setDemand(Optional.<Demand>absent());
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
	public ReservationCandidate clone() {
		return clone(true);
	}

	@Override
	public ReservationCandidate clone(Boolean withAssociation) {
		return super.clone(ReservationCandidate.class, withAssociation);
	}
}
