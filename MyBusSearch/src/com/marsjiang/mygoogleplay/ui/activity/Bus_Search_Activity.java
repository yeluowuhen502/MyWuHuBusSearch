package com.marsjiang.mygoogleplay.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marsjiang.mygoogleplay.R;
import com.marsjiang.mygoogleplay.adapter.MainPagerAdapter;
import com.marsjiang.mygoogleplay.ui.view.PagerSlidingTab;
import com.marsjiang.mygoogleplay.util.SharedUtils;

public class Bus_Search_Activity extends ActionBarActivity {
	private TextView exitTextView;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private ViewPager viewpager;
	private PagerSlidingTab slidingTab;
	private ViewPager viewpagerHeader;
	int[] imgs = new int[] { R.drawable.wuhu, R.drawable.wuhu1,R.drawable.wuhu2 };
	
	Handler mhandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			viewpagerHeader.setCurrentItem(viewpagerHeader.getCurrentItem() + 1);
			mhandler.sendEmptyMessageDelayed(0,3000);
		};
	};

	
	protected void onPause() {
		super.onPause();
		mhandler.removeCallbacksAndMessages(null);
	};
	
	/* List<ImageView> imgViews; */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		setActionBar();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mhandler.sendEmptyMessageDelayed(0, 3000);
	}
	
	private void setActionBar() {
		// 获取actionbar对象并且设置相应的标题和图片
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("我的掌上公交");
		actionBar.setIcon(R.drawable.ic_launcher);

		// 显示Home按钮并且使之可以被点击
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer_am, 0, 0);
		drawerToggle.syncState();
		drawerLayout.setDrawerListener(drawerToggle);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			drawerToggle.onOptionsItemSelected(item);
			break;

		}

		return super.onOptionsItemSelected(item);
	}

	private void initView() {
		viewpager = (ViewPager) findViewById(R.id.viewPager);

		slidingTab = (PagerSlidingTab) findViewById(R.id.slidingTab);
		viewpagerHeader = (ViewPager) findViewById(R.id.viewpagerHeader);
		/*
		 * imgViews = new ArrayList<ImageView>(); for(int
		 * i=0;i<imgs.length;i++){ ImageView imgTemp = new ImageView(this);
		 * imgViews.add(imgTemp); }
		 */
		slidingTab.setShouldExpand(true);
		viewpagerHeader.setAdapter(new ViewPagerHeaderAdapter());

		viewpager.setAdapter(new MainPagerAdapter(this,getSupportFragmentManager()));
		slidingTab.setViewPager(viewpager);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

		exitTextView = (TextView) findViewById(R.id.tv_exit);
		exitTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 把viewpager条目位置设置到最大值的中间，为了实现往做无限滑动
		int currentIndexOffse = Integer.MAX_VALUE / 2 % imgs.length;
		viewpagerHeader.setCurrentItem(Integer.MAX_VALUE / 2 - currentIndexOffse);
		// 自动轮播
		autoPlay();

	}

	private boolean isPlay = true;

	private void autoPlay() {
		/*
		new Thread() {
			public void run() {
				while (isPlay) {
					try {
						Thread.sleep(3000);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								//System.out.println("切换到下一页");
								// 往右滑动一页
								viewpagerHeader.setCurrentItem(viewpagerHeader.getCurrentItem() + 1);
							}
						});
					} catch (InterruptedException e) {
					}
				}
			};
		}.start();
	*/
		mhandler.sendEmptyMessageDelayed(0, 3000);
	}

	public class ViewPagerHeaderAdapter extends PagerAdapter {
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			ImageView iv_temp = new ImageView(Bus_Search_Activity.this);
			iv_temp.setBackgroundResource(imgs[position % imgs.length]);
			container.addView(iv_temp);
			return iv_temp;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			// super.destroyItem(container, position, object);
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

	}

}
