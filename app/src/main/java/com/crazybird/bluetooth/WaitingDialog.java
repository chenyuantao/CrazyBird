package com.crazybird.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.crazybird.R;

public class WaitingDialog {
	private static Dialog watting;
	private static Handler handler;
	private static ImageView img;
	private static TextView tv;
	private static int i = 0;
	private static Activity activity;

	// 对话框的显示
	public static void showWaitting(Context context, String message) {

		int screenWidth = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
		int screenHeight = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getHeight();
		activity = (Activity) context;
		watting = new AlertDialog.Builder(context).create();
		watting.setCanceledOnTouchOutside(false); // 触摸边缘不消失
		watting.show();
		Window window = watting.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		// lp.x = 135; // 新位置X坐标
		// lp.y = 228; // 新位置Y坐标
		lp.width = (int) (screenWidth / 1.3); // 宽度
		lp.height = (int) (screenHeight / 3.5); // 高度
		// lp.alpha = 0.7f; // 透明度
		window.setAttributes(lp);
		window.setContentView(R.layout.dialog_wait_for_connect);
		img = (ImageView) window.findViewById(R.id.connect_dialog_quan);
		watting.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				if (!Bluetooth.isConnected) {
					if (Bluetooth.isClient) {
						Bluetooth.stopClient();
					} else {
						Bluetooth.stopServer();
						activity.finish();
					}
				}
			}

		});
		tv = (TextView) window.findViewById(R.id.connect_dialog_message);
		tv.setText(message);
		handler = new Handler();
		handler.postDelayed(runnable, 300);
	}

	/**
	 * 启动搜索的时候圈圈转的动态
	 */
	public static Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// 如果进度条未满则在次调用自己，形成循环
			switch (i) {
			case 0:
				img.setImageResource(R.drawable.dengdai01);
				break;
			case 1:
				img.setImageResource(R.drawable.dengdai02);
				break;
			case 2:
				img.setImageResource(R.drawable.dengdai03);
				break;
			case 3:
				img.setImageResource(R.drawable.dengdai04);
				break;
			case 4:
				img.setImageResource(R.drawable.dengdai05);
				break;
			case 5:
				img.setImageResource(R.drawable.dengdai06);
				break;
			case 6:
				img.setImageResource(R.drawable.dengdai07);
				break;
			case 7:
				img.setImageResource(R.drawable.dengdai08);
				break;
			default:
				break;
			}
			if (i == 7) {
				i = 0;
			}
			handler.postDelayed(this, 1000);
			i++;
		}
	};

	/**
	 * 对话框的隐藏
	 */
	private static void hiddenWattion() {
		if (watting != null && watting.isShowing()) {
			watting.dismiss();
		}
	}

	/**
	 * 对话框的销毁
	 */
	public static void destoryWaitting() {
		if (watting != null) {
			hiddenWattion();
			watting = null;
		}
	}

	/**
	 * 判断对话框是否在显示状态
	 * 
	 * @return
	 */
	public static boolean isWatting() {
		if (watting != null && watting.isShowing()) {
			return true;
		}
		return false;
	}

}
