package com.skd.androidrecording.video;

import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

public class VideoRecordingManager implements SurfaceHolder.Callback {
	
	private AdaptiveSurfaceView videoView;
	private CameraManager cameraManager;
	private MediaRecorderManager recorderManager;
	private VideoRecordingHandler recordingHandler;
	
	public VideoRecordingManager(AdaptiveSurfaceView videoView, VideoRecordingHandler recordingHandler) {
		this.videoView = videoView;
		this.videoView.getHolder().addCallback(this);
		this.cameraManager = new CameraManager();
		this.recorderManager = new MediaRecorderManager();
		this.recordingHandler = recordingHandler;
	}
	
	public boolean startRecording(String fileName, Size videoSize) {
		int degree = cameraManager.getCameraDisplayOrientation();
		return recorderManager.startRecording(cameraManager.getCamera(), fileName, videoSize, degree);
	}
	
	public boolean stopRecording() {
		return recorderManager.stopRecording();
	}

	public void setPreviewSize(Size videoSize) {
		videoView.setPreviewSize(videoSize);
	}
	
	public SurfaceHolder getDisplay() {
		return videoView.getHolder();
	}
	
    public CameraManager getCameraManager() {
		return cameraManager;
	}
	
    public void dispose() {
    	videoView = null;
    	cameraManager.releaseCamera();
    	recorderManager.releaseRecorder();
    	recordingHandler = null;
    }
    
    //surface holder callbacks ******************************************************************
    
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
    	cameraManager.openCamera();
	}
    
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (recordingHandler == null) { return; }
		if (!recordingHandler.onPrepareRecording()) {
			cameraManager.setupCameraAndStartPreview(videoView.getHolder(),
												     recordingHandler.getVideoSize(),
												     recordingHandler.getDisplayRotation());
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		recorderManager.stopRecording();
		cameraManager.releaseCamera();
	}
}
