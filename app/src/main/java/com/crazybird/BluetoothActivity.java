package com.crazybird;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.crazybird.bluetooth.Bluetooth;
import com.crazybird.bluetooth.Bluetooth.BluetoothConnectListener;
import com.crazybird.bluetooth.MyAlertDialog;
import com.crazybird.bluetooth.WaitingDialog;
import com.crazybird.bluetooth.deviceListAdapter;
import com.crazybird.bluetooth.deviceListItem;

public class BluetoothActivity extends Activity {

	private ListView lv;
	private ImageView img;
	private LinearLayout searchView;
	private ArrayList<deviceListItem> list;
	private deviceListAdapter adapter;
	private Handler handler;
	private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mBtAdapter.enable();
		searchView = (LinearLayout) findViewById(R.id.searchView);
		OnClickListener onYesClickListener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MyAlertDialog.destory();
				searchView.setVisibility(View.GONE);
				Bluetooth.isClient = false;
				WaitingDialog.showWaitting(BluetoothActivity.this, "设备名："
						+ mBtAdapter.getName() + "\n等待对方连接");
				Bluetooth.startServer(new BluetoothConnectListener() {

					@Override
					public void onConnectSuccess() {
						Toast.makeText(BluetoothActivity.this, "成功建立连接",
								Toast.LENGTH_SHORT).show();
						WaitingDialog.destoryWaitting();
						BluetoothActivity.this.finish();
						Intent intent = new Intent();
						intent.setClass(BluetoothActivity.this,
								MultiplayActivity.class);
						startActivity(intent);
						Activity a = (Activity) App.context;
						a.finish();
					}

					@Override
					public void onConnectFailed(String error) {
						Toast.makeText(BluetoothActivity.this, error,
								Toast.LENGTH_SHORT).show();
						BluetoothActivity.this.finish();
					}
				});

			}
		};
		OnClickListener onNoClickListener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MyAlertDialog.destory();
				Bluetooth.isClient = true;
				searchView.setVisibility(View.VISIBLE);

				handler = new Handler();
				img = (ImageView) findViewById(R.id.connect_list_img);
				img.setImageResource(R.drawable.bt_sous);
				// 搜索按钮设置点击事件
				img.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// 如果蓝牙正在搜索的话
						if (mBtAdapter.isDiscovering()) {
							stopSearch();
						} else {
							doSearch();
						}

					}

				});

				lv = (ListView) findViewById(R.id.connect_list_lv);
				// 设置listview的快速滑动模式为true
				lv.setFastScrollEnabled(true);
				// 设置设备点击事件
				lv.setOnItemClickListener(mDeviceClickListener);
				// 注册 发现并刷新的广播
				IntentFilter discoveryFilter = new IntentFilter(
						BluetoothDevice.ACTION_FOUND);
				registerReceiver(mReceiver, discoveryFilter);
				// 注册 发现已完成时的广播
				IntentFilter foundFilter = new IntentFilter(
						BluetoothDevice.ACTION_FOUND);
				registerReceiver(mReceiver, foundFilter);
				list = new ArrayList<deviceListItem>();
				adapter = new deviceListAdapter(BluetoothActivity.this, list);
				lv.setAdapter(adapter);
				// 查找已配对的设备
				Set<BluetoothDevice> pairedDevices = mBtAdapter
						.getBondedDevices();
				if (pairedDevices.size() > 0) {
					for (BluetoothDevice device : pairedDevices) {
						list.add(new deviceListItem(device.getName() + "\n"
								+ device.getAddress(), true));
						adapter.notifyDataSetChanged();
						lv.setSelection(list.size() - 1);
					}
				} else {
					list.add(new deviceListItem("没有设备已经配对", true));
					adapter.notifyDataSetChanged();
					lv.setSelection(list.size() - 1);
				}

			}
		};
		OnCancelListener onCancListener = new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				BluetoothActivity.this.finish();
			}
		};
		MyAlertDialog.show(this, "是否作为服务器？", onYesClickListener,
				onNoClickListener, onCancListener);

	}
	
	protected void onDestory(){
		unregisterReceiver(mReceiver);		
		super.onDestroy();
	}

	private void doSearch() {
		img.setImageResource(R.drawable.dengdai01);
		list.clear();
		adapter.notifyDataSetChanged();
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				list.add(new deviceListItem(device.getName() + "\n"
						+ device.getAddress(), true));
				adapter.notifyDataSetChanged();
				lv.setSelection(list.size() - 1);
			}
		} else {
			list.add(new deviceListItem("没有设备已经配对", true));
			adapter.notifyDataSetChanged();
			lv.setSelection(list.size() - 1);
		}
		/* 开始搜索 */
		mBtAdapter.startDiscovery();
		handler = new Handler();
		handler.postDelayed(runnable, 500);
	}

	private void stopSearch() {
		// 取消搜索
		mBtAdapter.cancelDiscovery();
		if (handler != null && runnable != null) {
			handler.removeCallbacks(runnable);
		}
		img.setImageResource(R.drawable.bt_sous);
	}

	public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// 从意向到蓝牙设备对象
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// 如果已经配对，跳过它，因为它已被列出
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					if (list != null && mBtAdapter.isDiscovering()) {
						list.add(new deviceListItem(device.getName() + "\n"
								+ device.getAddress(), false));
						adapter.notifyDataSetChanged();
						lv.setSelection(list.size() - 1);
					}
				}
				// 当发现完成后，更改活动名称
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				BluetoothActivity.this
						.setProgressBarIndeterminateVisibility(false);
				if (lv.getCount() == 0) {
					list.add(new deviceListItem("没有发现蓝牙设备", false));
					adapter.notifyDataSetChanged();
					lv.setSelection(list.size() - 1);
				}
				img.setImageResource(R.drawable.bt_sous);
				handler.removeCallbacks(runnable);
			}
		}
	};
	private int i = 0;
	private Runnable runnable = new Runnable() {
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
			handler.postDelayed(this, 500);
			i++;
		}
	};

	// 点击监听器在列表视图的所有设备
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			// Cancel discovery because it's costly and we're about to connect

			deviceListItem item = list.get(arg2);
			if (arg2 == 0 && item.getMessage().equals("没有设备已经配对")) {
				Toast.makeText(BluetoothActivity.this, "没有设备已经配对，请重修选择",
						Toast.LENGTH_SHORT).show();
				return;
			}
			// 获取整个的盾的昵称
			String info = item.message;
			// 把截取后的简短名称赋给address
			selectedAddress = info.substring(info.length() - 17);
			MyAlertDialog.show(BluetoothActivity.this, "是否连接"+item.message, onConnectClickListener, onConnectNoListener, onConnectCancelListener);
//			// 定义一个弹出框对象确认是否连接
//			AlertDialog.Builder StopDialog = new AlertDialog.Builder(
//					BluetoothActivity.this);
//			StopDialog.setTitle("连接");// 标题
//			StopDialog.setMessage(item.message);
//			StopDialog.setPositiveButton("连接",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface d, int which) {
//
//						}
//					});
//			StopDialog.setNegativeButton("取消", null);
//			StopDialog.show();
		}
	};
	private String selectedAddress = "";

	private OnClickListener onConnectClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			MyAlertDialog.destory();	
			// 不判断app是否已经被绑定
			if (TextUtils.isEmpty(selectedAddress)) {
				Toast.makeText(BluetoothActivity.this, "该设备地址为空，请连接其它设备。",
						Toast.LENGTH_SHORT).show();
				return;
			}

			mBtAdapter.cancelDiscovery();
			if (handler != null) {
				handler.removeCallbacks(runnable);
			}
			img.setImageResource(R.drawable.bt_sous);
			WaitingDialog.showWaitting(BluetoothActivity.this, "正在连接服务器");
			Bluetooth.startClient(selectedAddress,
					new BluetoothConnectListener() {

						@Override
						public void onConnectSuccess() {
							Toast.makeText(BluetoothActivity.this, "连接成功",
									Toast.LENGTH_SHORT).show();
							WaitingDialog.destoryWaitting();
							Intent intent = new Intent();
							intent.setClass(BluetoothActivity.this,
									MultiplayActivity.class);
							startActivity(intent);
							BluetoothActivity.this.unregisterReceiver(mReceiver);		
							BluetoothActivity.this.finish();							
							Activity a = (Activity) App.context;
							a.finish();
						}

						@Override
						public void onConnectFailed(String error) {
							Toast.makeText(BluetoothActivity.this, error,
									Toast.LENGTH_SHORT).show();
							WaitingDialog.destoryWaitting();
						}
					});

		}
	};
	
	private OnClickListener onConnectNoListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			MyAlertDialog.destory();			
		}
	};
	
	private OnCancelListener onConnectCancelListener = new OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface arg0) {
			MyAlertDialog.destory();				
		}
	};
	
	
	
	

}
