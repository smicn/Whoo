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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class FaceActivity extends Activity implements View.OnClickListener {

    private final String TAG = "whoo.FaceActivity";

    private Button mButtonOK, mButtonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_face);

        mButtonOK = (Button) findViewById(R.id.button_ok);
        mButtonOK.setOnClickListener(this);
        mButtonCancel = (Button) findViewById(R.id.button_cancel);
        mButtonCancel.setOnClickListener(this);

        ImageView imageView = (ImageView) findViewById(R.id.imageview_face);
        FaceDetector fd = FaceDetector.getInstance();
        Mat mat = fd.getDetectedFaceForDisplaying();
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        imageView.setImageBitmap(bitmap);

		WFaceRecognizer wfr = WFaceRecognizer.getInstance();
		String result = wfr.getResult();
		if (result != null) {
			TextView textView = (TextView) findViewById(R.id.textview_person);
			if (result.equals("Unknown")) {
				textView.setText("Sorry, unknown");
			} else {
				textView.setText(result + " (" + (int)wfr.getConfidence() + ")");
			}
		}
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_ok:
                //userInputName();
                userInputNameSmart();
                //this.finish();
                break;

            case R.id.button_cancel:
                this.finish();
                break;
        }
    }

    private void userInputName() {
        //
        // User Interaction:
        //
        // let user input name if tapping the face or where-ever.
        //
        final EditText input = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enter person's name" );
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                //
                // This is in a different context, so the instances
                // should be re-required.
                //
                FaceDetector fd = FaceDetector.getInstance();
                WFaceRecognizer wfr = WFaceRecognizer.getInstance();
                WFRDataFactory factory = WFRDataFactory.getInstance();
                LocalNameList lnlist = LocalNameList.getInstance();

                String instr = input.getText().toString();
                lnlist.inputLocalName(instr);
                int index = lnlist.findExistNameLocation(instr);
                if (-1 == index) {
                    Log.e(TAG, "WRONG NAME, WHY?");
                    WhooConfig.DBG(FaceActivity.this, "WRONG NAME, WHY?");
                    return;
                }

                String name = lnlist.getLocalName(index);

                WFRPerson person = factory.addPerson(name);
                if (null == person) {
                    Log.e(TAG, "Add Person Failed, WHY?");
                    WhooConfig.DBG(FaceActivity.this, "Add Person Failed, WHY?");
                    return;
                }

                Mat mat = fd.getDetectedFace();
                assert (mat != null);
                // resize the image to normalized size.
                mat = WhooTools.resize(mat);

                boolean ret = person.addFaceImage(mat);
                if (!ret) {
                    Log.e(TAG, "Add Image Failed, WHY?");
                    WhooConfig.DBG(FaceActivity.this, "Add Image Failed, WHY?");
                    return;
                } else {
                    WhooConfig.DBG(FaceActivity.this, "A face image added for " + name + " !");
                }

                // call FR.train() now, maybe it will run later on.
                wfr.train();

                // let the faceActivity exit
                FaceActivity.this.finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                // let the faceActivity exit
                FaceActivity.this.finish();
            }
        });
        builder.setCancelable(false); // cancel with button only
        builder.show();
    }

	private void userInputNameSmart() {
        //
        // Smart input implemented by AutoCompleteTextView
        //
		final AutoCompleteTextView input = new AutoCompleteTextView(this);
        input.setThreshold(1);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        WFaceRecognizer wfr = WFaceRecognizer.getInstance();
        String result = wfr.getResult();
        if (result != null && !result.equalsIgnoreCase("Unknown")) {
            input.setCompletionHint(result);
            input.setText(result);
        }

        String items[] = LocalNameList.getInstance().getLocalNames();
        input.setAdapter (new ArrayAdapter<String>(this ,android.R.layout.simple_dropdown_item_1line,items));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enter person's name" );
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                //
                // This is in a different context, so the instances
                // should be re-required.
                //
                FaceDetector fd = FaceDetector.getInstance();
                WFaceRecognizer wfr = WFaceRecognizer.getInstance();
                WFRDataFactory factory = WFRDataFactory.getInstance();
                LocalNameList lnlist = LocalNameList.getInstance();

                String instr = input.getText().toString();
                lnlist.inputLocalName(instr);
                int index = lnlist.findExistNameLocation(instr);
                if (-1 == index) {
                    Log.e(TAG, "WRONG NAME, WHY?");
                    WhooConfig.DBG(FaceActivity.this, "WRONG NAME, WHY?");
                    return;
                }

                String name = lnlist.getLocalName(index);

                WFRPerson person = factory.addPerson(name);
                if (null == person) {
                    Log.e(TAG, "Add Person Failed, WHY?");
                    WhooConfig.DBG(FaceActivity.this, "Add Person Failed, WHY?");
                    return;
                }

                Mat mat = fd.getDetectedFace();
                assert (mat != null);
                // resize the image to normalized size.
                mat = WhooTools.resize(mat);

                boolean ret = person.addFaceImage(mat);
                if (!ret) {
                    Log.e(TAG, "Add Image Failed, WHY?");
                    WhooConfig.DBG(FaceActivity.this, "Add Image Failed, WHY?");
                    return;
                } else {
                    WhooConfig.DBG(FaceActivity.this, "A face image added for " + name + " !");
                }

                // call FR.train() now, maybe it will run later on.
                wfr.train();

                // let the faceActivity exit
                FaceActivity.this.finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                // let the faceActivity exit
                FaceActivity.this.finish();
            }
        });
        builder.setCancelable(false); // cancel with button only
        builder.show();
	}
}
