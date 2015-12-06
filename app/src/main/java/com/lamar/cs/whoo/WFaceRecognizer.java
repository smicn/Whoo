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
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.View;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.CvType; 
import org.opencv.imgproc.Imgproc;
import org.opencv.contrib.FaceRecognizer;
import org.opencv.android.Utils;

/**
 * Created by Samuel on 10/03/2015.
 */
public class WFaceRecognizer {

	private final String TAG = "whoo.wfr";
	
    private FaceRecognizer mWritable;
	private FaceRecognizer mReadable;
	private static WFaceRecognizer mInstance;

	private String  mPredictResult;
	private String  mAlgorithm;
	private boolean mTraining;
	/*
	 * http://stackoverflow.com/questions/25683514/how-to-calculate-
	 * percentage-format-prediction-confidence-of-face-recognition-usi
	 */
	private double  mConfidence;

	private Context mContext;
	private View    mView;
	private Paint   mPaintText;

	private static final int FACE_WIDTH  = WhooConfig.FACE_WIDTH;
	private static final int FACE_HEIGHT = WhooConfig.FACE_HEIGHT;

    private WFaceRecognizer() {
		mAlgorithm = null;
		mWritable  = null;
		mReadable  = null;
		mTraining  = false;
		mPredictResult = null;

		mPaintText = new Paint();
		mPaintText.setStyle(Paint.Style.FILL);
		mPaintText.setColor(Color.RED);
		mPaintText.setTextSize(64);
        mPaintText.setFakeBoldText(true);
		mPaintText.setTextAlign(Align.CENTER);	
    }

	public static WFaceRecognizer getInstance() {
		if (null == mInstance) {
			mInstance = new WFaceRecognizer();
		}
		return mInstance;
	}

	public FaceRecognizer getRecognizerInstance() {
		return mReadable;
	}

	public FaceRecognizer getTrainerInstance() {
		return mWritable;
	}

	public void init() {
		Log.d(TAG, "init() called:");

		String algorithm = Settings.getInstance().getFaceRecognitionAlgorithm();
		if (algorithm.equals(Settings.ALGORITHM_EIGEN)) {
			mAlgorithm = "egien";
		} else if (algorithm.equals(Settings.ALGORITHM_FISHER)) {
			mAlgorithm = "fisher";
		} else if (algorithm.equals(Settings.ALGORITHM_LBPH)) {
			mAlgorithm = "LBPH";
		} else {
			mAlgorithm = WhooConfig.DEFAULT_ALGORITHM;
		}
		
		String modelPath = getModelPath(true);
		if (null == modelPath) {
			Log.e(TAG, "fatal error: model-path cannot be null!");
			mReadable = null;
		} else {
			Log.d(TAG, "mReadable.created:");
			mReadable = createFaceRecognizer();
			
			Log.d(TAG, "mReadable.load():");
			mReadable.load(modelPath);
			Log.d(TAG, "mReadable.load()-ed from: " + modelPath);
		}
	}

	private void beginTraining() {
		if (mTraining) {
			Log.e(TAG, "warning: cannot run train() too frequently!");
			return;
		}

		mWritable  = createFaceRecognizer();
		
		mTraining = true;
	}

	private void endTraining(boolean result) {
		if (!mTraining) return;

		if (result) {
			String modelPath = getModelPath(false);
			if (null == modelPath) {
				Log.e(TAG, "fatal error: model-path cannot be null!");
			} else {
				Log.d(TAG, "mWritable.save():");
				mWritable.save(modelPath);
				Log.d(TAG, "mWritable.save()-ed to: " + modelPath);

				//
				// update the instance references at the end of training.
				//
				mReadable = mWritable;
			}
		}
		
		mTraining = false;
	}

	private String getModelPath(boolean checkExist) {
		if (null == mAlgorithm) return null;

		File dir = null;
		
        if (WhooConfig.USING_EXTERNAL_STORAGE) {
            dir = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/" + WhooConfig.ROOT_DIR());
            if (!dir.exists()) {
				Log.d(TAG, "error, not exist path: " + dir.getAbsolutePath());
				return null;
            } else {
            	dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
					 + "/" + WhooConfig.ROOT_DIR() + "/" + mAlgorithm + ".xml");
            }
        }
        else {
            dir = mContext.getFileStreamPath(mAlgorithm + ".xml");
        }
		
        if (dir != null) {
            if (!checkExist || dir.exists()) {
                return dir.getAbsolutePath();
            }
        }

		return null;
	}

	private FaceRecognizer createFaceRecognizer() {
        FaceRecognizer model = null;

		if (mAlgorithm.equals("eigen")) {
			model = new EigenFaceRecognizer();
		}
		else if (mAlgorithm.equals("fisher")) {
			model = new FisherFaceRecognizer();
			//model.setDouble("threshold", WhooConfig.FRTHRESHOLD);
		}
		else if (mAlgorithm.equals("LBPH")) {
			model =  new LBPHFaceRecognizer(2, 8, 8, 8, WhooConfig.FRTHRESHOLD);
		}
		else {
			Log.e(TAG, "failure: cannot support algorithm " + mAlgorithm);
		}
		
        return model;
	}

	public void setAlgorithm(String algorithm) {
		if (algorithm.equals(mAlgorithm))
			return;

		if (algorithm.toLowerCase().contains("eigen")) {
			mAlgorithm = "eigen";
		} else if (algorithm.toLowerCase().contains("fisher")) {
			mAlgorithm = "fisher";
		} else {
			mAlgorithm = "LBPH";
		}
		
		String modelPath = getModelPath(true);
		if (null == modelPath) {
			Log.e(TAG, "fatal error: model-path cannot be null!");
			mReadable = null;
		} else {
			Log.d(TAG, "mReadable.created:");
			mReadable = createFaceRecognizer();
			
			Log.d(TAG, "mReadable.load():");
			mReadable.load(modelPath);
			Log.d(TAG, "mReadable.load()-ed from: " + modelPath);
		}
	}

	public void setContext(Context context) {
		mContext = context;
	}

	public void setView(View view) {
		mView = view;
	}

	public void onDrawView(Canvas canvas) {
		//
		// so, the current version places predict() in the main thread. and
		// the tests showed that it is pretty fast. If more performance optimizations
		// are necessary, it is not a problem if we move predict() into other
		// background threads.
		//
		predict();

		if (mPredictResult != null) {
			//
			//TODO: it's not good to have hard code here..
			//
			final int FR_TEXT_XX = WhooConfig.SCREEN_WIDTH/2;
			final int FR_TEXT_YY = WhooConfig.SCREEN_HEIGHT - 550;
			
			canvas.drawText(mPredictResult, FR_TEXT_XX, FR_TEXT_YY, mPaintText);
		}
		
	}

	public void clear() {
		if (mPredictResult != null) {
			mPredictResult = null;
			mConfidence = 0.0;
		}
	}

	public String getResult() {
		return mPredictResult;
	}

	public double getConfidence() {
		return mConfidence;
	}

	private boolean predict() {
		Log.d(TAG, "predict() is called:");
		
		FaceDetector fd = FaceDetector.getInstance();
		assert(fd != null);
		if (!fd.hasDetected()) {
			Log.e(TAG, "fd.hasDetected()==false? unexpected!");
			mPredictResult = null;
			return false;
		}

        if (null == mReadable) {
			Log.e(TAG, "this.mReadable is not ready.");
			mPredictResult = null;
            return false;
        }

		// never call predict() if dataset is empty!
        WFRDataFactory wdf = WFRDataFactory.getInstance();
		assert(wdf != null);
        Vector<WFRFaceImage> images = wdf.getFaceImages();
		if (images.size() <= 0) {
			Log.e(TAG, "wdf.getFaceImages() has images.size()=" + images.size());
			mPredictResult = null;
			return false;
		}

		Mat face = fd.getDetectedFace();
		assert(face != null);

		// never forget to resize and normalize the face!
		face = WhooTools.resize(face);

		// only obtain the result with most confidence if we have more than one.
		int label[] = new int[1];
		double confidence[] = new double[1];

		Log.d(TAG, "call opencv.predict():");
		mReadable.predict(face, label, confidence);
		Log.d(TAG, "opencv.predict() returned, label[0]=" + label[0]);

		if (-1 == label[0]) {
			// here we got a negative result.
			Log.d(TAG, "result: negative, i do not know who this is.");
			
			mPredictResult = "Unknown";
			mConfidence = 0.0;
		} else {
			// translate the label from integer to person's name.
			WFRPerson person = wdf.getPersonByLabel(label[0]);
			if (null == person) {
				Log.e(TAG, "unexpected result: label=" + label[0]);

				mPredictResult = null;
				mConfidence = 0.0;
			} else {
				Log.e(TAG, "successfully predicted result: label=" + label[0]
					+ ", person= " + person.getName() + ", confidence=" + (int)confidence[0]);
				
				mPredictResult = person.getName();
				mConfidence = confidence[0];
				
				return true;
			}
		}

		return false;
	}

	private Mat clearBackground(Mat mat) {
		return mat;
	}

	/********************************************************
	//
	// cv:elbp
	//
	template <typename _Tp> static
	inline void elbp_(InputArray _src, OutputArray _dst, int radius, int neighbors) {
	    //get matrices
	    Mat src = _src.getMat();
	    // allocate memory for result
	    _dst.create(src.rows-2*radius, src.cols-2*radius, CV_32SC1);
	    Mat dst = _dst.getMat();
	    // zero
	    dst.setTo(0);
	    for(int n=0; n<neighbors; n++) {
	        // sample points
	        float x = (float)(-radius) * sin(2.0*CV_PI*n/(float)(neighbors));
	        float y = (float)(radius) * cos(2.0*CV_PI*n/(float)(neighbors));
	        // relative indices
	        int fx = static_cast<int>(floor(x));
	        int fy = static_cast<int>(floor(y));
	        int cx = static_cast<int>(ceil(x));
	        int cy = static_cast<int>(ceil(y));
	        // fractional part
	        float ty = y - fy;
	        float tx = x - fx;
	        // set interpolation weights
	        float w1 = (1 - tx) * (1 - ty);
	        float w2 =      tx  * (1 - ty);
	        float w3 = (1 - tx) *      ty;
	        float w4 =      tx  *      ty;
	        // iterate through your data
	        for(int i=radius; i < src.rows-radius;i++) {
	            for(int j=radius;j < src.cols-radius;j++) {
	                // calculate interpolated value
	                float t = w1*src.at<_Tp>(i+fy,j+fx) + w2*src.at<_Tp>(i+fy,j+cx) + w3*src.at<_Tp>(i+cy,j+fx) + w4*src.at<_Tp>(i+cy,j+cx);
	                // floating point precision, so check some machine-dependent epsilon
	                dst.at<int>(i-radius,j-radius) += ((t > src.at<_Tp>(i,j)) || (std::abs(t-src.at<_Tp>(i,j)) < std::numeric_limits<float>::epsilon())) << n;
	            }
	        }
	    }
	}
	*******************************************************/
    /*
	private Mat doELBP(Mat src, int radius, int neighbors) {
		Mat dst = new Mat(src.rows()-2*radius, src.cols()-2*radius, CvType.CV_32SC1);
		dst.setTo(0);
	    for(int n=0; n<neighbors; n++) {
	        // sample points
	        float x = (float)(-radius) * Math.sin(2.0*Math.PI*n/(float)(neighbors));
	        float y = (float)(radius)  * Math.cos(2.0*Math.PI*n/(float)(neighbors));
	        // relative indices
	        int fx = (int)(floor(x));
	        int fy = (int)(floor(y));
	        int cx = (int)(ceil(x));
	        int cy = (int)(ceil(y));
	        // fractional part
	        float ty = y - fy;
	        float tx = x - fx;
	        // set interpolation weights
	        float w1 = (1 - tx) * (1 - ty);
	        float w2 =      tx  * (1 - ty);
	        float w3 = (1 - tx) *      ty;
	        float w4 =      tx  *      ty;
	        // iterate through your data
	        for(int i=radius; i < src.rows()-radius;i++) {
	            for(int j=radius;j < src.cols()-radius;j++) {
	                // calculate interpolated value
	                float t = w1*src.at<_Tp>(i+fy,j+fx) + w2*src.at<_Tp>(i+fy,j+cx) + w3*src.at<_Tp>(i+cy,j+fx) + w4*src.at<_Tp>(i+cy,j+cx);
	                // floating point precision, so check some machine-dependent epsilon
	                dst.at<int>(i-radius,j-radius) += ((t > src.at<_Tp>(i,j)) || (std::abs(t-src.at<_Tp>(i,j)) < std::numeric_limits<float>::epsilon())) << n;
	            }
	        }
	    }
        return dst;
	}*/

	private boolean doTrain() {
		WFRDataFactory wdf = WFRDataFactory.getInstance();
		assert(wdf != null);
		if (null == wdf)
			return false;

        Vector<WFRFaceImage> vector = wdf.getFaceImages();

		int imageCount = vector.size();
		if (0 == imageCount) {
			Log.d(TAG, "doTrain() failed due to empty face image set!");
			return false;
		}

		// Fisherfaces does need at least two face image subsets.
		if (mAlgorithm.equals("fisher")) {
			if (wdf.getPersonCount() < 2) {
				Log.d(TAG, "doTrain() failed coz fisher requires more persons!");
				return false;
			}
		}

	    List<Mat> images = new ArrayList<Mat>();
	    int[] labels = new int[imageCount];

		WhooLog.d("do_train(): img_cnt=" + imageCount);
	   
	    for (int ii = 0; ii < imageCount; ii++) {
	    	WFRFaceImage wfi = vector.get(ii);
			assert(wfi != null);
			WFRPerson person = wfi.mPerson;
			assert(person != null);
			
            Mat image = wfi.mMat;
	        if (null == image) {
	        	Log.e(TAG,"doTrain(): failed to cVLoadImage(" + wfi.mPath + ")!");
				return false;
	        }

			if (FACE_WIDTH != image.width() || FACE_HEIGHT != image.height()) {
				Log.e(TAG,"doTrain(): fatal error: invalid image size: " +
					image.width() + "x" + image.height() + " !");
				return false;
			}
			
            images.add(image);
	        labels[ii] = person.getLableID();

			Log.d(TAG, "img[" + ii + "]: " + person.getLableID() + 
				", " + wfi.mName + ", " + wfi.mPath);
	    }
		
		WhooLog.d("start training: current algorithm is " + mAlgorithm);
        mWritable.train(images, new MatOfInt(labels));
		WhooLog.d("end of training!");
		
		Log.d(TAG, "data factory updated and saved.");

		return true;
	}

	//
	// one of the contributions of this software is to let the predict() 
	// and training() procedures run simutaneusly and this is obtained by
	// using multi-threading. training() runs in the background thread
	// and the training datasets and their holder instances are duplicated,
	// which is a better way rather than mutual exclusion. 
	//
	private class Trainer implements Runnable {
        public void run() {
			beginTraining();

            WhooLog.d("doTrain() start: ");
			WhooTools.startTickCount();
			boolean ret = doTrain();
            WhooLog.d("doTrain() end, cost=" + WhooTools.endTickCount() + "ms.");

			endTraining(ret);
        }
	}
	
	public void train() {
		if (mTraining) {
			//
			// a timer machanism works here if there are two frequent 
			// face recognition requests. 
			//
			final Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
    			public void run() {
    				if (!mTraining) {
						timer.cancel();
	    				Thread thread = new Thread(new Trainer());
	        			thread.start();
    				}
				}
			}, 0, 8 * 1000);
		} else {
            Thread thread = new Thread(new Trainer());
            thread.start();
        }
	}
}
