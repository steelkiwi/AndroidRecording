package com.skd.androidrecordingtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		ListView list = (ListView) findViewById(android.R.id.list);
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getApplicationContext(),
																	android.R.layout.simple_list_item_1,
																	getResources().getStringArray(R.array.activities));
		list.setAdapter(listAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				launchDemo(arg2);
			}
		});
	}

	private void launchDemo(int position) {
		switch (position) {
			case 0: default: {
				Intent i = new Intent(MainActivity.this, AudioRecordingActivity.class);
				startActivity(i);
				break;
			}
			case 1: {
				Intent i = new Intent(MainActivity.this, VideoRecordingActivity.class);
				startActivity(i);
				break;
			}
		}
	}
}
