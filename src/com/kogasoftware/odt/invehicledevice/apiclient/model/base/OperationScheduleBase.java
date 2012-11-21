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
	private static final long serialVersionUID = 5801615234864614611L;

	// Columns
	@JsonProperty private Optional<Date> arrivalEstimate = Optional.absent();
	@JsonProperty private Date createdAt = new Date();
	@JsonProperty private Optional<Date> deletedAt = Optional.absent();
	@JsonProperty private Optional<Date> departureEstimate = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonDeserialize(using=RailsOptionalDateDeserializer.class) @JsonSerialize(using=RailsOptionalDateSerializer.class)
	@JsonProperty private Optional<Date> operationDate = Optional.absent();
	@JsonProperty private Integer passengerCountChange = 0;
	@JsonProperty private Optional<Integer> platformId = Optional.absent();
	@JsonProperty private Integer remain = 0;
	@JsonProperty private Optional<Integer> serviceProviderId = Optional.absent();
	@JsonProperty private Optional<Integer> unitAssignmentId = Optional.absent();
	@JsonProperty private Date updatedAt = new Date();

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private Optional<OperationRecord> operationRecord = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<Platform> platform = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private List<Reservation> reservationsAsArrival = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<Reservation> reservationsAsDeparture = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private Optional<ServiceProvider> serviceProvider = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private Optional<UnitAssignment> unitAssignment = Optional.absent();

	public static final String UNDERSCORE = "operation_schedule";
	public static final ResponseConverter<OperationSchedule> RESPONSE_CONVERTER = getResponseConverter(OperationSchedule.class);
	public static final ResponseConverter<List<OperationSchedule>> LIST_RESPONSE_CONVERTER = getListResponseConverter(OperationSchedule.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static OperationSchedule parse(String jsonString) throws IOException {
		return parse(jsonString, OperationSchedule.class);
	}

	public static List<OperationSchedule> parseList(String jsonString) throws IOException {
		return parseList(jsonString, OperationSchedule.class);
	}

	@JsonIgnore
	public Optional<Date> getArrivalEstimate() {
		return wrapNull(arrivalEstimate);
	}

	@JsonIgnore
	public void setArrivalEstimate(Optional<Date> arrivalEstimate) {
		refreshUpdatedAt();
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
	public Optional<Date> getDepartureEstimate() {
		return wrapNull(departureEstimate);
	}

	@JsonIgnore
	public void setDepartureEstimate(Optional<Date> departureEstimate) {
		refreshUpdatedAt();
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
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	@JsonIgnore
	public Optional<Date> getOperationDate() {
		return wrapNull(operationDate);
	}

	@JsonIgnore
	public void setOperationDate(Optional<Date> operationDate) {
		refreshUpdatedAt();
		this.operationDate = wrapNull(operationDate);
	}

	@JsonIgnore
	public void setOperationDate(Date operationDate) {
		setOperationDate(Optional.fromNullable(operationDate));
	}

	public void clearOperationDate() {
		setOperationDate(Optional.<Date>absent());
	}

	@JsonIgnore
	public Integer getPassengerCountChange() {
		return wrapNull(passengerCountChange);
	}

	@JsonIgnore
	public void setPassengerCountChange(Integer passengerCountChange) {
		refreshUpdatedAt();
		this.passengerCountChange = wrapNull(passengerCountChange);
	}

	@JsonIgnore
	public Optional<Integer> getPlatformId() {
		return wrapNull(platformId);
	}

	@JsonIgnore
	public void setPlatformId(Optional<Integer> platformId) {
		refreshUpdatedAt();
		this.platformId = wrapNull(platformId);
		for (Platform presentPlatform : getPlatform().asSet()) {
			for (Integer presentPlatformId : getPlatformId().asSet()) {
				presentPlatform.setId(presentPlatformId);
			}
		}
	}

	@JsonIgnore
	public void setPlatformId(Integer platformId) {
		setPlatformId(Optional.fromNullable(platformId));
	}

	public void clearPlatformId() {
		setPlatformId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Integer getRemain() {
		return wrapNull(remain);
	}

	@JsonIgnore
	public void setRemain(Integer remain) {
		refreshUpdatedAt();
		this.remain = wrapNull(remain);
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
		refreshUpdatedAt();
		this.platform = wrapNull(platform);
		for (Platform presentPlatform : getPlatform().asSet()) {
			setPlatformId(presentPlatform.getId());
		}
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

	@Override
	public OperationSchedule clone() {
		return clone(true);
	}

	@Override
	public OperationSchedule clone(Boolean withAssociation) {
		return super.clone(OperationSchedule.class, withAssociation);
	}
}
