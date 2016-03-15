package com.marsjiang.mygoogleplay.bean;

import java.util.List;

public class Get_Bike_bean {

	public Result result;

	public class Result {
		public List<Data> list;
		public List<String> list_meta;
		
	}

	public class Data {
		int pos_id;
		String pos;
		String blank;
		int bike_avail;
		int bike_no_avail;
		String blank2;
		String weidu;
		String jingdu;
		String diatance;
	}

	
	
}
