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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Samuel on 11/11/2015.
 */
public class WhooTools {

    private static final int FACE_WIDTH  = WhooConfig.FACE_WIDTH;
    private static final int FACE_HEIGHT = WhooConfig.FACE_HEIGHT;

    public static Mat resize(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(mat, bitmap);

        bitmap = Bitmap.createScaledBitmap(bitmap, FACE_WIDTH, FACE_HEIGHT, false);

        // now, copy bitmap data into matrix
        Mat matRGB = new Mat(FACE_WIDTH, FACE_HEIGHT, CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, matRGB);

        // convert RGB to GrayScale
        Mat matR = new Mat(FACE_WIDTH, FACE_HEIGHT, CvType.CV_8UC1);
        Imgproc.cvtColor(matRGB, matR, Imgproc.COLOR_RGB2GRAY);

        return matR;
    }

	public static Mat bitmap2GrayScaleMat(Bitmap bitmap) {
        // copy bitmap data into matrix
        Mat matRGB = new Mat(FACE_WIDTH, FACE_HEIGHT, CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, matRGB);

        // convert RGB to GrayScale
        Mat matR = new Mat(FACE_WIDTH, FACE_HEIGHT, CvType.CV_8UC1);
        Imgproc.cvtColor(matRGB, matR, Imgproc.COLOR_RGB2GRAY);

        return matR;
    }

	public static boolean isTextEnglish(String text) {
		Pattern p = Pattern.compile("[a-zA-Z]");
     	Matcher m = p.matcher(text);
    	return m.matches();
	}

	public static boolean isTextNumbers(String text) {
		Pattern p = Pattern.compile("[0-9]*"); 
     	Matcher m = p.matcher(text); 
    	return m.matches();
	}

	public static boolean isTextChinese(String text) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]"); 
     	Matcher m = p.matcher(text); 
    	return m.matches();
	}

	public static void startTickCount() {
		mTicksMS = System.currentTimeMillis();
	}

	public static long endTickCount() {
		long ticksMS1 = System.currentTimeMillis();
		return (ticksMS1 - mTicksMS);
	}

	private static long mTicksMS;

	public static String getLocalIP(Context context) {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."  
                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));  
    } 
}
