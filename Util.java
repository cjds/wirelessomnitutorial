package com.harman.wirelessomni;

import java.util.ArrayList;
import java.util.List;

import com.harman.hkwirelessapi.DeviceObj;
import com.harman.hkwirelessapi.HKWirelessHandler;

public class Util {
	

	public static final int MSG_PCM_PLAY = 2;
	public static final int MSG_PCM_PAUSE = 3;

	public static final String MSG_TYPE_MUSIC = "msg";
	public static final String MSG_URL_MUSIC = "url";

    public static final String MSG_ACTIVITY = "com.harman.wirelessomni.MusicPlayerService";

	public class DeviceData {
		public DeviceObj deviceObj;
		public Boolean status;
	}

	private List<DeviceData> devices = new ArrayList<DeviceData>();

	public  HKWirelessHandler hkwireless = new HKWirelessHandler();
 	private static Util instance = new Util();


	public static Util getInstance() {
		return instance;
	}

	public void initDeviceInfor() {
		synchronized (this) {
			devices.clear();
			if (!hkwireless.isInitialized())return;
			
			for (int i=0; i<hkwireless.getDeviceCount(); i++) {
				DeviceData device = new DeviceData();
				device.deviceObj = hkwireless.getDeviceInfoByIndex(i);
				device.status = hkwireless.isDeviceActive(device.deviceObj.deviceId);
				devices.add(device);
			}
 		}
	}

	public void refreshDeviceInfoOnce(){
		hkwireless.refreshDeviceInfoOnce();
	}

	public List<DeviceData> getDevices() {
		return devices;
	}

	public boolean getDeviceStatus(int position) {
		synchronized (this) {
			return devices.get(position).status;
		}
	}

    public void updateDeviceStatus(long deviceid){
        for (int i=0; i<devices.size(); i++) {
            DeviceData device = devices.get(i);
            if (device.deviceObj.deviceId == deviceid) {
                device.deviceObj = hkwireless.findDeviceFromList(deviceid);
                if (device.deviceObj == null) {
                    devices.remove(i);
                } else {
                    device.status = hkwireless.isDeviceActive(device.deviceObj.deviceId);
                    devices.set(i, device);
                }
                break;
            }
        }
    }

	public boolean removeDeviceFromSession(long deviceid){
		boolean ret = hkwireless.removeDeviceFromSession(deviceid);
		if (ret) {
			updateDeviceStatus(deviceid);
		}
        return ret;
    }
	
	public boolean addDeviceToSession(long deviceid){
        boolean ret = hkwireless.addDeviceToSession(deviceid);
        if (ret) {
            updateDeviceStatus(deviceid);
        }
        return ret;
    }
}
