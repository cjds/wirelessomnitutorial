package com.harman.wirelessomni;

import com.harman.hkwirelessapi.AudioCodecHandler;
import com.harman.hkwirelesscore.Util;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public class MusicPlayerService extends Service {

	private AudioCodecHandler pcmCodec = new AudioCodecHandler();

	private Thread mThread = null;
	private Handler mHandler = null;

	private static final int PCMCODEC_PLAY = 1;
	private static final int PCMCODEC_PAUSE = 3;

	private static final String URL_FLAG = "url:";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        //recorder = ExtAudioRecorder.getInstanse(false);
        mThread = new PcmCodecThread();
        mThread.start();
    }
	
	@Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mThread.stop();
    }

	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int cmd = 0;
		if (intent != null)
		{
			cmd = intent.getIntExtra(Util.MSG_TYPE_MUSIC, 0);
			if(cmd == Util.MSG_PCM_PLAY) {
				final String url = intent.getStringExtra(Util.MSG_URL_MUSIC);
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putString(URL_FLAG, url);
				msg.what = PCMCODEC_PLAY;
				msg.setData(bundle);
				mHandler.sendMessage(msg);
			} else if(cmd == Util.MSG_PCM_PAUSE) {
				Message msg = new Message();
				msg.what = PCMCODEC_PAUSE;
				mHandler.sendMessage(msg);
				//stop();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	class PcmCodecThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            mHandler = new Handler(){
                @SuppressWarnings("static-access")
				public void handleMessage (Message msg) {
                	Bundle bundle = msg.getData();
                    switch(msg.what) {
                    case PCMCODEC_PLAY:
                    	String url = bundle.getString(URL_FLAG);
                    	playMusic(url, Util.getInstance().getMusicTimeElapse());
                        break;
                    case PCMCODEC_PAUSE:
                    	stopMusic();
                    	break;
                    default:
                    	break;
                    }
                }
            };
            Looper.loop();
        }
    }
	

	private void playMusic(String url, int time) {
		String songName = null;
		int indx = url.lastIndexOf("/");
		songName = url.substring(indx);
		pcmCodec.playCAFFromCertainTime(url, songName, time);
	}

	private void stopMusic(){
		pcmCodec.stop();
	}
	
}