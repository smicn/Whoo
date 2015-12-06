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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.util.Log;

public class FTPClient {
	private final String TAG = "whoo";
	
	// default server in lab#212A
	private static final String HOSTIPADDR = "140.158.129.111";
	
	// default port
	private static final int PROTOPORT = 5191;

	private Socket socket;
	private String hostIPAddress;
	private int    hostPortID;
	
	//
	// the header of ftp control message.
	//
	private static final String TAGPROTOCOL = "A5B4C3D2E1F0#.ftp.";
	//
	// A5B4C3D2E1F0#.ftp.name=picture001.jpg
	//
	private static final String TAGFILENAME = "name";
	//
	// A5B4C3D2E1F0#.ftp.size=12341024
	//
	private static final String TAGFILESIZE = "size";
	//
	// A5B4C3D2E1F0#.ftp.start
	//
	private static final String TAGFTPSTART = "start";
	//
	// A5B4C3D2E1F0#.ftp.end
	//
	private static final String TAGFTPEND   = "end";
	//
	// A5B4C3D2E1F0#.ftp.ack
	//
	private static final String TAGFTPACK   = "ack";

	private int DEBUG_FILE_SIZE;
	
	public FTPClient()
	{
		hostPortID    = PROTOPORT;
		hostIPAddress = HOSTIPADDR;
	}
	
	public FTPClient(int port)
	{
		hostPortID    = port;
		hostIPAddress = HOSTIPADDR;
	}
	
	public FTPClient(String IP, int port)
	{
		hostPortID    = port;
		hostIPAddress = IP;
	}
	
	public boolean send(String path)
	{
		DEBUG_FILE_SIZE= 0;
		
		if (!transmit_file_name(path)) {
			Log.e(TAG, "ftp client: failed to snd file name!");
			return false;
		}
		if (!transmit_file_size(path)) {
			Log.e(TAG, "ftp client: failed to snd file size!");
			return false;
		}
		if (!transmit_file_data_start()) {
			Log.e(TAG, "ftp client: failed to snd file start tag!");
			return false;
		}
		if (!transmit_file_data(path)) {
			Log.e(TAG, "ftp client: failed to snd file data!");
			return false;
		}
		if (!transmit_file_data_end()) {
			Log.e(TAG, "ftp client: failed to snd file end tag!");
			return false;
		}
		
		return true;
	}
	
	private boolean connect()
	{
		try {
			InetAddress serverAddr = InetAddress.getByName(hostIPAddress);
			socket = new Socket(serverAddr, hostPortID);
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
	
	private boolean transmit_and_ack(String cmd)
	{
		boolean ret = false;
		
		Log.d(TAG, "1. tran_and_ack(" + cmd + ").");
		if (connect()) {
			try {
				Log.d(TAG, "2.1");
				PrintWriter pw = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())),
						true);
				pw.print(cmd);
				pw.flush();
				Log.d(TAG, "2.2");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				Log.d(TAG, "3.1");
				BufferedReader br = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				String ack = br.readLine();
				Log.d(TAG, "3.2 ack=" + ack);
				if (ack != null && ack.equals(TAGPROTOCOL+TAGFTPACK)) {
					Log.d(TAG, "3.3");
					ret = true;
				}
				else {
					Log.d(TAG, "3.4");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d(TAG, "4.1");
			disconnect();
			Log.d(TAG, "4.2");
		}
		
		return ret;
	}
	
	private boolean transmit_file_name(String path)
	{
		String name = path.substring(path.lastIndexOf("/") + 1);
		String cmd = TAGPROTOCOL + TAGFILENAME + "=" + name;
		return transmit_and_ack(cmd);
	}
	
	private boolean transmit_file_size(String path)
	{
		File file = new File(path);
		String size = String.valueOf(file.length());
		DEBUG_FILE_SIZE = (int)file.length();
		String cmd = TAGPROTOCOL + TAGFILESIZE + "=" + size;
		return transmit_and_ack(cmd);
	}
	
	private boolean transmit_file_data_start()
	{
		String cmd = TAGPROTOCOL + TAGFTPSTART;
		return transmit_and_ack(cmd);
	}
	
	private boolean transmit_file_data(String path)
	{
		boolean ret = false;
		
		Log.d(TAG, "1. tran_file_data(" + path + ").");
		if (connect()) {
			try {
				Log.d(TAG, "2.1");
				OutputStream os = socket.getOutputStream();
				Log.d(TAG, "2.2");
				FileInputStream fis = new FileInputStream(new File(path));
				
				Log.d(TAG, "2.3");
				byte buffer[] = new byte[128];
				int size = 0;
				int count;
				while ((count = fis.read(buffer)) != -1) {
					if (count > 0) {
						os.write(buffer, 0, count);
						os.flush();
					}
					size += count;
				}
				Log.d(TAG, "2.4");
				if (DEBUG_FILE_SIZE == size) {
					Log.d(TAG, "2.4.2: yes, same size!");
				}
				else {
					Log.d(TAG, "2.4.3: No, not same size. old=" + DEBUG_FILE_SIZE + ", new=" + size);
				}
				//os.flush();
				Log.d(TAG, "2.5");
				fis.close();
				Log.d(TAG, "2.6");
				ret = true;
			} catch (IOException e) {
				e.printStackTrace();
			}

			disconnect();
			Log.d(TAG, "3");
		}
		
		return ret;
	}
	
	private boolean transmit_file_data_end()
	{
		String cmd = TAGPROTOCOL + TAGFTPEND;
		return transmit_and_ack(cmd);
	}
}
