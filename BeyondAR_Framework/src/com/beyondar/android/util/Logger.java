package com.beyondar.android.util;

import android.util.Log;

public class Logger {

	/** All the BeyondAR logs output have this tag */
	public static final String TAG = "BeyondAR";

	public volatile static boolean DEBUG = true;

	/**
	 * Set this flag to enable the OpenGL debug log. If You use this, the touch
	 * events will not work! use only to debug the openGL Draw stuff
	 */
	public static boolean DEBUG_OPENGL = false;

	public static void e(String msg) {
		e(null, msg);
	}

	public static void e(String msg, Throwable tr) {
		e(null, msg, tr);
	}

	public static void e(String tag, String msg) {
		if (!DEBUG)
			return;
		Log.e(generateTag(tag), msg);
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (!DEBUG)
			return;
		Log.e(generateTag(tag), msg, tr);
	}

	public static void i(String msg) {
		if (!DEBUG)
			return;
		i(null, msg);
	}

	public static void i(String msg, Throwable tr) {
		i(null, msg, tr);
	}

	public static void i(String tag, String msg) {
		if (!DEBUG)
			return;
		Log.i(generateTag(tag), msg);
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (!DEBUG)
			return;
		Log.i(generateTag(tag), msg, tr);
	}

	public static void d(String msg) {
		d(null, msg);
	}

	public static void d(String msg, Throwable tr) {
		d(null, msg, tr);
	}

	public static void d(String tag, String msg) {
		if (!DEBUG)
			return;
		Log.d(generateTag(tag), msg);
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (!DEBUG)
			return;
		Log.d(generateTag(tag), msg, tr);
	}

	public static void v(String msg) {
		v(null, msg);
	}

	public static void v(String tag, String msg, Throwable tr) {
		if (!DEBUG)
			return;
		Log.v(generateTag(tag), msg, tr);
	}

	public static void v(String tag, String msg) {
		if (!DEBUG)
			return;
		Log.v(generateTag(tag), msg);
	}

	public static void v(String msg, Throwable tr) {
		if (!DEBUG)
			return;
		Log.v(TAG, msg, tr);
	}

	public static void w(String msg) {
		w(null, msg);
	}

	public static void w(String msg, Throwable tr) {
		w(null, msg, tr);
	}

	public static void w(String tag, String msg) {
		if (!DEBUG)
			return;
		Log.w(generateTag(tag), msg);
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (!DEBUG)
			return;
		Log.w(generateTag(tag), msg, tr);
	}

	private static String generateTag(String sufixTag) {
		if (sufixTag == null) {
			return TAG;
		}
		return TAG + "_" + sufixTag;
	}

}
