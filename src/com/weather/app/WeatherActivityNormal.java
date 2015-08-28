package com.weather.app;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class WeatherActivityNormal extends Activity {
	
	private TextView cityNameT;
	private TextView weatherT;
	private TextView windDT;
	private TextView windPT;
	private TextView tempT;
	private TextView day1T;
	private TextView day2T;
	private TextView day3T;
	private TextView day4T;
	private TextView weather1T;
	private TextView weather2T;
	private TextView weather3T;
	private TextView weather4T;
	private TextView coldT;
	
	private Button choose;
	private Button refresh;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_activity_normal);
		cityNameT = (TextView) findViewById(R.id.area_name);
		weatherT = (TextView) findViewById(R.id.weather);
		windDT = (TextView) findViewById(R.id.wind_direction);
		windPT = (TextView) findViewById(R.id.wind_power);
		tempT = (TextView) findViewById(R.id.temp);
		day1T= (TextView) findViewById(R.id.day1);
		day2T = (TextView) findViewById(R.id.day2);
		day3T = (TextView) findViewById(R.id.day3);
		day4T = (TextView) findViewById(R.id.day4);
		weather1T = (TextView) findViewById(R.id.weather1);
		weather2T = (TextView) findViewById(R.id.weather2);
		weather3T = (TextView) findViewById(R.id.weather3);
		weather4T = (TextView) findViewById(R.id.weather4);
		coldT = (TextView) findViewById(R.id.cold);
		
		choose = (Button) findViewById(R.id.choose);
		choose.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WeatherActivityNormal.this, ChooseAreaActivity.class);
				startActivity(intent);
			}
		});
		
		refresh = (Button) findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String cityCode = getIntent().getStringExtra("citycode");
				queryWeather(cityCode);
			}
			
		});
		
		String cityCode = getIntent().getStringExtra("citycode");
		queryWeather(cityCode);
		showWeather();
	}

	private void queryWeather(String cityCode) {
		String address = "http://wthrcdn.etouch.cn/weather_mini?citykey=" + cityCode;
		queryFromServer(address, "cityCode");
	}

	private void queryFromServer(String address, String string) {
		sendHttpRequeset(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				handleWeatherResponse(WeatherActivityNormal.this, response);
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						showWeather();
					}
				});
			}

			@Override
			public void onError(Exception e) {
				
			}
		});
	}

	public static void handleWeatherResponse(
			Context context, String response) {
		try{
			JSONObject jsonObject = new JSONObject(response);
			JSONObject data = jsonObject.getJSONObject("data");
			String temp = data.getString("wendu");
			Log.d("WeatherActivityNormal", temp);
			String cold = data.getString("ganmao");
			String cityName = data.getString("city");
			JSONArray forecast =data.getJSONArray("forecast");
			JSONObject jsonObjectF = forecast.getJSONObject(0);
			String weather = jsonObjectF.getString("type");
			String windD = jsonObjectF.getString("fengxiang");
			String windP = jsonObjectF.getString("fengli");
			JSONObject jsonObjectF1 = forecast.getJSONObject(1);
			String weather1 = jsonObjectF1.getString("type");
			String day1 = jsonObjectF1.getString("date");
			JSONObject jsonObjectF2 = forecast.getJSONObject(2);
			String day2 = jsonObjectF2.getString("date");
			String weather2 = jsonObjectF2.getString("type");
			JSONObject jsonObjectF3 = forecast.getJSONObject(3);
			String day3 = jsonObjectF3.getString("date");
			String weather3 = jsonObjectF3.getString("type");
			JSONObject jsonObjectF4 = forecast.getJSONObject(4);
			String day4 = jsonObjectF4.getString("date");
			String weather4 = jsonObjectF4.getString("type");
			saveWeatherInfo(context, cityName, weather, windD, windP, temp, day1, day2, day3, day4, weather1, weather2, weather3, weather4, cold);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameT.setText(prefs.getString("city_name", ""));
		weatherT.setText(prefs.getString("weather", ""));
		windDT.setText(prefs.getString("wind_d", ""));
		windPT.setText(prefs.getString("wind_p", ""));
		tempT.setText(prefs.getString("temp", "") + "¡æ");
		day1T.setText(prefs.getString("day1", ""));
		day2T.setText(prefs.getString("day2", ""));
		day3T.setText(prefs.getString("day3", ""));
		day4T.setText(prefs.getString("day4", ""));
		weather1T.setText(prefs.getString("weather1", ""));
		weather2T.setText(prefs.getString("weather2", ""));
		weather3T.setText(prefs.getString("weather3", ""));
		weather4T.setText(prefs.getString("weather4", ""));
		coldT.setText(prefs.getString("cold", ""));
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}

	public static void saveWeatherInfo(Context context,
			String cityName, String weather, String windD,
			String windP, String temp, String day1, String day2,
			String day3, String day4, String weather1,
			String weather2, String weather3, String weather4,
			String cold) {
		SimpleDateFormat sdf = new SimpleDateFormat("MÔÂ", Locale.CHINA);
		String month = sdf.format(new Date());
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather", weather);
		editor.putString("wind_d", windD);
		editor.putString("wind_p", windP);
		editor.putString("temp", temp);
		editor.putString("day1",month + day1);
		editor.putString("day2",month + day2);
		editor.putString("day3",month + day3);
		editor.putString("day4",month + day4);
		editor.putString("weather1",weather1);
		editor.putString("weather2",weather2);
		editor.putString("weather3",weather3);
		editor.putString("weather4",weather4);
		editor.putString("cold", cold);
		editor.commit();
	}

	private void sendHttpRequeset(final String address,
			final HttpCallbackListener listener) {
		new Thread(new Runnable(){

			@Override
			public void run() {
				HttpURLConnection connection = null;
				try{
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					if(listener != null){
						listener.onFinish(response.toString());
					}
				}catch(Exception e){
					if(listener != null){
						listener.onError(e);
					}
				}finally{
					if(connection != null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}
	

	
	
}
