package com.marsjiang.mygoogleplay.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	private static Toast toast;
	public static void showToast(Context context,String text){
		if(toast==null){
			toast = Toast.makeText(context, text, 0);
		}else {
			//如果toast不为空，则直接更改当前toast的文本
			toast.setText(text);
		}
		toast.show();
		
	}
}
