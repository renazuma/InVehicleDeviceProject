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
 * 乗降場
 */
@SuppressWarnings("unused")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = Model.JACKSON_IDENTITY_INFO_PROPERTY)
public abstract class PlatformBase extends Model {
	private static final long serialVersionUID = 5903286734249735506L;

	// Columns
	@JsonProperty private String address = "";
	@JsonProperty private Date createdAt = new Date();
	@JsonProperty private Optional<Date> deletedAt = Optional.absent();
	@JsonProperty private Optional<Integer> demandAreaId = Optional.absent();
	@JsonProperty private Optional<Date> endAt = Optional.absent();
	@JsonProperty private Integer id = 0;
	@JsonProperty private Optional<String> image = Optional.absent();
	@JsonProperty private String keyword = "";
	@JsonProperty private BigDecimal latitude = BigDecimal.ZERO;
	@JsonProperty private BigDecimal longitude = BigDecimal.ZERO;
	@JsonProperty private String memo = "";
	@JsonProperty private String name = "";
	@JsonProperty private String nameRuby = "";
	@JsonProperty private Optional<Integer> platformCategoryId = Optional.absent();
	@JsonProperty private Integer reportingRegionId = 0;
	@JsonProperty private Optional<Integer> semiDemandAreaId = Optional.absent();
	@JsonProperty private Optional<Integer> serviceProviderId = Optional.absent();
	@JsonProperty private Optional<Date> startAt = Optional.absent();
	@JsonProperty private Optional<Integer> typeOfDemand = Optional.absent();
	@JsonProperty private Integer typeOfPlatform = 0;
	@JsonProperty private Date updatedAt = new Date();

	// Associations
	@JsonProperty @JsonView(AssociationView.class) private List<Demand> demandsAsArrival = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<Demand> demandsAsDeparture = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<OperationSchedule> operationSchedules = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<ReservationCandidate> reservationCandidatesAsArrival = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<ReservationCandidate> reservationCandidatesAsDeparture = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<Reservation> reservationsAsArrival = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private List<Reservation> reservationsAsDeparture = Lists.newLinkedList();
	@JsonProperty @JsonView(AssociationView.class) private Optional<ServiceProvider> serviceProvider = Optional.absent();

	public static final String UNDERSCORE = "platform";
	public static final ResponseConverter<Platform> RESPONSE_CONVERTER = getResponseConverter(Platform.class);
	public static final ResponseConverter<List<Platform>> LIST_RESPONSE_CONVERTER = getListResponseConverter(Platform.class);

	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}

	public static Platform parse(String jsonString) throws IOException {
		return parse(jsonString, Platform.class);
	}

	public static List<Platform> parseList(String jsonString) throws IOException {
		return parseList(jsonString, Platform.class);
	}

	@JsonIgnore
	public String getAddress() {
		return wrapNull(address);
	}

	@JsonIgnore
	public void setAddress(String address) {
		refreshUpdatedAt();
		this.address = wrapNull(address);
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
	public Optional<Integer> getDemandAreaId() {
		return wrapNull(demandAreaId);
	}

	@JsonIgnore
	public void setDemandAreaId(Optional<Integer> demandAreaId) {
		refreshUpdatedAt();
		this.demandAreaId = wrapNull(demandAreaId);
	}

	@JsonIgnore
	public void setDemandAreaId(Integer demandAreaId) {
		setDemandAreaId(Optional.fromNullable(demandAreaId));
	}

	public void clearDemandAreaId() {
		setDemandAreaId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Optional<Date> getEndAt() {
		return wrapNull(endAt);
	}

	@JsonIgnore
	public void setEndAt(Optional<Date> endAt) {
		refreshUpdatedAt();
		this.endAt = wrapNull(endAt);
	}

	@JsonIgnore
	public void setEndAt(Date endAt) {
		setEndAt(Optional.fromNullable(endAt));
	}

	public void clearEndAt() {
		setEndAt(Optional.<Date>absent());
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
	public Optional<String> getImage() {
		return wrapNull(image);
	}

	@JsonIgnore
	public void setImage(Optional<String> image) {
		refreshUpdatedAt();
		this.image = wrapNull(image);
	}

	@JsonIgnore
	public void setImage(String image) {
		setImage(Optional.fromNullable(image));
	}

	public void clearImage() {
		setImage(Optional.<String>absent());
	}

	@JsonIgnore
	public String getKeyword() {
		return wrapNull(keyword);
	}

	@JsonIgnore
	public void setKeyword(String keyword) {
		refreshUpdatedAt();
		this.keyword = wrapNull(keyword);
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
	public BigDecimal getLongitude() {
		return wrapNull(longitude);
	}

	@JsonIgnore
	public void setLongitude(BigDecimal longitude) {
		refreshUpdatedAt();
		this.longitude = wrapNull(longitude);
	}

	@JsonIgnore
	public String getMemo() {
		return wrapNull(memo);
	}

	@JsonIgnore
	public void setMemo(String memo) {
		refreshUpdatedAt();
		this.memo = wrapNull(memo);
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
	public String getNameRuby() {
		return wrapNull(nameRuby);
	}

	@JsonIgnore
	public void setNameRuby(String nameRuby) {
		refreshUpdatedAt();
		this.nameRuby = wrapNull(nameRuby);
	}

	@JsonIgnore
	public Optional<Integer> getPlatformCategoryId() {
		return wrapNull(platformCategoryId);
	}

	@JsonIgnore
	public void setPlatformCategoryId(Optional<Integer> platformCategoryId) {
		refreshUpdatedAt();
		this.platformCategoryId = wrapNull(platformCategoryId);
	}

	@JsonIgnore
	public void setPlatformCategoryId(Integer platformCategoryId) {
		setPlatformCategoryId(Optional.fromNullable(platformCategoryId));
	}

	public void clearPlatformCategoryId() {
		setPlatformCategoryId(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Integer getReportingRegionId() {
		return wrapNull(reportingRegionId);
	}

	@JsonIgnore
	public void setReportingRegionId(Integer reportingRegionId) {
		refreshUpdatedAt();
		this.reportingRegionId = wrapNull(reportingRegionId);
	}

	@JsonIgnore
	public Optional<Integer> getSemiDemandAreaId() {
		return wrapNull(semiDemandAreaId);
	}

	@JsonIgnore
	public void setSemiDemandAreaId(Optional<Integer> semiDemandAreaId) {
		refreshUpdatedAt();
		this.semiDemandAreaId = wrapNull(semiDemandAreaId);
	}

	@JsonIgnore
	public void setSemiDemandAreaId(Integer semiDemandAreaId) {
		setSemiDemandAreaId(Optional.fromNullable(semiDemandAreaId));
	}

	public void clearSemiDemandAreaId() {
		setSemiDemandAreaId(Optional.<Integer>absent());
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
	public Optional<Date> getStartAt() {
		return wrapNull(startAt);
	}

	@JsonIgnore
	public void setStartAt(Optional<Date> startAt) {
		refreshUpdatedAt();
		this.startAt = wrapNull(startAt);
	}

	@JsonIgnore
	public void setStartAt(Date startAt) {
		setStartAt(Optional.fromNullable(startAt));
	}

	public void clearStartAt() {
		setStartAt(Optional.<Date>absent());
	}

	@JsonIgnore
	public Optional<Integer> getTypeOfDemand() {
		return wrapNull(typeOfDemand);
	}

	@JsonIgnore
	public void setTypeOfDemand(Optional<Integer> typeOfDemand) {
		refreshUpdatedAt();
		this.typeOfDemand = wrapNull(typeOfDemand);
	}

	@JsonIgnore
	public void setTypeOfDemand(Integer typeOfDemand) {
		setTypeOfDemand(Optional.fromNullable(typeOfDemand));
	}

	public void clearTypeOfDemand() {
		setTypeOfDemand(Optional.<Integer>absent());
	}

	@JsonIgnore
	public Integer getTypeOfPlatform() {
		return wrapNull(typeOfPlatform);
	}

	@JsonIgnore
	public void setTypeOfPlatform(Integer typeOfPlatform) {
		refreshUpdatedAt();
		this.typeOfPlatform = wrapNull(typeOfPlatform);
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
	public List<Demand> getDemandsAsArrival() {
		return wrapNull(demandsAsArrival);
	}

	@JsonIgnore
	public void setDemandsAsArrival(Iterable<Demand> demandsAsArrival) {
		this.demandsAsArrival = wrapNull(demandsAsArrival);
	}

	public void clearDemandsAsArrival() {
		setDemandsAsArrival(new LinkedList<Demand>());
	}

	@JsonIgnore
	public List<Demand> getDemandsAsDeparture() {
		return wrapNull(demandsAsDeparture);
	}

	@JsonIgnore
	public void setDemandsAsDeparture(Iterable<Demand> demandsAsDeparture) {
		this.demandsAsDeparture = wrapNull(demandsAsDeparture);
	}

	public void clearDemandsAsDeparture() {
		setDemandsAsDeparture(new LinkedList<Demand>());
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
	public List<ReservationCandidate> getReservationCandidatesAsArrival() {
		return wrapNull(reservationCandidatesAsArrival);
	}

	@JsonIgnore
	public void setReservationCandidatesAsArrival(Iterable<ReservationCandidate> reservationCandidatesAsArrival) {
		this.reservationCandidatesAsArrival = wrapNull(reservationCandidatesAsArrival);
	}

	public void clearReservationCandidatesAsArrival() {
		setReservationCandidatesAsArrival(new LinkedList<ReservationCandidate>());
	}

	@JsonIgnore
	public List<ReservationCandidate> getReservationCandidatesAsDeparture() {
		return wrapNull(reservationCandidatesAsDeparture);
	}

	@JsonIgnore
	public void setReservationCandidatesAsDeparture(Iterable<ReservationCandidate> reservationCandidatesAsDeparture) {
		this.reservationCandidatesAsDeparture = wrapNull(reservationCandidatesAsDeparture);
	}

	public void clearReservationCandidatesAsDeparture() {
		setReservationCandidatesAsDeparture(new LinkedList<ReservationCandidate>());
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

	@Override
	public Platform clone() {
		return clone(true);
	}

	@Override
	public Platform clone(Boolean withAssociation) {
		return super.clone(Platform.class, withAssociation);
	}
}
