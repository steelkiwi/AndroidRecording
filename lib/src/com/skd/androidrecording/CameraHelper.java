package com.skd.androidrecording;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.view.Surface;

public class CameraHelper {

	public static int getAvailableCamerasCount() {
		return Camera.getNumberOfCameras();
	}
	
	public static int getDefaultCameraID() {
		int camerasCnt = getAvailableCamerasCount();
		int defaultCameraID = 0;
		CameraInfo cameraInfo = new CameraInfo();
        for (int i=0; i <camerasCnt; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
            	defaultCameraID = i;
            }
        }
        return defaultCameraID;
	}
	
	public static boolean isCameraFacingBack(int cameraID) {
		CameraInfo cameraInfo = new CameraInfo();
		Camera.getCameraInfo(cameraID, cameraInfo);
		return (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK);
	}
	
	public static int setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}

		int camRotationDegree = 0;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			camRotationDegree = (info.orientation + degrees) % 360;
			camRotationDegree = (360 - camRotationDegree) % 360; // compensate the mirror
		} else { 
			camRotationDegree = (info.orientation - degrees + 360) % 360;
		}

		camera.setDisplayOrientation(camRotationDegree);
		return camRotationDegree;
	}
	
}
