//
// EigenFaceRecognizer.java 
//
// https://gist.githubusercontent.com/berak/9830239/raw/
// 4d0f2f3939d766b1af4f5405c8d30839db312291/gistfile1.txt
//
package com.lamar.cs.whoo;

import org.opencv.contrib.FaceRecognizer;

public class EigenFaceRecognizer extends FaceRecognizer {

    //static{ System.loadLibrary("facerec"); } // .so

    private static native long createEigenFaceRecognizer0();
	private static native long createEigenFaceRecognizer(int num_components, double threshold);
 
    public EigenFaceRecognizer() {
    	super(createEigenFaceRecognizer0());
    }

	public EigenFaceRecognizer(int num_components, double threshold) {
    	super(createEigenFaceRecognizer(num_components, threshold));
    }
}