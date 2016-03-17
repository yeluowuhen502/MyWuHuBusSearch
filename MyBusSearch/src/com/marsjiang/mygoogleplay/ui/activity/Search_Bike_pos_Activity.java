package com.marsjiang.mygoogleplay.ui.activity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.marsjiang.mygoogleplay.R;
import com.marsjiang.mygoogleplay.bean.GetBike_bean_item;
import com.marsjiang.mygoogleplay.bean.Get_Bike_bean;
import com.marsjiang.mygoogleplay.bean.Send_Bike_Bean;
import com.marsjiang.mygoogleplay.util.ToastUtil;

public class Search_Bike_pos_Activity extends Activity {

	private LocationManager mLocationManager;
	private LocationListener mListener;
	private TextView mtv;

///	ArrayList<ArrayList<GetBike_bean_item>> getBikeBeanList;

	Send_Bike_Bean send_bike_bean = new Send_Bike_Bean();
	Get_Bike_bean get_bikebean = new Get_Bike_bean();
	// 用来解析json数据
	Gson gson = new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_bike_pos);
		mtv = (TextView) findViewById(R.id.tv_GPSTest);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// getSystemService - XxxManager - aidl - 底层服务
		// IPC - 进程间通信 inter-process communication, aidl/Binder, Socket
		// 获取所有的定位方式
		List<String> allProviders = mLocationManager.getAllProviders();
		for (String string : allProviders) {
			//System.out.println(string);
		}
		// 请求位置服务, 获取位置信息
		// 第一个参数: 定位方式
		// 第二个参数: minTime, 获取位置信息的最小时间间隔
		// 第三个参数: minDistance, 获取位置信息的最小距离
		long minTime = 10 * 1000;
		float minDistance = 10;
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
			//	System.out.println("纬度: " + latitude + " 经度:" + longitude);
				mtv.setText("纬度: " + latitude + " 经度:" + longitude);
				
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
			//	System.out.println("纬度: " + latitude + " 经度:" + longitude);
				mtv.setText("纬度: " + latitude + " 经度:" + longitude);
				getDataFromServer(latitude, longitude);
			}
		};
		// mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// minTime, minDistance,
		// mListener);
	}

	protected void getDataFromServer(double latitude,double longitude) {
		
		final double latitude_00 = latitude;
		final double longitude_00 = longitude;
		
		Toast.makeText(getApplicationContext(), "纬度: " + latitude + " 经度:" + longitude, 0).show();
    	// {"cmd":"getBicycleRental","params":{"lng":"116.296482","lat":"40.0493","searchKey":""}}
    	
		
		send_bike_bean.cmd = "getBicycleRental";
		send_bike_bean.params = send_bike_bean.new Params();
		send_bike_bean.params.lat = latitude+"";
		send_bike_bean.params.lng = longitude+"";
		send_bike_bean.params.searchKey="";
		//设置传输参数。​
		RequestParams params = new RequestParams("UTF-8");
		
		try{
		/*	params.setBodyEntity(new StringEntity(gson.toJson("要转成json的对象"),"UTF-8"));
​					params.setContentType("applicatin/json");*/
			params.setBodyEntity(new StringEntity(gson.toJson(send_bike_bean),"UTF-8"));
			//System.out.println(gson.toJson(send_bike_bean));
			params.setContentType("applicatin/json");
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
	//	System.out.println("fangwen");
		//访问网络获取数据
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, "http://220.180.139.42:8980/SmartBusServer/Main", params, new RequestCallBack<String>() {

			@Override
			public void onFailure(com.lidroid.xutils.exception.HttpException arg0, String arg1) {	
			//	System.out.println("failed");
				ToastUtil.showToast(getApplicationContext(), "请检查网络连接！");}

			@Override
			public void onSuccess(ResponseInfo<String> string) {

				
				if(string!=null){
					//System.out.println(string.result);
				//	get_bikebean = gson.fromJson(string.result,Get_Bike_bean.class);
					//mHandler.sendEmptyMessage(0);
				//	System.out.println(get_bikebean.result.list.size());
					
					try {
						JSONObject jsonObj = new JSONObject(string.result);
						JSONObject result = jsonObj.getJSONObject("result");
						JSONObject list = result.getJSONObject("list");
						JSONArray data = list.getJSONArray("data");
						//System.out.println("数目："+data.length());
						
						//System.out.println(data.getJSONArray(0).get(0)+""+data.getJSONArray(0).get(1));
						final ArrayList<GetBike_bean_item> buslist = new ArrayList<GetBike_bean_item>();
						for(int i = 0 ;i < data.length(); i++){
							GetBike_bean_item getbike_bean_item =new GetBike_bean_item();
							getbike_bean_item.pos_id = data.getJSONArray(i).get(0)+"";
							getbike_bean_item.pos = (String) data.getJSONArray(i).get(1);
							getbike_bean_item.blank = (String) data.getJSONArray(i).get(2);
							
							getbike_bean_item.bike_avail = Integer.parseInt(data.getJSONArray(i).get(3)+"");
							getbike_bean_item.bike_no_avail = Integer.parseInt(data.getJSONArray(i).get(4)+"");
							
							getbike_bean_item.blank2 = (String) data.getJSONArray(i).get(5);
							getbike_bean_item.weidu = (String) data.getJSONArray(i).get(6);
							getbike_bean_item.jingdu = (String) data.getJSONArray(i).get(7);
							getbike_bean_item.diatance = (String) data.getJSONArray(i).get(8);
							
							buslist.add(getbike_bean_item);
						}
						
						new Handler().postDelayed(new Runnable() {
							
							@Override
							public void run() {Intent intent = new Intent(Search_Bike_pos_Activity.this,Search_Bike_View_Activity.class);
							intent.putExtra("BikeList", (Serializable)buslist);
							
							//double latitude,double longitude 
							intent.putExtra("latitude", latitude_00);
							intent.putExtra("longitude", longitude_00);
							
							startActivity(intent);
							finish();
							}
						}, 100);
						
						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}else{
					ToastUtil.showToast(getApplicationContext(), "请输入正确格式的数据");
					//System.out.println("请输入正确格式的数据");
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
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime, minDistance, mListener);
	}

	// 在页面不可见时停止获取位置信息
	@Override
	protected void onStop() {
		super.onStop();
		mLocationManager.removeUpdates(mListener);
	}
}
