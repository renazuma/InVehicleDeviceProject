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
 * 号車
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class UnitAssignmentBase extends Model {
	private static final long serialVersionUID = 452935171422747174L;

	// Columns
	@JsonProperty private Date createdAt = new Date();
	@JsonProperty private Optional<Date> deletedAt = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private String name = "";
	@JsonProperty private Optional<Integer> serviceProviderId = Optional.absent();
	@JsonProperty private Date updatedAt = new Date();
	@JsonProperty private Boolean working = false;

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private List<OperationSchedule> operationSchedules = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<ReservationCandidate> reservationCandidates = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<Reservation> reservations = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private Optional<ServiceProvider> serviceProvider = Optional.absent();
	@JsonProperty @JsonView(AssociationView.class) private List<ServiceUnit> serviceUnits = Lists.newLinkedList();

	public static final String UNDERSCORE = "unit_assignment";
	public static final ResponseConverter<UnitAssignment> RESPONSE_CONVERTER = getResponseConverter(UnitAssignment.class);
	public static final ResponseConverter<List<UnitAssignment>> LIST_RESPONSE_CONVERTER = getListResponseConverter(UnitAssignment.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static UnitAssignment parse(String jsonString) throws IOException {
		return parse(jsonString, UnitAssignment.class);
	}

	public static List<UnitAssignment> parseList(String jsonString) throws IOException {
		return parseList(jsonString, UnitAssignment.class);
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
	public String getName() {
		return wrapNull(name);
	}

	@JsonIgnore
	public void setName(String name) {
		refreshUpdatedAt();
		this.name = wrapNull(name);
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
	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	@JsonIgnore
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	@JsonIgnore
	public Boolean getWorking() {
		return wrapNull(working);
	}

	@JsonIgnore
	public void setWorking(Boolean working) {
		refreshUpdatedAt();
		this.working = wrapNull(working);
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

	@Override
	public UnitAssignment clone() {
		return clone(true);
	}

	@Override
	public UnitAssignment clone(Boolean withAssociation) {
		return super.clone(UnitAssignment.class, withAssociation);
	}
}
