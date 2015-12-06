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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Samuel on 1/20/2015.
 */
public class Settings {

	public static final String ALGORITHM_EIGEN  = "Eigenfaces";
	public static final String ALGORITHM_FISHER = "Fisherfaces";
	public static final String ALGORITHM_LBPH   = "LBPH";

	public static final int ALGORITHM_EIGEN_NO  = 0;
	public static final int ALGORITHM_FISHER_NO = 1;
	public static final int ALGORITHM_LBPH_NO   = 2;
	
	public static final String prefs_face_detection = "prefs_face_detection";
	public static final String prefs_server_addr    = "prefs_server_addr";
	public static final String prefs_algorithm      = "prefs_algorithm";

	private boolean mFaceDetectionEnabled;
	private String mServerAddress;
	private int mAlgorithmNo;
	
    private static Settings ourInstance = new Settings();

	private Context mContext;

	private String mFILE = "settings.dat";

	private boolean mOpenCVInited;

    public static Settings getInstance() {
        return ourInstance;
    }

    private Settings() {
		mOpenCVInited = false;
    }

	public void initContext(Context c) {
		mContext = c;
	}

    public void load() {
		assert(mContext != null);
		
		SharedPreferences prefs = mContext.getSharedPreferences(mFILE, mContext.MODE_PRIVATE);
		
        mFaceDetectionEnabled = prefs.getBoolean(prefs_face_detection, true);
        mServerAddress        = prefs.getString(prefs_server_addr, "140.158.129.111");
		mAlgorithmNo          = prefs.getInt(prefs_algorithm, 2);
    }

    public void sync() {
		assert(mContext != null);
		
		SharedPreferences prefs = mContext.getSharedPreferences(mFILE, mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
		
        editor.putBoolean(prefs_face_detection, mFaceDetectionEnabled);
        editor.putString (prefs_server_addr,    mServerAddress);
		editor.putInt(prefs_algorithm,      getFaceRecognitionAlgorithmNo());
		
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return false;
    }

    public int getInteger(String key) {
        return 0;
    }

    public String getString(String key) {
        return "null";
    }

    public void setBoolean(String key, boolean value) {
    }

    public void setInteger(String key, int value) {
    }

    public void setString(String key, String value) {
    }

	public boolean isFaceDetectionEnabled() {
		return mFaceDetectionEnabled;
	}

	public void setFaceDetectionEnabled(boolean yes) {
		mFaceDetectionEnabled = yes;
	}

	public String getServerAddress() {
		return mServerAddress;
	}

	public void setServerAddress(String address) {
		mServerAddress = address;
	}

	public String getFaceRecognitionAlgorithm() {
		switch (mAlgorithmNo) {
		case ALGORITHM_EIGEN_NO:  return ALGORITHM_EIGEN;
		case ALGORITHM_FISHER_NO: return ALGORITHM_FISHER;
		case ALGORITHM_LBPH_NO:   return ALGORITHM_LBPH;
		default:
			return ALGORITHM_LBPH;
		}
	}

	public void setFaceRecognitionAlgorithm(String algorithm) {
		if (algorithm.equals(ALGORITHM_EIGEN)) {
			mAlgorithmNo = ALGORITHM_EIGEN_NO;
		} else if (algorithm.equals(ALGORITHM_FISHER)) {
			mAlgorithmNo = ALGORITHM_FISHER_NO;
		} else if (algorithm.equals(ALGORITHM_LBPH)) {
			mAlgorithmNo = ALGORITHM_LBPH_NO;
		} else {
			//assert(0);
			mAlgorithmNo = ALGORITHM_LBPH_NO;
		}
	}

	public int getFaceRecognitionAlgorithmNo() {
		return mAlgorithmNo;
	}

	public void setFaceRecognitionAlgorithmNo(int no) {
		mAlgorithmNo = no;
	}

	public boolean isOpenCVInited() {
		return mOpenCVInited;
	}

	public void onOpenCVInited() {
		mOpenCVInited = true;
	}
}
