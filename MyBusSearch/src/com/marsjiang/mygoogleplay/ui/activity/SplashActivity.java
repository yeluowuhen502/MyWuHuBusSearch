package com.marsjiang.mygoogleplay.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import cn.jpush.android.api.JPushInterface;

import com.marsjiang.mygoogleplay.R;
import com.marsjiang.mygoogleplay.util.SharedUtils;

public class SplashActivity extends Activity {

	private RelativeLayout rl_welcome_bg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		
		initAnimation();
		
		
		/*new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this,
						SelectionActivity.class);
				startActivity(intent);
				finish();
			}
		}, 1000);*/

	}

	private void initAnimation() {
		rl_welcome_bg = (RelativeLayout) findViewById(R.id.splash_rl);
		// 旋转动画，0 ~ 360
		RotateAnimation rotateAnimation = new RotateAnimation(
				0, 360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(1000);
		rotateAnimation.setFillAfter(true);
		
		// 缩放动画，从无到有
		ScaleAnimation scaleAnimation = new ScaleAnimation(
				0, 1, 
				0, 1, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(1000);
		scaleAnimation.setFillAfter(true);
		
		// 渐变动画，从无到有
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(2000);
		alphaAnimation.setFillAfter(true);
		
		// 创建动画集合
		AnimationSet animationSet = new AnimationSet(false);
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(rotateAnimation);
		animationSet.addAnimation(scaleAnimation);
		
		rl_welcome_bg.startAnimation(animationSet);
		
		// 监听动画
		animationSet.setAnimationListener(new MyAnimationListener());
	}
	class MyAnimationListener implements AnimationListener{

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			boolean flag = SharedUtils.getBoolean(SplashActivity.this, "IS_APP_FIRST_OPEN", true);
			if(flag){
				Intent intent = new Intent(SplashActivity.this,GuideUI.class);
				startActivity(intent);
				finish();
			}else{
			Intent intent = new Intent(SplashActivity.this,SelectionActivity.class);
			startActivity(intent);
			finish();
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		JPushInterface.onPause(getApplicationContext());
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		JPushInterface.onResume(getApplicationContext());
		super.onResume();
	}
}
