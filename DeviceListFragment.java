package com.harman.wirelessomni;

import com.harman.hkwirelessapi.DeviceObj;
import com.harman.hkwirelessapi.HKWirelessHandler;
import com.harman.hkwirelesscore.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceListFragment extends Fragment {
	
	private ListView deviceList = null;
	private Button btnRefresh = null;
	private DeviceAdapter adapter;

	private HKWirelessHandler hkwireless = new HKWirelessHandler();

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			adapter.notifyDataSetChanged();
		}
	};

	@Override  
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.devicelist, container, false);
		return fragmentView;
	}

	@Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);

        deviceList = (ListView)(getActivity().findViewById(R.id.device_list));
        Util.getInstance().initDeviceInfor();

        adapter = new DeviceAdapter(getActivity());
        deviceList.setAdapter(adapter);

        btnRefresh = (Button)(getActivity().findViewById(R.id.refresh_btn));
        btnRefresh.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v)
        	{
        		hkwireless.refreshDeviceInfoOnce();
        	}
        });
    }


	
	class DeviceAdapter extends BaseAdapter{
        private LayoutInflater mInflater;

        public DeviceAdapter(Context context){
            this.mInflater=LayoutInflater.from(context);
        }
        
        public int getCount() {
            return Util.getInstance().getDevices().size();
        }
        
        public Object getItem(int position) {
            return Util.getInstance().getDevices().get(position);
        }
        
        public long getItemId(int position) {
            return position;
        }
        
        public View getView(final int position, View convertView, ViewGroup parent) {
            final CheckBox checkbox;
            final TextView textView;
            convertView = mInflater.inflate(R.layout.device_list_item, null);
            textView = (TextView)convertView.findViewById(R.id.device_name);
            checkbox = (CheckBox)convertView.findViewById(R.id.select);

            final String s = (String)Util.getInstance().getDevices().get(position).deviceObj.deviceName;
            textView.setText(s);
            if (Util.getInstance().getDeviceStatus(position)) {
            	 checkbox.setChecked(true);
            } else {
            	 checkbox.setChecked(false);
            }
            
            convertView.setClickable(true);
            convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (Util.getInstance().getDeviceStatus(position)) {
						Util.getInstance().removeDeviceFromSession(Util.getInstance().getDevices().get(position).deviceObj.deviceId);
						checkbox.setChecked(false);
		            } else {
		            	Util.getInstance().addDeviceToSession(Util.getInstance().getDevices().get(position).deviceObj.deviceId);
		            	checkbox.setChecked(true);
		            }
				}
            });
            return convertView;
        }
    }

}
