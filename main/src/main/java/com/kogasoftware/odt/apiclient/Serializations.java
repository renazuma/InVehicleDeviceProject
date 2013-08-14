package com.kogasoftware.odt.apiclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class Serializations {
	/**
	 * commons-langのSerializationUtils.deserializeのラッパーメソッド
	 * 
	 * SerializationUtils.deserialize()がAndroidで発生させた実績のある非チェック例外をキャッチし
	 * SerializationExceptionでラップして再スローする。
	 * 
	 * @param inputStream デシリアライズされるストリーム。自動で閉じられる。
	 * @return デシリアライズされたオブジェクト
	 */
	public static Object deserialize(InputStream inputStream) {
		// TODO: 一度InputStreamを全読みしてからdeserializeしないと
		// LocalStorageTestCase.testNewScheduleでEBADFが発生するのの原因を調査
		ByteArrayInputStream byteArrayInputStream = null;
		try {
			byteArrayInputStream = new ByteArrayInputStream(
					ByteStreams.toByteArray(inputStream));
			return SerializationUtils.deserialize(byteArrayInputStream);
		} catch (IllegalArgumentException e) {
			throw new SerializationException(e);
		} catch (IndexOutOfBoundsException e) {
			throw new SerializationException(e);
		} catch (IOException e) {
			throw new SerializationException(e);
		} finally {
			Closeables.closeQuietly(inputStream);
			Closeables.closeQuietly(byteArrayInputStream);
		}
	}

	/**
	 * commons-langのSerializationUtils.serializeのラッパーメソッド
	 *
	 * @param serializable シリアライズされるオブジェクト
	 * @param outputStream シリアライズされたオブジェクトを出力するストリーム。自動で閉じられる。
	 * @return デシリアライズされたオブジェクト
	 */
	public static void serialize(Serializable serializable,
			OutputStream outputStream) {
		SerializationUtils.serialize(serializable, outputStream);
	}

	public static byte[] serialize(Serializable serializable) {
		return SerializationUtils.serialize(serializable);
	}

	public static <T extends Serializable> T clone(T serializable) {
		return SerializationUtils.clone(serializable);
	}

	public static <T extends Serializable> T deserialize(
			InputStream inputStream, Class<T> originalClass) {
		Object object = deserialize(inputStream);
		if (originalClass.isInstance(object)) {
			return originalClass.cast(object);
		}
		throw new SerializationException(new ClassCastException("object["
				+ object + "] is not instance of " + originalClass));
	}
}
