package com.skd.androidrecordingtest;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.skd.androidrecording.AudioRecordingHandler;
import com.skd.androidrecording.AudioRecordingThread;
import com.skd.androidrecording.visualizer.VisualizerView;
import com.skd.androidrecording.visualizer.renderer.BarGraphRenderer;
import com.skd.androidrecordingtest.utils.NotificationUtils;
import com.skd.androidrecordingtest.utils.StorageUtils;

public class AudioRecordActivity extends Activity {
	private static String fileName = null;
    
	private Button recordBtn, playBtn;
	private VisualizerView visualizerView;
	
	private AudioRecordingThread recordingThread;
	private MediaPlayer player = null;
	
	private boolean startRecording = true;
	private boolean startPlaying = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio);
		
		if (!StorageUtils.checkExternalStorageAvailable()) {
			NotificationUtils.showInfoDialog(this, getString(R.string.noExtStorageAvailable));
			return;
		}
		fileName = StorageUtils.getFileName();
		
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
		
		Paint paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(200, 227, 69, 53));
        BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(2, paint, false);
        visualizerView.addRenderer(barGraphRendererBottom);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		recordStop();
		playStop();
	}
	
	//recording *********************************************************************************
	
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
    	recordBtn.setText(R.string.stopRecordBtn);
    	startRecording = false;
	}
	
	private void recordStop() {
		stopRecording();
    	recordBtn.setText(R.string.recordBtn);
    	startRecording = true;
	}
	
	private void startRecording() {
	    recordingThread = new AudioRecordingThread(fileName, new AudioRecordingHandler() {
			@Override
			public void onFftDataCapture(final byte[] bytes) {
				runOnUiThread(new Runnable() {
					public void run() {
						visualizerView.updateVisualizerFFT(bytes);
					}
				});
			}

			@Override
			public void onRecordingError() {
				runOnUiThread(new Runnable() {
					public void run() {
						recordStop();
						NotificationUtils.showInfoDialog(AudioRecordActivity.this, getString(R.string.recordingError));
					}
				});
			}

			@Override
			public void onRecordSaveError() {
				runOnUiThread(new Runnable() {
					public void run() {
						recordStop();
						NotificationUtils.showInfoDialog(AudioRecordActivity.this, getString(R.string.saveRecordError));
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
    
	//playing ***********************************************************************************
	
	private void play() {
        if (startPlaying) {
        	playStart();
        }
        else {
        	playStop();
        }
	}
	
	private void playStart() {
		startPlaying();
		playBtn.setText(R.string.stopPlayBtn);
		startPlaying = false;
	}
	
	private void playStop() {
		stopPlaying();
		playBtn.setText(R.string.playBtn);
		startPlaying = true;
	}
	
	private void startPlaying() {
        player = new MediaPlayer();
        try {
        	player.setDataSource(fileName);
            player.prepare();
            player.start();
            visualizerView.link(player);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlaying() {
    	if (player != null) {
	        player.release();
	        player = null;
    	}    
    }
}
