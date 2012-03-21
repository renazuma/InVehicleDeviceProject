package com.kogasoftware.odt.webapi.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;
import com.kogasoftware.odt.webapi.WebAPI;

public class Platform extends Model {
	private static final long serialVersionUID = 6509583642897973107L;
	public static final String JSON_NAME = "platform";
	public static final String CONTROLLER_NAME = "platforms";

	public static class URL {
		public static final String ROOT = "/" + CONTROLLER_NAME;
		public static final String CREATE = "/" + CONTROLLER_NAME + "/create";
		public static final String DESTROY = "/" + CONTROLLER_NAME + "/destroy";
		public static final String EDIT = "/" + CONTROLLER_NAME + "/edit";
		public static final String IMAGE = "/" + CONTROLLER_NAME + "/image";
		public static final String INDEX = "/" + CONTROLLER_NAME + "/index";
		public static final String NEW = "/" + CONTROLLER_NAME + "/new";
		public static final String SEARCH = "/" + CONTROLLER_NAME + "/search";
		public static final String SHOW = "/" + CONTROLLER_NAME + "/show";
		public static final String UPDATE = "/" + CONTROLLER_NAME + "/update";
	}

	public Platform() {
	}

	public Platform(JSONObject jsonObject) throws JSONException, ParseException {
		setAddress(parseString(jsonObject, "address"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseDate(jsonObject, "deleted_at"));
		setDemandAreaId(parseLong(jsonObject, "demand_area_id"));
		setEndAt(parseDate(jsonObject, "end_at"));
		setId(parseLong(jsonObject, "id"));
		setImage(parseString(jsonObject, "image"));
		setKeyword(parseString(jsonObject, "keyword"));
		setLatitude(parseBigDecimal(jsonObject, "latitude"));
		setLongitude(parseBigDecimal(jsonObject, "longitude"));
		setMemo(parseString(jsonObject, "memo"));
		setName(parseString(jsonObject, "name"));
		setNameRuby(parseString(jsonObject, "name_ruby"));
		setServiceProviderId(parseLong(jsonObject, "service_provider_id"));
		setStartAt(parseDate(jsonObject, "start_at"));
		setTypeOfDemand(parseLong(jsonObject, "type_of_demand"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setReservationsAsDeparture(Reservation.parseList(jsonObject, "reservations_as_departure"));
		setReservationsAsArrival(Reservation.parseList(jsonObject, "reservations_as_arrival"));
	}

	public static List<Platform> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Platform>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		List<Platform> models = new LinkedList<Platform>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new Platform(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	public static class ResponseConverter implements
			WebAPI.ResponseConverter<Platform> {
		@Override
		public Platform convert(byte[] rawResponse) throws JSONException, ParseException {
			return new Platform(new JSONObject(new String(rawResponse)));
		}
	}

	public static class ListResponseConverter implements
			WebAPI.ResponseConverter<List<Platform>> {
		@Override
		public List<Platform> convert(byte[] rawResponse) throws JSONException,
				ParseException {
			JSONArray array = new JSONArray(new String(rawResponse));
			List<Platform> models = new LinkedList<Platform>();
			for (Integer i = 0; i < array.length(); ++i) {
				if (array.isNull(i)) {
					continue;
				}
				JSONObject object = array.getJSONObject(i);
				models.add(new Platform(object));
			}
			return models;
		}
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("address", toJSON(getAddress().orNull()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("demand_area_id", toJSON(getDemandAreaId().orNull()));
		jsonObject.put("end_at", toJSON(getEndAt().orNull()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("image", toJSON(getImage().orNull()));
		jsonObject.put("keyword", toJSON(getKeyword().orNull()));
		jsonObject.put("latitude", toJSON(getLatitude()));
		jsonObject.put("longitude", toJSON(getLongitude()));
		jsonObject.put("memo", toJSON(getMemo().orNull()));
		jsonObject.put("name", toJSON(getName()));
		jsonObject.put("name_ruby", toJSON(getNameRuby()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("start_at", toJSON(getStartAt().orNull()));
		jsonObject.put("type_of_demand", toJSON(getTypeOfDemand().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("reservations_as_departure", toJSON(getReservationsAsDeparture()));
		jsonObject.put("reservations_as_arrival", toJSON(getReservationsAsArrival()));
		return jsonObject;
	}

	private Optional<String> address = Optional.<String>absent();

	public Optional<String> getAddress() {
		return wrapNull(address);
	}

	public void setAddress(Optional<String> address) {
		this.address = wrapNull(address);
	}

	public void setAddress(String address) {
		this.address = Optional.fromNullable(address);
	}

	public void clearAddress() {
		this.address = Optional.<String>absent();
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> deletedAt = Optional.<Date>absent();

	public Optional<Date> getDeletedAt() {
		return wrapNull(deletedAt);
	}

	public void setDeletedAt(Optional<Date> deletedAt) {
		this.deletedAt = wrapNull(deletedAt);
	}

	public void setDeletedAt(Date deletedAt) {
		this.deletedAt = Optional.fromNullable(deletedAt);
	}

	public void clearDeletedAt() {
		this.deletedAt = Optional.<Date>absent();
	}

	private Optional<Long> demandAreaId = Optional.<Long>absent();

	public Optional<Long> getDemandAreaId() {
		return wrapNull(demandAreaId);
	}

	public void setDemandAreaId(Optional<Long> demandAreaId) {
		this.demandAreaId = wrapNull(demandAreaId);
	}

	public void setDemandAreaId(Long demandAreaId) {
		this.demandAreaId = Optional.fromNullable(demandAreaId);
	}

	public void clearDemandAreaId() {
		this.demandAreaId = Optional.<Long>absent();
	}

	private Optional<Date> endAt = Optional.<Date>absent();

	public Optional<Date> getEndAt() {
		return wrapNull(endAt);
	}

	public void setEndAt(Optional<Date> endAt) {
		this.endAt = wrapNull(endAt);
	}

	public void setEndAt(Date endAt) {
		this.endAt = Optional.fromNullable(endAt);
	}

	public void clearEndAt() {
		this.endAt = Optional.<Date>absent();
	}

	private Long id = 0l;

	public Long getId() {
		return wrapNull(id);
	}

	public void setId(Long id) {
		this.id = wrapNull(id);
	}

	private Optional<String> image = Optional.<String>absent();

	public Optional<String> getImage() {
		return wrapNull(image);
	}

	public void setImage(Optional<String> image) {
		this.image = wrapNull(image);
	}

	public void setImage(String image) {
		this.image = Optional.fromNullable(image);
	}

	public void clearImage() {
		this.image = Optional.<String>absent();
	}

	private Optional<String> keyword = Optional.<String>absent();

	public Optional<String> getKeyword() {
		return wrapNull(keyword);
	}

	public void setKeyword(Optional<String> keyword) {
		this.keyword = wrapNull(keyword);
	}

	public void setKeyword(String keyword) {
		this.keyword = Optional.fromNullable(keyword);
	}

	public void clearKeyword() {
		this.keyword = Optional.<String>absent();
	}

	private BigDecimal latitude = BigDecimal.ZERO;

	public BigDecimal getLatitude() {
		return wrapNull(latitude);
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = wrapNull(latitude);
	}

	private BigDecimal longitude = BigDecimal.ZERO;

	public BigDecimal getLongitude() {
		return wrapNull(longitude);
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = wrapNull(longitude);
	}

	private Optional<String> memo = Optional.<String>absent();

	public Optional<String> getMemo() {
		return wrapNull(memo);
	}

	public void setMemo(Optional<String> memo) {
		this.memo = wrapNull(memo);
	}

	public void setMemo(String memo) {
		this.memo = Optional.fromNullable(memo);
	}

	public void clearMemo() {
		this.memo = Optional.<String>absent();
	}

	private String name = "";

	public String getName() {
		return wrapNull(name);
	}

	public void setName(String name) {
		this.name = wrapNull(name);
	}

	private String nameRuby = "";

	public String getNameRuby() {
		return wrapNull(nameRuby);
	}

	public void setNameRuby(String nameRuby) {
		this.nameRuby = wrapNull(nameRuby);
	}

	private Optional<Long> serviceProviderId = Optional.<Long>absent();

	public Optional<Long> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Optional<Long> serviceProviderId) {
		this.serviceProviderId = wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Long serviceProviderId) {
		this.serviceProviderId = Optional.fromNullable(serviceProviderId);
	}

	public void clearServiceProviderId() {
		this.serviceProviderId = Optional.<Long>absent();
	}

	private Optional<Date> startAt = Optional.<Date>absent();

	public Optional<Date> getStartAt() {
		return wrapNull(startAt);
	}

	public void setStartAt(Optional<Date> startAt) {
		this.startAt = wrapNull(startAt);
	}

	public void setStartAt(Date startAt) {
		this.startAt = Optional.fromNullable(startAt);
	}

	public void clearStartAt() {
		this.startAt = Optional.<Date>absent();
	}

	private Optional<Long> typeOfDemand = Optional.<Long>absent();

	public Optional<Long> getTypeOfDemand() {
		return wrapNull(typeOfDemand);
	}

	public void setTypeOfDemand(Optional<Long> typeOfDemand) {
		this.typeOfDemand = wrapNull(typeOfDemand);
	}

	public void setTypeOfDemand(Long typeOfDemand) {
		this.typeOfDemand = Optional.fromNullable(typeOfDemand);
	}

	public void clearTypeOfDemand() {
		this.typeOfDemand = Optional.<Long>absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private List<Reservation> reservationsAsDeparture = new LinkedList<Reservation>();

	public List<Reservation> getReservationsAsDeparture() {
		return new LinkedList<Reservation>(wrapNull(reservationsAsDeparture));
	}

	public void setReservationsAsDeparture(List<Reservation> reservationsAsDeparture) {
		this.reservationsAsDeparture = wrapNull(reservationsAsDeparture);
	}

	public void clearReservationsAsDeparture() {
		this.reservationsAsDeparture = new LinkedList<Reservation>();
	}

	private List<Reservation> reservationsAsArrival = new LinkedList<Reservation>();

	public List<Reservation> getReservationsAsArrival() {
		return new LinkedList<Reservation>(wrapNull(reservationsAsArrival));
	}

	public void setReservationsAsArrival(List<Reservation> reservationsAsArrival) {
		this.reservationsAsArrival = wrapNull(reservationsAsArrival);
	}

	public void clearReservationsAsArrival() {
		this.reservationsAsArrival = new LinkedList<Reservation>();
	}
}
