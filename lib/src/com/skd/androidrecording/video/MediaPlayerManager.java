package com.skd.androidrecording.video;

import android.media.MediaPlayer;
import android.view.SurfaceHolder;

public class MediaPlayerManager {

	private MediaPlayer player;
	
	public MediaPlayerManager() {
		player = new MediaPlayer();
	}

	public MediaPlayer getPlayer() {
		return player;
	}
	
	public void setupPlayback(String fileName) {
		try {
        	player.setDataSource(fileName);
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void setDisplay(SurfaceHolder sf) {
		player.setDisplay(sf);
	}
	
	public void startPlaying() {
	    player.start();
    }
	
	public void pausePlaying() {
		player.pause();
    }

	public void seekTo(int pos) {
		player.seekTo(pos);
	}
	
	public void stopPlaying() {
	    player.stop();
    }
	
	public boolean isPlaying() {
		return player.isPlaying();
	}
	
	public int getCurrentPosition() {
		return player.getCurrentPosition();
	}

	public int getDuration() {
		return player.getDuration();
	}
	
	public void releasePlayer() {
		player.release();
		player = null;
	}
}
