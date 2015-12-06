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

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private final String TAG = "whoo";

	private Context       mContext;
    private Camera        mCamera;
	private int           mCameraID;

    @SuppressWarnings("deprecation")
	public CameraPreview(Context context) {
        super(context);
        Log.e(TAG, "CameraPreview() constructed!");
		mContext = context;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        getHolder().addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    @Override
	public void surfaceCreated(SurfaceHolder holder) {
    	Log.e(TAG, "CameraPreview.surfaceCreated()!");

		try {
			connectCamera();
			
		} catch (Exception exception) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage("Error: Cannot open camera!" )
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				})
				.show();
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
		}

		// unless this, the surface.onDraw() method will not be called.
		setWillNotDraw(false);
    }

    @Override
	public void surfaceDestroyed(SurfaceHolder holder) {
    	Log.e(TAG, "CameraPreview.surfaceDestroyed()!");
        // empty. Take care of releasing the Camera preview in your activity.
        disconnectCamera();
    }

    @Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
    	Log.e(TAG, "CameraPreview.surfaceChanged(.., w=" + w + ", h=" + h + ")!");

        if (getHolder().getSurface() == null){
          // preview surface does not exist
          return;
        }

        disconnectCamera();

		Log.e(TAG, "CameraPreview.surfaceChanged(): will reopen camera!");

        // start preview with new settings
        connectCamera();
    }

	@Override
	protected void onDraw(Canvas canvas) {
		if (!Settings.getInstance().isOpenCVInited()) return;
		
		//
		// face-detector draws if needed 
		//
		FaceDetector fd = FaceDetector.getInstance();
		WFaceRecognizer fr = WFaceRecognizer.getInstance();
		
		fd.onDrawView(canvas);

		//
		// face-recognizer draws if needed
		//
		if (fd.hasDetected()) {
			//
			// if face predicted, show the name to user
			//
			fr.onDrawView(canvas);
		} else {
			//
			// otherwise, clear the previous results
			//
			fr.clear();
		}

		super.onDraw(canvas);
	}

	public void switchCamera() {
		Log.d(TAG, "CameraPreview.switchCamera()!");
		
		FaceDetector fd = FaceDetector.getInstance();
		
		disconnectCamera();
		
		if (Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraID) {
			mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
			
			fd.setCameraFacingBack(true);
		} else {
			mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;

			fd.setCameraFacingBack(false);
		}

		connectCamera();
	}

	public boolean isCameraFront() {
		return (Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraID);
	}

	public void initCameraPreview() {
		
		Log.d(TAG, "CameraPreview.initCameraPreview()!");
		
		mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
	}

	public void startCameraPreview() {

		Log.d(TAG, "CameraPreview.startCameraPreview()!");
		
		disconnectCamera();

		Log.d(TAG, "CameraPreview.startCameraPreview(): reopen camera.");

		connectCamera();
	}

	public void stopCameraPreview() {

		Log.d(TAG, "CameraPreview.stopCameraPreview()!");
		
		disconnectCamera();
	}

	private void disconnectCamera() {
		synchronized (this) {
			if (mCamera != null) {
	            mCamera.stopPreview();
	            mCamera.setPreviewCallback(null);

	            mCamera.release();
				mCamera = null;
	        }
		}
	}

	private void connectCamera() {
		synchronized (this) {
			try {
	            mCamera = Camera.open(mCameraID);
	        }
	        catch (Exception e){
	            Log.e(TAG, "CameraPreview.connectCamera(): failed to open Camera!");
	        }

			// set preview size and make any resize, rotate or
	        // reformatting changes here
	        Camera.Parameters params = mCamera.getParameters();
	        List<Camera.Size> sizeList = params.getSupportedPreviewSizes();
	        if (sizeList.size() > 0) {
	        	Camera.Size size = sizeList.get(0);
	        	Log.i(TAG, "Camera.params.supportedPreviewSize[0]: "
	        			+ size.width + "x" + size.height);

		        params.setPictureSize(size.width, size.height);
		        params.setPreviewSize(size.width, size.height);

				//
				// face detector also needs Camera.Preview.size
				//
				FaceDetector fd = FaceDetector.getInstance();
				fd.setResolution(size.width, size.height);
	        }
			else {
				Log.e(TAG, "Fatal Error to find supported preview size!");
			}
			
	        params.set("orientation", "portrait");
	        mCamera.setDisplayOrientation(90);

			// Preview callback used whenever new viewfinder frame is available
			mCamera.setPreviewCallback(new PreviewCallback() {
				//@Override
				public void onPreviewFrame(byte[] data, Camera camera) {

					//Log.d(TAG, "camera preview: onPreviewFrame()!");

					FaceDetector fd = FaceDetector.getInstance();
					fd.onCameraFrameInjected(data);
					
				}
			});

			//try to set preferred parameters
			try {
				//params.setPreviewFrameRate(10);
				//params.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);
				//params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				mCamera.setParameters(params);
				
			} catch (Exception e) {
				Log.d(TAG, "Error setting camera params: " + e.getMessage());
			}

			//Finally we are ready to start the preview
			try {
			    mCamera.setPreviewDisplay(getHolder());
                mCamera.startPreview();
			} catch (Exception e) {
				Log.d(TAG, "Error starting camera preview: " + e.getMessage());
			}
		}
	}
}
