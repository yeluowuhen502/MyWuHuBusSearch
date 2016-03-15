package com.marsjiang.mygoogleplay.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.marsjiang.mygoogleplay.R;
import com.marsjiang.mygoogleplay.R.drawable;
import com.marsjiang.mygoogleplay.R.id;
import com.marsjiang.mygoogleplay.R.layout;

public class RoutePlanActivity extends ActionBarActivity {

	private ActionBar actionBar;
	private MapView mMapView;
	private RoutePlanSearch mSearch = null;
	private BaiduMap mBaidumap = null;
	private String lat_00;
	private String log_00;
	private String log_01;
	private String lat_01;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_plan);
		intent = getIntent();
		setActionBar();
		initData();
		initView();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}
	private void initView() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaidumap = mMapView.getMap();
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {

			@Override
			public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetTransitRouteResult(TransitRouteResult arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetDrivingRouteResult(DrivingRouteResult result) {

				System.out.println(result + "------" + result.error);
				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR) {
					Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();
				}
				if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
					// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
					// result.getSuggestAddrInfo()
					return;
				}
				if (result.error == SearchResult.ERRORNO.NO_ERROR) {
					DrivingRouteOverlay overlay = new DrivingRouteOverlay(
							mBaidumap);
					mBaidumap.setOnMarkerClickListener(overlay);
					overlay.setData(result.getRouteLines().get(0));
					overlay.addToMap();
					overlay.zoomToSpan();
				}

			}

			@Override
			public void onGetBikingRouteResult(BikingRouteResult arg0) {
				// TODO Auto-generated method stub

			}
		});
		System.out.println(lat_00+"---=+===================="+log_00);
		LatLng stlatlng = new LatLng(Double.parseDouble(lat_00),Double.parseDouble(log_00));
		LatLng enlatlng = new LatLng(Double.parseDouble(lat_01),Double.parseDouble(log_01));
		System.out.println(lat_01+"-------"+log_01);
		PlanNode stNode = PlanNode.withLocation(stlatlng);
		PlanNode enNode = PlanNode.withLocation(enlatlng);

		mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
		
	}

	private void initData() {
	/*	//初始坐标
		intent.putExtra("latitude_00", latitude);
		intent.putExtra("longitude", longitude);
		//条目的坐标
		intent.putExtra("latitude", ic_card_pos_bean_item.jingdu);
		intent.putExtra("longitude", ic_card_pos_bean_item.weidu);*/
		
		
		lat_00 = intent.getStringExtra("latitude_00");
		log_00 = intent.getStringExtra("longitude_00");
		
		log_01 = intent.getStringExtra("latitude");
		lat_01 = intent.getStringExtra("longitude");
		
		
	}

	private void setActionBar() {
		actionBar = getSupportActionBar();
		actionBar.setIcon(R.drawable.ic_launcher);

		// 显示Home按钮并且使之可以被点击
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

	}

}
