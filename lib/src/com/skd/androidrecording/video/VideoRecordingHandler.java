package com.skd.androidrecording.video;

import android.hardware.Camera.Size;

public interface VideoRecordingHandler {
	public boolean onPrepareRecording();
	public Size getVideoSize();
	public int getDisplayRotation();
}
