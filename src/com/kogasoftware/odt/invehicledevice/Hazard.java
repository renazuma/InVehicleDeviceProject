package com.kogasoftware.odt.invehicledevice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

public class Hazard {
	public static class InvalidHazardException extends Exception {
		private static final long serialVersionUID = -2897682940726195774L;
		public final JSONException cause;

		public InvalidHazardException(JSONException cause) {
			this.cause = cause;
		}
	}

	public static enum Severity {
		SEVERITY_1("緊急度1", 1), //
		SEVERITY_Y("緊急度Y", 2), //
		SEVERITY_Z("緊急度Z", 3), //
		;

		private final String stringValue;
		private final Integer integerValue;

		Severity(String stringValue, Integer integerValue) {
			this.stringValue = stringValue;
			this.integerValue = integerValue;
		}

		public Integer toInteger() {
			return integerValue;
		}

		@Override
		public String toString() {
			return stringValue;
		}
	}

	public static enum Type {
		HAZARD_FROM_LEFT("左から接近するハザード"), //
		HAZARD_FROM_RIGHT("右から接近するハザード"), //
		HAZARD_FROM_TOP("上から接近するハザード"), //
		HAZARD_FROM_BOTTOM("下から接近するハザード"), //
		EMERGENCY_VEHICLE("テストデータハザード"), //
		HAZARD_CANCEL(""), ;

		private final String stringValue;

		private Type(String stringValue) {
			this.stringValue = stringValue;
		}

		@Override
		public String toString() {
			return stringValue;
		}
	};

	String name = "";
	Type type = Type.HAZARD_CANCEL;
	Severity severity = Severity.SEVERITY_1;
	String message = "";
	String recommendedAction = "";
	ArrayList<TimeLocation> timeLocations = new ArrayList<TimeLocation>();

	public byte[] toByteArray() {
		String json = JSON.encode(this);
		return json.getBytes();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRecommendedAction() {
		return recommendedAction;
	}

	public void setRecommendedAction(String recommendedAction) {
		this.recommendedAction = recommendedAction;
	}

	public List<TimeLocation> getTimeLocations() {
		return new ArrayList<TimeLocation>(timeLocations);
	}

	public void setTimeLocations(Collection<TimeLocation> timeLocations) {
		this.timeLocations = new ArrayList<TimeLocation>(timeLocations);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((recommendedAction == null) ? 0 : recommendedAction
						.hashCode());
		result = prime * result
				+ ((severity == null) ? 0 : severity.hashCode());
		result = prime * result
				+ ((timeLocations == null) ? 0 : timeLocations.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hazard other = (Hazard) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (recommendedAction == null) {
			if (other.recommendedAction != null)
				return false;
		} else if (!recommendedAction.equals(other.recommendedAction))
			return false;
		if (severity != other.severity)
			return false;
		if (timeLocations == null) {
			if (other.timeLocations != null)
				return false;
		} else if (!timeLocations.equals(other.timeLocations))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public static Hazard fromByteArray(byte[] hazardBytes)
			throws InvalidHazardException {
		String json = new String(hazardBytes);
		try {
			return JSON.decode(json, Hazard.class);
		} catch (JSONException e) {
			throw new InvalidHazardException(e);
		}
	}
}
