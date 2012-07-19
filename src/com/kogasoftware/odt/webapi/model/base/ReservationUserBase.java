package com.kogasoftware.odt.webapi.model.base;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;
import com.kogasoftware.odt.webapi.model.*;

@SuppressWarnings("unused")
public abstract class ReservationUserBase extends Model {
	private static final long serialVersionUID = 4147461493005701870L;

	@Override
	public void fill(JSONObject jsonObject) throws JSONException, ParseException {
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setId(parseInteger(jsonObject, "id"));
		setLikeReservationId(parseInteger(jsonObject, "like_reservation_id"));
		setLikeReservationType(parseString(jsonObject, "like_reservation_type"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setUserId(parseInteger(jsonObject, "user_id"));
		setUser(User.parse(jsonObject, "user"));
	}

	public static Optional<ReservationUser> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static ReservationUser parse(JSONObject jsonObject) throws JSONException, ParseException {
		ReservationUser model = new ReservationUser();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<ReservationUser> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ReservationUser>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<ReservationUser> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<ReservationUser> models = new LinkedList<ReservationUser>();
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
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("like_reservation_id", toJSON(getLikeReservationId()));
		jsonObject.put("like_reservation_type", toJSON(getLikeReservationType()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("user_id", toJSON(getUserId()));
		if (getUser().isPresent()) {
			if (recursive) {
				jsonObject.put("user", getUser().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("user_id", toJSON(getUser().get().getId()));
			}
		}
		return jsonObject;
	}

	@Override
	public ReservationUser cloneByJSON() throws JSONException {
		try {
			return parse(toJSONObject(true));
		} catch (ParseException e) {
			throw new JSONException(e.toString() + "\n"
				+ ExceptionUtils.getStackTrace(e));
		}
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private Integer likeReservationId = 0;

	public Integer getLikeReservationId() {
		return wrapNull(likeReservationId);
	}

	public void setLikeReservationId(Integer likeReservationId) {
		this.likeReservationId = wrapNull(likeReservationId);
	}

	private String likeReservationType = "";

	public String getLikeReservationType() {
		return wrapNull(likeReservationType);
	}

	public void setLikeReservationType(String likeReservationType) {
		this.likeReservationType = wrapNull(likeReservationType);
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Integer userId = 0;

	public Integer getUserId() {
		return wrapNull(userId);
	}

	public void setUserId(Integer userId) {
		this.userId = wrapNull(userId);
	}

	private Optional<User> user = Optional.absent();

	public Optional<User> getUser() {
		return wrapNull(user);
	}

	public void setUser(Optional<User> user) {
		this.user = wrapNull(user);
	}

	public void setUser(User user) {
		this.user = Optional.fromNullable(user);
	}

	public void clearUser() {
		this.user = Optional.absent();
	}
}
