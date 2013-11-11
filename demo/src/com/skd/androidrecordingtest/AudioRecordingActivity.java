package com.skd.androidrecordingtest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.skd.androidrecording.audio.AudioRecordingHandler;
import com.skd.androidrecording.audio.AudioRecordingThread;
import com.skd.androidrecording.visualizer.VisualizerView;
import com.skd.androidrecording.visualizer.renderer.BarGraphRenderer;
import com.skd.androidrecordingtest.utils.NotificationUtils;
import com.skd.androidrecordingtest.utils.StorageUtils;

public class AudioRecordingActivity extends Activity {
	private static String fileName = null;
    
	private Button recordBtn, playBtn;
	private VisualizerView visualizerView;
	
	private AudioRecordingThread recordingThread;
	private boolean startRecording = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio_rec);
		
		if (!StorageUtils.checkExternalStorageAvailable()) {
			NotificationUtils.showInfoDialog(this, getString(R.string.noExtStorageAvailable));
			return;
		}
		fileName = StorageUtils.getFileName(true);
		
		recordBtn = (Button) findViewById(R.id.recordBtn);
		recordBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				record();
			}
		});
		
		playBtn = (Button) findViewById(R.id.playBtn);
		playBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				play();
			}
		});
		
		visualizerView = (VisualizerView) findViewById(R.id.visualizerView);
		setupVisualizer();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		recordStop();
	}
	
	@Override
	protected void onDestroy() {
		recordStop();
		releaseVisualizer();
		
		super.onDestroy();
	}
	
	private void setupVisualizer() {
		Paint paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(200, 227, 69, 53));
        BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(2, paint, false);
        visualizerView.addRenderer(barGraphRendererBottom);
	}
	
	private void releaseVisualizer() {
		visualizerView.release();
		visualizerView = null;
	}
	
	private void record() {
        if (startRecording) {
        	recordStart();
        }
        else {
        	recordStop();
        }
	}
	
	private void recordStart() {
		startRecording();
    	startRecording = false;
    	recordBtn.setText(R.string.stopRecordBtn);
    	playBtn.setEnabled(false);
	}
	
	private void recordStop() {
		stopRecording();
		startRecording = true;
    	recordBtn.setText(R.string.recordBtn);
    	playBtn.setEnabled(true);
	}
	
	private void startRecording() {
	    recordingThread = new AudioRecordingThread(fileName, new AudioRecordingHandler() {
			@Override
			public void onFftDataCapture(final byte[] bytes) {
				runOnUiThread(new Runnable() {
					public void run() {
						if (visualizerView != null) {
							visualizerView.updateVisualizerFFT(bytes);
						}
					}
				});
			}

			@Override
			public void onRecordSuccess() {}

			@Override
			public void onRecordingError() {
				runOnUiThread(new Runnable() {
					public void run() {
						recordStop();
						NotificationUtils.showInfoDialog(AudioRecordingActivity.this, getString(R.string.recordingError));
					}
				});
			}

			@Override
			public void onRecordSaveError() {
				runOnUiThread(new Runnable() {
					public void run() {
						recordStop();
						NotificationUtils.showInfoDialog(AudioRecordingActivity.this, getString(R.string.saveRecordError));
					}
				});
			}
		});
	    recordingThread.start();
    }
    
    private void stopRecording() {
    	if (recordingThread != null) {
    		recordingThread.stopRecording();
    		recordingThread = null;
	    }
    }

    private void play() {
		Intent i = new Intent(AudioRecordingActivity.this, AudioPlaybackActivity.class);
		i.putExtra(VideoPlaybackActivity.FileNameArg, fileName);
		startActivityForResult(i, 0);
	}
}
