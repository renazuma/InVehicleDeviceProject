package com.kogasoftware.odt.webapi;

import java.util.Collection;

/**
 * Identifiableに関連する処理
 */
public class Identifiables {
	/**
	 * getId()が同一のものが含まれるかのチェック
	 * 
	 * @param collection
	 *            対象のCollection.
	 * @param searchElement
	 *            探す要素.
	 * @return Boolean collectionと同一のものが含まれていたらtrue.
	 */
	public static <T extends Identifiable> Boolean contains(
			Collection<T> collection, T searchElement) {
		for (Identifiable element : collection) {
			if (element.getId().equals(searchElement.getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * コレクションに要素を追加する. ただしgetId()が同一のものが存在する場合追加しない. 追加された場合はtrueを返す
	 * 
	 * @param collection
	 *            マージ対象のCollection
	 * @param additionalCollection
	 *            collection引数にマージする要素
	 * @return Boolean マージが一度でも発生したらtrue
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
	 * コレクションに要素を追加する. ただしgetId()が同一のものが存在する場合追加しない. 追加された場合はtrueを返す
	 * 
	 * @param collection
	 *            マージ対象のCollection
	 * @param additionalElement
	 *            collection引数にマージする要素
	 * @return Boolean マージが発生したらtrue
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
