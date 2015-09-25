package com.harman.wirelessomni;

import java.util.ArrayList;
import java.util.List;

import com.harman.hkwirelesscore.Util;
import com.harman.hkwirelesscore.Util.DeviceData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MusicPlayerFragment extends Fragment {
	
	long id;
	String url;
	String title="";

	private ListView lvMusic = null;

	private boolean canPlay = true;//false;
	private boolean canPause= false;




	@Override  
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.musicplayer, container, false);
		return fragmentView;
	}

	
	@Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);

		Intent intent = new Intent();
		intent.putExtra(Util.MSG_TYPE_MUSIC, Util.MSG_PCM_INIT);
		intent.setAction(Util.MUSICPLAYER);
		intent.setPackage(getActivity().getPackageName());
		getActivity().startService(intent);

		lvMusic = (ListView)(getActivity().findViewById(R.id.music_list));

		Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (cursor == null)
			return;
		cursor.moveToFirst();


		id=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
		title=cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
		url=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

		
		lvMusic.setOnItemClickListener(new musicListItemClickListener());  
		lvMusic.setAdapter(new MusicAdapter(getActivity()));
    }  



	private class musicListItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if(canPlay)
				playMusic();
			else
				pauseMusic();
			return;
		}
	}
	

	private void playMusic() {

		if (Util.getInstance().getDevices().size() == 0) {
			Toast.makeText(getActivity(), "No device connected", Toast.LENGTH_LONG).show();
			return;
		}

		List<DeviceData> devices = Util.getInstance().getDevices();

		if (devices.size() <= 0){
			Toast.makeText(getActivity(), "No device in use", Toast.LENGTH_LONG).show();
			return;
		}


		for (int i=0; i<devices.size(); i++) {
			if (!Util.getInstance().getDeviceStatus(i)) {
				Toast.makeText(getActivity(), "No device in use", Toast.LENGTH_LONG).show();
				return;
			}
		}


		if (canPlay) {
			canPlay = false;
			canPause = true;
		}
		Intent intent = new Intent();
		intent.putExtra(Util.MSG_URL_MUSIC, url);
		intent.putExtra(Util.MSG_TYPE_MUSIC, Util.MSG_PCM_PLAY);
		intent.setAction(Util.MUSICPLAYER); 
		intent.setPackage(getActivity().getPackageName());
		getActivity().startService(intent);
	}



	
	private void pauseMusic() {
		if (canPause) {
			canPause = false;
			canPlay = true;
		}
		Intent intent = new Intent();
		intent.putExtra(Util.MSG_URL_MUSIC, url);
		intent.putExtra(Util.MSG_TYPE_MUSIC, Util.MSG_PCM_PAUSE);
		intent.setAction(Util.MUSICPLAYER);
		intent.setPackage(getActivity().getPackageName());
		getActivity().startService(intent);
	}

	class MusicAdapter extends BaseAdapter{
        private LayoutInflater mInflater;
        private Context context = null;

        public MusicAdapter(Context context){
            this.mInflater=LayoutInflater.from(context);
            this.context = context;
        }
        
        public int getCount() {
            return 1;
        }
        
        public Object getItem(int position) {
            return "";
        }
        
        public long getItemId(int position) {
            return position;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = mInflater.inflate(R.layout.musicplayer_list_item, null);
			TextView title= (TextView)convertView.findViewById(R.id.music_title);
			TextView music= (TextView)convertView.findViewById(R.id.music_name);
            title.setText(MusicPlayerFragment.this.title);
			String url =MusicPlayerFragment.this.url;
			int lastDiv = url.lastIndexOf("/");

            final String name = url.substring(lastDiv+1, url.length());
            music.setText(name);

            return convertView;
        }
    }
	

}
