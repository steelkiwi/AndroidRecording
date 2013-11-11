Android Recording Library
=======================================

Android Recording library offers convenient tools for audio/video recording and playback.

For audio it uses:
* AudioRecord to capture and save audio signal from microphone
* MediaPlayer with MediaController to play recorded audio
* custom Visualizer (like bar chart) to represent audio signal on screen while recording and during playback

For video it uses:
* Camera and MediaRecorder to record a video of specified resolution
* MediaPlayer with MediaController to play recorded video
* custom SurfaceView with adjustable size to properly display Camera preview and recorded video (in portrait and landscape modes)

Record audio: how to use
------------------------

1. Setup VisualizerView

    ```xml
    <com.skd.androidrecording.visualizer.VisualizerView
    	android:id="@+id/visualizerView"
    	android:layout_width="fill_parent"
    	android:layout_height="100dp" >
    </com.skd.androidrecording.visualizer.VisualizerView>
    ```
    
    ```java
    visualizerView = (VisualizerView) findViewById(R.id.visualizerView);
    setupVisualizer();
    
    ...
    
    private void setupVisualizer() {
    	Paint paint = new Paint();
    	paint.setStrokeWidth(5f);                     //set bar width
    	paint.setAntiAlias(true);
    	paint.setColor(Color.argb(200, 227, 69, 53)); //set bar color
    	BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(2, paint, false);
    	visualizerView.addRenderer(barGraphRendererBottom);
    }
```

2. Start recording thread

    ```java
    private void startRecording() {
    	recordingThread = new AudioRecordingThread(fileName, new AudioRecordingHandler() { //pass file name where to store the recorded audio
    		@Override
    		public void onFftDataCapture(final byte[] bytes) {
    			runOnUiThread(new Runnable() {
    				public void run() {
    					if (visualizerView != null) {
    						visualizerView.updateVisualizerFFT(bytes); //update VisualizerView with new audio portion
    					}
    				}
    			});
    		}
    
    		@Override
    		public void onRecordSuccess() {}
    
    		@Override
    		public void onRecordingError() {}
    
    		@Override
    		public void onRecordSaveError() {}
    	});
    	recordingThread.start();
    }
    ```

3. When done, stop recording

    ```java
    private void stopRecording() {
    	if (recordingThread != null) {
    		recordingThread.stopRecording();
    		recordingThread = null;
    	}
    }
    ``` 

Play audio: how to use
------------------------

1. Setup VisualizerView

2. Setup AudioPlaybackManager. It will attach MediaPlayer to a VisualizerView

    ```java
    playbackManager = new AudioPlaybackManager(this, visualizerView, playbackHandler);
    playbackManager.setupPlayback(fileName); //pass file name of the recorded audio
    ``` 

3. Use onscreen MediaController to play/pause/stop/rewind audio


Record video: how to use
------------------------

1. Setup custom SurfaceView (AdaptiveSurfaceView)

    ```xml
    <com.skd.androidrecording.video.AdaptiveSurfaceView
    	android:id="@+id/videoView"
    	android:layout_width="wrap_content"
    	android:layout_height="wrap_content" />
    ``` 

2. Setup RecordingManager

    ```java
    videoView = (AdaptiveSurfaceView) findViewById(R.id.videoView);
    recordingManager = new VideoRecordingManager(videoView, recordingHandler); //pass reference to custom SurfaceView
    ``` 

3. Choose desired video resolution and pass it to RecordingManager, it will adjust size of AdaptiveSurfaceView to properly display Camera output

    ```java
    recordingManager.setPreviewSize(videoSize);
    ``` 

4. To start recording use

    ```java
    recordingManager.startRecording(fileName, videoSize)
    ``` 

5. To stop recording use

    ```java
    recordingManager.stopRecording()
    ``` 

Play video: how to use
------------------------

1. Setup custom SurfaceView (AdaptiveSurfaceView)

2. Setup VideoPlaybackManager. It will attach MediaPlayer to a VisualizerView

    ```java
    playbackManager = new VideoPlaybackManager(this, videoView, playbackHandler);
    playbackManager.setupPlayback(fileName); //pass file name of the recorded video
    ``` 

3. Use onscreen MediaController to play/pause/stop/rewind video


====================================

For more details, please, see the demo project.
