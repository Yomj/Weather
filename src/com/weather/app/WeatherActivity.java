package com.weather.app;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.TextView;

//简易版，仿制《第一行代码》酷欧天气

public class WeatherActivity extends Activity {
	
	private TextView cityNameText,publishText,weatherText,temp1Text,temp2Text;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_weather);
		cityNameText = (TextView) findViewById(R.id.area_name);
		publishText = (TextView) findViewById(R.id.publish_time);
		weatherText = (TextView) findViewById(R.id.weather);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		String cityCode = getIntent().getStringExtra("citycode");
		queryWeather(cityCode);
		showWeather();
	}
	private void queryWeather(String cityCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + cityCode + ".html";
		queryFromServer(address, "cityCode");
	}
	
	private void queryFromServer(String address, String string) {
		sendHttpRequeset(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				handleWeatherResponse(WeatherActivity.this, response);
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
	
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp2", ""));
		temp2Text.setText(prefs.getString("temp1", ""));
		weatherText.setText(prefs.getString("weather", ""));
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}
	
	public static void sendHttpRequeset(final String address,
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
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
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
	public static void handleWeatherResponse(Context context, String response){
		try{
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String cityId = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weather = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, cityId, temp1, temp2, weather, publishTime);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private static void saveWeatherInfo(Context context, String cityName,
			String cityId, String temp1, String temp2, String weather,
			String publishTime) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("city_id", cityId);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather", weather);
		editor.putString("publish_time", publishTime);
		editor.commit();
	}
	

}
