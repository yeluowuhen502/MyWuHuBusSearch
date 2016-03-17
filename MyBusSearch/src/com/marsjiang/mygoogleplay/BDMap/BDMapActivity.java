package com.marsjiang.mygoogleplay.BDMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.marsjiang.mygoogleplay.R;

public class BDMapActivity extends Activity {

	private MyBroadCast mybroadcast;
	private MapView mapView;
	private BaiduMap baiduMap;
	private LatLng hmPos;
	private double latitude;
	private double longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSDK();
		setContentView(R.layout.activity_bdmap);

		mapView = (MapView) findViewById(R.id.test_view);
		baiduMap = mapView.getMap();

		MapStatusUpdate zoom = MapStatusUpdateFactory.zoomTo(15);
		baiduMap.setMapStatus(zoom);

		longitude = Double.parseDouble(getIntent().getStringExtra("latitude"));
		latitude = Double.parseDouble(getIntent().getStringExtra("longitude"));

		//System.out.println("latitude" + latitude + "longitude" + longitude);

		hmPos = new LatLng(latitude, longitude);
		draw();
	}

	private void draw() {

		MarkerOptions markeroptions = new MarkerOptions();
		BitmapDescriptor iconw = BitmapDescriptorFactory
				.fromResource(R.drawable.ic_poi);

		markeroptions.position(hmPos).title("站点信息").icon(iconw);

		LatLng latlog = new LatLng(latitude, longitude);
		MapStatusUpdate center = MapStatusUpdateFactory.newLatLng(latlog);
		baiduMap.setMapStatus(center);

		baiduMap.addOverlay(markeroptions);

	}

	private void initSDK() {

		SDKInitializer.initialize(getApplicationContext());

		IntentFilter filter = new IntentFilter();
		filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		mybroadcast = new MyBroadCast();

		registerReceiver(mybroadcast, filter);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mybroadcast);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mapView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mapView.onPause();
		super.onPause();
	}

	class MyBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(getApplicationContext(), "wangluocuowu", 0)
						.show();
			} else if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Toast.makeText(getApplicationContext(), BDMapActivity.this.getPackageName(), 0).show();
				BDMapActivity.this.getPackageName();
			}
		}

	}
}
