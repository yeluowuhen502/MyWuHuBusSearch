package com.marsjiang.mygoogleplay.ui.fragment;

import android.support.v4.app.Fragment;

public class FragmentFactory {
	public static Fragment create(int position){
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new BusLineFragment();
			break;
		case 1:
			fragment = new StationFragment();
			break;
		}//switch
		
		return fragment;
		
	}
}

