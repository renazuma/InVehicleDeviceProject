package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import java.math.BigDecimal;

import android.content.ContentValues;

import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Platform;

/**
 * 乗降場のJSON
 */
public class PlatformJson {
	public Long id;
	public String name;
	public String nameRuby;
	public String address;
	public BigDecimal latitude;
	public BigDecimal longitude;
	public String memo;
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(Platform.Columns._ID, id);
		values.put(Platform.Columns.NAME, name);
		values.put(Platform.Columns.NAME_RUBY, Strings.nullToEmpty(nameRuby));
		values.put(Platform.Columns.ADDRESS, Strings.nullToEmpty(address));
		values.put(Platform.Columns.MEMO, Strings.nullToEmpty(memo));
		values.put(Platform.Columns.LATITUDE, latitude.toPlainString());
		values.put(Platform.Columns.LONGITUDE, longitude.toPlainString());
		return values;
	}
}
