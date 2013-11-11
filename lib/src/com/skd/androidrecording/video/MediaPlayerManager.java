/*
 * Copyright (C) 2013 Steelkiwi Development, Julia Zudikova, Viacheslav Tiagotenkov
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skd.androidrecording.video;

import android.media.MediaPlayer;
import android.view.SurfaceHolder;

/*
 * Manages media player playback
 */

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
		if (pos < 0) {
			player.seekTo(0);
		}
		else if (pos > getDuration()) {
			player.seekTo(getDuration());
		}
		else {
			player.seekTo(pos);
		}
	}
	
	public void stopPlaying() {
		if (player.isPlaying()) {
			player.stop();
		}	
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
		setDisplay(null);
		player.release();
		player = null;
	}
}
