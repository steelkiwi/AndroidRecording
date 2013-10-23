package com.skd.androidrecording;

public interface AudioRecordingHandler {
	public void onFftDataCapture(byte[] bytes);
	public void onRecordingError();
	public void onRecordSaveError();
}
