package com.crazybird.bluetooth;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

public class Bluetooth {
	public static boolean isClient = false;
	private static final int ON_SHUT_DOWN = 0;
	private static final int ON_CONNECT_SUCCESS = 1;
	private static final int ON_CONNECT_FAILED = 2;
	private static final int ON_COMMAND_RECEIVE = 3;
	/* 处理返回结果 */
	private static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ON_CONNECT_FAILED:
				mConnectListener.onConnectFailed(msg.obj.toString());
				break;
			case ON_CONNECT_SUCCESS:
				if (msg.obj.toString().equals("ok")) {
					if (!isConnected && mConnectListener != null) {
						isConnected = true;
						mConnectListener.onConnectSuccess();
					}
					// 重置心跳计数
					mHeartCount = 0;
				} else if (msg.obj.toString().equals("shutdown")) {
					if (isConnected && mCommandListener != null) {						
						isConnected = false;
						mCommandListener.onShutDown();
						if (isClient) {
							stopClient();
						} else {
							stopServer();
						}
					}
				}
				break;
			case ON_SHUT_DOWN:
				if (isConnected && mCommandListener != null) {					
					isConnected = false;
					mCommandListener.onShutDown();
					if (isClient) {
						stopClient();
					} else {
						stopServer();
					}
				}
				break;
			case ON_COMMAND_RECEIVE:
				if (isConnected && mCommandListener != null) {
					mCommandListener.onReceiveCommand(msg.obj.toString());
				}
				break;
			}
		}
	};

	// 连接回调接口
	private static BluetoothConnectListener mConnectListener;
	// 是否已连接
	public static boolean isConnected = false;
	/* 设备地址 */
	private static String mAddress;
	// 心跳连接计数器
	private static int mHeartCount;
	// 心跳定时器
	private static Timer mConnectTimer;
	// 心跳任务
	private static TimerTask mConnectTask;
	// 取得默认的蓝牙适配器
	private static BluetoothAdapter mAdapter = BluetoothAdapter
			.getDefaultAdapter();
	// 蓝牙设备
	private static BluetoothDevice mDevice = null;
	// 蓝牙socket
	private static BluetoothSocket mSocket;
	// 读取协议，可添加读取接口
	private static readThread mReadThread = null;
	// 客户端线程
	private static ClientThread mClientThread = null;

	/* 启动客户端 */
	public static synchronized void startClient(String _address,
			BluetoothConnectListener _listener) {
		mConnectListener = _listener;
		if (isConnected) { // 若已经连接则启动失败
			mConnectListener.onConnectFailed("连接已建立，请断开后重试。");
			return;
		}
		mAddress = _address; // 赋值需要连接的蓝牙设备地址
		if (!mAddress.isEmpty()) { // 若不为空则启动成功
			// 将心跳计数归零
			mHeartCount = 0;
			// 根据蓝牙设备地址获取蓝牙设备
			mDevice = mAdapter.getRemoteDevice(mAddress);
			// 开启客户端
			mClientThread = new ClientThread();
			new Thread(mClientThread).start();
		} else {
			mConnectListener.onConnectFailed("蓝牙设备地址为空"); // 若为空则启动失败
		}
	}

	public static void stopClient() {
		if (isConnected) {
			isConnected = false;
		}
		if (mConnectTimer != null) {
			mConnectTimer.cancel();
			mConnectTimer = null;
		}
		if (mReadThread != null) {
			mReadThread.close();
			mReadThread = null;
		}
		if (mCommandListener != null) {
			mCommandListener = null;
		}
		if (mSocket != null) {
			try {
				mSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mSocket = null;
		}
		if (mConnectListener != null) {
			mConnectListener = null;
		}
	}

	public static void startServer(BluetoothConnectListener _listener) {
		mConnectListener = _listener;
		if (isConnected) { // 若已经连接则启动失败
			mConnectListener.onConnectFailed("连接已建立，请断开后重试。");
			return;
		}
		mServerThread = new ServerThread();
		new Thread(mServerThread).start();
		mHeartCount = 0;
		// 将心跳计数归零
		startConnectThread();
	}

	public static void stopServer() {
		isInterrupt = false;
		if (isConnected) {
			if (mConnectListener != null) {
				mConnectListener = null;
			}
			isConnected = false;
		}
		if (mConnectTimer != null) {
			mConnectTimer.cancel();
			mConnectTimer = null;
		}
		if (mServerThread != null) {
			mServerThread = null;
		}
		if (mServerSocket != null) {
			try {
				mServerSocket.close();
			} catch (IOException e) {
			}
		}
		if (mSocket != null) {
			try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSocket = null;
		}
		if (mReadThread != null) {
			mReadThread.close();
			mReadThread = null;
		}
		if (mCommandListener != null) {
			mCommandListener = null;
		}
	}

	/* 开启客户端的线程 */
	private static class ClientThread implements Runnable {

		public void run() {
			try {
				mSocket = mDevice.createRfcommSocketToServiceRecord(UUID
						.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				mSocket.connect();
			} catch (Exception e) {
				try {
					mSocket = mDevice
							.createRfcommSocketToServiceRecord(UUID
									.fromString("00000000-0000-1000-8000-00805F9B34FB"));
					mSocket.connect();
				} catch (Exception e1) {
					try {
						mSocket = (BluetoothSocket) mDevice
								.getClass()
								.getMethod("createRfcommSocket",
										new Class[] { int.class })
								.invoke(mDevice, 1);
					} catch (Exception e2) {
						try {
							mSocket = (BluetoothSocket) mDevice
									.getClass()
									.getMethod("createRfcommSocket",
											new Class[] { int.class })
									.invoke(mDevice, 2);
						} catch (Exception e3) {
							Message message = new Message();
							message.what = ON_CONNECT_FAILED;
							message.obj = "打开蓝牙失败";
							handler.sendMessage(message);
							return;
						}
					}
				}
			}
			// 启动连接线程
			startConnectThread();
			// 启动接受数据
			mReadThread = readThread.getInstance(mSocket);
			// 添加连接接收器
			mReadThread.addOnReceiverListener("connect", mConnectReiceiver);
			new Thread(mReadThread).start();
		}
	}

	/* 保持连接 */
	private static void startConnectThread() {
		// 启动连接线程
		mConnectTimer = new Timer();
		// 执行的任务
		mConnectTask = new TimerTask() {

			// 开启run方法
			@Override
			public void run() {
				if (isConnected) {
					// 若发送3次服务端没有回应,则断开
					if (mHeartCount == 3) {
						handler.sendEmptyMessage(ON_SHUT_DOWN);
						// 取消执行任务
						mConnectTimer.cancel();
					} else {
						// 若未断开,则每次向服务端发送个请求
						sendMessageHandle("connect,ok");
						// 因为最多发3次,若无响应就断开连接,所以每次发送次数加1
						mHeartCount++;
					}
				} else {
					sendMessageHandle("connect,ok");
				}
			}
		};
		// 0.5秒以后毎0.5秒发送一次心跳连接
		mConnectTimer.schedule(mConnectTask, 500, 500);
	}

	/* 发送数据 */
	public static void sendMessageHandle(String msg) {

		if (mSocket == null) {
			return;
		}
		try {
			OutputStream os = mSocket.getOutputStream();
			os.write(msg.getBytes());
//			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 连接接收器 */
	private static OnReadReiceiver mConnectReiceiver = new OnReadReiceiver() {

		@Override
		public void OnreadForStr(String str) {
			Message message = new Message();
			message.what = ON_CONNECT_SUCCESS;
			message.obj = str;
			handler.sendMessage(message);
		}
	};

	/* 命令接收器 */
	private static OnReadReiceiver mCommandReiceiver = new OnReadReiceiver() {

		@Override
		public void OnreadForStr(String str) {
			Message message = new Message();
			message.what = ON_COMMAND_RECEIVE;
			message.obj = str;
			handler.sendMessage(message);
		}
	};

	private static Boolean isInterrupt = false;
	private static BluetoothServerSocket mServerSocket = null;
	private static ServerThread mServerThread = null;
	/* 一些常量，代表服务器的名称 */
	private static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";
	private static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
	private static final String PROTOCOL_SCHEME_BT_OBEX = "btgoep";
	private static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";

	// 开启服务器
	private static class ServerThread extends Thread {
		public void run() {

			try {
				isInterrupt = true;
				mServerSocket = mAdapter
						.listenUsingRfcommWithServiceRecord(
								PROTOCOL_SCHEME_RFCOMM,
								UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				while (isInterrupt) {
					isInterrupt = false;
					/* 接受客户端的连接请求 */
					mSocket = mServerSocket.accept();
					// 启动接受数据
					mReadThread = readThread.getInstance(mSocket);
					// 添加连接接收器
					mReadThread.addOnReceiverListener("connect",
							mConnectReiceiver);
					new Thread(mReadThread).start();
				}
			} catch (IOException e) {
				Message msg = new Message();
				msg.obj = "启动蓝牙服务器失败";
				msg.what = ON_CONNECT_FAILED;
				handler.sendMessage(msg);
			}
		}
	};

	public interface BluetoothConnectListener {
		public void onConnectSuccess();

		public void onConnectFailed(String error);
	}

	public interface CommandListener {
		public void onReceiveCommand(String command);

		public void onShutDown();
	}

	private static CommandListener mCommandListener;

	public static void setCommandListener(CommandListener commandListener) {
		mCommandListener = commandListener;
		if (mReadThread != null) {
			// 添加命令接收器
			mReadThread.addOnReceiverListener("command", mCommandReiceiver);
		}
	}

}
