//
// LBPHFaceRecognizer.java 
//
// https://gist.githubusercontent.com/berak/9830239/raw/
// 4d0f2f3939d766b1af4f5405c8d30839db312291/gistfile1.txt
//

package com.lamar.cs.whoo;

import org.opencv.contrib.FaceRecognizer;
import org.opencv.core.Core;


public class LBPHFaceRecognizer extends FaceRecognizer {

    //static{ System.loadLibrary("facerec"); } // .so

    private static native long createLBPHFaceRecognizer0();
    private static native long createLBPHFaceRecognizer1(int radius);
    private static native long createLBPHFaceRecognizer2(int radius,int neighbours);
	private static native long createLBPHFaceRecognizer(
		int radius,int neighbours, int grid_x, int grid_y, double threshold);
 
    public LBPHFaceRecognizer() {
    	super(createLBPHFaceRecognizer0());
    }
	
    public LBPHFaceRecognizer(int radius) {
    	super(createLBPHFaceRecognizer1(radius));
    }
	
    public LBPHFaceRecognizer(int radius,int neighbours) {
    	super(createLBPHFaceRecognizer2(radius, neighbours));
    }

	public LBPHFaceRecognizer(int radius,int neighbours, int grid_x, int grid_y, double threshold) {
    	super(createLBPHFaceRecognizer(radius, neighbours, grid_x, grid_y, threshold));
    }
}