/*
 * Copyright (C) 2013 Steelkiwi Development, Julia Zudikova, Viacheslav Tiagotenkov
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skd.androidrecording.video;

import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

/*
 * Controls process of previewing and recording video
 */

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
