/*
 * Modified by Steelkiwi Development, Julia Zudikova
 */

/**
 * Copyright 2011, Felix Palmer
 *
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */
package com.skd.androidrecording.visualizer;

// Data class to explicitly indicate that these bytes are raw audio data
public class AudioData
{
  public AudioData() {}

  private byte[] bytes;

  public byte[] getBytes() {
	return bytes;
  }

  public void setBytes(byte[] bytes) {
	this.bytes = bytes;
  }
}
