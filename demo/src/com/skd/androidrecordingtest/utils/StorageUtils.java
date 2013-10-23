package com.skd.androidrecordingtest.utils;

import android.os.Environment;

public class StorageUtils {
	private static final String FILE_NAME = "audiorecordtest.wav";
	
	public static boolean checkExternalStorageAvailable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
	    }
		else {
			return false;
		}
	}
	
	public static String getFileName() {
		String storageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		return String.format("%s/%s", storageDir, FILE_NAME);
	}
}
