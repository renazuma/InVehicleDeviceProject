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

public class Driver extends Model {
	private static final long serialVersionUID = 6446114419898747990L;

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
		setServiceUnits(ServiceUnit.parseList(jsonObject, "service_units"));
	}

	public static Optional<Driver> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<Driver>absent();
		}
		return Optional.<Driver>of(new Driver(jsonObject.getJSONObject(key)));
	}

	public static LinkedList<Driver> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Driver>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
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
		jsonObject.put("service_units", toJSON(getServiceUnits()));
		return jsonObject;
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
