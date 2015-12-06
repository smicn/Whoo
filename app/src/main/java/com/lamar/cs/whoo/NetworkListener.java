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

public interface NetworkListener {

	public static final String NETWORK_DROPPED = "Network-Dropped";
	public static final String FACEREC_SUCCESS = "FaceRec-Success";
	public static final String FACEREC_FAILURE = "FaceRec-Failure";
	public static final String FTP_SUCCESS     = "FTP-Success";
	public static final String FTP_FAILURE     = "FTP-Failure";
	
	public static final String NETWORK_MAIN = "main";
	public static final String NETWORK_FTP  = "FTP";
	
	//
	// network: main OR FTP
	// hostIP : 140.158.129.111
	// message: FaceRec-Success (hoped to be)
	// extra  : will be face-label when message is FaceRec-Success.
	//
	public abstract void onNetworkCallback(String network, String hostIP, String message, String extra);
}
