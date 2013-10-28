package com.skd.androidrecordingtest;

import java.util.List;

import android.hardware.Camera.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PreviewSizeAdapter extends BaseAdapter {

	private List<Size> sizes; 
	
	public PreviewSizeAdapter(List<Size> sizes) {
		this.sizes = sizes;
	}

	@Override
	public int getCount() {
		return sizes != null ? sizes.size() : 0;
	}

	@Override
	public Size getItem(int position) {
		return sizes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = new TextView(parent.getContext());
		view.setText(String.format("Preview: %sx%s", sizes.get(position).width, sizes.get(position).height));
		view.setPadding(16, 16, 16, 16);
		return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView view = new TextView(parent.getContext());
		view.setText(String.format("Preview: %sx%s", sizes.get(position).width, sizes.get(position).height));
		view.setPadding(16, 16, 16, 16);
		return view;
	}

}
