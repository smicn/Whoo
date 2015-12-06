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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.util.Log;

public class NetworkClient {
	private final String TAG = "whoo";
	
	// default server in lab#212A
	private static final String HOSTIPADDR = "140.158.129.111";
	
	// default port
	private static final int PROTOPORT = 5190;

	private Socket socket;
	private String hostIPAddress;
	private int    hostPortID;
	private Thread thread;
	private String path;
	private NetworkListener listener;
	private OutputStream os;
	private InputStream  is;
	
	//
	// the header of tcp message.
	//
	private static final String TAGPROTOCOL0 = "F0E1D2C3B4A5#.msg=";
	//
	// ack for tcp message.
	//
	private static final String TAGTCPMSGACK = "tcpmsg.ack";
	//
	// messages
	//
	private static final String MSG_START    = "start";
	private static final String MSG_CANCEL   = "cancel";
	private static final String MSG_END      = "end";
	
	public NetworkClient()
	{
		hostPortID    = PROTOPORT;
		hostIPAddress = HOSTIPADDR;
		initThread();
	}
	
	public NetworkClient(int port)
	{
		hostPortID    = port;
		hostIPAddress = HOSTIPADDR;
		initThread();
	}
	
	public NetworkClient(String IP, int port)
	{
		hostPortID    = port;
		hostIPAddress = IP;
		initThread();
	}
	
	public void setListener(NetworkListener listener)
	{
		this.listener = listener;
	}
	
	public void connectToServer(String path) 
	{
		this.path = path;
		if (thread != null) {
			thread.start();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void disconnectFromServer() 
	{
		if (thread != null) {
			thread.stop();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			thread = null;
		}
	}
	
	private void initThread()
	{
		thread = new Thread() {
			@Override
		    public void run() {
				//
				// 1. create a long TCP connection.
				//
				if (connect()) {
					Log.d(TAG, "step-1: main-server connected.");
					//
					// 2. send message=START (and be acked).
					//
					boolean ret = transmit_and_ack(TAGPROTOCOL0 + MSG_START);
					if (ret) {
						Log.d(TAG, "step-2: main-server acked.");
						//
						// 3. build up FTP connection with ftpserver.
						//
						FTPClient ftp = new FTPClient();
						Log.d(TAG, "step-3.0: ftp-server recv-s file from: " + path);
						// 3.2 transmit the image file via ftp.
						ret = ftp.send(path);
						if (ret) {
							Log.d(TAG, "step-3: ftp-server recv-ed file!");
							//
							// 4. now, wait for the good news from the main server.
							//
							String result = wait_for_result();
							if (result != null) {
								Log.d(TAG, "step-4: main-server snd-ed back result!");
								//
								// Notice: SUCCESS, msg.extra=RESULT.
								//
								Log.e(TAG, "received SUCCESS msg from server!");
								if (listener != null) {
									listener.onNetworkCallback(NetworkListener.NETWORK_MAIN, 
											hostIPAddress, NetworkListener.FACEREC_SUCCESS, result);
								}
							}
							else {
								//
								// Notice: FAILURE, face-recognizer said.
								//
								Log.e(TAG, "received message but the server said failure!");
								if (listener != null) {
									listener.onNetworkCallback(NetworkListener.NETWORK_MAIN, 
											hostIPAddress, NetworkListener.FACEREC_FAILURE, null);
								}
							}
						}
						else {
							//
							// Notice: ftp connection failed.
							//
							Log.e(TAG, "ftp send file might be failed!");
							if (listener != null) {
								listener.onNetworkCallback(NetworkListener.NETWORK_FTP, 
										hostIPAddress, NetworkListener.NETWORK_DROPPED, null);
							}
						}
					}
					else {
						//
						// Notice: main connection failed.
						//
						Log.e(TAG, "try to send START msg failed!");
						if (listener != null) {
							listener.onNetworkCallback(NetworkListener.NETWORK_MAIN, 
									hostIPAddress, NetworkListener.NETWORK_DROPPED, null);
						}
					}
					
					//
					// 5. and last, close the long TCP connection (and terminate this thread).
					//
					disconnect();
				}
				else {
					Log.d(TAG, "step-1 failed: main-server tcp connect failed.");
					
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}
	
	private boolean connect()
	{
		try {
			InetAddress serverAddr = InetAddress.getByName(hostIPAddress);
			socket = new Socket(serverAddr, hostPortID);
			socket.setKeepAlive(true);
			//
			// it is supposed here socket is connected automatically.
			//
			if (socket.isConnected()) {
				return true;
			}
			else {
				Log.e(TAG, "ftpclient socket is not connected!");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void disconnect()
	{
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			socket = null;
		}
	}
	
	private boolean transmit_and_ack(String msg)
	{
		boolean ret = false;
		
		Log.d(TAG, "main-server: transmit_and_ack(): snd s:");
		
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())),
					true);
			pw.print(msg);
			pw.flush();
			Log.d(TAG, "main-server: transmit_and_ack(): snd e!");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Log.d(TAG, "main-server: transmit_and_ack(): wait-ack s:");
		try {
			BufferedReader br = new BufferedReader(new 
					InputStreamReader(is = socket.getInputStream()));
			String ack = br.readLine();
			//char buffer[] = new char[256];
			//br.read(buffer);
			//String ack = buffer.toString();
			Log.d(TAG, "main-server: transmit_and_ack(): wait-ack e: -half");
			if (ack != null && ack.equals(TAGTCPMSGACK)) {
				ret = true;
				Log.d(TAG, "main-server: transmit_and_ack(): wait-ack e: confirmed!");
			}
			else {
				Log.e(TAG, "main-server: transmit_and_ack(): wait-ack e: ack.msg=" + ack);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.d(TAG, "main-server: transmit_and_ack(): e!");
		
		return ret;
	}
	
	private String wait_for_result()
	{
		try {
			BufferedReader br = new BufferedReader(new 
					InputStreamReader(is = socket.getInputStream()));
			String msg = br.readLine();
			if (msg != null && msg.startsWith(TAGPROTOCOL0)) {
				msg = msg.substring(TAGPROTOCOL0.length());
				if (msg.startsWith(MSG_END)) {
					String ret = msg.substring((MSG_END + " :").length());
					
					Log.d(TAG, "see what I received: " + ret);
					
					return ret;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
