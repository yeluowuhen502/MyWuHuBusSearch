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
import com.marsjiang.mygoogleplay.ui.activity.Bus_Line_Search_Activity;
import com.marsjiang.mygoogleplay.ui.activity.Bus_Line_Show_Activity;
import com.marsjiang.mygoogleplay.util.SharedUtils;
import com.marsjiang.mygoogleplay.util.ToastUtil;

public class BusLineFragment extends Fragment{
	private String bus_line_sp;
	private Button btn_busline;
	private EditText et_busline;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		/*TextView textView = new TextView(getActivity());
		textView.setText(this.getClass().getSimpleName());
		return textView;*/
		View view = View.inflate(getActivity(), R.layout.bus_lines_search, null);
		btn_busline = (Button) view.findViewById(R.id.btn_busline);
		et_busline = (EditText) view.findViewById(R.id.et_busline);
		
		bus_line_sp = SharedUtils.getString(getActivity(), "BUS_LINE_SEARCH", "");
		
		et_busline.requestFocus();
		et_busline.setText(bus_line_sp);
		
		btn_busline.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if("".equals(bus_line_sp)){
					ToastUtil.showToast(getActivity(), "请点击查询的车辆");
				}else{
				Intent intent = new Intent(getActivity(),Bus_Line_Show_Activity.class);
				intent.putExtra("bus_line", bus_line_sp);
				startActivity(intent);
				}
			}
		});
		et_busline.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),Bus_Line_Search_Activity.class);
				startActivity(intent);
			}
		});
		
		
		return view;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bus_line_sp = SharedUtils.getString(getActivity(), "BUS_LINE_SEARCH", "");
		et_busline.setText(bus_line_sp);
	}
}
