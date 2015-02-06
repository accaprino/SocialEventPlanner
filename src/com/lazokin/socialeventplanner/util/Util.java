package com.lazokin.socialeventplanner.util;

import android.content.Context;
import android.widget.Toast;
//class to make toast's slightly neater in demo.
public class Util {

	public static void toast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static void toast(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
	}
}
