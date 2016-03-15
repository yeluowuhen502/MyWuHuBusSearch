package com.marsjiang.mygoogleplay.bean;

public class Send_Bike_Bean {
	// {"cmd":"getBicycleRental","params":{"lng":"116.296482","lat":"40.0493","searchKey":""}}

	public String cmd;
	public Params params;

	public class Params {

		public String searchKey;
		public String lng;
		public String lat;
	}
}
