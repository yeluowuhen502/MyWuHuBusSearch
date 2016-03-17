package com.marsjiang.mygoogleplay.ui.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.marsjiang.mygoogleplay.bean.DownLineStationList;
import com.marsjiang.mygoogleplay.bean.GetBusStationInfoBean;
import com.marsjiang.mygoogleplay.bean.SendBusNumBean;
import com.marsjiang.mygoogleplay.bean.SendBusStationInfoBean;
import com.marsjiang.mygoogleplay.bean.UpLineStationList;
import com.marsjiang.mygoogleplay.ui.view.PagerSlidingTab;
import com.marsjiang.mygoogleplay.util.ToastUtil;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class Bus_Line_Show_Activity extends ActionBarActivity {
	private String bus_line;
	private SendBusNumBean sendbusnumbean;
	private DrawerLayout drawerLayout;

	private ViewPager viewpager;
	private PagerSlidingTab slidingTab;

	private String downLine;
	private String lineName;
	private String startendTime;
	private String upLine;
	private ArrayList<UpLineStationList> uplinestationlist;
	private ArrayList<DownLineStationList> downlinestationlist;
	private List<View> viewContainer;
	private ActionBar actionBar;
	
	private UPBaseAdapterExtension upbaseadapterextension;
	private DownBaseAdapterExtension downbaseadapterextension;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_line_show);
		ToastUtil.showToast(getApplicationContext(), "请点击下面的站点来获取具体的信息");
		initView();

		getDataFromServer();

	}

	private void initView() {
		// TODO Auto-generated method stub
		setActionBar();
		viewpager = (ViewPager) findViewById(R.id.viewPager_bus_line);
		slidingTab = (PagerSlidingTab) findViewById(R.id.slidingTab_bus_line);
		//slidingTab.setShouldExpand(true);
	}

	private void setActionBar() {
		actionBar = getSupportActionBar();
		actionBar.setIcon(R.drawable.ic_launcher);

		// 显示Home按钮并且使之可以被点击
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		bus_line = getIntent().getStringExtra("bus_line");

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}

	private void getDataFromServer() {
		sendbusnumbean = new SendBusNumBean();
		sendbusnumbean.cmd = "lineDetail";
		sendbusnumbean.params = sendbusnumbean.new Params();
		sendbusnumbean.params.lineName = bus_line;

		RequestParams params = new RequestParams("UTF-8");
		Gson gson = new Gson();

		try {
			params.setBodyEntity(new StringEntity(gson.toJson(sendbusnumbean),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(gson.toJson(send_bike_bean));
		params.setContentType("applicatin/json");

		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST,"http://220.180.139.42:8980/SmartBusServer/Main", params,new RequestCallBack<String>() {

					private TextView tv_coming_bus01;
					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> resultInfo) {
						 System.out.println(resultInfo.result);
						JSONObject jsonObj;
						try {
							jsonObj = new JSONObject(resultInfo.result);
							JSONObject result = jsonObj.getJSONObject("result");
							downLine = result.getString("downLine");
							lineName = result.getString("lineName");
							startendTime = result.getString("startendTime");
							upLine = result.getString("upLine");

							JSONArray upLineStationList_00 = result.getJSONArray("upLineStationList");
							JSONArray downLineStationList_00 = result.getJSONArray("downLineStationList");

							uplinestationlist = new ArrayList<UpLineStationList>();
							downlinestationlist = new ArrayList<DownLineStationList>();

							for (int i = 0; i < upLineStationList_00.length(); i++) {
								UpLineStationList temp00 = new UpLineStationList();
								temp00.num00 = upLineStationList_00.getJSONArray(i).get(0) + "";
								temp00.stationName = upLineStationList_00.getJSONArray(i).get(1) + "";
								temp00.num01 = Integer.parseInt(upLineStationList_00.getJSONArray(i).get(2) + "");
								uplinestationlist.add(temp00);
							}
							for (int i = 0; i < downLineStationList_00.length(); i++) {
								DownLineStationList temp01 = new DownLineStationList();temp01.num00 = downLineStationList_00.getJSONArray(i).get(0) + "";
								temp01.stationName = downLineStationList_00.getJSONArray(i).get(1) + "";
								temp01.num01 = Integer.parseInt(downLineStationList_00.getJSONArray(i).get(2) + "");
								downlinestationlist.add(temp01);
							}
							
							System.out.println("uplinestationlist"+upLineStationList_00.length()+"downlinestationlist"+downLineStationList_00.length());
							
							
							//向ViewPager中填充的界面布局：
							viewContainer = new ArrayList<View>();
							
							View view_00 = View.inflate(getApplicationContext(), R.layout.bus_line_state, null);
							TextView tv_time2time = (TextView) view_00.findViewById(R.id.tv_time2time);
							final TextView tv_coming_bus = (TextView) view_00.findViewById(R.id.tv_coming_bus);
							ListView lv_comingbus = (ListView) view_00.findViewById(R.id.lv_bus_Line);
							
							tv_time2time.setText("运营时间："+startendTime);
							tv_coming_bus.setText("请点击下面的条目获取状态");
							
							downbaseadapterextension = new DownBaseAdapterExtension();
							lv_comingbus.setAdapter(downbaseadapterextension);//lv_comingbus.setAdapter
							
							//项目的点击事件
							lv_comingbus.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,View arg1, int arg2, long arg3) {
									final int currentup = arg2;
									//final UpLineStationList upLineStationList2 = uplinestationlist.get(arg2);
									final DownLineStationList upLineStationList2 = downlinestationlist.get(arg2);
									ToastUtil.showToast(getApplicationContext(), upLineStationList2.stationName);
									
									final SendBusStationInfoBean sendbusstationinfobean = new SendBusStationInfoBean();
									sendbusstationinfobean.cmd = "getArriveInfo";
									sendbusstationinfobean.params = sendbusstationinfobean.new Params();
									sendbusstationinfobean.params.lineName=lineName;
									sendbusstationinfobean.params.stationId = upLineStationList2.num00;
									sendbusstationinfobean.params.type = 2;
									//用来解析json数据
									final Gson gson = new Gson();
									
									//设置传输参数。​
									RequestParams params = new RequestParams("UTF-8");
									
									try{
										params.setBodyEntity(new StringEntity(gson.toJson(sendbusstationinfobean),"UTF-8"));
										System.out.println(gson.toJson(sendbusstationinfobean));
										params.setContentType("applicatin/json");
									} catch (UnsupportedEncodingException e){
										e.printStackTrace();
									}
									
									//访问网络获取数据
									HttpUtils httpUtils = new HttpUtils();
									httpUtils.send(HttpMethod.POST, "http://220.180.139.42:8980/SmartBusServer/Main", params, new RequestCallBack<String>() {

										@Override
										public void onFailure(HttpException arg0, String arg1) {
											System.out.println("failed");
											ToastUtil.showToast(getApplicationContext(), "请检查网络连接！");
										}

										@Override
										public void onSuccess(ResponseInfo<String> string) {
											System.out.println(string.result+"------------------------------");
											if(string!=null){
												//System.out.println(string.result);
												//转换json对象
												Gson gson2 = new Gson();
												//这里需要处理返回空数据的情况
												if("{\"result".equals(string.result.toString().substring(0,8))){
													GetBusStationInfoBean getbusstationinfobean = gson2.fromJson(string.result, GetBusStationInfoBean.class); 
													//System.out.println("getbusstationinfobean"+getbusstationinfobean.result.distance);
													tv_coming_bus.setText("距离"+upLineStationList2.stationName+"还有"+getbusstationinfobean.result.willArriveTime+getbusstationinfobean.result.distance);
													for(int i=0;i<downlinestationlist.size();i++){
														downlinestationlist.get(i).isComing = 0;
													}
													for(int i=0;i<downlinestationlist.size();i++){
														downlinestationlist.get(i).ischecked = 0;
													}
													
													downlinestationlist.get(currentup).ischecked = 1;
													
													int isComing = currentup - Integer.parseInt(getbusstationinfobean.result.willArriveTime.substring(0,1));
													if(isComing<0){

													}else{
													//	Toast.makeText(getApplicationContext(), "--------"+currentup+"']]]]]]]]]]]]]]]]"+Integer.parseInt(getbusstationinfobean.result.willArriveTime.substring(0,1)),0).show();
													//downlinestationlist.get(currentup - Integer.parseInt(getbusstationinfobean.result.willArriveTime.substring(0,1))).isComing=1;
														String str = getbusstationinfobean.result.willArriveTime.substring(0,1);
														str = str.replaceAll("站", "");
														downlinestationlist.get(currentup - Integer.parseInt(str)).isComing=1;
													}
													downbaseadapterextension.notifyDataSetChanged();
													
												}else {
													tv_coming_bus.setText("当前暂时没有车辆信息");
												}
											}else{
												ToastUtil.showToast(getApplicationContext(), "请输入正确格式的数据");
												//System.out.println("请输入正确格式的数据");
											}
											
										}
									});
									
								}
							});
							
							viewContainer.add(view_00);
							
							View view_01 = View.inflate(getApplicationContext(), R.layout.bus_line_state, null);
							TextView tv_time2time01 = (TextView) view_01.findViewById(R.id.tv_time2time);
							tv_coming_bus01 = (TextView) view_01.findViewById(R.id.tv_coming_bus);
							ListView lv_comingbus01 = (ListView) view_01.findViewById(R.id.lv_bus_Line);
							
							tv_time2time01.setText("运营时间："+startendTime);
							tv_coming_bus01.setText("请点击下面的条目获取状态");
							upbaseadapterextension = new UPBaseAdapterExtension();
							lv_comingbus01.setAdapter(upbaseadapterextension);//lv_comingbus01.setAdapter
							
							lv_comingbus01.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,View arg1, int arg2, long arg3) {
								//	final DownLineStationList downLineStationList2 = downlinestationlist.get(arg2);
									
									final UpLineStationList downLineStationList2 = uplinestationlist.get(arg2);
									ToastUtil.showToast(getApplicationContext(), downLineStationList2.stationName);
									final int current_01 = arg2;
									final SendBusStationInfoBean sendbusstationinfobean = new SendBusStationInfoBean();
									sendbusstationinfobean.cmd = "getArriveInfo";
									sendbusstationinfobean.params = sendbusstationinfobean.new Params();
									sendbusstationinfobean.params.lineName=lineName;
									sendbusstationinfobean.params.stationId = downLineStationList2.num00;
									sendbusstationinfobean.params.type = 1;
									//用来解析json数据
									final Gson gson = new Gson();
									
									//设置传输参数。​
									RequestParams params = new RequestParams("UTF-8");
									
									try{
									/*	params.setBodyEntity(new StringEntity(gson.toJson("要转成json的对象"),"UTF-8"));
							​					params.setContentType("applicatin/json");*/
										params.setBodyEntity(new StringEntity(gson.toJson(sendbusstationinfobean),"UTF-8"));
										//System.out.println(gson.toJson(sendbusstationinfobean));
										params.setContentType("applicatin/json");
									} catch (UnsupportedEncodingException e){
										e.printStackTrace();
									}
									
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
											//	System.out.println(string.result);
												//转换成json对象：
												Gson gson2 = new Gson();
												//这里需要处理返回空数据的情况
												if("{\"result".equals(string.result.toString().substring(0,8))){
													GetBusStationInfoBean getbusstationinfobean = gson2.fromJson(string.result, GetBusStationInfoBean.class); 
												//	System.out.println("getbusstationinfobean"+getbusstationinfobean.result.distance);
													tv_coming_bus01.setText("距离"+downLineStationList2.stationName+"还有"+getbusstationinfobean.result.willArriveTime+getbusstationinfobean.result.distance);
													
													for(int i=0;i<uplinestationlist.size();i++){
														uplinestationlist.get(i).isComing = 0;
													}
													for(int i=0;i<uplinestationlist.size();i++){
														uplinestationlist.get(i).ischecked = 0;
													}
													
													uplinestationlist.get(current_01).ischecked = 1;
													
													int isComing = current_01 - Integer.parseInt(getbusstationinfobean.result.willArriveTime.substring(0,1));
													if(isComing<0){

													}else{
														//Toast.makeText(getApplicationContext(), "--------"+current_01+"']]]]]]]]]]]]]]]]"+Integer.parseInt(getbusstationinfobean.result.willArriveTime),0).show();
													    String str = getbusstationinfobean.result.willArriveTime;
													    str = str.replaceAll("站", "");
													    //System.out.println("站"+"]]]]]]]]]"+str);
														//uplinestationlist.get(current_01 - Integer.parseInt(getbusstationinfobean.result.willArriveTime.substring(0,1))).isComing=1;
													    uplinestationlist.get(current_01 - Integer.parseInt(str)).isComing=1;
													}
													upbaseadapterextension.notifyDataSetChanged();
													
												}else {
													tv_coming_bus01.setText("当前暂时没有车辆信息");
												}
											}else{
												ToastUtil.showToast(getApplicationContext(), "请输入正确格式的数据");
											//	System.out.println("请输入正确格式的数据");
											}
											
										}
									});
									
								}
							});
							
							viewContainer.add(view_01);
							
							actionBar.setTitle(lineName);
							//slidingTab.setShouldExpand(true);
							viewpager.setAdapter(new Bus_Line_ViewPager_Adpater());
							
							slidingTab.setViewPager(viewpager);
							slidingTab.setShouldExpand(true);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				});

	}
	private final class DownBaseAdapterExtension extends BaseAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			DownLineStationList downLineStationListTemp = downlinestationlist.get(position);
		//	System.out.println(downlinestationlist.size());
			
			View view = View.inflate(getApplicationContext(), R.layout.bus_coming_info_item, null);
			TextView tv = (TextView) view.findViewById(R.id.tv_bus_coming_info_item);
			/*TextView tv = new TextView(getApplicationContext());
			tv.setText(upLineStationListTemp.stationName);*/
			ImageView iv = (ImageView) view.findViewById(R.id.iv_bus_coming_info_item);
			
			if(downLineStationListTemp.isComing == 1){
				iv.setImageResource(R.drawable.bus);
			}else{
				iv.setImageResource(R.drawable.bus_trans);
			}
			if(downLineStationListTemp.ischecked==1){
				tv.setTextColor(Color.rgb(255,106,106));
			}else{
				tv.setTextColor(Color.rgb(0,0,0));
			}
			tv.setText(downLineStationListTemp.stationName);
			
			ViewHelper.setScaleX(view, 0.5f);
			ViewHelper.setScaleY(view, 0.5f);

			ViewPropertyAnimator.animate(view).scaleX(1.0f)
					.setDuration(500)
					.setInterpolator(new OvershootInterpolator()).start();
			ViewPropertyAnimator.animate(view).scaleY(1.0f)
					.setDuration(500)
					.setInterpolator(new OvershootInterpolator()).start();
					
			return view;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return downlinestationlist.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return downlinestationlist.size();
		}
	}
	private final class UPBaseAdapterExtension extends BaseAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		//	System.out.println(uplinestationlist.size());
		//	System.out.println(position);
			UpLineStationList upLineStationListTemp = uplinestationlist.get(position);
			/*TextView tv = new TextView(getApplicationContext());
			tv.setText(upLineStationListTemp.stationName);*/
			View view2 = View.inflate(getApplicationContext(), R.layout.bus_coming_info_item, null);
			TextView tv = (TextView) view2.findViewById(R.id.tv_bus_coming_info_item);

			ImageView iv = (ImageView) view2.findViewById(R.id.iv_bus_coming_info_item);
			
			if(upLineStationListTemp.isComing == 1){
				iv.setImageResource(R.drawable.bus);
			}else{
				iv.setImageResource(R.drawable.bus_trans);
			}
			
			if(upLineStationListTemp.ischecked==1){
				tv.setTextColor(Color.rgb(255,106,106));
			}else{
				tv.setTextColor(Color.rgb(0,0,0));
			}
			tv.setText(upLineStationListTemp.stationName);
			
			ViewHelper.setScaleX(view2, 0.5f);
			ViewHelper.setScaleY(view2, 0.5f);

			ViewPropertyAnimator.animate(view2).scaleX(1.0f)
					.setDuration(500)
					.setInterpolator(new OvershootInterpolator()).start();
			ViewPropertyAnimator.animate(view2).scaleY(1.0f)
					.setDuration(500)
					.setInterpolator(new OvershootInterpolator()).start();
			
			return view2;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return uplinestationlist.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return uplinestationlist.size();
		}
	}
	class Bus_Line_ViewPager_Adpater extends PagerAdapter{
		@Override
		public CharSequence getPageTitle(int position) {
			if(position == 0){
				return "    "+downLine+"      ";
			}else{
				return "    "+upLine+"       ";
			}
				
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			container.addView(viewContainer.get(position));
			return viewContainer.get(position);
		}
		@Override
		public void destroyItem(ViewGroup container, int position,Object object) {
			// TODO Auto-generated method stub
			container.removeView((View) object);
		}
		
	}
}
