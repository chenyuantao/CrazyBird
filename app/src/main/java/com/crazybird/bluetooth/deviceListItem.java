package com.crazybird.bluetooth;

import java.io.Serializable;

public class deviceListItem implements Serializable {
	//钃濈墮鍚嶅瓧
	public String message;
	//鏄惁宸插尮閰?
	public boolean isSiri;

	public deviceListItem(String msg, boolean siri) {
		message = msg;
		isSiri = siri;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSiri() {
		return isSiri;
	}

	public void setSiri(boolean isSiri) {
		this.isSiri = isSiri;
	}
	
	
}
