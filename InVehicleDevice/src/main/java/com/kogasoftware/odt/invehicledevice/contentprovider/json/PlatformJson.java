package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import java.math.BigDecimal;

import android.content.ContentValues;

import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Platforms;

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
		values.put(Platforms.Columns._ID, id);
		values.put(Platforms.Columns.NAME, name);
		values.put(Platforms.Columns.NAME_RUBY, Strings.nullToEmpty(nameRuby));
		values.put(Platforms.Columns.ADDRESS, Strings.nullToEmpty(address));
		values.put(Platforms.Columns.MEMO, Strings.nullToEmpty(memo));
		values.put(Platforms.Columns.LATITUDE, latitude.toPlainString());
		values.put(Platforms.Columns.LONGITUDE, longitude.toPlainString());
		return values;
	}
}
