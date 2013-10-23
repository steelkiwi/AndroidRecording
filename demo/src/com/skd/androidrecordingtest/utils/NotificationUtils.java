package com.skd.androidrecordingtest.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class NotificationUtils {
	public static void showInfoDialog(Context ctx, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(msg);
		builder.setPositiveButton(ctx.getString(android.R.string.ok), new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            	dialog.cancel();
            }
        });
		AlertDialog alert = builder.create();
		alert.show();
	}
}
