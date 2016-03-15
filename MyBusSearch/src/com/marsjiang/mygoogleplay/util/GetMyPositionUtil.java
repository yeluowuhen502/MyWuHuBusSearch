package com.marsjiang.mygoogleplay.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GetMyPositionUtil {
	private LocationManager mLocationManager;
	private LocationListener mListener;
	Context context;
	
	long minTime = 10 * 1000;
	float minDistance = 10;
	
	private double latitude;
	private double longitude;
	
	public GetMyPositionUtil(Context context) {
		this.context = context;
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		mListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// 定位方式状态发生变化时调用
			}

			@Override
			public void onProviderEnabled(String provider) {
				// 定位方式可用的时候调用
				Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				location.getSpeed(); // 速度
				location.getBearing(); // 轴向, 手机Y轴和地磁之间的角度
			//	System.out.println("纬度: " + latitude + " 经度:" + longitude);
			//	mtv.setText("纬度: " + latitude + " 经度:" + longitude);
				
			}

			@Override
			public void onProviderDisabled(String provider) {
				// 定位方式不可用的时候调用
			}

			@Override
			public void onLocationChanged(Location location) {
				// 位置信息发生变化时调用, location 表示当前的位置信息
				latitude = location.getLatitude(); // 纬度
				longitude = location.getLongitude(); // 经度
				location.getSpeed(); // 速度
				location.getBearing(); // 轴向, 手机Y轴和地磁之间的角度
				System.out.println("纬度: " + latitude + " 经度:" + longitude);
				//mtv.setText("纬度: " + latitude + " 经度:" + longitude);
			}
		
		};
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime, minDistance, mListener);
	}

	public double getLatitude() {
		System.out.println(latitude+"---------------");
		return latitude;
	}

	public double getlongtitude() {
		mLocationManager.removeUpdates(mListener);
		return longitude;
	}
}
