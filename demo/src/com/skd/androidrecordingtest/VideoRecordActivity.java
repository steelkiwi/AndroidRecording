package com.skd.androidrecordingtest;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.Spinner;
import android.widget.Toast;

import com.skd.androidrecording.AdaptiveSurfaceView;
import com.skd.androidrecording.CameraHelper;
import com.skd.androidrecording.MediaRecorderHelper;
import com.skd.androidrecordingtest.utils.NotificationUtils;
import com.skd.androidrecordingtest.utils.StorageUtils;

public class VideoRecordActivity extends Activity implements SurfaceHolder.Callback, OnPreparedListener, MediaPlayerControl, OnCompletionListener {
	private static String fileName = null;
    
	private Button recordBtn;
	private ImageButton switchBtn;
	private Spinner videoSizeSpinner;
	private AdaptiveSurfaceView videoView;
	
	private Camera camera;
	private MediaRecorderHelper recorder;
	private MediaPlayer player = null;
	private MediaController controller = null;
	
	private int camerasCnt, defaultCameraID;
	private int cameraRotationDegree;
	private Size videoSize = null;
	private boolean isPreviewStarted = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		
		if (!StorageUtils.checkExternalStorageAvailable()) {
			NotificationUtils.showInfoDialog(this, getString(R.string.noExtStorageAvailable));
			return;
		}
		fileName = StorageUtils.getFileName(false);
		
		camerasCnt = CameraHelper.getAvailableCamerasCount();
		defaultCameraID = CameraHelper.getDefaultCameraID();
		
		recordBtn = (Button) findViewById(R.id.recordBtn);
		recordBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				record();
			}
		});
		
		switchBtn = (ImageButton) findViewById(R.id.switchBtn);
		if (camerasCnt > 1) {
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
		
		videoView = (AdaptiveSurfaceView) findViewById(R.id.videoView);
		videoView.getHolder().addCallback(this);
		
		recorder = new MediaRecorderHelper();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		pausePlaying();
	}
	
	@Override
	protected void onDestroy() {
		finishPlaying();
		
		super.onDestroy();
	}
	
	@SuppressLint("NewApi")
	private void initVideoSizeSpinner() {
		videoSizeSpinner = (Spinner) findViewById(R.id.videoSizeSpinner);
		if (Build.VERSION.SDK_INT >= 11) {
			List<Size> sizes = camera.getParameters().getSupportedVideoSizes();
			videoSizeSpinner.setAdapter(new SizeAdapter(sizes, false));
			videoSizeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					videoSize = (Size) arg0.getItemAtPosition(arg2);
					videoView.setPreviewSize(videoSize);
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
			((SizeAdapter) videoSizeSpinner.getAdapter()).set(camera.getParameters().getSupportedVideoSizes());
			videoSizeSpinner.setSelection(0);
			videoSize = (Size) videoSizeSpinner.getItemAtPosition(0);
			videoView.setPreviewSize(videoSize);
		}
	}
	
	//previewing ********************************************************************************
	
	private void setupCamera(Size sz) {
		try {
			stopCameraPreviewIfNeeded();
			
			cameraRotationDegree = CameraHelper.setCameraDisplayOrientation(this, defaultCameraID, camera);
			Parameters param = camera.getParameters();
			param.setPreviewSize(sz.width, sz.height);
			camera.setParameters(param);
			
			camera.setPreviewDisplay(videoView.getHolder());
			
			camera.startPreview();
			isPreviewStarted = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void stopCameraPreviewIfNeeded() {
		if (isPreviewStarted) {
			camera.stopPreview();
		}
	}
	
	private void switchCamera() {
		stopCameraPreviewIfNeeded();
		camera.release();
		camera = null;
		
		defaultCameraID = (defaultCameraID + 1) % camerasCnt;
		camera = Camera.open(defaultCameraID);
	
		updateVideoSizes();
	}
	
	//recording *********************************************************************************

	private void record() {
		if (recorder.isRecording()) {
			recorder.stopRecording();
			recordBtn.setText(R.string.recordBtn);
			switchBtn.setEnabled(true);
			videoSizeSpinner.setEnabled(true);
			
			preparePlaying();
		}
		else {
			finishPlaying();
			
			tryToStartRecording();
		}
	}
	
	private void tryToStartRecording() {
		int degree = (CameraHelper.isCameraFacingBack(defaultCameraID)) ? cameraRotationDegree : cameraRotationDegree + 180;
		if (recorder.startRecording(camera, fileName, videoSize, degree)) {
			recordBtn.setText(R.string.stopRecordBtn);
			switchBtn.setEnabled(false);
			videoSizeSpinner.setEnabled(false);
			return;
		}
		Toast.makeText(this, getString(R.string.videoRecordingError), Toast.LENGTH_LONG).show();
	}
	
    //surface holder callbacks ******************************************************************
    
    @Override
	public void surfaceCreated(SurfaceHolder holder) {
    	camera = Camera.open(defaultCameraID);
	}
    
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (videoSizeSpinner == null) {
    		initVideoSizeSpinner();
		}
    	else {
			setupCamera(videoSize);
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (recorder.isRecording()) {
			recorder.stopRecording();
		}
		camera.release();
		camera = null;
	}
	
	//playing ***********************************************************************************
	
	private void preparePlaying() {
		player = new MediaPlayer();
        player.setOnPreparedListener(this);
        try {
        	if (camera != null) {
        		stopCameraPreviewIfNeeded();
        		camera.setPreviewDisplay(null);
        	}
        	
        	player.setDataSource(fileName);
        	player.setDisplay(videoView.getHolder());
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void startPlaying() {
		if (player != null) {
	        player.start();
    	}
    }

    private void pausePlaying() {
    	if (player != null) {
			player.pause();
		}
    }

    private void stopPlaying() {
    	if (player != null) {
	        player.stop();
    	}
    }
    
    private void finishPlaying() {
    	try {
	    	if (player != null) {
	    		player.stop();
	    		player.setDisplay(null);
	    		if (camera != null) {
					camera.setPreviewDisplay(videoView.getHolder());
	        	}
		        player.release();
		        player = null;
	    	}
	    	if (controller != null) {
	    		controller.hide();
	    		controller = null;
	    	}
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    //media player and controller ***************************************************************
    
	@Override
	public void onPrepared(MediaPlayer mp) {
        controller = new MediaController(this);
		controller.setMediaPlayer(this);
	    controller.setAnchorView(findViewById(R.id.videoView));

	    runOnUiThread(new Runnable() {
	    	public void run() {
		        controller.setEnabled(true);
		        controller.show();
	    	}
	    });
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		stopPlaying();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (controller != null) {
			controller.show();
		}
	    return false;
	}
	
	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		if (player != null) {
			return player.getCurrentPosition();
		}
		return 0;
	}

	@Override
	public int getDuration() {
		if (player != null) {
			return player.getDuration();
		}
		return 0;
	}

	@Override
	public boolean isPlaying() {
		if (player != null) {
			return player.isPlaying();
		}
		return false;
	}

	@Override
	public void pause() {
		pausePlaying();
	}

	@Override
	public void seekTo(int arg0) {
		if (player != null) {
			player.seekTo(arg0);
		}
	}

	@Override
	public void start() {
		startPlaying();
	}
}
