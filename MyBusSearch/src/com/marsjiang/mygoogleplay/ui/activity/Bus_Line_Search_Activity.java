package com.marsjiang.mygoogleplay.ui.activity;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

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
import com.marsjiang.mygoogleplay.bean.GetSearchBusNumBean;
import com.marsjiang.mygoogleplay.bean.SendBusNumBean;
import com.marsjiang.mygoogleplay.bean.Send_Bike_Bean.Params;
import com.marsjiang.mygoogleplay.util.SharedUtils;
import com.marsjiang.mygoogleplay.util.ToastUtil;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class Bus_Line_Search_Activity extends ActionBarActivity {

	private EditText search_00;
	private ListView lv_search;
	
	private String currentNum = "-1";

	private GetSearchBusNumBean getsearchbusnumbean;
	
	private SearchBusNumAdapter searchbusnumadapter;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//System.out.println("收到消息");
			searchbusnumadapter.notifyDataSetChanged();
		};
	};
	private ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_line_search);
		//进行总体的初始化
		initView();
		initData();
		
	}

	// 初始化界面控件
	private void initView() {
		setActionBar();
		search_00 = (EditText) findViewById(R.id.et_search);
		lv_search = (ListView) findViewById(R.id.lv_search);
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
				getDateFromServer();
				//searchbusnumadapter.notifyDataSetChanged();
			}
		});
		//listview点击事件监听
		lv_search.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				//ToastUtil.showToast(getApplicationContext(), getsearchbusnumbean.result.list.get(arg2));
				//点击了相应的公交车之后便需要跳转到相应的显示界面了：
				Intent intent = new Intent(Bus_Line_Search_Activity.this,Bus_Line_Show_Activity.class);
				intent.putExtra("bus_line", getsearchbusnumbean.result.list.get(arg2));
				SharedUtils.putString(getApplicationContext(), "BUS_LINE_SEARCH", getsearchbusnumbean.result.list.get(arg2));
				
				startActivity(intent);
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
			currentNum = search_00.getText().toString().trim();
		}
		
		final SendBusNumBean sendbusnumbean = new SendBusNumBean();
		sendbusnumbean.cmd = "searchLine";
		sendbusnumbean.params = sendbusnumbean.new Params();
		sendbusnumbean.params.lineName=currentNum;
		//用来解析json数据
		final Gson gson = new Gson();

		//设置传输参数。​
		RequestParams params = new RequestParams("UTF-8");
		
		try{
		/*	params.setBodyEntity(new StringEntity(gson.toJson("要转成json的对象"),"UTF-8"));
​					params.setContentType("applicatin/json");*/
			params.setBodyEntity(new StringEntity(gson.toJson(sendbusnumbean),"UTF-8"));
			//System.out.println(gson.toJson(sendbusnumbean));
			params.setContentType("applicatin/json");
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		//访问网络获取数据
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, "http://220.180.139.42:8980/SmartBusServer/Main", params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				//System.out.println("failed");
				ToastUtil.showToast(getApplicationContext(), "请检查网络连接！");
			}

			@Override
			public void onSuccess(ResponseInfo<String> string) {
				
				if(string!=null){
					//System.out.println(string.result);
					getsearchbusnumbean = gson.fromJson(string.result,GetSearchBusNumBean.class);
					mHandler.sendEmptyMessage(0);
				}else{
					ToastUtil.showToast(getApplicationContext(), "请输入正确格式的数据");
					//System.out.println("请输入正确格式的数据");
				}
				
			}
		});
		
	}

	//listView的adapter
	class SearchBusNumAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(getsearchbusnumbean == null||currentNum == "-1"||getsearchbusnumbean.result == null){
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
			ViewHolder viewHolder;
			String busNum = getsearchbusnumbean.result.list.get(position);
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.searchbusnumitem, null);
				viewHolder.tv_searchNum = (TextView) convertView.findViewById(R.id.tv_searchbusnum);
				convertView.setTag(viewHolder);
				
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			viewHolder.tv_searchNum.setText(busNum);
			ViewHelper.setScaleX(convertView, 0.5f);
			ViewHelper.setScaleY(convertView, 0.5f);
			
			ViewPropertyAnimator.animate(convertView).scaleX(1.0f).setDuration(500).setInterpolator(new OvershootInterpolator()).start();
			ViewPropertyAnimator.animate(convertView).scaleY(1.0f).setDuration(500).setInterpolator(new OvershootInterpolator()).start();
			return convertView;
		}
		
	}//Adapter的结尾处
	
	static class ViewHolder{
		TextView tv_searchNum;
	}
	
}
