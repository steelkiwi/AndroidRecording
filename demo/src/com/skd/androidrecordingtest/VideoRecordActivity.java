package com.skd.androidrecordingtest;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.skd.androidrecordingtest.utils.NotificationUtils;
import com.skd.androidrecordingtest.utils.StorageUtils;

public class VideoRecordActivity extends Activity implements SurfaceHolder.Callback {
	private static String fileName = null;
    
	private Button recordBtn;
	private SurfaceView videoView;
	
	private Camera camera;
	private MediaRecorder recorder;
	
	private boolean prepareRecording = false;
	private boolean startRecording = true;
	
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
		
		videoView = (SurfaceView) findViewById(R.id.videoView);
		videoView.getHolder().addCallback(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		recordStop();
		//pausePlaying();
	}
	
	@Override
	protected void onDestroy() {
		//finishPlaying();
		
		super.onDestroy();
	}
	
	//recording *********************************************************************************

	private void record() {
		if (!prepareRecording) {
			NotificationUtils.showInfoDialog(this, getString(R.string.videoPrepareError));
			return;
		}

        if (startRecording) {
        	//finishPlaying();
        	recordStart();
        }
        else {
        	recordStop();
        }
	}
	
	private void recordStart() {
		startRecording();
    	recordBtn.setText(R.string.stopRecordBtn);
    	startRecording = false;
	}
	
	private void recordStop() {
		stopRecording();
    	recordBtn.setText(R.string.recordBtn);
    	startRecording = true;
	}
	
	private void prepareRecording() {
		try {
			camera = Camera.open();
			camera.setPreviewDisplay(videoView.getHolder());
/*			Camera.Parameters parameters = camera.getParameters();
	        parameters.setPreviewSize(640, 480);
	        camera.setParameters(parameters);*/
	        camera.startPreview();
	        camera.unlock();
	        
	        recorder = new MediaRecorder();
	        recorder.setCamera(camera);
	        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	       	recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	       	recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	       	recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
	       	recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
	       	prepareOutputFile();
	    	recorder.setOutputFile(fileName);
	    	//recorder.setVideoSize(720, 1280);
	    	recorder.setPreviewDisplay(videoView.getHolder().getSurface());
	    	recorder.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startRecording() {
		if (recorder == null) {
			prepareRecording();
		}
		try {
			recorder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void stopRecording() {
    	if (recorder != null) {
    		try {
	    		recorder.stop();
    		} catch (IllegalStateException e) {
				e.printStackTrace();
			}
    		recorder.release();
			recorder = null;
    	}
    	if (camera != null) {
            try {
				camera.reconnect();
				camera.stopPreview();
	            camera.release();
	            camera = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    //surface ***********************************************************************************
    
    @Override
	public void surfaceCreated(SurfaceHolder holder) {
		prepareRecording();
		prepareRecording = true;
	}
    
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//finishRecording();
	}
	
	private void prepareOutputFile() {
		File f = new File(fileName);
		if (f.exists()) { f.delete(); }
	}
}
