package com.harman.wirelessomni;

import com.harman.hkwirelessapi.AudioCodecHandler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MusicPlayerService extends Service {

	private AudioCodecHandler pcmCodec = new AudioCodecHandler();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null) {
			int cmd = intent.getIntExtra(Util.MSG_TYPE_MUSIC, 0);
			if (cmd == Util.MSG_PCM_PLAY) {
				final String url = intent.getStringExtra(Util.MSG_URL_MUSIC);
				String songName =  url.substring(url.lastIndexOf("/"));
				pcmCodec.playCAFFromCertainTime(url, songName, 0);
			} else if (cmd == Util.MSG_PCM_PAUSE) {
				pcmCodec.stop();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
}