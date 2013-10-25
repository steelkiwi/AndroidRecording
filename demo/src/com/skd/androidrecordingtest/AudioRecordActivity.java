package com.skd.androidrecordingtest;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

import com.skd.androidrecording.AudioRecordingHandler;
import com.skd.androidrecording.AudioRecordingThread;
import com.skd.androidrecording.visualizer.VisualizerView;
import com.skd.androidrecording.visualizer.renderer.BarGraphRenderer;
import com.skd.androidrecordingtest.utils.NotificationUtils;
import com.skd.androidrecordingtest.utils.StorageUtils;

public class AudioRecordActivity extends Activity implements OnPreparedListener, MediaPlayerControl, OnCompletionListener {
	private static String fileName = null;
    
	private Button recordBtn;
	private VisualizerView visualizerView;
	
	private AudioRecordingThread recordingThread;
	private MediaPlayer player = null;
	private MediaController controller = null;
	
	private boolean startRecording = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio);
		
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
		pausePlaying();
	}
	
	@Override
	protected void onDestroy() {
		recordStop();
		finishPlaying();
		
		super.onDestroy();
	}
	
	//recording *********************************************************************************

	private void record() {
        if (startRecording) {
        	finishPlaying();
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
						if (visualizerView != null) {
							visualizerView.updateVisualizerFFT(bytes);
						}
					}
				});
			}

			@Override
			public void onRecordSuccess() {
				preparePlaying();
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
	
	private void preparePlaying() {
		player = new MediaPlayer();
        player.setOnPreparedListener(this);
        try {
        	player.setDataSource(fileName);
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void startPlaying() {
		if (player != null) {
	        player.start();
    	}
		if (visualizerView != null) {
			visualizerView.link(player);
    	}
    }

    private void pausePlaying() {
    	if (player != null) {
			player.pause();
		}
    	if (visualizerView != null) {
    		visualizerView.release();
    	}
    }

    private void stopPlaying() {
    	if (player != null) {
	        player.stop();
    	}
    	if (visualizerView != null) {
    		visualizerView.release();
    	}
    }
    
    private void finishPlaying() {
    	if (player != null) {
    		player.stop();
	        player.release();
	        player = null;
    	}
    	if (visualizerView != null) {
    		visualizerView.release();
    	}
    	if (controller != null) {
    		controller.hide();
    		controller = null;
    	}
    }
    
    //media player and controller ***************************************************************
    
	@Override
	public void onPrepared(MediaPlayer mp) {
        controller = new MediaController(this);
		controller.setMediaPlayer(this);
	    controller.setAnchorView(findViewById(R.id.visualizerView));

	    runOnUiThread(new Runnable() {
	    	public void run() {
		        controller.setEnabled(true);
		        controller.show();
	    	}
	    });
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		stopPlaying();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (controller != null) {
			controller.show();
		}
	    return false;
	}
	
	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		if (player != null) {
			return player.getCurrentPosition();
		}
		return 0;
	}

	@Override
	public int getDuration() {
		if (player != null) {
			return player.getDuration();
		}
		return 0;
	}

	@Override
	public boolean isPlaying() {
		if (player != null) {
			return player.isPlaying();
		}
		return false;
	}

	@Override
	public void pause() {
		pausePlaying();
	}

	@Override
	public void seekTo(int arg0) {
		if (player != null) {
			player.seekTo(arg0);
		}
	}

	@Override
	public void start() {
		startPlaying();
	}
}
