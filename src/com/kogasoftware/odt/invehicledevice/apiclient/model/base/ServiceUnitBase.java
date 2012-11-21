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
 * 時点号車
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class ServiceUnitBase extends Model {
	private static final long serialVersionUID = 3533304298123592042L;

	// Columns
	@JsonDeserialize(using=RailsOptionalDateDeserializer.class) @JsonSerialize(using=RailsOptionalDateSerializer.class)
	@JsonProperty private Optional<Date> activatedAt = Optional.absent();
	@JsonProperty private Date createdAt = new Date();
	@JsonProperty private Optional<Date> deletedAt = Optional.absent();
	@JsonProperty private Optional<Integer> driverId = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private Optional<Integer> inVehicleDeviceId = Optional.absent();
	@JsonProperty private Optional<Integer> serviceProviderId = Optional.absent();
	@JsonProperty private Optional<Integer> unitAssignmentId = Optional.absent();
	@JsonProperty private Date updatedAt = new Date();
	@JsonProperty private Optional<Integer> vehicleId = Optional.absent();

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private Optional<Driver> driver = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<InVehicleDevice> inVehicleDevice = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private List<OperationRecord> operationRecords = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private Optional<ServiceProvider> serviceProvider = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<UnitAssignment> unitAssignment = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<Vehicle> vehicle = Optional.absent();

	public static final String UNDERSCORE = "service_unit";
	public static final ResponseConverter<ServiceUnit> RESPONSE_CONVERTER = getResponseConverter(ServiceUnit.class);
	public static final ResponseConverter<List<ServiceUnit>> LIST_RESPONSE_CONVERTER = getListResponseConverter(ServiceUnit.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static ServiceUnit parse(String jsonString) throws IOException {
		return parse(jsonString, ServiceUnit.class);
	}

	public static List<ServiceUnit> parseList(String jsonString) throws IOException {
		return parseList(jsonString, ServiceUnit.class);
	}

	@JsonIgnore
	public Optional<Date> getActivatedAt() {
		return wrapNull(activatedAt);
	}

	@JsonIgnore
	public void setActivatedAt(Optional<Date> activatedAt) {
		refreshUpdatedAt();
		this.activatedAt = wrapNull(activatedAt);
	}

	@JsonIgnore
	public void setActivatedAt(Date activatedAt) {
		setActivatedAt(Optional.fromNullable(activatedAt));
	}

	public void clearActivatedAt() {
		setActivatedAt(Optional.<Date>absent());
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
	public Optional<Integer> getDriverId() {
		return wrapNull(driverId);
	}

	@JsonIgnore
	public void setDriverId(Optional<Integer> driverId) {
		refreshUpdatedAt();
		this.driverId = wrapNull(driverId);
		for (Driver presentDriver : getDriver().asSet()) {
			for (Integer presentDriverId : getDriverId().asSet()) {
				presentDriver.setId(presentDriverId);
			}
		}
	}

	@JsonIgnore
	public void setDriverId(Integer driverId) {
		setDriverId(Optional.fromNullable(driverId));
	}

	public void clearDriverId() {
		setDriverId(Optional.<Integer>absent());
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
	public Optional<Integer> getInVehicleDeviceId() {
		return wrapNull(inVehicleDeviceId);
	}

	@JsonIgnore
	public void setInVehicleDeviceId(Optional<Integer> inVehicleDeviceId) {
		refreshUpdatedAt();
		this.inVehicleDeviceId = wrapNull(inVehicleDeviceId);
		for (InVehicleDevice presentInVehicleDevice : getInVehicleDevice().asSet()) {
			for (Integer presentInVehicleDeviceId : getInVehicleDeviceId().asSet()) {
				presentInVehicleDevice.setId(presentInVehicleDeviceId);
			}
		}
	}

	@JsonIgnore
	public void setInVehicleDeviceId(Integer inVehicleDeviceId) {
		setInVehicleDeviceId(Optional.fromNullable(inVehicleDeviceId));
	}

	public void clearInVehicleDeviceId() {
		setInVehicleDeviceId(Optional.<Integer>absent());
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
	public Optional<Integer> getVehicleId() {
		return wrapNull(vehicleId);
	}

	@JsonIgnore
	public void setVehicleId(Optional<Integer> vehicleId) {
		refreshUpdatedAt();
		this.vehicleId = wrapNull(vehicleId);
		for (Vehicle presentVehicle : getVehicle().asSet()) {
			for (Integer presentVehicleId : getVehicleId().asSet()) {
				presentVehicle.setId(presentVehicleId);
			}
		}
	}

	@JsonIgnore
	public void setVehicleId(Integer vehicleId) {
		setVehicleId(Optional.fromNullable(vehicleId));
	}

	public void clearVehicleId() {
		setVehicleId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Optional<Driver> getDriver() {
		return wrapNull(driver);
	}

	@JsonIgnore
	public void setDriver(Optional<Driver> driver) {
		refreshUpdatedAt();
		this.driver = wrapNull(driver);
		for (Driver presentDriver : getDriver().asSet()) {
			setDriverId(presentDriver.getId());
		}
	}

	@JsonIgnore
	public void setDriver(Driver driver) {
		setDriver(Optional.fromNullable(driver));
	}

	public void clearDriver() {
		setDriver(Optional.<Driver>absent());
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
	public List<OperationRecord> getOperationRecords() {
		return wrapNull(operationRecords);
	}

	@JsonIgnore
	public void setOperationRecords(Iterable<OperationRecord> operationRecords) {
		this.operationRecords = wrapNull(operationRecords);
	}

	public void clearOperationRecords() {
		setOperationRecords(new LinkedList<OperationRecord>());
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
	public Optional<Vehicle> getVehicle() {
		return wrapNull(vehicle);
	}

	@JsonIgnore
	public void setVehicle(Optional<Vehicle> vehicle) {
		refreshUpdatedAt();
		this.vehicle = wrapNull(vehicle);
		for (Vehicle presentVehicle : getVehicle().asSet()) {
			setVehicleId(presentVehicle.getId());
		}
	}

	@JsonIgnore
	public void setVehicle(Vehicle vehicle) {
		setVehicle(Optional.fromNullable(vehicle));
	}

	public void clearVehicle() {
		setVehicle(Optional.<Vehicle>absent());
	}

	@Override
	public ServiceUnit clone() {
		return clone(true);
	}

	@Override
	public ServiceUnit clone(Boolean withAssociation) {
		return super.clone(ServiceUnit.class, withAssociation);
	}
}
