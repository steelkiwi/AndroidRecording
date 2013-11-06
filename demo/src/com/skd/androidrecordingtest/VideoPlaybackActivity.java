package com.skd.androidrecordingtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.skd.androidrecording.video.AdaptiveSurfaceView;
import com.skd.androidrecording.video.VideoPlaybackHandler;
import com.skd.androidrecording.video.VideoPlaybackManager;

public class VideoPlaybackActivity extends Activity {
	public static String FileNameArg = "arg_filename";
	
	private static String fileName = null;
	
	private AdaptiveSurfaceView videoView;
	
	private VideoPlaybackManager playbackManager;
	
	private VideoPlaybackHandler playbackHandler = new VideoPlaybackHandler() {
		@Override
		public void onPreparePlayback() {
			runOnUiThread (new Runnable() {
		    	public void run() {
		    		playbackManager.showMediaController();
		    	}
		    });
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play);
	
		Intent i = getIntent();
		if ((i != null) && (i.getExtras() != null)) {
			fileName = i.getExtras().getString(FileNameArg);
		}
		
		videoView = (AdaptiveSurfaceView) findViewById(R.id.videoView);
		
		playbackManager = new VideoPlaybackManager(this, videoView, playbackHandler);
		playbackManager.setupPlayback(fileName);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		playbackManager.showMediaController();
	    return false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		playbackManager.pause();
		playbackManager.hideMediaController();
	}
	
	@Override
	protected void onDestroy() {
		playbackManager.dispose();
		playbackHandler = null;
		
		super.onDestroy();
	}
}
