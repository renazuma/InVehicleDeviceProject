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
 * 自治体
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class ServiceProviderBase extends Model {
	private static final long serialVersionUID = 932268091369734434L;

	// Columns
	@JsonProperty private Date createdAt = new Date();
	@JsonProperty private Optional<Date> deletedAt = Optional.absent();
	@JsonProperty private String domain = "";
	@JsonProperty private Integer id = 0;
	@JsonProperty private BigDecimal latitude = BigDecimal.ZERO;
	@JsonProperty private Optional<String> logAccessKeyIdAws = Optional.absent();
	@JsonProperty private Optional<String> logSecretAccessKeyAws = Optional.absent();
	@JsonProperty private BigDecimal longitude = BigDecimal.ZERO;
	@JsonProperty private Integer mustContactGap = 0;
	@JsonProperty private String name = "";
	@JsonProperty private Boolean recommend = false;
	@JsonProperty private Integer reservationStartDate = 0;
	@JsonProperty private Boolean semiDemand = false;
	@JsonProperty private Integer semiDemandExtentLimit = 0;
	@JsonProperty private Date updatedAt = new Date();
	@JsonProperty private Integer userLoginLength = 0;

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private List<Demand> demands = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<Driver> drivers = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<InVehicleDevice> inVehicleDevices = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<OperationSchedule> operationSchedules = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<Operator> operators = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<PassengerRecord> passengerRecords = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<Platform> platforms = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<ReservationCandidate> reservationCandidates = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<Reservation> reservations = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<ServiceUnit> serviceUnits = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<UnitAssignment> unitAssignments = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<User> users = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<Vehicle> vehicles = Lists.newLinkedList();

	public static final String UNDERSCORE = "service_provider";
	public static final ResponseConverter<ServiceProvider> RESPONSE_CONVERTER = getResponseConverter(ServiceProvider.class);
	public static final ResponseConverter<List<ServiceProvider>> LIST_RESPONSE_CONVERTER = getListResponseConverter(ServiceProvider.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static ServiceProvider parse(String jsonString) throws IOException {
		return parse(jsonString, ServiceProvider.class);
	}

	public static List<ServiceProvider> parseList(String jsonString) throws IOException {
		return parseList(jsonString, ServiceProvider.class);
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
	public String getDomain() {
		return wrapNull(domain);
	}

	@JsonIgnore
	public void setDomain(String domain) {
		refreshUpdatedAt();
		this.domain = wrapNull(domain);
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
	public BigDecimal getLatitude() {
		return wrapNull(latitude);
	}

	@JsonIgnore
	public void setLatitude(BigDecimal latitude) {
		refreshUpdatedAt();
		this.latitude = wrapNull(latitude);
	}

	@JsonIgnore
	public Optional<String> getLogAccessKeyIdAws() {
		return wrapNull(logAccessKeyIdAws);
	}

	@JsonIgnore
	public void setLogAccessKeyIdAws(Optional<String> logAccessKeyIdAws) {
		refreshUpdatedAt();
		this.logAccessKeyIdAws = wrapNull(logAccessKeyIdAws);
	}

	@JsonIgnore
	public void setLogAccessKeyIdAws(String logAccessKeyIdAws) {
		setLogAccessKeyIdAws(Optional.fromNullable(logAccessKeyIdAws));
	}

	public void clearLogAccessKeyIdAws() {
		setLogAccessKeyIdAws(Optional.<String>absent());
	}

	@JsonIgnore
	public Optional<String> getLogSecretAccessKeyAws() {
		return wrapNull(logSecretAccessKeyAws);
	}

	@JsonIgnore
	public void setLogSecretAccessKeyAws(Optional<String> logSecretAccessKeyAws) {
		refreshUpdatedAt();
		this.logSecretAccessKeyAws = wrapNull(logSecretAccessKeyAws);
	}

	@JsonIgnore
	public void setLogSecretAccessKeyAws(String logSecretAccessKeyAws) {
		setLogSecretAccessKeyAws(Optional.fromNullable(logSecretAccessKeyAws));
	}

	public void clearLogSecretAccessKeyAws() {
		setLogSecretAccessKeyAws(Optional.<String>absent());
	}

	@JsonIgnore
	public BigDecimal getLongitude() {
		return wrapNull(longitude);
	}

	@JsonIgnore
	public void setLongitude(BigDecimal longitude) {
		refreshUpdatedAt();
		this.longitude = wrapNull(longitude);
	}

	@JsonIgnore
	public Integer getMustContactGap() {
		return wrapNull(mustContactGap);
	}

	@JsonIgnore
	public void setMustContactGap(Integer mustContactGap) {
		refreshUpdatedAt();
		this.mustContactGap = wrapNull(mustContactGap);
	}

	@JsonIgnore
	public String getName() {
		return wrapNull(name);
	}

	@JsonIgnore
	public void setName(String name) {
		refreshUpdatedAt();
		this.name = wrapNull(name);
	}

	@JsonIgnore
	public Boolean getRecommend() {
		return wrapNull(recommend);
	}

	@JsonIgnore
	public void setRecommend(Boolean recommend) {
		refreshUpdatedAt();
		this.recommend = wrapNull(recommend);
	}

	@JsonIgnore
	public Integer getReservationStartDate() {
		return wrapNull(reservationStartDate);
	}

	@JsonIgnore
	public void setReservationStartDate(Integer reservationStartDate) {
		refreshUpdatedAt();
		this.reservationStartDate = wrapNull(reservationStartDate);
	}

	@JsonIgnore
	public Boolean getSemiDemand() {
		return wrapNull(semiDemand);
	}

	@JsonIgnore
	public void setSemiDemand(Boolean semiDemand) {
		refreshUpdatedAt();
		this.semiDemand = wrapNull(semiDemand);
	}

	@JsonIgnore
	public Integer getSemiDemandExtentLimit() {
		return wrapNull(semiDemandExtentLimit);
	}

	@JsonIgnore
	public void setSemiDemandExtentLimit(Integer semiDemandExtentLimit) {
		refreshUpdatedAt();
		this.semiDemandExtentLimit = wrapNull(semiDemandExtentLimit);
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
	public Integer getUserLoginLength() {
		return wrapNull(userLoginLength);
	}

	@JsonIgnore
	public void setUserLoginLength(Integer userLoginLength) {
		refreshUpdatedAt();
		this.userLoginLength = wrapNull(userLoginLength);
	}

	@JsonIgnore
	public List<Demand> getDemands() {
		return wrapNull(demands);
	}

	@JsonIgnore
	public void setDemands(Iterable<Demand> demands) {
		this.demands = wrapNull(demands);
	}

	public void clearDemands() {
		setDemands(new LinkedList<Demand>());
	}

	@JsonIgnore
	public List<Driver> getDrivers() {
		return wrapNull(drivers);
	}

	@JsonIgnore
	public void setDrivers(Iterable<Driver> drivers) {
		this.drivers = wrapNull(drivers);
	}

	public void clearDrivers() {
		setDrivers(new LinkedList<Driver>());
	}

	@JsonIgnore
	public List<InVehicleDevice> getInVehicleDevices() {
		return wrapNull(inVehicleDevices);
	}

	@JsonIgnore
	public void setInVehicleDevices(Iterable<InVehicleDevice> inVehicleDevices) {
		this.inVehicleDevices = wrapNull(inVehicleDevices);
	}

	public void clearInVehicleDevices() {
		setInVehicleDevices(new LinkedList<InVehicleDevice>());
	}

	@JsonIgnore
	public List<OperationSchedule> getOperationSchedules() {
		return wrapNull(operationSchedules);
	}

	@JsonIgnore
	public void setOperationSchedules(Iterable<OperationSchedule> operationSchedules) {
		this.operationSchedules = wrapNull(operationSchedules);
	}

	public void clearOperationSchedules() {
		setOperationSchedules(new LinkedList<OperationSchedule>());
	}

	@JsonIgnore
	public List<Operator> getOperators() {
		return wrapNull(operators);
	}

	@JsonIgnore
	public void setOperators(Iterable<Operator> operators) {
		this.operators = wrapNull(operators);
	}

	public void clearOperators() {
		setOperators(new LinkedList<Operator>());
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
	public List<Platform> getPlatforms() {
		return wrapNull(platforms);
	}

	@JsonIgnore
	public void setPlatforms(Iterable<Platform> platforms) {
		this.platforms = wrapNull(platforms);
	}

	public void clearPlatforms() {
		setPlatforms(new LinkedList<Platform>());
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
	public List<Reservation> getReservations() {
		return wrapNull(reservations);
	}

	@JsonIgnore
	public void setReservations(Iterable<Reservation> reservations) {
		this.reservations = wrapNull(reservations);
	}

	public void clearReservations() {
		setReservations(new LinkedList<Reservation>());
	}

	@JsonIgnore
	public List<ServiceUnit> getServiceUnits() {
		return wrapNull(serviceUnits);
	}

	@JsonIgnore
	public void setServiceUnits(Iterable<ServiceUnit> serviceUnits) {
		this.serviceUnits = wrapNull(serviceUnits);
	}

	public void clearServiceUnits() {
		setServiceUnits(new LinkedList<ServiceUnit>());
	}

	@JsonIgnore
	public List<UnitAssignment> getUnitAssignments() {
		return wrapNull(unitAssignments);
	}

	@JsonIgnore
	public void setUnitAssignments(Iterable<UnitAssignment> unitAssignments) {
		this.unitAssignments = wrapNull(unitAssignments);
	}

	public void clearUnitAssignments() {
		setUnitAssignments(new LinkedList<UnitAssignment>());
	}

	@JsonIgnore
	public List<User> getUsers() {
		return wrapNull(users);
	}

	@JsonIgnore
	public void setUsers(Iterable<User> users) {
		this.users = wrapNull(users);
	}

	public void clearUsers() {
		setUsers(new LinkedList<User>());
	}

	@JsonIgnore
	public List<Vehicle> getVehicles() {
		return wrapNull(vehicles);
	}

	@JsonIgnore
	public void setVehicles(Iterable<Vehicle> vehicles) {
		this.vehicles = wrapNull(vehicles);
	}

	public void clearVehicles() {
		setVehicles(new LinkedList<Vehicle>());
	}

	@Override
	public ServiceProvider clone() {
		return super.clone(ServiceProvider.class);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(createdAt)
			.append(deletedAt)
			.append(domain)
			.append(id)
			.append(latitude)
			.append(logAccessKeyIdAws)
			.append(logSecretAccessKeyAws)
			.append(longitude)
			.append(mustContactGap)
			.append(name)
			.append(recommend)
			.append(reservationStartDate)
			.append(semiDemand)
			.append(semiDemandExtentLimit)
			.append(updatedAt)
			.append(userLoginLength)
			.append(demands)
			.append(drivers)
			.append(inVehicleDevices)
			.append(operationSchedules)
			.append(operators)
			.append(passengerRecords)
			.append(platforms)
			.append(reservationCandidates)
			.append(reservations)
			.append(serviceUnits)
			.append(unitAssignments)
			.append(users)
			.append(vehicles)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof ServiceProviderBase)) {
			return false;
		}
		ServiceProviderBase other = (ServiceProviderBase) obj;
		return new EqualsBuilder()
			.append(createdAt, other.createdAt)
			.append(deletedAt, other.deletedAt)
			.append(domain, other.domain)
			.append(id, other.id)
			.append(latitude, other.latitude)
			.append(logAccessKeyIdAws, other.logAccessKeyIdAws)
			.append(logSecretAccessKeyAws, other.logSecretAccessKeyAws)
			.append(longitude, other.longitude)
			.append(mustContactGap, other.mustContactGap)
			.append(name, other.name)
			.append(recommend, other.recommend)
			.append(reservationStartDate, other.reservationStartDate)
			.append(semiDemand, other.semiDemand)
			.append(semiDemandExtentLimit, other.semiDemandExtentLimit)
			.append(updatedAt, other.updatedAt)
			.append(userLoginLength, other.userLoginLength)
			.append(demands, other.demands)
			.append(drivers, other.drivers)
			.append(inVehicleDevices, other.inVehicleDevices)
			.append(operationSchedules, other.operationSchedules)
			.append(operators, other.operators)
			.append(passengerRecords, other.passengerRecords)
			.append(platforms, other.platforms)
			.append(reservationCandidates, other.reservationCandidates)
			.append(reservations, other.reservations)
			.append(serviceUnits, other.serviceUnits)
			.append(unitAssignments, other.unitAssignments)
			.append(users, other.users)
			.append(vehicles, other.vehicles)
			.isEquals();
	}
}
