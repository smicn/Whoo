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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.SurfaceHolder;
import android.view.View;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.CvType; 
import org.opencv.imgproc.Imgproc;

import org.opencv.objdetect.CascadeClassifier;

/**
 * Created by Samuel on 10/10/2015.
 */
public class FaceDetector {
    private final String TAG = "whoo.fd";

    private Mat mYUVData;
    private Mat mRGBData;

    private int mWidth;
    private int mHeight;

    private Context mContext;
    private View    mView;

    private boolean mHasDetected;
    private boolean mIsLocked;

    private CascadeClassifier mDetector;
    private File mCascadeFile;
    private final String mCascadeFileName = "lppcascade.xml";

    private final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);

    private Mat mImageRGBA;
    private Mat mImageGray;

    private Mat mDetectedFace;

    private static FaceDetector mInstance = new FaceDetector();
    private boolean mInited = false;
    private int mFrameCount;
    private final int FRAMERATE = 4;

    private final long TIMEOUT_FAILURE = 2000;
    private long mTicksForTimeout;

    private Paint mPaintRect;
    private Paint mPaintText;

    private boolean mIsCameraFacingBack;

    private FaceDetector() {
        Log.i(TAG, "fd.constructor().");
        
        mYUVData  = null;
        mRGBData  = null;
        mDetector = null;
        mInited   = false;

        mPaintRect = new Paint();
        mPaintRect.setStyle(Paint.Style.STROKE);
        mPaintRect.setColor(Color.GREEN);
        mPaintRect.setStrokeWidth(5);

        mPaintText = new Paint();
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setColor(Color.GREEN);
        mPaintText.setTextSize(25);
        mPaintText.setTextAlign(Align.CENTER);    

        // set front-facing camera as default
        mIsCameraFacingBack = false;
    }

    public static FaceDetector getInstance() {
        return mInstance;
    }

    public void init() {
        Log.i(TAG, "fd.init().");
        
        try {
            //
            // load cascade file from application resources raw/xxcascadexx.xml
            // and copy it a private but writable directory for classifier will
            // probably modify the content.
            //
            InputStream is = mContext.getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = mContext.getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, mCascadeFileName);

            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            mDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            if (null == mDetector || mDetector.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                mDetector = null;
            } else {
                Log.i(TAG, "Successfully loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
            }
            
            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception: " + e);
        }

        if (mHeight > 0 && mWidth > 0) {
            mYUVData = new Mat(mHeight + (mHeight/2), mWidth, CvType.CV_8UC1);
        } else {
            mYUVData = new Mat();
        }
        mRGBData = new Mat();

        mInited = true;
    }

    public void destroy() {
        Log.i(TAG, "fd.destroy().");
        
        if (mYUVData != null) {
            mYUVData.release();
            mYUVData = null;
        }
        if (mRGBData != null) {
            mRGBData.release();
            mRGBData = null;
        }
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void setView(View view) {
        mView = view;
    }

    public void setResolution(int w, int h) {
        mWidth  = w;
        mHeight = h;

        if (!mInited) return;

        mYUVData = new Mat(mHeight + (mHeight/2), mWidth, CvType.CV_8UC1);
        mRGBData = new Mat();
    }

    public void setCameraFacingBack(boolean back) {
        mIsCameraFacingBack = back;
    }

    public void onCameraFrameInjected(byte[] data) {
        if (!mInited) return;
        
        if (mIsLocked) return;

        if (null == mYUVData) {
            mYUVData = new Mat(mHeight + (mHeight/2), mWidth, CvType.CV_8UC1);
            mRGBData = new Mat();
        }

        
        if (0 == (++mFrameCount % FRAMERATE)) {
            mFrameCount = 0;
            return;
        }

        synchronized (this) {
            mYUVData.put(0, 0, data);
        }

        if (mView != null) {
            mView.invalidate();
        }
    }

    public void onDrawView(Canvas canvas) {
        //Log.i(TAG, "fd.onDrawView() is called.");
        if (!mInited) return;
        
        //
        // exceptions if any.
        //
        if (null == mYUVData || mIsLocked)
            return;
        if (null == mDetector)
            return;

        synchronized (this) {
            //Log.d(TAG, "fd.onDraw(1): h=" + mHeight + ",w=" + mWidth);
            mImageGray = mYUVData.submat(0, mHeight, 0, mWidth);
            //Log.d(TAG, "fd.onDraw(2): h=" + mImageGray.height() + ",w=" + mImageGray.width());
            
            //
            // http://stackoverflow.com/questions/16265673/rotate-image-by-90-180-or-270-degrees
            //
            if (mIsCameraFacingBack) {
                // Rotate clockwise 90 degrees
                Core.flip(mImageGray.t(), mImageGray, 1);
            } else {
                // Rotate clockwise 270 degrees
                Core.flip(mImageGray.t(), mImageGray, 0);
            }
            
            //Log.d(TAG, "fd.onDraw(3): h=" + mImageGray.height() + ",w=" + mImageGray.width());
        }

        // the detected faces will be output into mats
        MatOfRect mats = new MatOfRect();

        // calc the possible face size
        int faceSize = Math.round(mImageGray.rows() * 0.1f);

        //
        // detect faces and output them into mats
        //
        mDetector.detectMultiScale(mImageGray, mats, 1.1, 2, 2,
                new Size(faceSize, faceSize), new Size());

        //
        // draw rects around the faces if found
        //
        Rect[] faces = mats.toArray();
        for (int ii = 0; ii < faces.length; ii++) {
            if (faces[ii].width >= (int)(0.33 * canvas.getWidth())) {
                //
                // change the state: from NO to YES !!!
                //
                if (!mHasDetected) {
                    mHasDetected = true;
                }

                //
                // refresh the timer
                //
                mTicksForTimeout = System.currentTimeMillis();

                //Log.d(TAG, "face_1[" + ii + "]: x0=" + faces[ii].x + " y0=" + faces[ii].y  + " x1=" +
                //        (faces[ii].x + faces[ii].width)  + " y1=" + (faces[ii].y + faces[ii].height));

                // Crop the face
                mDetectedFace = mImageGray.submat(faces[ii].y,
                        faces[ii].y + faces[ii].height, faces[ii].x, faces[ii].x + faces[ii].width);
            

                //Log.d(TAG, "cropped_face: w*h=" + mDetectedFace.width() + "x" + mDetectedFace.height() +
                //        ", Row*Col=" + mDetectedFace.rows() + "x" + mDetectedFace.cols());

                adjustRectPosition(faces[ii], canvas.getWidth(), canvas.getHeight(), mWidth, mHeight);
                
                //Log.d(TAG, "face_2[" + ii + "]: x0=" + faces[ii].x + " y0=" + faces[ii].y  + " x1=" +
                //        (faces[ii].x + faces[ii].width)  + " y1=" + (faces[ii].y + faces[ii].height));
                
                canvas.drawRect(faces[ii].x, faces[ii].y, faces[ii].x + faces[ii].width, faces[ii].y + faces[ii].height, mPaintRect);
            }
        }

        //
        // change the state: from YES to NO !!!
        // 
        if (mHasDetected && faces.length <= 0) {
            //Log.d(TAG, "canvas: w=" + canvas.getWidth() + ",h=" + canvas.getHeight());
            //Log.i(TAG, "fd.onDraw(non): aha, " + faces.length + " faces are detected!");
            //
            // the simple timeout mechanism works pretty good here.
            //
            if (System.currentTimeMillis() - mTicksForTimeout >= TIMEOUT_FAILURE) {
                mHasDetected = false;
            }
        }
    }

    private void adjustRectPosition(Rect rect, int wv, int hv, int wc, int hc) {
        rect.y = rect.y - (wc - hv)/2;
        if (rect.y < 0) {
            rect.y = 0;
        }
        // 11:44 PM Nov 12 2015, the coordinates of two cameras are slight diff.
        if (!mIsCameraFacingBack) {
            rect.x = wv - (rect.x + rect.width);
        }
        if (rect.x < 0) {
            rect.x = 0;
        }
    }

    public boolean hasDetected() {
        return mHasDetected;
    }

    public void lock() {
        mIsLocked = true;
    }

    public void unlock() {
        mIsLocked = false;
    }

    public Mat getDetectedFace() {
        return mDetectedFace;
    }

    public Mat getDetectedFaceForDisplaying() {
        //
        // The following operation is only needed for Front-facing camera
        //
        if (!mIsCameraFacingBack) {
            Mat mat = new Mat();
            //
            // Mirror the face image
            //
            Core.flip(mDetectedFace, mat, 1);

            return mat;
        } else {
            return mDetectedFace;
        }
    }
}
