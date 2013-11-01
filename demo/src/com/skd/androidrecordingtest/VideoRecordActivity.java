package com.skd.androidrecordingtest;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.skd.androidrecording.AdaptiveSurfaceView;
import com.skd.androidrecording.CameraHelper;
import com.skd.androidrecording.MediaRecorderHelper;
import com.skd.androidrecordingtest.utils.NotificationUtils;
import com.skd.androidrecordingtest.utils.StorageUtils;

public class VideoRecordActivity extends Activity implements SurfaceHolder.Callback {
	private static String fileName = null;
    
	private Button recordBtn;
	private Spinner previewSizeSpinner, videoSizeSpinner;
	private AdaptiveSurfaceView videoView;
	
	private Camera camera;
	private MediaRecorderHelper recorder;
	
	private Size previewSize = null, videoSize = null;
	private int cameraRotationDegree;
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
		
		recordBtn = (Button) findViewById(R.id.recordBtn);
		recordBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				record();
			}
		});
		
		videoView = (AdaptiveSurfaceView) findViewById(R.id.videoView);
		videoView.getHolder().addCallback(this);
		
		recorder = new MediaRecorderHelper();
	}
	
	private void initPreviewSizeSpinner() {
		previewSizeSpinner = (Spinner) findViewById(R.id.previewSizeSpinner);
		List<Size> sizes = camera.getParameters().getSupportedPreviewSizes();
		previewSizeSpinner.setAdapter(new PreviewSizeAdapter(sizes));
		previewSizeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				previewSize = (Size) arg0.getItemAtPosition(arg2);
				videoView.setPreviewSize(previewSize);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		previewSize = (Size) previewSizeSpinner.getItemAtPosition(0);
	}
	
	@SuppressLint("NewApi")
	private void initVideoSizeSpinner() {
		videoSizeSpinner = (Spinner) findViewById(R.id.videoSizeSpinner);
		if (Build.VERSION.SDK_INT >= 11) {
			List<Size> sizes = camera.getParameters().getSupportedVideoSizes();
			videoSizeSpinner.setAdapter(new PreviewSizeAdapter(sizes));
			videoSizeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					videoSize = (Size) arg0.getItemAtPosition(arg2);
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
	
	//previewing ********************************************************************************
	
	private void setupCamera(Size sz) {
		try {
			stopCameraPreviewIfNeeded();
			
			cameraRotationDegree = CameraHelper.setCameraDisplayOrientation(this, 0, camera); //TODO switch camera
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
	
	//recording *********************************************************************************

	private void record() {
		if (recorder.isRecording()) {
			recorder.stopRecording();
			recordBtn.setText(R.string.recordBtn);
			videoSizeSpinner.setEnabled(true);
		}
		else {
			tryToStartRecording();
		}
	}
	
	private void tryToStartRecording() {
		if (recorder.startRecording(camera, fileName, videoSize, cameraRotationDegree)) {
			recordBtn.setText(R.string.stopRecordBtn);
			videoSizeSpinner.setEnabled(false);
			return;
		}
		Toast.makeText(this, getString(R.string.videoRecordingError), Toast.LENGTH_LONG).show();
	}
	
    //surface holder callbacks ******************************************************************
    
    @Override
	public void surfaceCreated(SurfaceHolder holder) {
    	camera = Camera.open();
	}
    
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (previewSizeSpinner == null) {
    		initPreviewSizeSpinner();
    		initVideoSizeSpinner();
		}
    	else {
			setupCamera(previewSize);
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
}
