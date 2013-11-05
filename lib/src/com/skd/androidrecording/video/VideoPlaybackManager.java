package com.skd.androidrecording.video;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

public class VideoPlaybackManager implements OnPreparedListener, MediaPlayerControl, OnCompletionListener {
	private MediaPlayerManager playerManager;
	private MediaController controller;
	private VideoPlaybackHandler playbackHandler;
	
	public VideoPlaybackManager(Context ctx, AdaptiveSurfaceView videoView, VideoPlaybackHandler playbackHandler) {
		this.playerManager = new MediaPlayerManager();
		this.playerManager.getPlayer().setOnPreparedListener(this);
        
        this.controller = new MediaController(ctx);
        this.controller.setMediaPlayer(this);
        this.controller.setAnchorView(videoView);
	    
	    this.playbackHandler = playbackHandler;
	}
	
	public void setupPlayback(String fileName) {
		playerManager.setupPlayback(fileName);
	}
	
	public void showMediaController() {
		if (!controller.isEnabled()) {
			controller.setEnabled(true);
		}
        controller.show();
	}
	
	public void hideMediaController() {
        controller.hide();
        controller.setEnabled(false);
	}
	
	public MediaPlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public void dispose() {
		playerManager.releasePlayer();
		controller = null;
		playbackHandler = null;
	}
	
	//media player and controller callbacks *****************************************************

	public void setPlayerManager(MediaPlayerManager playerManager) {
		this.playerManager = playerManager;
	}

	public void onPrepared(MediaPlayer mp) {
		playbackHandler.onPreparePlayback();
	}

	@Override
	public void start() {
		playerManager.startPlaying();
	}
	
	@Override
	public void pause() {
		playerManager.pausePlaying();
	}
	
	@Override
	public void seekTo(int arg0) {
		playerManager.seekTo(arg0);
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		playerManager.stopPlaying();
	}

	@Override
	public boolean isPlaying() {
		return playerManager.isPlaying();
	}
	
	@Override
	public int getCurrentPosition() {
		return playerManager.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return playerManager.getDuration();
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
}
