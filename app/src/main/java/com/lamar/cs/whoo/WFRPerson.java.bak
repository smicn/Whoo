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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Created by Samuel on 10/13/2015.
 */
public class WFRPerson {

	private final String TAG = "whoo.dm.p";
	
	private String mName;
	private int    mLabelID;

	private Context mContext;

	private Vector<WFRFaceImage> mImageList;

	private String mDirName;

	public WFRPerson(Context context, String name, int lable) {
		mContext = context;
		mName = name;
		mLabelID = lable;
		mImageList = new Vector<WFRFaceImage>();

		//
		// This is to avoid the bug that fs does not support
		// bland within the directory string. However, this bug
		// is confirmed to be not existing.
		//
		//StringBuilder sb = new StringBuilder(mName);
		//for (int ii = 0; ii < sb.length(); ii++) {
		//	if (sb.charAt(ii) == ' ') {
		//		sb.setCharAt(ii, '_');
		//	}
		//}
		//mDirName = sb.toString();
		//
		mDirName = mName;
	}

	public int count() {
		return mImageList.size();
	}

	public int getFaceImageCount() {
		return mImageList.size();
	}

	public WFRFaceImage getFaceImage(int index) {
		if (0 <= index && index < mImageList.size()) {
			return mImageList.get(index);
		}
		return null;
	}

	public String getName() {
		return mName;
	}

	public String getDirName() {
		return mDirName;
	}

	public int getLableID() {
		return mLabelID;
	}

    public boolean addFaceImage(String path) {
        WFRFaceImage image = null;
        Bitmap bitmap = null;

        int count = mImageList.size();
        for (int ii = 0; ii < count; ii++) {
            image = mImageList.get(ii);
            if (image.mPath.equals(path)) {
                Log.d(TAG, "addFaceImage() error: duplicate path " + path);
                return false;
            }
        }

		try {
			FileInputStream fis = null;
			
			if (WhooConfig.USING_EXTERNAL_STORAGE) {
                File file = new File(path);
                if (!file.exists()) {
                    WhooConfig.DBG("Error: SD card file " + path + " does not exist!");
                }
                fis = new FileInputStream(file);

			} else {
				File file = mContext.getFileStreamPath(path);
				if (!file.exists()) {
					Log.d(TAG, "addFaceImage() error: file not exist: " + path);
	                return false;
				}

	            fis = mContext.openFileInput(path);
			}
			
            bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

		Mat mat = WhooTools.bitmap2GrayScaleMat(bitmap);

        image = new WFRFaceImage(mName, path);
        image.mMat    = mat;
        image.mBitmap = bitmap;
        image.mPerson = this;
        mImageList.add(image);

        return true;
    }

	public boolean addFaceImage(Mat mat) {
		WFRFaceImage image = null;
		String path = null;
        File file = null;
		File dir = null;

		if (WhooConfig.USING_EXTERNAL_STORAGE) {
    		dir = Environment.getExternalStorageDirectory();
            if (!dir.exists()) {
                WhooConfig.DBG("Error: SD card path " + dir.getAbsolutePath() + " does not exist!");
            }
            else {
                dir = new File(Environment.getExternalStorageDirectory().
					getAbsolutePath() + "/" + WhooConfig.ROOT_DIR() + "/" + mDirName);
                if (!dir.exists()) {
                    dir.mkdirs();
                    dir.setReadable(true, true);
                    dir.setWritable(true, true);
                }
            }
		} else {
	        dir = mContext.getFileStreamPath(mName);
	        if (!dir.exists()) {
	            dir.mkdir();
	            dir.setReadable(true, false);
	            dir.setWritable(true, false);
	        }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        try {
            file = File.createTempFile(mDirName + timeStamp, ".jpg", dir);
        } catch (IOException e) {
            e.printStackTrace();
        }

		path = file.getAbsolutePath();

        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int count = mImageList.size();
		for (int ii = 0; ii < count; ii++) {
			image = mImageList.get(ii);
			if (image.mPath.equals(path)) {
				Log.d(TAG, "addFaceImage() error: duplicate path " + path);
				return false;
			}
		}

		image = new WFRFaceImage(mName, path);
		image.mMat    = mat;
		image.mBitmap = bitmap;
		image.mPerson = this;
		mImageList.add(image);
		
		return true;
	}
}
