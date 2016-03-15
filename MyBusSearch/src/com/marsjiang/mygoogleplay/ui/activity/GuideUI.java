package com.marsjiang.mygoogleplay.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.marsjiang.mygoogleplay.R;
import com.marsjiang.mygoogleplay.util.SharedUtils;

public class GuideUI extends Activity implements OnClickListener {
	private ViewPager vp_guide_bg;
	private List<ImageView> imgs;
	private LinearLayout ll_guide_points;
	private ImageView iv_guide_redPoint;
	private Button bt_guide_start;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guide);

		init();
	}

	private void init() {
		ll_guide_points = (LinearLayout) findViewById(R.id.ll_guide_points);
		vp_guide_bg = (ViewPager) findViewById(R.id.vp_guide_bg);
		iv_guide_redPoint = (ImageView) findViewById(R.id.iv_guide_redPoint);
		bt_guide_start = (Button) findViewById(R.id.bt_guide_start);
		bt_guide_start.setOnClickListener(this);
		// 准备数据
		initData();
		// 设置Adapter
		vp_guide_bg.setAdapter(new MyAdapter());
		// 监听ViewPager
		vp_guide_bg.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			System.out.println("position:" + position + ":positionOffset:"
					+ positionOffset + ":positionOffsetPixels:"
					+ positionOffsetPixels);
			// 计算红点移动的距离= 手指移动的距离/屏幕的宽度 * 灰点的间距 = positionOffset * 灰点的间距
			int redPointX = (int) ((position + positionOffset) * dp2px(20));
			// 通过设置红点的左边距，实现红点的移动
			android.widget.RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) iv_guide_redPoint
					.getLayoutParams();
			layoutParams.leftMargin = redPointX;
			iv_guide_redPoint.setLayoutParams(layoutParams);
		}

		@Override
		public void onPageSelected(int position) {
			// 当选中最后一页时，把button显示出来
			if(position==imgs.size()-1){
				bt_guide_start.setVisibility(View.VISIBLE);
			}else{
				bt_guide_start.setVisibility(View.GONE);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}

	}

	private void initData() {
		int[] imgId = new int[] { R.drawable.guide_00, R.drawable.guide_01,
				R.drawable.guide_02 };
		imgs = new ArrayList<ImageView>();
		for (int i = 0; i < imgId.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setBackgroundResource(imgId[i]);
			imgs.add(imageView);
			// 创建灰点
			ImageView point = new ImageView(this);
			point.setBackgroundResource(R.drawable.guide_point_nomal);
			int dp2px = dp2px(10);
			// 设置宽高
			LayoutParams params = new LayoutParams(dp2px, dp2px);
			if (i != 0) {
				params.leftMargin = dp2px;
			}
			point.setLayoutParams(params);
			// 添加到线性容器中
			ll_guide_points.addView(point);
		}
	}

	class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imgs.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView = imgs.get(position);
			container.addView(imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	@Override
	public void onClick(View v) {
		// 跳到主界面
		startActivity(new Intent(this,SelectionActivity.class));
		// 把已经打开过应用的boolean值存起来
		SharedUtils.putBoolean(this, "IS_APP_FIRST_OPEN", false);
		finish();
	}
	
	// dp转换px
	private int dp2px(int dp){
		// px = dp * 密度比
		float density = getResources().getDisplayMetrics().density;// 0.75 1 1.5 2
		return (int) (dp * density + 0.5f);// 4舍5入
	}
}
