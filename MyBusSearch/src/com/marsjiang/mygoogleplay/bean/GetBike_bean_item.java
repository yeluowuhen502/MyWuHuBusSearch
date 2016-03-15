package com.marsjiang.mygoogleplay.bean;

import java.io.Serializable;

public class GetBike_bean_item implements Serializable{
	public String pos_id;
	public String pos;
	public String blank;
	public int bike_avail;
	public int bike_no_avail;
	public String blank2;
	public String weidu;
	public String jingdu;
	public String diatance;
	@Override
	public String toString() {
		return "GetBike_bean_item [pos_id=" + pos_id + ", pos=" + pos
				+ ", blank=" + blank + ", bike_avail=" + bike_avail
				+ ", bike_no_avail=" + bike_no_avail + ", blank2=" + blank2
				+ ", weidu=" + weidu + ", jingdu=" + jingdu + ", diatance="
				+ diatance + "]";
	}
	
}
