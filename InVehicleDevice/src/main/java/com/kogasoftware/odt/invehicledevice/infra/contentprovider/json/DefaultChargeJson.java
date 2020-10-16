package com.kogasoftware.odt.invehicledevice.infra.contentprovider.json;

import android.content.ContentValues;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.DefaultCharge;

/**
 * 基本料金のJSON
 */
public class DefaultChargeJson {
	public Long id;
	public Long value;
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(DefaultCharge.Columns._ID, id);
		values.put(DefaultCharge.Columns.VALUE, value);
		return values;
	}
}
