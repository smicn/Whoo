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

import android.graphics.Bitmap;
import org.opencv.core.Mat;

/**
 * Created by Samuel on 10/13/2015.
 */
public class WFRFaceImage {

    public String mName;
    public String mPath;
	public Mat mMat;
	public Bitmap mBitmap;

	public WFRPerson mPerson;

	public WFRFaceImage(String name, String path) {
		mName = name;
		mPath = path;
	}
}
