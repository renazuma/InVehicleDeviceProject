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

public class Vehicle extends Model {
	private static final long serialVersionUID = 5315922442506255047L;

	public Vehicle() {
	}

	public Vehicle(JSONObject jsonObject) throws JSONException, ParseException {
		setCapacity(parseInteger(jsonObject, "capacity"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setId(parseInteger(jsonObject, "id"));
		setImage(parseOptionalString(jsonObject, "image"));
		setModelName(parseString(jsonObject, "model_name"));
		setNumber(parseString(jsonObject, "number"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		if (getServiceProvider().isPresent()) {
			setServiceProviderId(getServiceProvider().get().getId());
		}
		setServiceUnits(ServiceUnit.parseList(jsonObject, "service_units"));
	}

	public static Optional<Vehicle> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<Vehicle>absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<Vehicle> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.<Vehicle>of(new Vehicle(jsonObject));
	}

	public static LinkedList<Vehicle> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Vehicle>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<Vehicle> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<Vehicle> models = new LinkedList<Vehicle>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new Vehicle(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("capacity", toJSON(getCapacity()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("image", toJSON(getImage().orNull()));
		jsonObject.put("model_name", toJSON(getModelName()));
		jsonObject.put("number", toJSON(getNumber()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));

		if (getServiceProvider().isPresent()) {
			jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
		}
		if (getServiceUnits().size() > 0) {
	   		jsonObject.put("service_units", toJSON(getServiceUnits()));
		}

		return jsonObject;
	}

	private Integer capacity = 0;

	public Integer getCapacity() {
		return wrapNull(capacity);
	}

	public void setCapacity(Integer capacity) {
		this.capacity = wrapNull(capacity);
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

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
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

	private String modelName = "";

	public String getModelName() {
		return wrapNull(modelName);
	}

	public void setModelName(String modelName) {
		this.modelName = wrapNull(modelName);
	}

	private String number = "";

	public String getNumber() {
		return wrapNull(number);
	}

	public void setNumber(String number) {
		this.number = wrapNull(number);
	}

	private Optional<Integer> serviceProviderId = Optional.<Integer>absent();

	public Optional<Integer> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Optional<Integer> serviceProviderId) {
		this.serviceProviderId = wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Integer serviceProviderId) {
		this.serviceProviderId = Optional.fromNullable(serviceProviderId);
	}

	public void clearServiceProviderId() {
		this.serviceProviderId = Optional.<Integer>absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<ServiceProvider> serviceProvider = Optional.<ServiceProvider>absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = Optional.<ServiceProvider>fromNullable(serviceProvider);
	}

	public void clearServiceProvider() {
		this.serviceProvider = Optional.<ServiceProvider>absent();
	}

	private LinkedList<ServiceUnit> serviceUnits = new LinkedList<ServiceUnit>();

	public List<ServiceUnit> getServiceUnits() {
		return new LinkedList<ServiceUnit>(wrapNull(serviceUnits));
	}

	public void setServiceUnits(List<ServiceUnit> serviceUnits) {
		this.serviceUnits = new LinkedList<ServiceUnit>(wrapNull(serviceUnits));
	}

	public void clearServiceUnits() {
		this.serviceUnits = new LinkedList<ServiceUnit>();
	}
}
