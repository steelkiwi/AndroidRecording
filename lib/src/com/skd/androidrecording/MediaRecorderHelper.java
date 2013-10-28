package com.skd.androidrecording;

import java.io.IOException;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;

public class MediaRecorderHelper {
	private static final int VIDEO_W = 1280;
	private static final int VIDEO_H = 720;
	
	private MediaRecorder recorder;
	private boolean isRecording;

	public MediaRecorderHelper() {
		recorder = new MediaRecorder();
	}

	public boolean startRecording(Camera camera, Size size, int camRotationDegree, String fileName) {
		try {
			camera.unlock();
			recorder.setCamera(camera);
			recorder.setOrientationHint(camRotationDegree);
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setVideoSize(VIDEO_W, VIDEO_H);
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
