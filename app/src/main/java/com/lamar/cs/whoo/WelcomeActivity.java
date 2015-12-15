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

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class WelcomeActivity extends Activity {
    
    private final String TAG = "whoo";

	private final int MSG_FINISH_ACTIVITY = 1;
	private final int MSG_INSTALL_OPENCV  = 2;
    
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = MSG_FINISH_ACTIVITY;
            handler.sendMessage(message);
        }
    };
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
				case MSG_FINISH_ACTIVITY:
	                if (Settings.getInstance().isOpenCVInited()) {
	                    if (timer != null) {
	                        timer.cancel();
	                        timer = null;
	                    }
	                    
	                    Log.e(TAG, "Since Inits are done, timer invokes next Activity!");
	                    
	                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
	                    startActivity(intent);
	                    //
	                    // since it is an welcome window, it should be seen 
	                    // only once and never come back again.
	                    //
	                    WelcomeActivity.this.finish();
	                } else {
	                    Log.e(TAG, "what? initializations cost too long time!");
	                }
					break;

				case MSG_INSTALL_OPENCV:
					// Init OpenCV and other data-loading related..
                    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, WelcomeActivity.this.getApplicationContext(), mLoaderCallback);
					break;
					
				default:
					super.handleMessage(msg);
					break;
            }
        }
    };

    //
    // OpenCV init and callback
    //
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                        Log.i(TAG, "OpenCV loaded successfully");

                        // Load native library after(!) OpenCV initialization
                        System.loadLibrary("facerec");
                        Log.i(TAG, "libfacerec.so loaded successfully");

                        // it has to be placed here.. (need init of opencv)
                        WFRDataFactory.getInstance().load();
                        Log.i(TAG, "WFR data loaded.");

                         FaceDetector fd = FaceDetector.getInstance();
                        fd.setContext(WelcomeActivity.this);
                        fd.init();
                        Log.i(TAG, "fd inited.");

                        WFaceRecognizer wfr = WFaceRecognizer.getInstance();
                        wfr.setContext(WelcomeActivity.this);
                        wfr.init();
                        Log.i(TAG, "fr inited.");

                        // OpenCV inited.
                        Settings.getInstance().onOpenCVInited();

                        WhooLog.d("Whoo's initiliazitions end, cost=" + WhooTools.endTickCount() + "ms.");

                        Log.i(TAG, "now, all initilizations are done!");
                    }
                     break;

                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        if (WhooConfig.USING_CLIENT_SERVER_MODE) {
            Toast.makeText(this, "IP: " + WhooTools.getLocalIP(this),
                    Toast.LENGTH_LONG).show();
        }

        final Timer timer0 = new Timer();
        timer0.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timer0.cancel();

				// Init Log module.
                WhooLog.open(WelcomeActivity.this);

                // Start detecting time cost of inits.
                WhooTools.startTickCount();

                // Init Settings module and load data.
                Settings.getInstance().initContext(WelcomeActivity.this);
                Settings.getInstance().load();

                // Init Config module.
                WhooConfig.setContext(WelcomeActivity.this);

                // Init the Data Management module.
                WFRDataFactory.getInstance().setContext(WelcomeActivity.this);

                // Init LocalNameList module and load cache data.
                LocalNameList.getInstance().setContext(WelcomeActivity.this);
                LocalNameList.getInstance().load();

				// Init OpenCV and other data-loading related..
				// But this has to be done in the main thread!!
				Message message = new Message();
	            message.what = MSG_INSTALL_OPENCV;
	            handler.sendMessage(message);
            }
        }, 300, 100);

        // Let the timer check if the initilizations are done or not every 1000 ms.
        timer.schedule(task, 500, 3000);
    }
    
    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }
}
