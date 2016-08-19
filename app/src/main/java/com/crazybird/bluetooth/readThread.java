package com.crazybird.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import android.bluetooth.BluetoothSocket;

public class readThread implements Runnable {
	private static readThread thread;
	private Map<String, OnReadReiceiver> map = new HashMap<String, OnReadReiceiver>();
	// 客户端协议
	private static BluetoothSocket socket = null;
	private static boolean isRead;

	public void close() {
		isRead = false;
	}

	public static readThread getInstance(BluetoothSocket _socket) {
		socket = _socket;
		isRead = true;
		if (thread == null) {
			thread = new readThread();
		}
		return thread;
	}

	@Override
	public void run() {
		String last = "";
		byte[] buffer = new byte[1024];
		int bytes;
		InputStream mmInStream = null;
		try {
			// 读取蓝牙发送的信息
			mmInStream = socket.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (isRead) {
			try {
				if ((bytes = mmInStream.read(buffer)) > 0) {
					byte[] buf_data = new byte[bytes];
					for (int i = 0; i < bytes; i++) {
						buf_data[i] = buffer[i];
					}
					// 将字节重新赋给一个字符串变量
					String s = new String(buf_data);
					if (s.contains("<") && s.contains(">")) {						
						String[] result = s.split(",");
						OnReadReiceiver receiver = map.get("command");
						for (String game : result) {
							if (receiver != null) {
								// 调用接口将字符串传过去
								receiver.OnreadForStr(game.replace("<", "")
										.replace(">", ""));
							}
						}
					} else {
						// 并通过逗号将他们分隔开来,以便于好分辨在哪个类里用
						String[] sb = s.split(",");
						String sf = "";
						// 如果返回的字符串等于1的话,就将整个赋给sf
						if (sb.length == 1) {
							sf = sb[0];
						} else {
							// 否则将此字符串截取,去掉前面部分的Mark
							sf = s.substring(sb[0].length() + 1);
						}
						OnReadReiceiver receiver = map.get(sb[0].trim());
						if (receiver != null) {
							// 调用接口将字符串传过去
							receiver.OnreadForStr(sf);
						}
					}

				}
			} catch (IOException e) {
				try {
					mmInStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	// 添加接口和Mark
	public void addOnReceiverListener(String key, OnReadReiceiver receiver) {
		this.map.put(key, receiver);
	}

}
