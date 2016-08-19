package com.crazybird.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crazybird.R;

public class MyAlertDialog {

	private static Dialog alertDialog;
	private static TextView tv;
	private static Activity activity;
	private static Button btn_yes;
	private static Button btn_no;

	// 对话框的显示
	public static void show(Context context, String message,
			OnClickListener onYesClickListener,
			OnClickListener onNoClickListener, OnCancelListener onCancListener) {
		int screenWidth = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
		int screenHeight = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getHeight();
		activity = (Activity) context;
		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setCanceledOnTouchOutside(false); // 触摸边缘不消失
		alertDialog.show();
		Window window = alertDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		// lp.x = 135; // 新位置X坐标
		// lp.y = 228; // 新位置Y坐标
		lp.width = (int) (screenWidth / 1.3); // 宽度
		lp.height = (int) (screenHeight / 5);
		; // 高度
		// lp.alpha = 0.7f; // 透明度
		window.setAttributes(lp);
		window.setContentView(R.layout.dialog_alert);
		btn_yes = (Button) window.findViewById(R.id.dialog_alert_yes);
		btn_no = (Button) window.findViewById(R.id.dialog_alert_no);
		tv = (TextView) window.findViewById(R.id.dialog_alert_message);
		alertDialog.setOnCancelListener(onCancListener);
		btn_yes.setOnClickListener(onYesClickListener);
		btn_no.setOnClickListener(onNoClickListener);
		tv.setText(message);
	}

	/**
	 * 对话框的销毁
	 */
	public static void destory() {
		if (alertDialog != null) {
			alertDialog.dismiss();
			alertDialog = null;
		}
	}

	/**
	 * 判断对话框是否在显示状态
	 * 
	 * @return
	 */
	public static boolean isShowing() {
		if (alertDialog != null && alertDialog.isShowing()) {
			return true;
		}
		return false;
	}
}
