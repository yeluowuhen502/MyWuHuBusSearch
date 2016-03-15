package com.marsjiang.mygoogleplay.adapter;

import com.marsjiang.mygoogleplay.R;
import com.marsjiang.mygoogleplay.ui.fragment.FragmentFactory;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter {

	private String[] tabs;

	public MainPagerAdapter(Context context,FragmentManager fm) {
		super(fm);
		tabs = context.getResources().getStringArray(R.array.tab_names);
	}

	@Override
	public Fragment getItem(int position) {
		return FragmentFactory.create(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tabs.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return tabs[position];
	}
	
}
