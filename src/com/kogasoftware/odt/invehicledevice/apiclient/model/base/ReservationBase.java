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
 * 予約
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class ReservationBase extends Model {
	private static final long serialVersionUID = 540225908696305220L;

	// Columns
	@JsonProperty private Optional<String> arrivalLock = Optional.absent();
	@JsonProperty private Optional<Integer> arrivalPlatformId = Optional.absent();
	@JsonProperty private Optional<Integer> arrivalScheduleId = Optional.absent();
	@JsonProperty private Date arrivalTime = DEFAULT_DATE_TIME;
	@JsonProperty private Date createdAt = new Date();
	@JsonProperty private Optional<Date> deletedAt = Optional.absent();
	@JsonProperty private Optional<Integer> demandId = Optional.absent();
	@JsonProperty private Optional<String> departureLock = Optional.absent();
	@JsonProperty private Optional<Integer> departurePlatformId = Optional.absent();
	@JsonProperty private Optional<Integer> departureScheduleId = Optional.absent();
	@JsonProperty private Date departureTime = DEFAULT_DATE_TIME;
	@JsonProperty private Optional<String> deviceName = Optional.absent();
	@JsonProperty private Optional<Boolean> dummy = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private Optional<String> memo = Optional.absent();
	@JsonProperty private Optional<Integer> operatorId = Optional.absent();
	@JsonProperty private Integer passengerCount = 0;
	@JsonProperty private Integer payment = 0;
	@JsonProperty private Optional<Integer> serviceProviderId = Optional.absent();
	@JsonProperty private Integer status = 0;
	@JsonProperty private Optional<Integer> stoppageTime = Optional.absent();
	@JsonProperty private Optional<Date> transferredAt = Optional.absent();
	@JsonProperty private Integer unitAssignmentId = 0;
	@JsonProperty private Date updatedAt = new Date();
	@JsonProperty private Optional<Integer> userId = Optional.absent();

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private Optional<Platform> arrivalPlatform = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<OperationSchedule> arrivalSchedule = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<Demand> demand = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<Platform> departurePlatform = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<OperationSchedule> departureSchedule = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private List<User> fellowUsers = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private Optional<Operator> operator = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private List<PassengerRecord> passengerRecords = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<ReservationUser> reservationUsers = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private Optional<ServiceProvider> serviceProvider = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<UnitAssignment> unitAssignment = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<User> user = Optional.absent();

	public static final String UNDERSCORE = "reservation";
	public static final ResponseConverter<Reservation> RESPONSE_CONVERTER = getResponseConverter(Reservation.class);
	public static final ResponseConverter<List<Reservation>> LIST_RESPONSE_CONVERTER = getListResponseConverter(Reservation.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static Reservation parse(String jsonString) throws IOException {
		return parse(jsonString, Reservation.class);
	}

	public static List<Reservation> parseList(String jsonString) throws IOException {
		return parseList(jsonString, Reservation.class);
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
	public Optional<Integer> getArrivalScheduleId() {
		return wrapNull(arrivalScheduleId);
	}

	@JsonIgnore
	public void setArrivalScheduleId(Optional<Integer> arrivalScheduleId) {
		refreshUpdatedAt();
		this.arrivalScheduleId = wrapNull(arrivalScheduleId);
		for (OperationSchedule presentArrivalSchedule : getArrivalSchedule().asSet()) {
			for (Integer presentArrivalScheduleId : getArrivalScheduleId().asSet()) {
				presentArrivalSchedule.setId(presentArrivalScheduleId);
			}
		}
	}

	@JsonIgnore
	public void setArrivalScheduleId(Integer arrivalScheduleId) {
		setArrivalScheduleId(Optional.fromNullable(arrivalScheduleId));
	}

	public void clearArrivalScheduleId() {
		setArrivalScheduleId(Optional.<Integer>absent());
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
	public Optional<Integer> getDepartureScheduleId() {
		return wrapNull(departureScheduleId);
	}

	@JsonIgnore
	public void setDepartureScheduleId(Optional<Integer> departureScheduleId) {
		refreshUpdatedAt();
		this.departureScheduleId = wrapNull(departureScheduleId);
		for (OperationSchedule presentDepartureSchedule : getDepartureSchedule().asSet()) {
			for (Integer presentDepartureScheduleId : getDepartureScheduleId().asSet()) {
				presentDepartureSchedule.setId(presentDepartureScheduleId);
			}
		}
	}

	@JsonIgnore
	public void setDepartureScheduleId(Integer departureScheduleId) {
		setDepartureScheduleId(Optional.fromNullable(departureScheduleId));
	}

	public void clearDepartureScheduleId() {
		setDepartureScheduleId(Optional.<Integer>absent());
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

	@JsonIgnore
	public Optional<Boolean> getDummy() {
		return wrapNull(dummy);
	}

	@JsonIgnore
	public void setDummy(Optional<Boolean> dummy) {
		refreshUpdatedAt();
		this.dummy = wrapNull(dummy);
	}

	@JsonIgnore
	public void setDummy(Boolean dummy) {
		setDummy(Optional.fromNullable(dummy));
	}

	public void clearDummy() {
		setDummy(Optional.<Boolean>absent());
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
	public Integer getPassengerCount() {
		return wrapNull(passengerCount);
	}

	@JsonIgnore
	public void setPassengerCount(Integer passengerCount) {
		refreshUpdatedAt();
		this.passengerCount = wrapNull(passengerCount);
	}

	@JsonIgnore
	public Integer getPayment() {
		return wrapNull(payment);
	}

	@JsonIgnore
	public void setPayment(Integer payment) {
		refreshUpdatedAt();
		this.payment = wrapNull(payment);
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
	public Optional<Date> getTransferredAt() {
		return wrapNull(transferredAt);
	}

	@JsonIgnore
	public void setTransferredAt(Optional<Date> transferredAt) {
		refreshUpdatedAt();
		this.transferredAt = wrapNull(transferredAt);
	}

	@JsonIgnore
	public void setTransferredAt(Date transferredAt) {
		setTransferredAt(Optional.fromNullable(transferredAt));
	}

	public void clearTransferredAt() {
		setTransferredAt(Optional.<Date>absent());
	}

	@JsonIgnore
	public Integer getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	@JsonIgnore
	public void setUnitAssignmentId(Integer unitAssignmentId) {
		refreshUpdatedAt();
		this.unitAssignmentId = wrapNull(unitAssignmentId);
		for (UnitAssignment presentUnitAssignment : getUnitAssignment().asSet()) {
			presentUnitAssignment.setId(getUnitAssignmentId());
		}
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
	public Optional<OperationSchedule> getArrivalSchedule() {
		return wrapNull(arrivalSchedule);
	}

	@JsonIgnore
	public void setArrivalSchedule(Optional<OperationSchedule> arrivalSchedule) {
		refreshUpdatedAt();
		this.arrivalSchedule = wrapNull(arrivalSchedule);
		for (OperationSchedule presentArrivalSchedule : getArrivalSchedule().asSet()) {
			setArrivalScheduleId(presentArrivalSchedule.getId());
		}
	}

	@JsonIgnore
	public void setArrivalSchedule(OperationSchedule arrivalSchedule) {
		setArrivalSchedule(Optional.fromNullable(arrivalSchedule));
	}

	public void clearArrivalSchedule() {
		setArrivalSchedule(Optional.<OperationSchedule>absent());
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
	public Optional<OperationSchedule> getDepartureSchedule() {
		return wrapNull(departureSchedule);
	}

	@JsonIgnore
	public void setDepartureSchedule(Optional<OperationSchedule> departureSchedule) {
		refreshUpdatedAt();
		this.departureSchedule = wrapNull(departureSchedule);
		for (OperationSchedule presentDepartureSchedule : getDepartureSchedule().asSet()) {
			setDepartureScheduleId(presentDepartureSchedule.getId());
		}
	}

	@JsonIgnore
	public void setDepartureSchedule(OperationSchedule departureSchedule) {
		setDepartureSchedule(Optional.fromNullable(departureSchedule));
	}

	public void clearDepartureSchedule() {
		setDepartureSchedule(Optional.<OperationSchedule>absent());
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
	public Reservation clone() {
		return super.clone(Reservation.class);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(arrivalLock)
			.append(arrivalPlatformId)
			.append(arrivalScheduleId)
			.append(arrivalTime)
			.append(createdAt)
			.append(deletedAt)
			.append(demandId)
			.append(departureLock)
			.append(departurePlatformId)
			.append(departureScheduleId)
			.append(departureTime)
			.append(deviceName)
			.append(dummy)
			.append(id)
			.append(memo)
			.append(operatorId)
			.append(passengerCount)
			.append(payment)
			.append(serviceProviderId)
			.append(status)
			.append(stoppageTime)
			.append(transferredAt)
			.append(unitAssignmentId)
			.append(updatedAt)
			.append(userId)
			.append(arrivalPlatform)
			.append(arrivalSchedule)
			.append(demand)
			.append(departurePlatform)
			.append(departureSchedule)
			.append(fellowUsers)
			.append(operator)
			.append(passengerRecords)
			.append(reservationUsers)
			.append(serviceProvider)
			.append(unitAssignment)
			.append(user)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof ReservationBase)) {
			return false;
		}
		ReservationBase other = (ReservationBase) obj;
		return new EqualsBuilder()
			.append(arrivalLock, other.arrivalLock)
			.append(arrivalPlatformId, other.arrivalPlatformId)
			.append(arrivalScheduleId, other.arrivalScheduleId)
			.append(arrivalTime, other.arrivalTime)
			.append(createdAt, other.createdAt)
			.append(deletedAt, other.deletedAt)
			.append(demandId, other.demandId)
			.append(departureLock, other.departureLock)
			.append(departurePlatformId, other.departurePlatformId)
			.append(departureScheduleId, other.departureScheduleId)
			.append(departureTime, other.departureTime)
			.append(deviceName, other.deviceName)
			.append(dummy, other.dummy)
			.append(id, other.id)
			.append(memo, other.memo)
			.append(operatorId, other.operatorId)
			.append(passengerCount, other.passengerCount)
			.append(payment, other.payment)
			.append(serviceProviderId, other.serviceProviderId)
			.append(status, other.status)
			.append(stoppageTime, other.stoppageTime)
			.append(transferredAt, other.transferredAt)
			.append(unitAssignmentId, other.unitAssignmentId)
			.append(updatedAt, other.updatedAt)
			.append(userId, other.userId)
			.append(arrivalPlatform, other.arrivalPlatform)
			.append(arrivalSchedule, other.arrivalSchedule)
			.append(demand, other.demand)
			.append(departurePlatform, other.departurePlatform)
			.append(departureSchedule, other.departureSchedule)
			.append(fellowUsers, other.fellowUsers)
			.append(operator, other.operator)
			.append(passengerRecords, other.passengerRecords)
			.append(reservationUsers, other.reservationUsers)
			.append(serviceProvider, other.serviceProvider)
			.append(unitAssignment, other.unitAssignment)
			.append(user, other.user)
			.isEquals();
	}
}
