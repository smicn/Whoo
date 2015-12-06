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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
//import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.MotionEvent;
//import android.provider.MediaStore.Files.FileColumns;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

//implements OnClickListener 
public class MainActivity extends Activity implements OnTouchListener {

	private final String TAG = "whoo";
	
    private CameraPreview mPreview;
	private View mCameraSwitchView;
	private View mCameraPreviewView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
    	setContentView(R.layout.activity_main2);
        //Top-Menu
		LayoutInflater inflater = (LayoutInflater)this.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
		View top = inflater.inflate(R.layout.top_menu, null);
		//top.findViewById(R.id.cameraSwithButton).setOnClickListener(this);
		//top.findViewById(R.id.settingsButton).setOnClickListener(this);
		addContentView(top, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		//Bottom-Menu
		View bottom = inflater.inflate(R.layout.bottom_menu, null);
		//buttons.findViewById(R.id.helpButton).setOnClickListener(this);
		//buttons.findViewById(R.id.clearButton).setOnClickListener(this);			
		addContentView(bottom, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this);
        mCameraPreviewView = findViewById(R.id.camera_preview);
        ((ViewGroup)mCameraPreviewView).addView(mPreview);

		mCameraPreviewView.setOnTouchListener(this);
		
        // Add a listener for the camera switch
        mCameraSwitchView = findViewById(R.id.cameraSwithButton);
        mCameraSwitchView.setOnClickListener(
        		new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (Camera.getNumberOfCameras() > 1) {
							
							mPreview.switchCamera();
							
							if (mPreview.isCameraFront()) {
                                Toast.makeText(MainActivity.this, "Camera switched to Front-facing!", Toast.LENGTH_LONG).show();
							}
							else {
                                Toast.makeText(MainActivity.this, "Camera switched to Rear-facing!", Toast.LENGTH_LONG).show();
							}
						}
					}
				}
        );
		
		// Add a listener to the Settings Menu Button.
        Button settings = (Button)findViewById(R.id.settingsButton);
        settings.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
					startActivity(intent);
                }
            }
        );

		// Set surface view for face detector.
		FaceDetector.getInstance().setView(mPreview);
	}

	
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume() called.");
		
		super.onResume();

		if (Settings.getInstance().isOpenCVInited()) {
			Log.i(TAG, "onResume(): start camera preview:");
			mPreview.startCameraPreview();
		}
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause() called.");
		
		super.onPause();

		mPreview.stopCameraPreview();
		
		if (isFinishing()) {
			//
			// Exit by user pressing KEY_BACK.
			//
			WFRDataFactory.getInstance().flush();
			Settings.getInstance().sync();
			LocalNameList.getInstance().store();
			//FaceDetector.getInstance().destroy();
			WhooLog.close();
		} else {
			// Exit for other reasons.
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// take an picture using the camera
        	//mCamera.takePicture(null, null, mPicture);

			FaceDetector fd = FaceDetector.getInstance();
			//
			// fd.lock means: pause face detection
			//
			fd.lock();
			//
			// if no face detected, nothing will be done.
			//
			if (fd.hasDetected()) {
				//
				// User Interaction:
				//
				// let user input name if tapping the face or where-ever.
				//
                Intent intent = new Intent(MainActivity.this, FaceActivity.class);
                startActivity(intent);
			}
			//
			// now, unlock face detection.
			//
			fd.unlock();
			
			return true;
		}
		return false;
	}
}
