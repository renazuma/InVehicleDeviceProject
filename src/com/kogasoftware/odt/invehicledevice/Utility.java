package com.kogasoftware.odt.invehicledevice;

import java.util.Collection;
import java.util.LinkedList;

import com.kogasoftware.odt.webapi.model.Model;

public class Utility {
	/**
	 * getId()が同一のものが含まれるかのチェック
	 */
	public static <T extends Model> Boolean containsById(Collection<T> models,
			T extraModel) {
		for (T model : models) {
			if (model.getId().equals(extraModel.getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * getId()の結果でマージする。merge([1,7,5], [3,5,6,6]) => [1,7,5,3,6,6]
	 */
	public static <T extends Model> void mergeById(Collection<T> models,
			Collection<T> additionalModels) {
		Collection<T> baseModels = new LinkedList<T>(models);
		for (T additionalModel : additionalModels) {
			if (!containsById(baseModels, additionalModel)) {
				models.add(additionalModel);
			}
		}
	}

	/**
	 * getId()の結果でマージする。
	 */
	public static <T extends Model> void mergeById(Collection<T> models,
			T additionalModel) {
		if (!containsById(models, additionalModel)) {
			models.add(additionalModel);
		}
	}
}
