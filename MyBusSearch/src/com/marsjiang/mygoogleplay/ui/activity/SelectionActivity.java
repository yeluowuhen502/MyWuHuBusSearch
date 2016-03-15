package com.marsjiang.mygoogleplay.ui.activity;

import com.marsjiang.mygoogleplay.R;
import com.marsjiang.mygoogleplay.R.id;
import com.marsjiang.mygoogleplay.R.layout;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SelectionActivity extends Activity {
	TextView tv_bus_search;
	TextView tv_bike_station;
	TextView tv_ic_card;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selection);
		initView();

	}

	private void initView() {
		tv_bus_search = (TextView) findViewById(R.id.tv_bus_search);
		tv_bus_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SelectionActivity.this,Bus_Search_Activity.class);
				startActivity(intent);
			}
		});

		tv_bike_station = (TextView) findViewById(R.id.tv_bike_station);
		tv_bike_station.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SelectionActivity.this,Search_Bike_pos_Activity.class);
				startActivity(intent);
			}
		});
		
		tv_ic_card = (TextView) findViewById(R.id.tv_ic_card);
		tv_ic_card.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SelectionActivity.this,IC_Card_Pos_Activity.class);
				startActivity(intent);
			}
		});
		
	}

}
