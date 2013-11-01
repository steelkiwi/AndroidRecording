package com.skd.androidrecording;

import java.io.IOException;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;

public class MediaRecorderHelper {
	private static final int VIDEO_W_DEFAULT = 800;
	private static final int VIDEO_H_DEFAULT = 480;
	
	private MediaRecorder recorder;
	private boolean isRecording;

	public MediaRecorderHelper() {
		recorder = new MediaRecorder();
	}

	public boolean startRecording(Camera camera, String fileName, Size sz, int cameraRotationDegree) {
		if (sz == null) {
			sz = camera.new Size(VIDEO_W_DEFAULT, VIDEO_H_DEFAULT);
		}
		
		try {
			camera.unlock();
			recorder.setCamera(camera);
			recorder.setOrientationHint(cameraRotationDegree);
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setVideoSize(sz.width, sz.height);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
			recorder.setOutputFile(fileName);
			recorder.prepare();
			recorder.start();
			isRecording = true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return isRecording;
	}

	public void stopRecording() {
		isRecording = false;
		recorder.stop();
		recorder.reset();
	}

	public void recycle() {
		recorder.release();
	}

	public boolean isRecording() {
		return isRecording;
	}
}
