package com.marsjiang.mygoogleplay.ui.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.marsjiang.mygoogleplay.R;
import com.marsjiang.mygoogleplay.bean.BusComingBean;
import com.marsjiang.mygoogleplay.bean.DownLineStationList;
import com.marsjiang.mygoogleplay.bean.GetArrivalInfo;
import com.marsjiang.mygoogleplay.bean.GetSearchStationBean;
import com.marsjiang.mygoogleplay.bean.SendBusIdBean;
import com.marsjiang.mygoogleplay.bean.SendBusNumBean;
import com.marsjiang.mygoogleplay.bean.SendBusStationBean;
import com.marsjiang.mygoogleplay.bean.UpLineStationList;
import com.marsjiang.mygoogleplay.util.ToastUtil;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class BusStation_around_show extends ActionBarActivity {
	private TextView tv_bus_station_text;
	private ListView lv_bus_station;
	//当前的站点名字
	private String stationname;
	private GetSearchStationBean getsearchstationbean;
	private MyBusAdapter myBusAdapter;
	//获取数据后的集合
	private List<GetArrivalInfo> arrivalList;
	//记录当前已经查询的数目
	private int countNum=0;
	private boolean isFirstTime = false;
	private SwipeRefreshLayout swiperefreshlayout;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:
				countNum++;
				//System.out.println(countNum);
				if(countNum == getsearchstationbean.result.list.size()*2){
					handler.sendEmptyMessage(0);	
				}
				
				System.out.println(countNum);
				break;
			case 0:
				for(int i=0;i<arrivalList.size();i++){
					System.out.println(arrivalList.get(i));
				}
				countNum = 0;
				
				lv_bus_station.setAdapter(myBusAdapter);
				
				ToastUtil.showToast(getApplicationContext(), "刷新完成^_^");
				for (GetArrivalInfo arrivall : arrivalList) {
					System.out.println("之后"+arrivall);
				}
				swiperefreshlayout.setRefreshing(false);
				//arrivalList = new ArrayList<GetArrivalInfo>();
				break;
			case 1:
				getAllLines();
				break;
				
			case 2:
				/*for (GetArrivalInfo arrivall : arrivalList) {
					System.out.println("以前"+arrivall);
				}*/
				getBus_InfoDetail();
				break;
			
			default:
				break;
			}
			
		};
	};
	//private int tempi;
	private ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_line_station_search);
		stationname = getIntent().getStringExtra("stationname");
		ToastUtil.showToast(getApplicationContext(), stationname);
		getsearchstationbean = new GetSearchStationBean();
		arrivalList = new ArrayList<GetArrivalInfo>();

		
		initData();
		initView();
	}

	private void initView() {
		setActionBar();
		tv_bus_station_text = (TextView) findViewById(R.id.tv_bus_station_text);
		lv_bus_station = (ListView) findViewById(R.id.lv_bus_station);

		tv_bus_station_text.setText(stationname + "站");
		
		myBusAdapter = new MyBusAdapter();
		//lv_bus_station.setAdapter(myBusAdapter);

		
		swiperefreshlayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
		swiperefreshlayout.setColorScheme(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
		swiperefreshlayout.setRefreshing(true);
		swiperefreshlayout.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				getDataFromServer();
				ToastUtil.showToast(getApplicationContext(), "正在刷新");
				
			}
		});
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
	
	class MyBusAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrivalList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arrivalList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if(convertView == null){
				convertView = View.inflate(getApplicationContext(), R.layout.busstationitem, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_busstationshow_item = (TextView) convertView.findViewById(R.id.tv_busstationshow_item);
				viewHolder.tv_upline_station = (TextView) convertView.findViewById(R.id.tv_upline_station);
				viewHolder.tv_downline_station = (TextView) convertView.findViewById(R.id.tv_downline_station);
				
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			//System.out.println("Viewholder"+arrivalList.get(position).busNum);
			viewHolder.tv_busstationshow_item.setText(arrivalList.get(position).busNum);
			viewHolder.tv_upline_station.setText(arrivalList.get(position).finalStation01+":"+arrivalList.get(position).stationFromHere00);
			viewHolder.tv_downline_station.setText(arrivalList.get(position).finalStation00+":"+arrivalList.get(position).stationFromHere01);
			//System.out.println(arrivalList.get(position).finalStation01+":"+arrivalList.get(position).stationFromHere00);
		/*	for(int i=0;i<arrivalList.size();i++){
				System.out.println(arrivalList.get(i));
			}*/
			
			ViewHelper.setScaleX(convertView, 0.5f);
			ViewHelper.setScaleY(convertView, 0.5f);
			
			ViewPropertyAnimator.animate(convertView).scaleX(1.0f).setDuration(500).setInterpolator(new OvershootInterpolator()).start();
			ViewPropertyAnimator.animate(convertView).scaleY(1.0f).setDuration(500).setInterpolator(new OvershootInterpolator()).start();
			
			return convertView;
		}

	}
	
	static class ViewHolder{
		TextView tv_busstationshow_item;
		TextView tv_upline_station;
		TextView tv_downline_station;
	}
	
	private void initData() {
		// TODO Auto-generated method stub
		getDataFromServer();
	}

	private void getDataFromServer() {
		if(!isFirstTime){
			arrivalList = new ArrayList<GetArrivalInfo>();
		}
		/*
		 * 请求站点信息
		 * {"cmd":"stationDetail","params":{"stationName":"莲塘新村"}} 返回的查询结果
		 * {"result"
		 * :{"stationName":"莲塘新村","lat":"31.371205","lng":"118.3703367",
		 * "list":["2535路","26路","101路","游1路"]}}
		 */
		final SendBusStationBean sendbusnumbean = new SendBusStationBean();
		sendbusnumbean.cmd = "stationDetail";
		sendbusnumbean.params = sendbusnumbean.new Params();
		sendbusnumbean.params.stationName = stationname;
		// 用来解析json数据
		final Gson gson = new Gson();

		// 设置传输参数。​
		RequestParams params = new RequestParams("UTF-8");

		try {
			/*
			 * params.setBodyEntity(new StringEntity(gson.toJson("要转成json的对象"),
			 * "UTF-8")); ​ params.setContentType("applicatin/json");
			 */
			params.setBodyEntity(new StringEntity(gson.toJson(sendbusnumbean),
					"UTF-8"));
			//System.out.println(gson.toJson(sendbusnumbean));
			params.setContentType("applicatin/json");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 访问网络获取数据
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST,"http://220.180.139.42:8980/SmartBusServer/Main", params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						ToastUtil.showToast(getApplicationContext(), "请检查网络连接！");
					}

					@Override
					public void onSuccess(ResponseInfo<String> string) {
						// System.out.println(string.result);
						Gson gson = new Gson();
						getsearchstationbean = gson.fromJson(string.result, GetSearchStationBean.class);
						handler.sendEmptyMessage(1);
						
					}

				});
		
		
		
	}
	public void getBus_InfoDetail() {
		
			
		
		for(int i=0;i<getsearchstationbean.result.list.size();i++){
			synchronized (BusStation_around_show.this) {
			final int tempi = i;
			final SendBusIdBean sendbusidbean = new SendBusIdBean();
			sendbusidbean.cmd = "getArriveInfo";
			sendbusidbean.params = sendbusidbean.new Params();
			sendbusidbean.params.type = 1;
			sendbusidbean.params.stationId = arrivalList.get(i).finalStation00_busId00;
			
			//sendbusidbean.params.lineName = getsearchstationbean.result.list.get(i);
			sendbusidbean.params.lineName = arrivalList.get(i).busNum;
			System.out.println(sendbusidbean.params.lineName+"------"+sendbusidbean.params.stationId);
			// 用来解析json数据
			final Gson gson = new Gson();

			// 设置传输参数。​
			final RequestParams params2 = new RequestParams("UTF-8");

			try {
				/*
				 * params.setBodyEntity(new StringEntity(gson.toJson("要转成json的对象"),
				 * "UTF-8")); ​ params.setContentType("applicatin/json");
				 */
				params2.setBodyEntity(new StringEntity(gson.toJson(sendbusidbean),
						"UTF-8"));
				//System.out.println(gson.toJson(sendbusnumbean));
				params2.setContentType("applicatin/json");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.send(HttpMethod.POST,"http://220.180.139.42:8980/SmartBusServer/Main", params2,new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0,String arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(ResponseInfo<String> string) {
					
				//	System.out.println(string.result.toString().substring(0,8)+"-------------------------");
				//	System.out.println("{\"result".equals(string.result.toString().substring(0,8)));
					if("{\"result".equals(string.result.toString().substring(0,8))){
							BusComingBean busComing = gson.fromJson(string.result,BusComingBean.class);
							
							//System.out.println("5555555555555555555555555555555555555555555555555");
							
							synchronized (BusStation_around_show.this) {
								
							
							for(int i=0;i<arrivalList.size();i++){
								//System.out.println("000000"+i+arrivalList.get(i).busNum +"ddd"+ sendbusidbean.params.lineName);
								//System.out.println("000000"+arrivalList.get(i).busNum.equals(sendbusidbean.params.lineName));
								if(arrivalList.get(i).busNum.equals(sendbusidbean.params.lineName)){
									//System.out.println(i+"--");
									arrivalList.get(i).metersFromHere00 = busComing.result.distance;
									arrivalList.get(i).stationFromHere00 = busComing.result.willArriveTime;
									//System.out.println(arrivalList.get(i));
									//break;
									//System.out.println(i);
									
								}
							}
							}
							
							/*countNum++;
							//System.out.println("countnum++++++++++++++"+countNum);
							if(countNum == getsearchstationbean.result.list.size()){
								handler.sendEmptyMessage(0);	
							}*/
							
						} else{
							synchronized (BusStation_around_show.this) {
							//System.out.println("error");
							for(int i=0;i<arrivalList.size();i++){
							//	System.out.println(arrivalList.get(i).busNum +"ddd"+ arrivalList.size());
								//System.out.println("222222"+i+arrivalList.get(i).busNum +"ddd"+ sendbusidbean.params.lineName);
								//System.out.println("222222"+arrivalList.get(i).busNum.equals(sendbusidbean.params.lineName));
								if(arrivalList.get(i).busNum.equals(sendbusidbean.params.lineName)){
								//	System.out.println(i+"--");
									arrivalList.get(i).metersFromHere00 = "暂无";
									arrivalList.get(i).stationFromHere00 = "暂无";
									//System.out.println(i);
									//System.out.println(arrivalList.get(i));
								}
							}
							}
							//countNum++;
						/*//	System.out.println("countnum++++++++++++++"+countNum);
							if(countNum == getsearchstationbean.result.list.size()){
								handler.sendEmptyMessage(0);	
							}*/
						}
						
					handler.sendEmptyMessage(-1);

				}
				
			});//HttpUtils,查询最近车辆去程加返程
			
			
			
			sendbusidbean.params.type = 2;
			sendbusidbean.params.stationId = arrivalList.get(tempi).finalStation00_busId01;
			
			try {
				/*
				 * params.setBodyEntity(new StringEntity(gson.toJson("要转成json的对象"),
				 * "UTF-8")); ​ params.setContentType("applicatin/json");
				 */
				params2.setBodyEntity(new StringEntity(gson.toJson(sendbusidbean),
						"UTF-8"));
				//System.out.println(gson.toJson(sendbusnumbean));
				params2.setContentType("applicatin/json");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			HttpUtils httpUtils2 = new HttpUtils();
			httpUtils2.send(HttpMethod.POST,"http://220.180.139.42:8980/SmartBusServer/Main", params2,new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0,String arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(ResponseInfo<String> string) {
				//	System.out.println(string.result.toString().substring(0,8)+"-------------------------");
				//	System.out.println("{\"result".equals(string.result.toString().substring(0,8)));
					if("{\"result".equals(string.result.toString().substring(0,8))){
							//System.out.println(sendbusidbean.params.lineName);
							//System.out.println(string.result);	
							BusComingBean busComing = gson.fromJson(string.result,BusComingBean.class);
							synchronized (BusStation_around_show.this) {
							//System.out.println("tes6666666666666666666666666666666666666666666666");
							for(int i=0;i<arrivalList.size();i++){
								//System.out.println("333333"+i+arrivalList.get(i).busNum +"ddd"+ sendbusidbean.params.lineName);
								///System.out.println(arrivalList.get(i).busNum.equals(sendbusidbean.params.lineName));
								if(arrivalList.get(i).busNum.equals(sendbusidbean.params.lineName)){
									//System.out.println(i+"--");
									arrivalList.get(i).metersFromHere01 = busComing.result.distance;
									arrivalList.get(i).stationFromHere01 = busComing.result.willArriveTime;
								//	System.out.println(busComing.result.distance+busComing.result.willArriveTime);
								//	System.out.println(i);
									//System.out.println(arrivalList.get(i));
								}
							}
							}
							/*countNum++;
						//	System.out.println("countnum++++++++++++++"+countNum);
							if(countNum == getsearchstationbean.result.list.size()){
								handler.sendEmptyMessage(0);	
							}*/
						} else {
							synchronized (BusStation_around_show.this) {
							for(int i=0;i<arrivalList.size();i++){
								//System.out.println("444444"+i+arrivalList.get(i).busNum +"ddd"+ sendbusidbean.params.lineName);
								//System.out.println("44444"+arrivalList.get(i).busNum.equals(sendbusidbean.params.lineName));
								if(arrivalList.get(i).busNum.equals(sendbusidbean.params.lineName)){
								//	System.out.println(i+"--");
									arrivalList.get(i).metersFromHere01 = "暂无";
									arrivalList.get(i).stationFromHere01 = "暂无";
									//System.out.println(i);
									//System.out.println(arrivalList.get(i));
								}
							}
							}
							/*countNum++;
							//System.out.println("countnum++++++++++++++"+countNum);
							if(countNum == getsearchstationbean.result.list.size()){
								handler.sendEmptyMessage(0);	
							}*/
							
						}
						
						handler.sendEmptyMessage(-1);
				}
				
			});//HttpUtils,查询最近车辆返程
			
			
			/*if(countNum == getsearchstationbean.result.list.size()*2){
				handler.sendEmptyMessage(0);
			}*/
		
		}
		}
	}
public void getAllLines(){

	/*
	 * 请问站点相关的线路
	 * */
	Gson gson = new Gson();
	for(int i = 0;i<getsearchstationbean.result.list.size();i++){
		//tempi = i;
		final SendBusNumBean sendbusnumbean = new SendBusNumBean();
		sendbusnumbean.cmd = "lineDetail";
		sendbusnumbean.params = sendbusnumbean.new Params();
		final String BusNum = getsearchstationbean.result.list.get(i);
		sendbusnumbean.params.lineName = getsearchstationbean.result.list.get(i);
		
		// 设置传输参数。​
		RequestParams params = new RequestParams("UTF-8");

		try {
			/*
			 * params.setBodyEntity(new StringEntity(gson.toJson("要转成json的对象"),
			 * "UTF-8")); ​ params.setContentType("applicatin/json");
			 */
			params.setBodyEntity(new StringEntity(gson.toJson(sendbusnumbean),"UTF-8"));
			//System.out.println(gson.toJson(sendbusnumbean));
			params.setContentType("applicatin/json");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 访问网络获取数据
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST,"http://220.180.139.42:8980/SmartBusServer/Main", params,new RequestCallBack<String>() {

			

			@Override
			public void onFailure(HttpException arg0,String arg1) {
				ToastUtil.showToast(getApplicationContext(), "访问网络错误！");
			}

			@Override
			public void onSuccess(ResponseInfo<String> string) {
				//System.out.println(string.result);
				final GetArrivalInfo getarrivalinfo = new GetArrivalInfo();
				getarrivalinfo.busNum = BusNum;
				JSONObject jsonObj;
				try {
					jsonObj = new JSONObject(string.result);
					JSONObject result = jsonObj.getJSONObject("result");
					
					getarrivalinfo.finalStation00 = result.getString("downLine");
					getarrivalinfo.finalStation01 = result.getString("upLine");
					//System.out.println(getarrivalinfo.finalStation00+getarrivalinfo.finalStation01+"ssssssssssssssssss");
					
					JSONArray upLineStationList_00 = result.getJSONArray("upLineStationList");
					JSONArray downLineStationList_00 = result.getJSONArray("downLineStationList");

					List<UpLineStationList> uplinestationlist = new ArrayList<UpLineStationList>();
					List<DownLineStationList> downlinestationlist = new ArrayList<DownLineStationList>();
					
					for (int i = 0; i < upLineStationList_00.length(); i++) {
						UpLineStationList temp00 = new UpLineStationList();
						temp00.num00 = upLineStationList_00
								.getJSONArray(i).get(0) + "";
						temp00.stationName = upLineStationList_00
								.getJSONArray(i).get(1) + "";
						temp00.num01 = Integer
								.parseInt(upLineStationList_00
										.getJSONArray(i).get(2) + "");
						uplinestationlist.add(temp00);
					}
					
					
					for (int i = 0; i < downLineStationList_00.length(); i++) {
						DownLineStationList temp01 = new DownLineStationList();
						temp01.num00 = downLineStationList_00
								.getJSONArray(i).get(0) + "";
						temp01.stationName = downLineStationList_00
								.getJSONArray(i).get(1) + "";
						temp01.num01 = Integer
								.parseInt(downLineStationList_00
										.getJSONArray(i).get(2) + "");
						downlinestationlist.add(temp01);
					}
					for(int i=0;i<uplinestationlist.size();i++){
						//System.out.println(uplinestationlist.get(i).stationName+getarrivalinfo.finalStation00 );
						if(uplinestationlist.get(i).stationName.equals(stationname)){
							getarrivalinfo.finalStation00_busId00 = uplinestationlist.get(i).num00;
						}
					}
					for(int i=0;i<downlinestationlist.size();i++){
						if(downlinestationlist.get(i).stationName.equals(stationname)){
							getarrivalinfo.finalStation00_busId01 = downlinestationlist.get(i).num00;
						}
					}
					arrivalList.add(getarrivalinfo);
					//System.out.println(getarrivalinfo.finalStation00_busId00+"]]]]]]]]]]]]"+getarrivalinfo.finalStation00_busId01);
					

					//根据站点的id，查询最近公交车到达情况
					/*
					 * {"cmd":"getArriveInfo","params":{"type":2,"lineName":"2535路","stationId":"2526"}}
					 * 返回数据：
                     * {"result":{"willArriveTime":"5站","distance":"3300米","index":662,"plate":"B18443"}}
					 * */
					if(arrivalList.size() == getsearchstationbean.result.list.size()){
						handler.sendEmptyMessage(2);
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				
				
				
			}


		});
		
		
	}

}
}
