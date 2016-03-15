package com.marsjiang.mygoogleplay.ui.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.marsjiang.mygoogleplay.R;
import com.marsjiang.mygoogleplay.bean.GetNearBusStation;
import com.marsjiang.mygoogleplay.bean.GetSearchStationBean;
import com.marsjiang.mygoogleplay.bean.SendBusStationBean;
import com.marsjiang.mygoogleplay.bean.Send_Bike_Bean.Params;
import com.marsjiang.mygoogleplay.util.SharedUtils;
import com.marsjiang.mygoogleplay.util.ToastUtil;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class Bus_Station_Activity extends ActionBarActivity {

	private StickyListHeadersListView stickylistheaderslistview;
	
	private EditText search_00;
	private ListView lv_search;
	
	private String currentStationName = "-1";

	private GetSearchStationBean getsearchbusnumbean;
	
	private SearchBusNumAdapter searchbusnumadapter;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			System.out.println("收到消息");
			searchbusnumadapter.notifyDataSetChanged();
		};
	};

	private ArrayList<GetNearBusStation> nearBustationList;

	private ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_line_station_show);
		setActionBar();
		nearBustationList = (ArrayList<GetNearBusStation>) getIntent().getSerializableExtra("NearBusStation");
		
		//进行总体的初始化
		initView();
		initData();
		
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
	

	// 初始化界面控件
	private void initView() {
		search_00 = (EditText) findViewById(R.id.et_search);
		lv_search = (ListView) findViewById(R.id.lv_search);
		
		stickylistheaderslistview = (StickyListHeadersListView) findViewById(R.id.stickylistheaderslistview);
		stickylistheaderslistview.setAdapter(new MyAdapter());
		lv_search.setVisibility(View.INVISIBLE);
		stickylistheaderslistview.setVisibility(View.VISIBLE);
		
		stickylistheaderslistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Intent intent = new Intent(Bus_Station_Activity.this,BusStation_around_show.class);
				intent.putExtra("stationname", nearBustationList.get(arg2).location);
				SharedUtils.putString(getApplicationContext(), "BUS_STATION_SP", nearBustationList.get(arg2).location);
				startActivity(intent);
			}
		});
		
		
		
	//	currentNum = search_00.getText().toString().trim();
		searchbusnumadapter = new SearchBusNumAdapter();
		lv_search.setAdapter(searchbusnumadapter);
		
		search_00.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//System.out.println("onTextChanged");
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				//System.out.println("beforeTextChanged");
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				//System.out.println("afterTextChanged");
				lv_search.setVisibility(View.VISIBLE);
				stickylistheaderslistview.setVisibility(View.INVISIBLE);
				getDateFromServer();
				//searchbusnumadapter.notifyDataSetChanged();
			}
		});
		
		lv_search.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				String busstationname = getsearchbusnumbean.result.list.get(arg2);
				Intent intent = new Intent(Bus_Station_Activity.this,BusStation_around_show.class);
				intent.putExtra("stationname", busstationname);
				startActivity(intent);
			}
		});
		
	}
	public class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return nearBustationList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return nearBustationList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder viewholder;
			if(convertView == null){
				convertView = View.inflate(getApplicationContext(), R.layout.test_list_item_layout, null);
				viewholder = new ViewHolder();
				viewholder.tv_nearbusstation= (TextView) convertView.findViewById(R.id.tv_nearbusstation);
				viewholder.tv_nearbusstationposition= (TextView) convertView.findViewById(R.id.tv_nearbusstationposition);
				convertView.setTag(viewholder);
				
			}else{
				viewholder = (ViewHolder) convertView.getTag();
			}
			
			GetNearBusStation nearBustationListtemp =nearBustationList.get(position);
			
			viewholder.tv_nearbusstation.setText(nearBustationListtemp.location);
			viewholder.tv_nearbusstationposition.setText("距离此处"+nearBustationListtemp.meters);
			
			return convertView;
		
		}

		@Override
		public View getHeaderView(int position, View convertView,ViewGroup parent) {
			ViewHolderHeader viewholderheader;
			if(convertView == null){
				convertView = View.inflate(getApplicationContext(), R.layout.header, null);
				viewholderheader = new ViewHolderHeader();
				viewholderheader.tv_header= (TextView) convertView.findViewById(R.id.tv_busstation_headers);
				convertView.setTag(viewholderheader);
				
			}else{
				viewholderheader = (ViewHolderHeader) convertView.getTag();
			}
			
			viewholderheader.tv_header.setText("附近的站点信息");
			return convertView;
		}

		@Override
		public long getHeaderId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
	static class ViewHolderHeader{
		TextView tv_header;
	}
	static class ViewHolder{
		TextView tv_nearbusstation;
		TextView tv_nearbusstationposition;
	}
	// 初始化数据
	private void initData() {
		/*
		 * 写入数字发送内容： POST http://220.180.139.42:8980/SmartBusServer/Main
		 * HTTP/1.1 Accept-Encoding: gzip, deflate Content-Length: 46
		 * Content-Type: text/plain; charset=UTF-8 Host: 220.180.139.42:8980
		 * Connection: Keep-Alive
		 * 
		 * {"cmd":"searchLine","params":{"lineName":"3"}}
		 */
		getDateFromServer();
		
		//searchbusnumadapter.notifyDataSetChanged();
	}

	// 从数据库获取联想数据
	private void getDateFromServer() {
		//用来存储发送数据
		if(!"".equals(search_00.getText().toString().trim())){
			currentStationName = search_00.getText().toString().trim();
		}
		
		final SendBusStationBean sendbusnumbean = new SendBusStationBean();
		sendbusnumbean.cmd = "searchStation";
		sendbusnumbean.params = sendbusnumbean.new Params();
		sendbusnumbean.params.stationName=currentStationName;
		//用来解析json数据
		final Gson gson = new Gson();

		//设置传输参数。​
		RequestParams params = new RequestParams("UTF-8");
		
		try{
		/*	params.setBodyEntity(new StringEntity(gson.toJson("要转成json的对象"),"UTF-8"));
​					params.setContentType("applicatin/json");*/
			params.setBodyEntity(new StringEntity(gson.toJson(sendbusnumbean),"UTF-8"));
			System.out.println(gson.toJson(sendbusnumbean));
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
				
				if(string!=null){
					System.out.println(string.result);
					getsearchbusnumbean = gson.fromJson(string.result,GetSearchStationBean.class);
					mHandler.sendEmptyMessage(0);
				}else{
					ToastUtil.showToast(getApplicationContext(), "请输入正确格式的数据");
					System.out.println("请输入正确格式的数据");
				}
				
			}
		});
		
	}

	//listView的adapter
	class SearchBusNumAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(getsearchbusnumbean == null||currentStationName == "-1"||getsearchbusnumbean.result == null){
				return 0;
			}else{
				return getsearchbusnumbean.result.list.size();
			}
			
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return getsearchbusnumbean.result.list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolderAdapter viewHolder;
			String busNum = getsearchbusnumbean.result.list.get(position);
			if(convertView == null){
				viewHolder = new ViewHolderAdapter();
				convertView = View.inflate(getApplicationContext(), R.layout.search_bus_station_show_item, null);
				viewHolder.tv_searchNum = (TextView) convertView.findViewById(R.id.tv_searchbusnum);
				convertView.setTag(viewHolder);
				
			}else{
				viewHolder = (ViewHolderAdapter) convertView.getTag();
			}
			viewHolder.tv_searchNum.setText(busNum);
			ViewHelper.setScaleX(convertView, 0.5f);
			ViewHelper.setScaleY(convertView, 0.5f);
			
			ViewPropertyAnimator.animate(convertView).scaleX(1.0f).setDuration(500).setInterpolator(new OvershootInterpolator()).start();
			ViewPropertyAnimator.animate(convertView).scaleY(1.0f).setDuration(500).setInterpolator(new OvershootInterpolator()).start();
			viewHolder.tv_searchNum.setText(busNum);
			return convertView;
		}
		
	}//Adapter的结尾处
	
	static class ViewHolderAdapter{
		TextView tv_searchNum;
	}
	
}
