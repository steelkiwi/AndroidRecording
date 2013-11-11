package com.skd.androidrecordingtest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;

import com.skd.androidrecording.audio.AudioPlaybackManager;
import com.skd.androidrecording.video.PlaybackHandler;
import com.skd.androidrecording.visualizer.VisualizerView;
import com.skd.androidrecording.visualizer.renderer.BarGraphRenderer;

public class AudioPlaybackActivity extends Activity {
	public static String FileNameArg = "arg_filename";
	
	private static String fileName = null;
	
	private VisualizerView visualizerView;
	
	private AudioPlaybackManager playbackManager;
	
	private PlaybackHandler playbackHandler = new PlaybackHandler() {
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
		setContentView(R.layout.audio_play);
	
		Intent i = getIntent();
		if ((i != null) && (i.getExtras() != null)) {
			fileName = i.getExtras().getString(FileNameArg);
		}
		
		visualizerView = (VisualizerView) findViewById(R.id.visualizerView);
		setupVisualizer();
		
		playbackManager = new AudioPlaybackManager(this, visualizerView, playbackHandler);
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
	
	private void setupVisualizer() {
		Paint paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(200, 227, 69, 53));
        BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(2, paint, false);
        visualizerView.addRenderer(barGraphRendererBottom);
	}
}
