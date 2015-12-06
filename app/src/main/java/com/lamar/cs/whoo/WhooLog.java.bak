/* Author: Shaomin (Samuel) Zhang <smicn@foxmail.com>
 *
 * The Android application Whoo is the part of the author's thesis, MS of
 * computer science in 2015. The main purpose is easy and straightforward:
 * to develop an Android application based on OpenCV so that it has the
 * features of face detection and face recognition. OpenCV has supported
 * three face recognition algorithms and this software does not develop new
 * algorithms. However, it really did some careful design and optimizations
 * to make the face recognition easy and friendly to use. Just take pictures
 * to your friends and yourself, and hope you have fun from it.
 *
 * Licensed under the Academic Free License version 2.1
 *
 * Copyright(C)2015  Samuel Zhang <smicn@foxmail.com>
 */
package com.lamar.cs.whoo;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by Samuel on 11/14/2015.
 */
public class WhooLog {

	private final static String TAG = "whoo.log";

    private static WhooLog mInst;

	private final static String PRIVATE_FILE = "log.txt";

	private static PrintWriter mWriter;

    private WhooLog() {
    }

    public static void open(Context context) {
		try {
			FileOutputStream fos = null;

            if (WhooConfig.USING_EXTERNAL_STORAGE) {
                File dir = Environment.getExternalStorageDirectory();
                if (!dir.exists()) {
                    WhooConfig.DBG("Error: SD card path " + dir.getAbsolutePath() + " does not exist!");
					return;
                }
                else {
                    dir = new File(Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/" + WhooConfig.ROOT_DIR());
                    if (!dir.exists()) {
                        dir.mkdirs();
                        dir.setReadable(true, true);
                        dir.setWritable(true, true);

						Log.d(TAG, "d: created new directory by mkdir(): " + dir.getAbsolutePath());
                    }

                    File file = new File(dir.getAbsolutePath() + "/" + PRIVATE_FILE);
					
                    fos = new FileOutputStream(file, true/*append*/);
                }
            }
            else {
                fos = context.openFileOutput(PRIVATE_FILE, context.MODE_PRIVATE | context.MODE_APPEND);
            }

            mWriter = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(fos)), true);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "error, file " + PRIVATE_FILE + " cannot be open!" );
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "error, file " + PRIVATE_FILE + " write failed!" );
			e.printStackTrace();
		}
    }

    public static void close() {
		if (mWriter != null) {
	    	mWriter.flush();
			mWriter.close();
			mWriter = null;
    	}
    }

    // debug
    public static void d(String message) {
    	Log.d(TAG, "d(" + new Date() + ") " + message);
    	if (mWriter != null) {
    		mWriter.println("d(" + new Date() + ") " + message);
    	}
    }

    // error
    public static void e(String message) {
    	Log.d(TAG, "e(" + new Date() + ") " + message);
    	if (mWriter != null) {
    		mWriter.println("e(" + new Date() + ") " + message);
    	}
    }
}
