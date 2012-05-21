package com.kogasoftware.odt.webapi.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class Driver extends Model {
	private static final long serialVersionUID = 6951241086202488163L;

	public Driver() {
	}

	public Driver(JSONObject jsonObject) throws JSONException, ParseException {
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setFirstName(parseString(jsonObject, "first_name"));
		setId(parseInteger(jsonObject, "id"));
		setLastName(parseString(jsonObject, "last_name"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setTelephoneNumber(parseString(jsonObject, "telephone_number"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		if (getServiceProvider().isPresent()) {
			setServiceProviderId(getServiceProvider().get().getId());
		}
		setServiceUnits(ServiceUnit.parseList(jsonObject, "service_units"));
	}

	public static Optional<Driver> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<Driver> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.of(new Driver(jsonObject));
	}

	public static LinkedList<Driver> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Driver>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<Driver> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<Driver> models = new LinkedList<Driver>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new Driver(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("first_name", toJSON(getFirstName()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("last_name", toJSON(getLastName()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("telephone_number", toJSON(getTelephoneNumber()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));

		if (getServiceProvider().isPresent()) {
			jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
		}
		if (getServiceUnits().size() > 0) {
	   		jsonObject.put("service_units", toJSON(getServiceUnits()));
		}

		return jsonObject;
	}

	@Override
	public Driver clone() {
		return SerializationUtils.clone(this);
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> deletedAt = Optional.absent();

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
		this.deletedAt = Optional.absent();
	}

	private String firstName = "";

	public String getFirstName() {
		return wrapNull(firstName);
	}

	public void setFirstName(String firstName) {
		this.firstName = wrapNull(firstName);
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private String lastName = "";

	public String getLastName() {
		return wrapNull(lastName);
	}

	public void setLastName(String lastName) {
		this.lastName = wrapNull(lastName);
	}

	private Optional<Integer> serviceProviderId = Optional.absent();

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
		this.serviceProviderId = Optional.absent();
	}

	private String telephoneNumber = "";

	public String getTelephoneNumber() {
		return wrapNull(telephoneNumber);
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = wrapNull(telephoneNumber);
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<ServiceProvider> serviceProvider = Optional.absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = Optional.fromNullable(serviceProvider);
	}

	public void clearServiceProvider() {
		this.serviceProvider = Optional.absent();
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
