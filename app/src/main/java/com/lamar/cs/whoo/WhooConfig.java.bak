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
import android.widget.Toast;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * Created by Samuel on 10/6/2015.
 */
public class WhooConfig {

    public static final boolean USING_EXTERNAL_STORAGE    = true;

    public static final boolean USING_OPENCV_JAVAPREVIEW  = false;

    public static final boolean USING_CLIENT_SERVER_MODE  = false;

    public static final boolean USING_TRAINING_DEBUG_MODE = false;

	public static final boolean USING_DEBUGGING_MODE      = true;

    public static final boolean USING_USER_RELEASE_MODE   = true;

	//
	// the fixed face size in this software.
	//
	public static final int SCREEN_WIDTH  = 1080;
	public static final int SCREEN_HEIGHT = 1920;
	
	//
	// the fixed face size in this software.
	//
	public static final int FACE_WIDTH  = 128;
	public static final int FACE_HEIGHT = 128;

	//
	// the threshold for face recognition.
	//
	public static double FRTHRESHOLD = 200.0;
	
	//
	// supported algorithms: { eigen, fisher, LBPH }
	//
	public static final String DEFAULT_ALGORITHM = "LBPH";

	//
	// my personal website
	//
	public static final String ABOUT_ME = "https://www.linkedin.com/in/shaomin-zhang-0ba60667";

    public static void DBG(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void DBG(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
    }

    public static void setContext(Context context) {
        mContext = context;
    }

	public static String ROOT_DIR() {
		if (USING_DEBUGGING_MODE) {
			return "Whoo";
		} else {
			return ".Whoo";
		}
	}
	
	public static String PERSON_FILE() {
		if (USING_DEBUGGING_MODE) {
			return "person.txt";
		} else {
			return ".person";
		}
	}
	
	public static String LOCALNAMES_FILE() {
		if (USING_DEBUGGING_MODE) {
			return "localnames.txt";
		} else {
			return ".localnames";
		}
	}

	public static String VERSION() {
		return "1.6.0";
	}

	public static String COMPILE_DATE() {
        String date = null;
        /****
        try {
            date = (new Date(new File(FaceDetector.getInstance().getClass().getClassLoader().
                    getResource(FaceDetector.getInstance().getClass().getCanonicalName().replace('.', '/') +
                            ".class").toURI()).lastModified())
            ).toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        ****/

        if (null == date) {
            // Hard-code if hack code does not work
            return "9:12 AM Nov 17, 2015";
        } else {
            return date;
        }
	}

    private static Context mContext;
}
