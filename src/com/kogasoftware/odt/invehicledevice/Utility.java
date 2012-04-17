package com.kogasoftware.odt.invehicledevice;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kogasoftware.odt.webapi.model.Model;

public class Utility {
	public static <T extends Model> Boolean contains(Collection<T> models,
			T extraModel) {
		for (T model : models) {
			if (model.getId().equals(extraModel.getId())) {
				return true;
			}
		}
		return false;
	}

	public static <T extends Model> void merge(Collection<T> models,
			Collection<T> extraModels) {
		models.addAll(extraModels);
		uniquify(models);
	}

	public static <T extends Model> void merge(Collection<T> models, T model) {
		models.add(model);
		uniquify(models);
	}

	public static <T extends Model> void uniquify(Collection<T> models) {
		Map<Integer, T> uniqueMap = new LinkedHashMap<Integer, T>();
		for (T model : models) {
			if (uniqueMap.get(model.getId()) == null) {
				uniqueMap.put(model.getId(), model);
			}
		}
		models.clear();
		models.addAll(uniqueMap.values());
	}
}
