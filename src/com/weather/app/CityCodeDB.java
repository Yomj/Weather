package com.weather.app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CityCodeDB {
	public static final String TABLE_PROVINCE = "province";
	public static final String TABLE_CITY = "city";
	public static final String TABLE_AREA = "area";
	public static final String TABLE_CITY_CODE = "city_code";

	private Context context;

	CityCodeDB(Context context) {
		this.context = context;
	}

	SQLiteDatabase getDatabase(String dbname) {
		AssetsDatabaseManager.initManager(context);
		AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
		SQLiteDatabase db = mg.getDatabase(dbname);
		return db;
	}

	// ��ѯprovince��,����ʡ����Ϣcursor
	Cursor getAllProvince(SQLiteDatabase db) {
		if (db != null) {
			Cursor cur = db
					.query(TABLE_PROVINCE, new String[] { "id", "name" }, null,
							null, null, null, null);
			return cur;
		} else {
			return null;
		}
	}

	// ��ѯcity��,����ָ��ʡ�ݵ����г�����Ϣcursor
	Cursor getCity(SQLiteDatabase db, String provinceid) {
		if (db != null) {
			Cursor cur = db.query(TABLE_CITY, new String[] { "id", "p_id",
					"name" }, "p_id = ?", new String[] { provinceid }, null,
					null, null);
			return cur;
		} else {
			return null;
		}
	}

	// ��ѯarea��,����ָ�����е����е�����Ϣcursor
	Cursor getArea(SQLiteDatabase db, String cityid) {
		if (db != null) {
			Cursor cur = db.query(TABLE_AREA, new String[] { "id", "c_id",
					"name" }, "c_id = ?", new String[] { cityid }, null, null,
					null);
			return cur;
		} else {
			return null;
		}
	}

	// ��ѯcity_code��,ͨ��areaid��ȡ������code
	String getCityCode(SQLiteDatabase db, String areaid) {
		if (db != null) {
			Cursor cur = db.query(TABLE_CITY_CODE, new String[] { "id", "code",
					"name" }, "id = ?", new String[] { areaid }, null, null,
					null);
			String citycode = null;
			if (cur.moveToFirst()) {
				citycode = cur.getString(cur.getColumnIndex("code"));
			}
			return citycode;
		} else {
			return null;
		}
	}

	// ��ѯcity_code��,ͨ��areaname��ȡ������code
	String getCityCodeByName(SQLiteDatabase db, String areaname) {
		if (db != null) {
			Cursor cur = db.query(TABLE_CITY_CODE, new String[] { "id", "code",
					"name" }, "name = ?", new String[] { areaname }, null,
					null, null);
			String citycode = null;
			if (cur.moveToFirst()) {
				citycode = cur.getString(cur.getColumnIndex("code"));
			}
			return citycode;
		} else {
			return null;
		}
	}

}
