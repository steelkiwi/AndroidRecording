package com.skd.androidrecordingtest;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.skd.androidrecording.video.AdaptiveSurfaceView;
import com.skd.androidrecording.video.CameraHelper;
import com.skd.androidrecording.video.VideoPlaybackHandler;
import com.skd.androidrecording.video.VideoPlaybackManager;
import com.skd.androidrecording.video.VideoRecordingHandler;
import com.skd.androidrecording.video.VideoRecordingManager;
import com.skd.androidrecordingtest.utils.NotificationUtils;
import com.skd.androidrecordingtest.utils.StorageUtils;

public class VideoRecordActivity extends Activity {
	private static String fileName = null;
    
	private Button recordBtn;
	private ImageButton switchBtn;
	private Spinner videoSizeSpinner;
	
	private Size videoSize = null;
	private VideoRecordingManager recordingManager;
	
	private VideoRecordingHandler recordingHandler = new VideoRecordingHandler() {
		@Override
		public boolean onPrepareRecording() {
			if (videoSizeSpinner == null) {
	    		initVideoSizeSpinner();
	    		return true;
			}
			return false;
		}
		
		@Override
		public Size getVideoSize() {
			return videoSize;
		}
		
		@Override
		public int getDisplayRotation() {
			return VideoRecordActivity.this.getWindowManager().getDefaultDisplay().getRotation();
		}
	};
	
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
		setContentView(R.layout.video);
		
		if (!StorageUtils.checkExternalStorageAvailable()) {
			NotificationUtils.showInfoDialog(this, getString(R.string.noExtStorageAvailable));
			return;
		}
		fileName = StorageUtils.getFileName(false);
		
		AdaptiveSurfaceView videoView = (AdaptiveSurfaceView) findViewById(R.id.videoView);
		recordingManager = new VideoRecordingManager(videoView, recordingHandler);
		
		recordBtn = (Button) findViewById(R.id.recordBtn);
		recordBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				record();
			}
		});
		
		switchBtn = (ImageButton) findViewById(R.id.switchBtn);
		if (recordingManager.getCameraManager().hasMultipleCameras()) {
			switchBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					switchCamera();
				}
			});
		}
		else {
			switchBtn.setVisibility(View.GONE);
		}	
		
		playbackManager = new VideoPlaybackManager(this, videoView, playbackHandler);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		playbackManager.showMediaController(); //TODO
	    return false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		playbackManager.pause();
	}
	
	@Override
	protected void onDestroy() {
		recordingManager.dispose();
		recordingHandler = null;
		playbackManager.dispose();
		playbackHandler = null;
		
		super.onDestroy();
	}
	
	@SuppressLint("NewApi")
	private void initVideoSizeSpinner() {
		videoSizeSpinner = (Spinner) findViewById(R.id.videoSizeSpinner);
		if (Build.VERSION.SDK_INT >= 11) {
			List<Size> sizes = CameraHelper.getCameraSupportedVideoSizes(recordingManager.getCameraManager().getCamera());
			videoSizeSpinner.setAdapter(new SizeAdapter(sizes));
			videoSizeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					videoSize = (Size) arg0.getItemAtPosition(arg2);
					recordingManager.setPreviewSize(videoSize);
				}
	
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			});
			videoSize = (Size) videoSizeSpinner.getItemAtPosition(0);
		}
		else {
			videoSizeSpinner.setVisibility(View.GONE);
		}
	}
	
	@SuppressLint("NewApi")
	private void updateVideoSizes() {
		if (Build.VERSION.SDK_INT >= 11) {
			((SizeAdapter) videoSizeSpinner.getAdapter()).set(CameraHelper.getCameraSupportedVideoSizes(recordingManager.getCameraManager().getCamera()));
			videoSizeSpinner.setSelection(0);
			videoSize = (Size) videoSizeSpinner.getItemAtPosition(0);
			recordingManager.setPreviewSize(videoSize);
		}
	}
	
	private void switchCamera() {
		recordingManager.getCameraManager().switchCamera();
		updateVideoSizes();
	}
	
	private void record() {
		if (recordingManager.stopRecording()) {
			recordBtn.setText(R.string.recordBtn);
			switchBtn.setEnabled(true);
			videoSizeSpinner.setEnabled(true);
			
    		playbackManager.getPlayerManager().getPlayer().reset(); //TODO
			switchDisplay(false);
			playbackManager.setupPlayback(fileName);
		}
		else {
			switchDisplay(true);
			
			startRecording();
		}
	}
	
	public void startRecording() {
		if (recordingManager.startRecording(fileName, videoSize)) {
			recordBtn.setText(R.string.stopRecordBtn);
			switchBtn.setEnabled(false);
			videoSizeSpinner.setEnabled(false);
			return;
		}
		Toast.makeText(this, getString(R.string.videoRecordingError), Toast.LENGTH_LONG).show();
	}
    
    public void switchDisplay(boolean isRecording) {
    	if (isRecording) {
    		playbackManager.hideMediaController();
    		playbackManager.getPlayerManager().stopPlaying();
    		playbackManager.getPlayerManager().setDisplay(null);
        	recordingManager.getCameraManager().setDisplay(recordingManager.getDisplay());
    	}
    	else {
    		recordingManager.getCameraManager().stopCameraPreview();
        	recordingManager.getCameraManager().setDisplay(null);
        	playbackManager.getPlayerManager().setDisplay(recordingManager.getDisplay());
    	}
    }
}
