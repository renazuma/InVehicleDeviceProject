package com.kogasoftware.odt.webapi.model.base;

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
import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.ResponseConverter;
import com.kogasoftware.odt.webapi.model.*;

@SuppressWarnings("unused")
public abstract class VehicleBase extends Model {
	private static final long serialVersionUID = 6843429742406163851L;
	public static final ResponseConverter<Vehicle> RESPONSE_CONVERTER = new ResponseConverter<Vehicle>() {
		@Override
		public Vehicle convert(byte[] rawResponse) throws JSONException {
			return parse(WebAPI.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<Vehicle>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<Vehicle>>() {
		@Override
		public List<Vehicle> convert(byte[] rawResponse) throws JSONException {
			return parseList(WebAPI.parseJSONArray(rawResponse));
		}
	};
	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setCapacity(parseInteger(jsonObject, "capacity"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setId(parseInteger(jsonObject, "id"));
		setImage(parseOptionalString(jsonObject, "image"));
		setModelName(parseString(jsonObject, "model_name"));
		setNumber(parseString(jsonObject, "number"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		setServiceUnits(ServiceUnit.parseList(jsonObject, "service_units"));

		setUpdatedAt(parseDate(jsonObject, "updated_at"));
	}

	public static Optional<Vehicle> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static Vehicle parse(JSONObject jsonObject) throws JSONException {
		Vehicle model = new Vehicle();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<Vehicle> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Vehicle>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<Vehicle> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<Vehicle> models = new LinkedList<Vehicle>();
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
		jsonObject.put("capacity", toJSON(getCapacity()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("image", toJSON(getImage()));
		jsonObject.put("model_name", toJSON(getModelName()));
		jsonObject.put("number", toJSON(getNumber()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		if (getServiceProvider().isPresent()) {
			if (recursive) {
				jsonObject.put("service_provider", getServiceProvider().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
			}
		}
		if (getServiceUnits().size() > 0 && recursive) {
			jsonObject.put("service_units", toJSON(getServiceUnits(), true, nextDepth));
		}
		return jsonObject;
	}

	@Override
	public Vehicle cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
	}

	private Integer capacity = 0;

	public Integer getCapacity() {
		return wrapNull(capacity);
	}

	public void setCapacity(Integer capacity) {
		refreshUpdatedAt();
		this.capacity = wrapNull(capacity);
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

	private String modelName = "";

	public String getModelName() {
		return wrapNull(modelName);
	}

	public void setModelName(String modelName) {
		refreshUpdatedAt();
		this.modelName = wrapNull(modelName);
	}

	private String number = "";

	public String getNumber() {
		return wrapNull(number);
	}

	public void setNumber(String number) {
		refreshUpdatedAt();
		this.number = wrapNull(number);
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
		setServiceProvider(Optional.fromNullable(serviceProvider));
	}

	public void clearServiceProvider() {
		setServiceProvider(Optional.<ServiceProvider>absent());
	}

	private LinkedList<ServiceUnit> serviceUnits = new LinkedList<ServiceUnit>();

	public List<ServiceUnit> getServiceUnits() {
		return wrapNull(serviceUnits);
	}

	public void setServiceUnits(Iterable<ServiceUnit> serviceUnits) {
		this.serviceUnits = wrapNull(serviceUnits);
	}

	public void clearServiceUnits() {
		setServiceUnits(new LinkedList<ServiceUnit>());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(capacity)
			.append(createdAt)
			.append(deletedAt)
			.append(id)
			.append(image)
			.append(modelName)
			.append(number)
			.append(serviceProviderId)
			.append(updatedAt)
			.append(serviceProvider)
			.append(serviceUnits)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof VehicleBase)) {
			return false;
		}
		VehicleBase other = (VehicleBase) obj;
		return new EqualsBuilder()
			.append(capacity, other.capacity)
			.append(createdAt, other.createdAt)
			.append(deletedAt, other.deletedAt)
			.append(id, other.id)
			.append(image, other.image)
			.append(modelName, other.modelName)
			.append(number, other.number)
			.append(serviceProviderId, other.serviceProviderId)
			.append(updatedAt, other.updatedAt)
			.append(serviceProvider, other.serviceProvider)
			.append(serviceUnits, other.serviceUnits)
			.isEquals();
	}
}
