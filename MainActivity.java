package com.harman.wirelessomni;

import com.harman.hkwirelessapi.HKErrorCode;
import com.harman.hkwirelessapi.HKPlayerState;
import com.harman.hkwirelessapi.HKWirelessListener;
import com.harman.hkwirelesscore.Util;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends FragmentActivity  {
	
	private ViewPager mViewPager = null;

	public final static int TAB_INDEX_DEVICELIST = 0;  
	public final static int TAB_INDEX_MUSICPLAYER = 1;  

	public final static int TAB_COUNT = 2;
	

	private DeviceListFragment deviceListFragment = null;
	private MusicPlayerFragment musicPlayerFragment = null;

	
	private final String ERROR_CODE = "error_code";
	private final String ERROR_MSG = "error_msg";
	private int lastErrorCode = -1;

	private Context context = null;
	private static final String KEY = "2FA8-2FD6-C27D-47E8-A256-D011-3751-2BD6";

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			String errorMsg = bundle.getString(ERROR_MSG);
			int errorCode = bundle.getInt(ERROR_CODE, -1);
			if (lastErrorCode == errorCode && errorCode == HKErrorCode.ERROR_DISC_TIMEOUT.ordinal())
				return;
			Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
			lastErrorCode = errorCode;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = this;
		
		Util.hkwireless.registerHKWirelessControllerListener(new HKWirelessListener(){

			@Override
			public void onPlayEnded() {
				// TODO Auto-generated method stub
				Util.getInstance().setMusicTimeElapse(0);

			}

			@Override
			public void onPlaybackStateChanged(int arg0) {
				// TODO Auto-generated method stub
				if (arg0 == HKPlayerState.EPlayerState_Stop.ordinal())
					Util.getInstance().setMusicTimeElapse(0);
			}

			@Override
			public void onPlaybackTimeChanged(int arg0) {
				// TODO Auto-generated method stub
				Util.getInstance().setMusicTimeElapse(arg0);
			}

			@Override
			public void onVolumeLevelChanged(long deviceId, int deviceVolume,
					int avgVolume) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onDeviceStateUpdated(long deviceId, int reason) {
				// TODO Auto-generated method stub
				Util.getInstance().updateDeviceInfor(deviceId);
				if (deviceListFragment != null) {
					deviceListFragment.handler.sendMessage(new Message());
				}
			}

			@Override
			public void onErrorOccurred(int errorCode, String errorMesg) {
				// TODO Auto-generated method stub
				Log.i("HKWirelessListener","hkwErrorOccurred,errorCode="+errorCode+",errorMesg="+errorMesg);

				Message errMsg = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt(ERROR_CODE, errorCode);
				bundle.putString(ERROR_MSG, errorMesg);
				errMsg.setData(bundle);
				handler.sendMessage(errMsg);

			}
		});

		if (!Util.hkwireless.isInitialized()) {
			Util.hkwireless.initializeHKWirelessController(KEY);
			if (Util.hkwireless.isInitialized()) {
				Toast.makeText(this, "Wireless controller init success", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Wireless controller init fail", Toast.LENGTH_LONG).show();
			}
		}

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		
		mViewPager = (ViewPager)findViewById(R.id.pager);
		getFragmentManager();
		
		mViewPager.setAdapter(new viewPagerAdapter(getSupportFragmentManager()));  
		mViewPager.setOnPageChangeListener(new pagerListener());  
		mViewPager.setCurrentItem(TAB_INDEX_DEVICELIST);
		
		setupDeviceList();
		setupMusicPlayer();

	}

	private void setupDeviceList(){
		Tab tab = this.getActionBar().newTab();
		tab.setContentDescription(getString(R.string.device_list));
		tab.setText(getString(R.string.device_list));
		tab.setTabListener(mTabListener);
		getActionBar().addTab(tab);
	}
	
	private void setupMusicPlayer(){
		Tab tab = this.getActionBar().newTab();
		tab.setContentDescription(getString(R.string.music_player));
		tab.setText(getString(R.string.music_player));
		tab.setTabListener(mTabListener);
		getActionBar().addTab(tab);
	}

	
	private final TabListener mTabListener = new TabListener() {
		private final static String TAG = "TabListener";
		
		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			if (mViewPager != null)
				mViewPager.setCurrentItem(tab.getPosition());
		}
		
		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}
	}; 
	
	class pagerListener implements OnPageChangeListener{
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onPageSelected(int arg0) {
			getActionBar().selectTab(getActionBar().getTabAt(arg0));
		}
	}
	
	public class viewPagerAdapter extends FragmentPagerAdapter {
		
		public viewPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			switch (arg0) {
			case TAB_INDEX_DEVICELIST:
				return (deviceListFragment = new DeviceListFragment());
			case TAB_INDEX_MUSICPLAYER:
				return (musicPlayerFragment = new MusicPlayerFragment());
			}
			throw new IllegalStateException("No fragment at position " + arg0);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return TAB_COUNT;
		}
	}  


}
