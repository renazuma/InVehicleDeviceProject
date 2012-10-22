package com.kogasoftware.odt.invehicledevice.apiclient.model.base;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;
import com.kogasoftware.odt.apiclient.DefaultApiClient;
import com.kogasoftware.odt.apiclient.ApiClient.ResponseConverter;
import com.kogasoftware.odt.invehicledevice.apiclient.model.*;

@SuppressWarnings("unused")
public abstract class PlatformBase extends Model {
	private static final long serialVersionUID = 8662293665841722247L;
	public static final ResponseConverter<Platform> RESPONSE_CONVERTER = new ResponseConverter<Platform>() {
		@Override
		public Platform convert(byte[] rawResponse) throws JSONException {
			return parse(DefaultApiClient.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<Platform>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<Platform>>() {
		@Override
		public List<Platform> convert(byte[] rawResponse) throws JSONException {
			return parseList(DefaultApiClient.parseJSONArray(rawResponse));
		}
	};
	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setAddress(parseString(jsonObject, "address"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDemandAreaId(parseOptionalInteger(jsonObject, "demand_area_id"));
		setEndAt(parseOptionalDate(jsonObject, "end_at"));
		setId(parseInteger(jsonObject, "id"));
		setImage(parseOptionalString(jsonObject, "image"));
		setKeyword(parseString(jsonObject, "keyword"));
		setLatitude(parseBigDecimal(jsonObject, "latitude"));
		setLongitude(parseBigDecimal(jsonObject, "longitude"));
		setMemo(parseString(jsonObject, "memo"));
		setName(parseString(jsonObject, "name"));
		setNameRuby(parseString(jsonObject, "name_ruby"));
		setPlatformCategoryId(parseOptionalInteger(jsonObject, "platform_category_id"));
		setReportingRegionId(parseInteger(jsonObject, "reporting_region_id"));
		setSemiDemandAreaId(parseOptionalInteger(jsonObject, "semi_demand_area_id"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setStartAt(parseOptionalDate(jsonObject, "start_at"));
		setTypeOfDemand(parseOptionalInteger(jsonObject, "type_of_demand"));
		setTypeOfPlatform(parseInteger(jsonObject, "type_of_platform"));
		setDemandsAsArrival(Demand.parseList(jsonObject, "demands_as_arrival"));
		setDemandsAsDeparture(Demand.parseList(jsonObject, "demands_as_departure"));
		setOperationSchedules(OperationSchedule.parseList(jsonObject, "operation_schedules"));
		setReservationCandidatesAsArrival(ReservationCandidate.parseList(jsonObject, "reservation_candidates_as_arrival"));
		setReservationCandidatesAsDeparture(ReservationCandidate.parseList(jsonObject, "reservation_candidates_as_departure"));
		setReservationsAsArrival(Reservation.parseList(jsonObject, "reservations_as_arrival"));
		setReservationsAsDeparture(Reservation.parseList(jsonObject, "reservations_as_departure"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));

		setUpdatedAt(parseDate(jsonObject, "updated_at"));
	}

	public static Optional<Platform> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static Platform parse(JSONObject jsonObject) throws JSONException {
		Platform model = new Platform();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<Platform> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Platform>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<Platform> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<Platform> models = new LinkedList<Platform>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(parse(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	protected JSONObject toJSONObject(Boolean recursive, Integer depth) throws JSONException {
		if (depth > MAX_RECURSE_DEPTH) {
			return new JSONObject();
		}
		Integer nextDepth = depth + 1;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("address", toJSON(getAddress()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt()));
		jsonObject.put("demand_area_id", toJSON(getDemandAreaId()));
		jsonObject.put("end_at", toJSON(getEndAt()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("image", toJSON(getImage()));
		jsonObject.put("keyword", toJSON(getKeyword()));
		jsonObject.put("latitude", toJSON(getLatitude()));
		jsonObject.put("longitude", toJSON(getLongitude()));
		jsonObject.put("memo", toJSON(getMemo()));
		jsonObject.put("name", toJSON(getName()));
		jsonObject.put("name_ruby", toJSON(getNameRuby()));
		jsonObject.put("platform_category_id", toJSON(getPlatformCategoryId()));
		jsonObject.put("reporting_region_id", toJSON(getReportingRegionId()));
		jsonObject.put("semi_demand_area_id", toJSON(getSemiDemandAreaId()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId()));
		jsonObject.put("start_at", toJSON(getStartAt()));
		jsonObject.put("type_of_demand", toJSON(getTypeOfDemand()));
		jsonObject.put("type_of_platform", toJSON(getTypeOfPlatform()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		if (getDemandsAsArrival().size() > 0 && recursive) {
			jsonObject.put("demands_as_arrival", toJSON(getDemandsAsArrival(), true, nextDepth));
		}
		if (getDemandsAsDeparture().size() > 0 && recursive) {
			jsonObject.put("demands_as_departure", toJSON(getDemandsAsDeparture(), true, nextDepth));
		}
		if (getOperationSchedules().size() > 0 && recursive) {
			jsonObject.put("operation_schedules", toJSON(getOperationSchedules(), true, nextDepth));
		}
		if (getReservationCandidatesAsArrival().size() > 0 && recursive) {
			jsonObject.put("reservation_candidates_as_arrival", toJSON(getReservationCandidatesAsArrival(), true, nextDepth));
		}
		if (getReservationCandidatesAsDeparture().size() > 0 && recursive) {
			jsonObject.put("reservation_candidates_as_departure", toJSON(getReservationCandidatesAsDeparture(), true, nextDepth));
		}
		if (getReservationsAsArrival().size() > 0 && recursive) {
			jsonObject.put("reservations_as_arrival", toJSON(getReservationsAsArrival(), true, nextDepth));
		}
		if (getReservationsAsDeparture().size() > 0 && recursive) {
			jsonObject.put("reservations_as_departure", toJSON(getReservationsAsDeparture(), true, nextDepth));
		}
		if (getServiceProvider().isPresent()) {
			if (recursive) {
				jsonObject.put("service_provider", getServiceProvider().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
			}
		}
		return jsonObject;
	}

	@Override
	public Platform cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
	}

	private String address = "";

	public String getAddress() {
		return wrapNull(address);
	}

	public void setAddress(String address) {
		refreshUpdatedAt();
		this.address = wrapNull(address);
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		refreshUpdatedAt();
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> deletedAt = Optional.absent();

	public Optional<Date> getDeletedAt() {
		return wrapNull(deletedAt);
	}

	public void setDeletedAt(Optional<Date> deletedAt) {
		refreshUpdatedAt();
		this.deletedAt = wrapNull(deletedAt);
	}

	public void setDeletedAt(Date deletedAt) {
		setDeletedAt(Optional.fromNullable(deletedAt));
	}

	public void clearDeletedAt() {
		setDeletedAt(Optional.<Date>absent());
	}

	private Optional<Integer> demandAreaId = Optional.absent();

	public Optional<Integer> getDemandAreaId() {
		return wrapNull(demandAreaId);
	}

	public void setDemandAreaId(Optional<Integer> demandAreaId) {
		refreshUpdatedAt();
		this.demandAreaId = wrapNull(demandAreaId);
	}

	public void setDemandAreaId(Integer demandAreaId) {
		setDemandAreaId(Optional.fromNullable(demandAreaId));
	}

	public void clearDemandAreaId() {
		setDemandAreaId(Optional.<Integer>absent());
	}

	private Optional<Date> endAt = Optional.absent();

	public Optional<Date> getEndAt() {
		return wrapNull(endAt);
	}

	public void setEndAt(Optional<Date> endAt) {
		refreshUpdatedAt();
		this.endAt = wrapNull(endAt);
	}

	public void setEndAt(Date endAt) {
		setEndAt(Optional.fromNullable(endAt));
	}

	public void clearEndAt() {
		setEndAt(Optional.<Date>absent());
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	private Optional<String> image = Optional.absent();

	public Optional<String> getImage() {
		return wrapNull(image);
	}

	public void setImage(Optional<String> image) {
		refreshUpdatedAt();
		this.image = wrapNull(image);
	}

	public void setImage(String image) {
		setImage(Optional.fromNullable(image));
	}

	public void clearImage() {
		setImage(Optional.<String>absent());
	}

	private String keyword = "";

	public String getKeyword() {
		return wrapNull(keyword);
	}

	public void setKeyword(String keyword) {
		refreshUpdatedAt();
		this.keyword = wrapNull(keyword);
	}

	private BigDecimal latitude = BigDecimal.ZERO;

	public BigDecimal getLatitude() {
		return wrapNull(latitude);
	}

	public void setLatitude(BigDecimal latitude) {
		refreshUpdatedAt();
		this.latitude = wrapNull(latitude);
	}

	private BigDecimal longitude = BigDecimal.ZERO;

	public BigDecimal getLongitude() {
		return wrapNull(longitude);
	}

	public void setLongitude(BigDecimal longitude) {
		refreshUpdatedAt();
		this.longitude = wrapNull(longitude);
	}

	private String memo = "";

	public String getMemo() {
		return wrapNull(memo);
	}

	public void setMemo(String memo) {
		refreshUpdatedAt();
		this.memo = wrapNull(memo);
	}

	private String name = "";

	public String getName() {
		return wrapNull(name);
	}

	public void setName(String name) {
		refreshUpdatedAt();
		this.name = wrapNull(name);
	}

	private String nameRuby = "";

	public String getNameRuby() {
		return wrapNull(nameRuby);
	}

	public void setNameRuby(String nameRuby) {
		refreshUpdatedAt();
		this.nameRuby = wrapNull(nameRuby);
	}

	private Optional<Integer> platformCategoryId = Optional.absent();

	public Optional<Integer> getPlatformCategoryId() {
		return wrapNull(platformCategoryId);
	}

	public void setPlatformCategoryId(Optional<Integer> platformCategoryId) {
		refreshUpdatedAt();
		this.platformCategoryId = wrapNull(platformCategoryId);
	}

	public void setPlatformCategoryId(Integer platformCategoryId) {
		setPlatformCategoryId(Optional.fromNullable(platformCategoryId));
	}

	public void clearPlatformCategoryId() {
		setPlatformCategoryId(Optional.<Integer>absent());
	}

	private Integer reportingRegionId = 0;

	public Integer getReportingRegionId() {
		return wrapNull(reportingRegionId);
	}

	public void setReportingRegionId(Integer reportingRegionId) {
		refreshUpdatedAt();
		this.reportingRegionId = wrapNull(reportingRegionId);
	}

	private Optional<Integer> semiDemandAreaId = Optional.absent();

	public Optional<Integer> getSemiDemandAreaId() {
		return wrapNull(semiDemandAreaId);
	}

	public void setSemiDemandAreaId(Optional<Integer> semiDemandAreaId) {
		refreshUpdatedAt();
		this.semiDemandAreaId = wrapNull(semiDemandAreaId);
	}

	public void setSemiDemandAreaId(Integer semiDemandAreaId) {
		setSemiDemandAreaId(Optional.fromNullable(semiDemandAreaId));
	}

	public void clearSemiDemandAreaId() {
		setSemiDemandAreaId(Optional.<Integer>absent());
	}

	private Optional<Integer> serviceProviderId = Optional.absent();

	public Optional<Integer> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Optional<Integer> serviceProviderId) {
		refreshUpdatedAt();
		this.serviceProviderId = wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Integer serviceProviderId) {
		setServiceProviderId(Optional.fromNullable(serviceProviderId));
	}

	public void clearServiceProviderId() {
		setServiceProviderId(Optional.<Integer>absent());
	}

	private Optional<Date> startAt = Optional.absent();

	public Optional<Date> getStartAt() {
		return wrapNull(startAt);
	}

	public void setStartAt(Optional<Date> startAt) {
		refreshUpdatedAt();
		this.startAt = wrapNull(startAt);
	}

	public void setStartAt(Date startAt) {
		setStartAt(Optional.fromNullable(startAt));
	}

	public void clearStartAt() {
		setStartAt(Optional.<Date>absent());
	}

	private Optional<Integer> typeOfDemand = Optional.absent();

	public Optional<Integer> getTypeOfDemand() {
		return wrapNull(typeOfDemand);
	}

	public void setTypeOfDemand(Optional<Integer> typeOfDemand) {
		refreshUpdatedAt();
		this.typeOfDemand = wrapNull(typeOfDemand);
	}

	public void setTypeOfDemand(Integer typeOfDemand) {
		setTypeOfDemand(Optional.fromNullable(typeOfDemand));
	}

	public void clearTypeOfDemand() {
		setTypeOfDemand(Optional.<Integer>absent());
	}

	private Integer typeOfPlatform = 0;

	public Integer getTypeOfPlatform() {
		return wrapNull(typeOfPlatform);
	}

	public void setTypeOfPlatform(Integer typeOfPlatform) {
		refreshUpdatedAt();
		this.typeOfPlatform = wrapNull(typeOfPlatform);
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private LinkedList<Demand> demandsAsArrival = new LinkedList<Demand>();

	public List<Demand> getDemandsAsArrival() {
		return wrapNull(demandsAsArrival);
	}

	public void setDemandsAsArrival(Iterable<Demand> demandsAsArrival) {
		this.demandsAsArrival = wrapNull(demandsAsArrival);
	}

	public void clearDemandsAsArrival() {
		setDemandsAsArrival(new LinkedList<Demand>());
	}

	private LinkedList<Demand> demandsAsDeparture = new LinkedList<Demand>();

	public List<Demand> getDemandsAsDeparture() {
		return wrapNull(demandsAsDeparture);
	}

	public void setDemandsAsDeparture(Iterable<Demand> demandsAsDeparture) {
		this.demandsAsDeparture = wrapNull(demandsAsDeparture);
	}

	public void clearDemandsAsDeparture() {
		setDemandsAsDeparture(new LinkedList<Demand>());
	}

	private LinkedList<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();

	public List<OperationSchedule> getOperationSchedules() {
		return wrapNull(operationSchedules);
	}

	public void setOperationSchedules(Iterable<OperationSchedule> operationSchedules) {
		this.operationSchedules = wrapNull(operationSchedules);
	}

	public void clearOperationSchedules() {
		setOperationSchedules(new LinkedList<OperationSchedule>());
	}

	private LinkedList<ReservationCandidate> reservationCandidatesAsArrival = new LinkedList<ReservationCandidate>();

	public List<ReservationCandidate> getReservationCandidatesAsArrival() {
		return wrapNull(reservationCandidatesAsArrival);
	}

	public void setReservationCandidatesAsArrival(Iterable<ReservationCandidate> reservationCandidatesAsArrival) {
		this.reservationCandidatesAsArrival = wrapNull(reservationCandidatesAsArrival);
	}

	public void clearReservationCandidatesAsArrival() {
		setReservationCandidatesAsArrival(new LinkedList<ReservationCandidate>());
	}

	private LinkedList<ReservationCandidate> reservationCandidatesAsDeparture = new LinkedList<ReservationCandidate>();

	public List<ReservationCandidate> getReservationCandidatesAsDeparture() {
		return wrapNull(reservationCandidatesAsDeparture);
	}

	public void setReservationCandidatesAsDeparture(Iterable<ReservationCandidate> reservationCandidatesAsDeparture) {
		this.reservationCandidatesAsDeparture = wrapNull(reservationCandidatesAsDeparture);
	}

	public void clearReservationCandidatesAsDeparture() {
		setReservationCandidatesAsDeparture(new LinkedList<ReservationCandidate>());
	}

	private LinkedList<Reservation> reservationsAsArrival = new LinkedList<Reservation>();

	public List<Reservation> getReservationsAsArrival() {
		return wrapNull(reservationsAsArrival);
	}

	public void setReservationsAsArrival(Iterable<Reservation> reservationsAsArrival) {
		this.reservationsAsArrival = wrapNull(reservationsAsArrival);
	}

	public void clearReservationsAsArrival() {
		setReservationsAsArrival(new LinkedList<Reservation>());
	}

	private LinkedList<Reservation> reservationsAsDeparture = new LinkedList<Reservation>();

	public List<Reservation> getReservationsAsDeparture() {
		return wrapNull(reservationsAsDeparture);
	}

	public void setReservationsAsDeparture(Iterable<Reservation> reservationsAsDeparture) {
		this.reservationsAsDeparture = wrapNull(reservationsAsDeparture);
	}

	public void clearReservationsAsDeparture() {
		setReservationsAsDeparture(new LinkedList<Reservation>());
	}

	private Optional<ServiceProvider> serviceProvider = Optional.<ServiceProvider>absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		setServiceProvider(Optional.fromNullable(serviceProvider));
	}

	public void clearServiceProvider() {
		setServiceProvider(Optional.<ServiceProvider>absent());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(address)
			.append(createdAt)
			.append(deletedAt)
			.append(demandAreaId)
			.append(endAt)
			.append(id)
			.append(image)
			.append(keyword)
			.append(latitude)
			.append(longitude)
			.append(memo)
			.append(name)
			.append(nameRuby)
			.append(platformCategoryId)
			.append(reportingRegionId)
			.append(semiDemandAreaId)
			.append(serviceProviderId)
			.append(startAt)
			.append(typeOfDemand)
			.append(typeOfPlatform)
			.append(updatedAt)
			.append(demandsAsArrival)
			.append(demandsAsDeparture)
			.append(operationSchedules)
			.append(reservationCandidatesAsArrival)
			.append(reservationCandidatesAsDeparture)
			.append(reservationsAsArrival)
			.append(reservationsAsDeparture)
			.append(serviceProvider)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof PlatformBase)) {
			return false;
		}
		PlatformBase other = (PlatformBase) obj;
		return new EqualsBuilder()
			.append(address, other.address)
			.append(createdAt, other.createdAt)
			.append(deletedAt, other.deletedAt)
			.append(demandAreaId, other.demandAreaId)
			.append(endAt, other.endAt)
			.append(id, other.id)
			.append(image, other.image)
			.append(keyword, other.keyword)
			.append(latitude, other.latitude)
			.append(longitude, other.longitude)
			.append(memo, other.memo)
			.append(name, other.name)
			.append(nameRuby, other.nameRuby)
			.append(platformCategoryId, other.platformCategoryId)
			.append(reportingRegionId, other.reportingRegionId)
			.append(semiDemandAreaId, other.semiDemandAreaId)
			.append(serviceProviderId, other.serviceProviderId)
			.append(startAt, other.startAt)
			.append(typeOfDemand, other.typeOfDemand)
			.append(typeOfPlatform, other.typeOfPlatform)
			.append(updatedAt, other.updatedAt)
			.append(demandsAsArrival, other.demandsAsArrival)
			.append(demandsAsDeparture, other.demandsAsDeparture)
			.append(operationSchedules, other.operationSchedules)
			.append(reservationCandidatesAsArrival, other.reservationCandidatesAsArrival)
			.append(reservationCandidatesAsDeparture, other.reservationCandidatesAsDeparture)
			.append(reservationsAsArrival, other.reservationsAsArrival)
			.append(reservationsAsDeparture, other.reservationsAsDeparture)
			.append(serviceProvider, other.serviceProvider)
			.isEquals();
	}
}
