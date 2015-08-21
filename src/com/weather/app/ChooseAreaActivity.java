package com.weather.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_AREA = 2;
	
	private TextView titleView;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private List<String> dataList = new ArrayList<String>();
	
	List<String> provinceid, provincename;
	List<String> cityid, cityname;
	List<String> areaid, areaname;
	String province;
	String city;
	String area;
	String citycode;
	String citycode_name;
	CityCodeDB cityCodeDB = null;
	SQLiteDatabase db = null;
	
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleView = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		provinceid = new ArrayList<String>();
		provincename = new ArrayList<String>();
		cityid = new ArrayList<String>();
		cityname = new ArrayList<String>();
		areaid = new ArrayList<String>();
		areaname = new ArrayList<String>();
		cityCodeDB = new CityCodeDB(ChooseAreaActivity.this);
		db = cityCodeDB.getDatabase("data.db");
		queryProvinces();
	}

	protected void queryProvinces() {
		Cursor provinceCursor = cityCodeDB.getAllProvince(db);
		if (provinceCursor != null) {
			provinceid.clear();
			provincename.clear();
			dataList.clear();
			if (provinceCursor.moveToFirst()) {
				do {
					String province_id = provinceCursor
							.getString(provinceCursor.getColumnIndex("id"));
					String province_name = provinceCursor
							.getString(provinceCursor.getColumnIndex("name"));
					provinceid.add(province_id);
					provincename.add(province_name);
					dataList.add(province_name);
				} while (provinceCursor.moveToNext());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText("中国");
			currentLevel = LEVEL_PROVINCE;
			listView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int index, long id) {
						queryCities(db, provinceid.get(index).toString());
				}
			});
		}else{
			Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
		}
	}

	protected void queryCities(SQLiteDatabase database, String provinceid) {
		Cursor cityCursor = cityCodeDB.getCity(db, provinceid);
		if (cityCursor != null) {
			cityid.clear();
			cityname.clear();
			dataList.clear();
			if (cityCursor.moveToFirst()) {
				do {
					String city_id = cityCursor.getString(cityCursor
							.getColumnIndex("id"));
					String city_name = cityCursor.getString(cityCursor
							.getColumnIndex("name"));
					cityid.add(city_id);
					cityname.add(city_name);
					dataList.add(city_name);
				} while (cityCursor.moveToNext());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText("选择您所在的市");
			currentLevel = LEVEL_CITY;
			listView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int index, long id) {
						queryAreas(db, cityid.get(index).toString());
				}
			});
		}else{
			Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
		}
	}

	private void queryAreas(SQLiteDatabase database, String cityid) {
		Cursor areaCursor = cityCodeDB.getArea(db, cityid);
		if (areaCursor != null) {
			areaid.clear();
			areaname.clear();
			dataList.clear();
			if (areaCursor.moveToFirst()) {
				do {
					String area_id = areaCursor.getString(areaCursor
							.getColumnIndex("id"));
					String area_name = areaCursor.getString(areaCursor
							.getColumnIndex("name"));
					areaid.add(area_id);
					areaname.add(area_name);
					dataList.add(area_name);
				} while (areaCursor.moveToNext());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText("选择您所在的地区");
			currentLevel = LEVEL_AREA;
			listView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int index, long id) {
					citycode_name = areaname.get(index).toString();
					citycode = cityCodeDB.getCityCode(db, areaid.get(index).toString());
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("citycode", citycode);
					intent.putExtra("citycode_name", citycode_name);
					startActivity(intent);
				}
			});
		}else{
			Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {
		if(currentLevel == LEVEL_AREA){
			queryProvinces();  //怎么退回到市级选择界面还需要优化
		}else if(currentLevel == LEVEL_CITY){
		queryProvinces();
		}else{
			finish();
		}
	}

}
