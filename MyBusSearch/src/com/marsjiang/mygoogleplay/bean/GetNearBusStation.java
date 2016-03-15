package com.marsjiang.mygoogleplay.bean;

import java.io.Serializable;

public class GetNearBusStation implements Serializable{
	public String location;
	public String meters;
	@Override
	public String toString() {
		return "GetNearBusStation [location=" + location + ", meters=" + meters
				+ "]";
	}
	
}
