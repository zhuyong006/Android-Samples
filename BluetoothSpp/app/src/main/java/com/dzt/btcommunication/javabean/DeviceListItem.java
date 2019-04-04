package com.dzt.btcommunication.javabean;

/**
 * Created by M02323 on 2017/9/12.
 */

public class DeviceListItem {
	public String message;
	public boolean isSiri;

	public DeviceListItem(String msg, boolean siri) {
		message = msg;
		isSiri = siri;
	}
}
