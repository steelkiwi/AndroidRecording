/*
 * Copyright (C) 2013 Steelkiwi Development, Julia Zudikova, Viacheslav Tyagotenkov
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

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceView;

/*
 * Represents a surface camera preview or media player output is drawn on.
 * Surface can adjust its size according to set preview's aspect ratio. 
 */

public class AdaptiveSurfaceView extends SurfaceView {
	private int previewWidth;
	private int previewHeight;
	private float ratio;

	public AdaptiveSurfaceView(Context context) {
		super(context);
	}
	
	public AdaptiveSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AdaptiveSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setPreviewSize(Camera.Size size) {
		int screenW = getResources().getDisplayMetrics().widthPixels;
		int screenH = getResources().getDisplayMetrics().heightPixels;
		if (screenW < screenH) {
			previewWidth = size.width < size.height ? size.width : size.height;
			previewHeight = size.width >= size.height ? size.width : size.height;
		}
		else {
			previewWidth = size.width > size.height ? size.width : size.height;
			previewHeight = size.width <= size.height ? size.width : size.height;
		}
		ratio = previewHeight / (float) previewWidth;
		requestLayout();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int previewW     = MeasureSpec.getSize(widthMeasureSpec);
		int previewWMode = MeasureSpec.getMode(widthMeasureSpec);
		int previewH     = MeasureSpec.getSize(heightMeasureSpec);
		int previewHMode = MeasureSpec.getMode(heightMeasureSpec);

		int measuredWidth  = 0;
		int measuredHeight = 0;

		if (previewWidth > 0 && previewHeight > 0) {
			measuredWidth = defineWidth(previewW, previewWMode);
			
			measuredHeight = (int) (measuredWidth * ratio);
			if (previewHMode != MeasureSpec.UNSPECIFIED && measuredHeight > previewH) {
				measuredWidth = (int) (previewH / ratio);
				measuredHeight = previewH;
			}

			setMeasuredDimension(measuredWidth, measuredHeight);
		}
		else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	private int defineWidth(int previewW, int previewWMode) {
		int measuredWidth;
		if (previewWMode == MeasureSpec.UNSPECIFIED) {
			measuredWidth = previewWidth;
		} 
		else if (previewWMode == MeasureSpec.EXACTLY) {
			measuredWidth = previewW;
		} 
		else {
			measuredWidth = Math.min(previewW, previewWidth);
		}
		return measuredWidth;
	}
}
