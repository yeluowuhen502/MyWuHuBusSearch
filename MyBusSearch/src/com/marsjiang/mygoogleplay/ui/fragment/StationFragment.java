package com.marsjiang.mygoogleplay.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.marsjiang.mygoogleplay.R;
import com.marsjiang.mygoogleplay.ui.activity.BusStation_around_show;
import com.marsjiang.mygoogleplay.ui.activity.Bus_Station_Activity;
import com.marsjiang.mygoogleplay.ui.activity.GetNearStationActivity;
import com.marsjiang.mygoogleplay.util.SharedUtils;
import com.marsjiang.mygoogleplay.util.ToastUtil;

public class StationFragment extends Fragment{
	private String bus_station_sp;
	private Button station_btn;
	private EditText et_bus_station;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.bus_station_search, null);
		et_bus_station = (EditText) view.findViewById(R.id.et_bus_station);
		station_btn = (Button) view.findViewById(R.id.station_btn);
		
		bus_station_sp = SharedUtils.getString(getActivity(), "BUS_STATION_SP", "");
		
		et_bus_station.requestFocus();
		et_bus_station.setText(bus_station_sp);
		
		et_bus_station.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),GetNearStationActivity.class);
				startActivity(intent);
			}
		});
		
		station_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if("".equals(bus_station_sp)){
					ToastUtil.showToast(getActivity(), "请点击查询的车站");
				}else{
				Intent intent = new Intent(getActivity(),BusStation_around_show.class);
				intent.putExtra("stationname", bus_station_sp);
				startActivity(intent);
				}
			}
		});
		return view;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bus_station_sp = SharedUtils.getString(getActivity(), "BUS_STATION_SP", "");
		et_bus_station.setText(bus_station_sp);
	}
}
