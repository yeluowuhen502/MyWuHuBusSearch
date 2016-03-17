package com.marsjiang.mygoogleplay.ui.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.marsjiang.mygoogleplay.R;
import com.marsjiang.mygoogleplay.BDMap.BDMapActivity;
import com.marsjiang.mygoogleplay.bean.GetBike_bean_item;
import com.marsjiang.mygoogleplay.bean.Send_Bike_Bean;
import com.marsjiang.mygoogleplay.ui.activity.IC_Card_Pos_view_Activity.MyBroadCast;
import com.marsjiang.mygoogleplay.util.ToastUtil;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class Search_Bike_View_Activity extends ActionBarActivity {
	private EditText et_bike_pos;
	private ListView lv_bike;
	private ArrayList<GetBike_bean_item> buslist;
	private Send_Bike_Bean send_bike_bean;
	private MyBikeAdapter myBikeAdapter;
	private SwipeRefreshLayout swiperefreshlayout;
	private MyBroadCast mybroadcast;
	
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			swiperefreshlayout.setRefreshing(false);
			myBikeAdapter.notifyDataSetChanged();
		};
	};
	private ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSDK();
		setContentView(R.layout.activity_bike);
		
		setActionBar();
		
		buslist = (ArrayList<GetBike_bean_item>) getIntent().getSerializableExtra("BikeList");
		final double latitude = getIntent().getDoubleExtra("latitude", 0);
		final double longitude = getIntent().getDoubleExtra("longitude", 0);
		
		et_bike_pos = (EditText) findViewById(R.id.et_bike_pos);
		
		swiperefreshlayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_bike);
		swiperefreshlayout.setColorScheme(android.R.color.holo_purple,android.R.color.holo_green_light, android.R.color.holo_blue_bright,  android.R.color.holo_orange_light);
		
		swiperefreshlayout.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
			//	GetMyPositionUtil getmypositionutil = new GetMyPositionUtil(getApplication());
				
			//	double latitude2 = getmypositionutil.getLatitude();
			//	double longitude2 = getmypositionutil.getlongtitude();
				
				String text_in = et_bike_pos.getText().toString().trim();
				//System.out.println("纬度: " + latitude2 + " 经度:" + longitude2);
				
				getDataFromServer(text_in,latitude,longitude);
				handler.sendEmptyMessage(0);
			}
		});
		
		et_bike_pos.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String text_in = et_bike_pos.getText().toString().trim();
				swiperefreshlayout.setRefreshing(true);
				getDataFromServer(text_in,latitude,longitude);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		

		for (GetBike_bean_item getBike_bean_item : buslist) {
		//	System.out.println(getBike_bean_item);
			// System.out.println("tes");
		}

		System.out.println();

		lv_bike = (ListView) findViewById(R.id.bike_lv);
		myBikeAdapter = new MyBikeAdapter();
		lv_bike.setAdapter(myBikeAdapter);
		lv_bike.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2   ,long arg3) {
				GetBike_bean_item getbike_bean_item = buslist.get(arg2);
				Intent intent = new Intent();
				//初始坐标
				intent.putExtra("latitude_00", latitude+"");
				intent.putExtra("longitude_00", longitude+"");
				//条目的坐标
				intent.putExtra("latitude",getbike_bean_item.jingdu);
				intent.putExtra("longitude",getbike_bean_item.weidu);
				intent.setClass(Search_Bike_View_Activity.this,RoutePlanActivity.class);
				startActivity(intent);
			}
		});
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
	class MyBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(getApplicationContext(), "wangluocuowu", 0)
						.show();
			} else if (action
					.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Toast.makeText(getApplicationContext(), "quanxianshibai", 0)
						.show();
			}
		}

	}

	
	private void setActionBar() {

		actionBar = getSupportActionBar();
		actionBar.setIcon(R.drawable.ic_launcher);

		// 显示Home按钮并且使之可以被点击
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}
	
	class MyBikeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return buslist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return buslist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GetBike_bean_item getbike_bean_item_temp = buslist.get(position);

			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(),
						R.layout.bike_items, null);
				viewHolder.tv_pos_name = (TextView) convertView
						.findViewById(R.id.tv_pos_name);
				viewHolder.tv_pos_meters = (TextView) convertView
						.findViewById(R.id.tv_pos_meters);
				viewHolder.tv_bike_avail = (TextView) convertView
						.findViewById(R.id.tv_bike_avail);
				viewHolder.tv_bike_no_avail = (TextView) convertView
						.findViewById(R.id.tv_bike_no_avail);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.tv_pos_name.setText(getbike_bean_item_temp.pos);
			viewHolder.tv_pos_meters.setText(getbike_bean_item_temp.diatance);
			viewHolder.tv_bike_avail.setText("可用车辆"
					+ getbike_bean_item_temp.bike_avail + "");
			viewHolder.tv_bike_no_avail.setText("不可用车辆"
					+ getbike_bean_item_temp.bike_no_avail + "");
			ViewHelper.setScaleX(convertView, 0.5f);
			ViewHelper.setScaleY(convertView, 0.5f);
			
			ViewPropertyAnimator.animate(convertView).scaleX(1.0f).setDuration(500).setInterpolator(new OvershootInterpolator()).start();
			ViewPropertyAnimator.animate(convertView).scaleY(1.0f).setDuration(500).setInterpolator(new OvershootInterpolator()).start();
			return convertView;
		}

	}

	class ViewHolder {
		TextView tv_pos_name;
		TextView tv_pos_meters;
		TextView tv_bike_avail;
		TextView tv_bike_no_avail;

	}

	void getDataFromServer(String text_in,double latitude,double longitude) {

		Toast.makeText(getApplicationContext(), "纬度: " + latitude + " 经度:" + longitude, 0).show();
    	// {"cmd":"getBicycleRental","params":{"lng":"116.296482","lat":"40.0493","searchKey":""}}
    	
		send_bike_bean = new Send_Bike_Bean();
		
		send_bike_bean.cmd = "getBicycleRental";
		send_bike_bean.params = send_bike_bean.new Params();
		send_bike_bean.params.lat = latitude+"";
		send_bike_bean.params.lng = longitude+"";
		send_bike_bean.params.searchKey=text_in;
		//设置传输参数。​
		RequestParams params = new RequestParams("UTF-8");
		
		try{
		Gson gson = new Gson();
			/*	params.setBodyEntity(new StringEntity(gson.toJson("要转成json的对象"),"UTF-8"));
​					params.setContentType("applicatin/json");*/
			params.setBodyEntity(new StringEntity(gson .toJson(send_bike_bean),"UTF-8"));
		//	System.out.println(gson.toJson(send_bike_bean));
			params.setContentType("applicatin/json");
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		//System.out.println("fangwen");
		//访问网络获取数据
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, "http://220.180.139.42:8980/SmartBusServer/Main", params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
			//	System.out.println("failed");
				ToastUtil.showToast(getApplicationContext(), "请检查网络连接！");
			}

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
						System.out.println("数目："+data.length());
						
						buslist.removeAll(buslist);
						
						//System.out.println(data.getJSONArray(0).get(0)+""+data.getJSONArray(0).get(1));
						//final ArrayList<GetBike_bean_item> buslist = new ArrayList<GetBike_bean_item>();
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
						
						handler.sendEmptyMessage(0);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}else{
					ToastUtil.showToast(getApplicationContext(), "请输入正确格式的数据");
					//System.out.println("请输入正确格式的数据");
				}//
				
			}
		});
		
	
	}

}
