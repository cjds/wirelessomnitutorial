package com.harman.wirelessomni;

import com.harman.hkwirelessapi.HKWirelessListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity {

	/**Linkto the song that's going to be played. Note the path may vary from device to device**/
	String url="/storage/emulated/0/Music/dog days are over.mp3";

	/**This is the key used to initialize the HK Wireless controller**/
	private static final String KEY = "2FA8-2FD6-C27D-47E8-A256-D011-3751-2BD6";

	/*Instance of the Utilities class created to maintain device state and update information about devices**/
	private Util util= Util.getInstance();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devicelist);

		/**Registering overriding methods for the Wireless Controller**/
		/** Over here you can override the methods to let your app react to
		 * events tht happen on the speaker
		 * **/
		util.hkwireless.registerHKWirelessControllerListener(new HKWirelessListener() {

			@Override
			public void onPlayEnded() {}

			@Override
			public void onPlaybackStateChanged(int arg0) {}

			@Override
			public void onPlaybackTimeChanged(int arg0) {}

			@Override
			public void onVolumeLevelChanged(long deviceId, int deviceVolume,int avgVolume) {}

			@Override
			public void onDeviceStateUpdated(long deviceId, int reason) {}

			@Override
			public void onErrorOccurred(int errorCode, String errorMsg) {
				Log.i("HKWirelessListener", "hkwErrorOccurred,errorCode=" + errorCode + "," + errorMsg);
				Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
			}
		});

		/**Initializing the HK Controller **/
		if (!util.hkwireless.isInitialized()) {
			util.hkwireless.initializeHKWirelessController(KEY);
			if (util.hkwireless.isInitialized()) {
				Toast.makeText(this, "Wireless controller init success", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Wireless controller init fail", Toast.LENGTH_LONG).show();
			}
		}
		/**This call will add the speakers connected to your WiFi **/
		util.initDeviceInfor();

		/*The listView for the list of devices on display*/
		ListView deviceList = (ListView)(this.findViewById(R.id.device_list));
		/*DeviceAdapter for the ListView of devices*/
		DeviceAdapter adapter = new DeviceAdapter(this);
		deviceList.setAdapter(adapter);

		//On Click for the the REFRESH button
		(this.findViewById(R.id.refresh_btn)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				util.refreshDeviceInfoOnce();
			}
		});

		//On click play button
		(this).findViewById(R.id.play_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				playMusic();
			}
		});

		//on click pause button
		(this.findViewById(R.id.pause_btn)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pauseMusic();
			}
		});
	}

	private void pauseMusic(){
		//start the service to run the Audio Codec
		//we add a message to play so that can pause the song
		Intent intent = new Intent();
		intent.putExtra(Util.MSG_URL_MUSIC, url);
		intent.putExtra(Util.MSG_TYPE_MUSIC, Util.MSG_PCM_PAUSE);
		intent.setPackage(MainActivity.this.getPackageName());
		MainActivity.this.startService(intent);
	}


	private void playMusic() {
		if (util.getDevices().size() == 0) {
			Toast.makeText(this, "No device connected", Toast.LENGTH_LONG).show();
			return;
		}
		List<Util.DeviceData> devices = util.getDevices();
		if (devices.size() <= 0){
			Toast.makeText(this, "No device in use", Toast.LENGTH_LONG).show();
			return;
		}

		for (int i=0; i<devices.size(); i++) {
			if (!util.getDeviceStatus(i)) {
				Toast.makeText(this, "No device in use", Toast.LENGTH_LONG).show();
				return;
			}
		}
		//start the service to run the Audio Codec
		//we add a message to play so that can play the song
		Intent intent = new Intent();
		intent.putExtra(Util.MSG_URL_MUSIC, url);
		intent.putExtra(Util.MSG_TYPE_MUSIC, Util.MSG_PCM_PLAY);
		intent.setPackage(this.getPackageName());
		this.startService(intent);
	}


	class DeviceAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public DeviceAdapter(Context context){
			this.mInflater=LayoutInflater.from(context);
		}

		public int getCount() {
			return util.getDevices().size();
		}

		public Object getItem(int position) {
			return util.getDevices().get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.device_list_item, null);
			final TextView textView = (TextView)convertView.findViewById(R.id.device_name);
			final CheckBox checkbox = (CheckBox)convertView.findViewById(R.id.select);


			textView.setText(util.getDevices().get(position).deviceObj.deviceName);
			if (util.getDeviceStatus(position)) {
				checkbox.setChecked(true);
			} else {
				checkbox.setChecked(false);
			}

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (util.getDeviceStatus(position)) {
						util.removeDeviceFromSession(util.getDevices().get(position).deviceObj.deviceId);
						checkbox.setChecked(false);
					} else {
						util.addDeviceToSession(util.getDevices().get(position).deviceObj.deviceId);
						checkbox.setChecked(true);
					}
				}
			});
			return convertView;
		}
	}

}
