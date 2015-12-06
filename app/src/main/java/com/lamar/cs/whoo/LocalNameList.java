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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class LocalNameList {

    private final String TAG = "whoo";
    
    private static final String PRIVATE_FILE = WhooConfig.LOCALNAMES_FILE();
    
    private Context mContext;
    
    private static final int MAXCOUNT_LIMIT = 100;

    private static LocalNameList mInstance;
    
    private class LocalName {
        public String  mWholeName;
        public String  mFirstName;
        public String  mLastName;
        
        public LocalName(String name) {
            mWholeName = name;
            mFirstName = null;
            mLastName  = null;
        }
        
        public void format() {
            if (null == mWholeName) return;

            //
            // trim, removing the leading and tailing spaces if any.
            //
            mWholeName = mWholeName.trim();
            if (null == mWholeName) return;
            
            //
            // split whole-name into first and last by the first space.
            //
            String[] ss = mWholeName.split(" ");
            assert(ss.length > 0);

            mFirstName = ss[0];
            if (ss.length > 1) {
                mLastName = mWholeName.substring(mWholeName.indexOf(" ") + 1);
            }

            //
            // let the initials of both names be upper-case.
            //
            if (WhooTools.isTextEnglish(mWholeName)) {
                StringBuilder sb = new StringBuilder(mFirstName.toLowerCase());
                sb.setCharAt(0, (char)(sb.charAt(0) + 'A' - 'a'));
                mFirstName = sb.toString();
                if (mLastName != null) {
                    sb = new StringBuilder(mLastName.toLowerCase());
                    sb.setCharAt(0, (char)(sb.charAt(0) + 'A' - 'a'));
                    mLastName = sb.toString();
                }
            }

            //
            // concatenate the names into one.
            //
            mWholeName = mFirstName;
            if (mLastName != null) {
                mWholeName += " " + mLastName;
            }
        }
        
        public boolean equals(LocalName that) {
            return (this.mWholeName.equals(that.mWholeName) &&
                    this.mFirstName.equals(that.mFirstName));
        }
    };
    
    private Vector<LocalName> mList;
    
    private LocalNameList() {
        mList = new Vector<LocalName>();
        mList.clear();
    }

    public static LocalNameList getInstance() {
        if (null == mInstance) {
            mInstance = new LocalNameList();
        }
        return mInstance;
    }

    public void setContext(Context context) {
        mContext = context;
    }
    
    public int getLocalNameCount() {
        return mList.size();
    }
    
    public String getLocalName(int location) {
        if (0 <= location && location < mList.size()) {
            LocalName name = mList.get(location);
            return name.mWholeName;
        }
        else {
            Log.e(TAG, "error, getLocalName(" + location + ") out of range!");
        }
        return null;
    }

    public String[] getLocalNames() {
        String rets[] = new String[mList.size()];
        for (int ii = 0; ii < rets.length; ii++) {
            LocalName name = mList.get(ii);
            rets[ii] = name.mWholeName;
        }
        return rets;
    }
    
    public int findExistNameLocation(String inputName) {
        LocalName name = new LocalName(inputName);
        name.format();
        for (int ii = 0; ii < mList.size(); ii++) {
            LocalName name_ii = mList.get(ii);
            if (name_ii.equals(name)) {
                return ii;
            }
        }
        return -1;
    }
    
    public void inputLocalName(String inputName) {
        LocalName name = new LocalName(inputName);
        name.format();
        for (int ii = 0; ii < mList.size(); ii++) {
            LocalName name_ii = mList.get(ii);
            if (name_ii.equals(name)) {
                return;
            }
        }
        //
        // remove the tail node and limit the vector size.
        //
        if (mList.size() >= MAXCOUNT_LIMIT) {
            mList.remove(mList.size()-1);
        }
        //
        // the newest input node will be at head position.
        //
        mList.insertElementAt(name, 0);
    }
    
    public void selectLocalName(int location) {
        if (0 <= location && location < mList.size()) {
            //
            // move this node to the head position.
            //
            LocalName name = mList.remove(location);
            mList.insertElementAt(name, 0);
        }
        else {
            Log.e(TAG, "error, selectLocalName(" + location + ") out of range!");
        }
    }
    
    public void update() {
        // TODO: nothing-to-do.
    }
    
    public void store() {
        try {
            FileOutputStream fos = null;

            if (WhooConfig.USING_EXTERNAL_STORAGE) {
                File dir = Environment.getExternalStorageDirectory();
                if (!dir.exists()) {
                    WhooConfig.DBG("Error: SD card path " + dir.getAbsolutePath() + " does not exist!");
                    return;
                }
                else {
                    dir = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/" + WhooConfig.ROOT_DIR());
                    if (!dir.exists()) {
                        dir.mkdirs();
                        dir.setReadable(true, true);
                        dir.setWritable(true, true);

                        Log.d(TAG, "d: created new directory by mkdir(): " + dir.getAbsolutePath());
                    }

                    File file = new File(dir.getAbsolutePath() + "/" + PRIVATE_FILE);
                    
                    fos = new FileOutputStream(file);
                }
            }
            else {
                fos = mContext.openFileOutput(PRIVATE_FILE, mContext.MODE_PRIVATE);
            }
            
            PrintWriter pw = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(fos)), true);
            for (int ii = 0; ii < mList.size(); ii++) {
                LocalName name = mList.get(ii);
                pw.println(name.mWholeName);
            }
            pw.flush();
            pw.close();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "error, file " + PRIVATE_FILE + " cannot be open!" );
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "error, file " + PRIVATE_FILE + " write failed!" );
            e.printStackTrace();
        }
    }
    
    public void load() {
        mList.clear();
        try {
            FileInputStream fis = null;

            if (WhooConfig.USING_EXTERNAL_STORAGE) {
                File dir = Environment.getExternalStorageDirectory();
                Log.d(TAG, "d: Environment.getExternalStorageDirectory()=" + dir.getAbsolutePath());
                
                if (!dir.exists()) {
                    WhooConfig.DBG("Error: SD card path " + dir.getAbsolutePath() + " does not exist!");
                    return;
                }
                else {
                    dir = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/" + WhooConfig.ROOT_DIR());
                    if (!dir.exists()) {
                        dir.mkdirs();
                        dir.setReadable(true, true);
                        dir.setWritable(true, true);
                        
                        Log.d(TAG, "d: created new directory by mkdir(): " + dir.getAbsolutePath());
                    }

                    File file = new File(dir.getAbsolutePath() + "/" + PRIVATE_FILE);

                    fis = new FileInputStream(file);
                }
            } else {
                fis = mContext.openFileInput(PRIVATE_FILE);
            }
            
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String nameStr;
            while ((nameStr = br.readLine()) != null) {
                LocalName name = new LocalName(nameStr);
                name.format();
                if (!mList.add(name)) {
                    Log.d(TAG, "localNameList.load(): add name " + nameStr + " failed!");
                }
            }
            br.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "error, file " + PRIVATE_FILE + " cannot be open!" );
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "error, file " + PRIVATE_FILE + " read failed!" );
            e.printStackTrace();
        }
    }
}
