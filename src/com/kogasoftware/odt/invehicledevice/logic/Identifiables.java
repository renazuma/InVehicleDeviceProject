package com.kogasoftware.odt.invehicledevice.logic;

import java.util.Collection;

import com.kogasoftware.odt.webapi.Identifiable;

/**
 * Identifiableに関連する処理 TODO: WebAPIへ移動
 */
public class Identifiables {
	/**
	 * getId()が同一のものが含まれるかのチェック
	 */
	public static <T extends Identifiable> Boolean contains(
			Collection<T> collection, T extraElement) {
		for (Identifiable element : collection) {
			if (element.getId().equals(extraElement.getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * コレクションに要素を追加する。ただしgetId()が同一のものが存在する場合追加しない
	 * 追加された場合はtrueを返す
	 */
	public static <T extends Identifiable> Boolean merge(
			Collection<T> collection, Collection<T> additionalCollection) {
		Boolean merged = false;
		for (T additionalElement : additionalCollection) {
			if (!contains(collection, additionalElement)) {
				collection.add(additionalElement);
				merged = true;
			}
		}
		return merged;
	}

	/**
	 * コレクションに要素を追加する。ただしgetId()が同一のものが存在する場合追加しない
	 * 追加された場合はtrueを返す
	 */
	public static <T extends Identifiable> Boolean merge(
			Collection<T> collection, T additionalElement) {
		if (contains(collection, additionalElement)) {
			return false;
		} else {
			collection.add(additionalElement);
			return true;
		}
	}
}
