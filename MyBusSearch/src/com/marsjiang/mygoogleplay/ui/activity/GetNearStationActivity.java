package com.marsjiang.mygoogleplay.ui.activity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.marsjiang.mygoogleplay.R;
import com.marsjiang.mygoogleplay.bean.GetNearBusStation;
import com.marsjiang.mygoogleplay.bean.SendCurrentPosBean;
import com.marsjiang.mygoogleplay.bean.Send_Bike_Bean.Params;
import com.marsjiang.mygoogleplay.util.ToastUtil;

public class GetNearStationActivity extends Activity {
	private LocationManager mLocationManager;
	private LocationListener mListener;
	private TextView mtv;

//	Send_IC_Card_Pos_Bean send_bike_bean = new Send_IC_Card_Pos_Bean();
	SendCurrentPosBean sendcurrentposbean = new SendCurrentPosBean();
	
	// 用来解析json数据
	Gson gson = new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_near_station);
		mtv = (TextView) findViewById(R.id.tv_StationGPSTest);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		mListener = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// 定位方式状态发生变化时调用
			}

			@Override
			public void onProviderEnabled(String provider) {
				// 定位方式可用的时候调用
				Location location = mLocationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				double latitude = location.getLatitude(); // 纬度
				double longitude = location.getLongitude(); // 经度
				location.getSpeed(); // 速度
				location.getBearing(); // 轴向, 手机Y轴和地磁之间的角度
				System.out.println("纬度: " + latitude + " 经度:" + longitude);
				mtv.setText("0000纬度: " + latitude + " 经度:" + longitude);
				
				getDataFromServer(latitude, longitude);

			}

			@Override
			public void onProviderDisabled(String provider) {
				// 定位方式不可用的时候调用
			}

			@Override
			public void onLocationChanged(Location location) {
				// 位置信息发生变化时调用, location 表示当前的位置信息
				double latitude = location.getLatitude(); // 纬度
				double longitude = location.getLongitude(); // 经度
				location.getSpeed(); // 速度
				location.getBearing(); // 轴向, 手机Y轴和地磁之间的角度
				System.out.println("0000纬度: " + latitude + " 经度:" + longitude);
				mtv.setText("纬度: " + latitude + " 经度:" + longitude);
				getDataFromServer(latitude, longitude);
			}
		};
		
	}
	
	
	
	protected void getDataFromServer(double latitude, double longitude) {
		final double latitude_00 = latitude;
		final double longitude_00 = longitude;
		
		Toast.makeText(getApplicationContext(), "纬度: " + latitude + " 经度:" + longitude, 0).show();
		
		sendcurrentposbean.cmd = "getStationListNear";
		sendcurrentposbean.params = sendcurrentposbean.new Params();
		sendcurrentposbean.params.lat = latitude+"";
		sendcurrentposbean.params.lng = longitude+"";
		
		RequestParams params = new RequestParams("UTF-8");
		
		try{
			/*	params.setBodyEntity(new StringEntity(gson.toJson("要转成json的对象"),"UTF-8"));
	​					params.setContentType("applicatin/json");*/
				params.setBodyEntity(new StringEntity(gson.toJson(sendcurrentposbean),"UTF-8"));
				//System.out.println(gson.toJson(sendcurrentposbean));
				params.setContentType("applicatin/json");
			} catch (UnsupportedEncodingException e){
				e.printStackTrace();
			}
		
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, "http://220.180.139.42:8980/SmartBusServer/Main", params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
			//	System.out.println("failed");
				ToastUtil.showToast(getApplicationContext(), "请检查网络连接！");
			}

			@Override
			public void onSuccess(ResponseInfo<String> string) {
				//System.out.println(string.result);
				if(string!=null){
					JSONObject jsonObj;
					try {
						jsonObj = new JSONObject(string.result);
						JSONObject result = jsonObj.getJSONObject("result");
						JSONObject list = result.getJSONObject("list");
						JSONArray data = list.getJSONArray("data");
						//System.out.println("数目："+data.length());
						
						final ArrayList<GetNearBusStation> nearBusStationlist = new ArrayList<GetNearBusStation>();
						for(int i=0;i<data.length();i++){
							GetNearBusStation tempgetnearbusstation = new GetNearBusStation();
							tempgetnearbusstation.location = data.getJSONArray(i).getString(0);
							tempgetnearbusstation.meters = data.getJSONArray(i).getString(1);
							
							nearBusStationlist.add(tempgetnearbusstation);
						}
						new Handler().postDelayed(new Runnable() {
							
						@Override
						public void run() {
							Intent intent = new Intent(GetNearStationActivity.this,Bus_Station_Activity.class);
							intent.putExtra("NearBusStation", (Serializable)nearBusStationlist);
							finish();
							startActivity(intent);
							}
						}, 1000);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
		});
		
	}
	
	
	
	// 在页面可见时获取位置信息
	@Override
	protected void onStart() {
		super.onStart();
		long minTime = 10 * 1000;
		float minDistance = 10;
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				minTime, minDistance, mListener);
	}

	// 在页面不可见时停止获取位置信息
	@Override
	protected void onStop() {
		super.onStop();
		mLocationManager.removeUpdates(mListener);
	}
}
