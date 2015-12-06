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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


/**
 * Created by Samuel on 10/13/2015.
 */
public class WFRDataFactory {

    private final String TAG = "whoo.dm";

	private HashMap<String, WFRPerson> mPersons;

	private Context mContext;

	private static WFRDataFactory mInstance = new WFRDataFactory();

	private final String FILE_PERSONS    = WhooConfig.PERSON_FILE();
	private final String DEFAULT_PERSONS = "default_persons.dat";

	private WFRDataFactory() {
        mPersons = new HashMap<String, WFRPerson>();
	}

	public static WFRDataFactory getInstance() {
		return mInstance;
	}

	public void setContext(Context context) {
		mContext = context;
	}

	public void load() {
		Log.d(TAG, "load():");
		
		try {
			File dir = null;
			FileInputStream fis = null;

            if (WhooConfig.USING_EXTERNAL_STORAGE) {
                dir = Environment.getExternalStorageDirectory();
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

                    File file = new File(dir.getAbsolutePath() + "/" + FILE_PERSONS);
                    if (!file.exists()) {
						Log.d(TAG, "d: .person not exist: " + dir.getAbsolutePath());
						Log.d(TAG, "d: should copy from res/raw.., but later on.");
						
                        //TODO: copy from default
                        //file.createNewFile();
                    }
                    fis = new FileInputStream(file);
                }
            } else {
                fis = mContext.openFileInput(FILE_PERSONS);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = br.readLine()) != null) {
                String words[] = line.split(":");
                if (words.length <= 0) {
                    Log.e(TAG, "load() error: unexpected line: " + line);
                    break;
                }

                int label = Integer.parseInt(words[0].trim());
                String name = words[1].trim();

                WFRPerson person = new WFRPerson(mContext, name, label);

				if (WhooConfig.USING_EXTERNAL_STORAGE) {
					dir = new File(Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/" + WhooConfig.ROOT_DIR() + 
							"/" + person.getDirName());
				} else {
                	dir = mContext.getFileStreamPath(name);
				}
				
                if (!dir.exists()) {
                    Log.e(TAG, "warning: non-exist person.dir " + name);
                    continue;
                }

                FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".jpg");
                    }
                };
                File[] imageFiles = dir.listFiles(filter);
				if (imageFiles.length <= 0) {
					Log.e(TAG, "warning: an empty directory (no-image) for " + name);
                    continue;
				}
				
                for (int ii = 0; ii < imageFiles.length; ii++) {
					String path = imageFiles[ii].getAbsolutePath();
					
                    boolean ret = person.addFaceImage(path);
					Log.d(TAG, "d: add_face_image(" + path + "): " +
                            (ret ? "success" : "failed"));
                }

                mPersons.put(name, person);
            }

			br.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "error, file " + FILE_PERSONS + " cannot be open!");
		} catch (IOException e) {
			Log.e(TAG, "error, file " + FILE_PERSONS + " read failed!" );
			e.printStackTrace();
		}

		checkAndReloadFromFS();

		Log.d(TAG, "load()!.");
	}

	//
	// this is a patch for load(), because we
	// find that person.txt might be clear to
	// be empty sometimes.
	//
	private void checkAndReloadFromFS() {
        File dir = null;

        if (WhooConfig.USING_EXTERNAL_STORAGE) {
            dir = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/" + WhooConfig.ROOT_DIR());
        } else {
            dir = mContext.getFilesDir();
        }

        if (null == dir || !dir.exists()) {
            return;
        }

        File files[] = dir.listFiles();
        int count = 0;
        for (int ii = 0; ii < files.length; ii++) {
            if (files[ii].isDirectory()) {
                count++;
            }
        }

        if (0 == count || (mPersons.size() > count/2)) {
            Log.d(TAG, "checked, dm good.");
            return;
        }

        Log.e(TAG, "checked and found exceptions. so, start to rebuild DM from FS:");
        WhooLog.e("checked and found exceptions. so, start to rebuild DM from FS:");

        mPersons.clear();

        for (int ii = 0; ii < files.length; ii++) {
            if (files[ii].isDirectory()) {
                String name = files[ii].getName();

                FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".jpg");
                    }
                };
                File[] imageFiles = files[ii].listFiles(filter);
                if (imageFiles.length <= 0) {
                    Log.e(TAG, "warning: an empty directory (no-image) for " + name);
                    continue;
                }

                WFRPerson person = new WFRPerson(mContext, name, (ii+1));

                for (int kk = 0; kk < imageFiles.length; kk++) {
                    String path = imageFiles[kk].getAbsolutePath();

                    boolean ret = person.addFaceImage(path);
                    Log.d(TAG, "d: add_face_image(" + path + "): " +
                            (ret ? "success" : "failed"));
                }

                mPersons.put(name, person);
            }
        }

        Log.e(TAG, "Rebuilding DM from FS finished, DM.size=" + mPersons.size());
        WhooLog.e("Rebuilding DM from FS finished, DM.size=" + mPersons.size());
	}

	public void flush() {
		Log.d(TAG, "flush():");
		
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

                    File file = new File(dir.getAbsolutePath() + "/" + FILE_PERSONS);
                    file.createNewFile();
					Log.d(TAG, "d: created new directory by file.createNewFile(): " + file.getAbsolutePath());
					
                    fos = new FileOutputStream(file);
                }
            }
            else {
                fos = mContext.openFileOutput(FILE_PERSONS, mContext.MODE_PRIVATE);
            }

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            Iterator iter = mPersons.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry)iter.next();
                WFRPerson person = (WFRPerson)entry.getValue();

                StringBuilder sb = new StringBuilder();
                sb.append(person.getLableID());
                sb.append(" : ");
                sb.append(person.getName() + "\n"); //similar as pw.println();

                bw.write(sb.toString());

				Log.d(TAG, "d-w: " + sb.toString());
            }

            bw.flush();
            bw.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "error, file " + FILE_PERSONS + " cannot be open!" );
		} catch (IOException e) {
			Log.e(TAG, "error, file " + FILE_PERSONS + " write failed!" );
			e.printStackTrace();
		}

		Log.d(TAG, "flush()!.");
	}

	public boolean restore() {
		Log.d(TAG, "restore(): not ready yet!");
		
		try {
            if (WhooConfig.USING_EXTERNAL_STORAGE) {
                //TODO:
            } else {
                File file = mContext.getFileStreamPath(DEFAULT_PERSONS);
                if (!file.exists()) {
                    Log.d(TAG, "default persons file does not exist!");
                    return false;
                }

                if (mContext.deleteFile(FILE_PERSONS)) {
                    Log.i(TAG, "yes, file " + FILE_PERSONS + " deleted.");
                } else {
                    Log.e(TAG, "error, file " + FILE_PERSONS + " cannot delete!");
                }
            }
		} catch (Exception e) {
			Log.e(TAG, "error, file " + FILE_PERSONS + " close failed!" );
			e.printStackTrace();
		}
		
		return false;
	}

	public int count() {
		return mPersons.size();
	}

	public int getPersonCount() {
		return mPersons.size();
	}

	public WFRPerson getPerson(int index) {
        int ii = 0;
        Iterator iter = mPersons.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            if (ii++ == index) {
                return (WFRPerson) entry.getValue();
            }
        }
		return null;
	}

	public WFRPerson getPersonByLabel(int label) {
        Iterator iter = mPersons.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry  = (Map.Entry)iter.next();
            WFRPerson person = (WFRPerson)entry.getValue();
			if (person.getLableID() == label) {
				return person;
			}
        }
		return null;
	}

	public WFRPerson addPerson(String name) {
		//
		// if existing, return it directly.
		//
		if (mPersons.containsKey(name)) {
			return mPersons.get(name);
		}

		//
		// calculate the lable_id by new=max+1
		//
		int label = 0;
		Iterator iter = mPersons.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            WFRPerson person = (WFRPerson)entry.getValue();

			if (person.getLableID() > label) {
				label = person.getLableID();
			}
        }
		label++;

		//
		// create new person
		//
		WFRPerson person = new WFRPerson(mContext, name, label);

		//
		// make directory if not existing.
		//
        //File dir = Environment.getExternalStoragePublicDirectory(
        //        Environment.DIRECTORY_PICTURES);
        File dir = null;
        if (WhooConfig.USING_EXTERNAL_STORAGE) {
            dir = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/" + WhooConfig.ROOT_DIR());
            if (!dir.exists()) {
				Log.d(TAG, "error, not exist path: " + dir.getAbsolutePath());
				dir = null;
            } else {
            	dir = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/" + WhooConfig.ROOT_DIR() + "/" + person.getDirName());
            }
        }
        else {
            dir = mContext.getFileStreamPath(name);
        }
		
        if (dir != null && !dir.exists()) {
			dir.mkdirs();
            dir.setReadable(true, true);
            dir.setWritable(true, true);
        }

        mPersons.put(name, person);
		
        return person;
	}

	public void deletePerson(WFRPerson person) {
		File dir = null;
        if (WhooConfig.USING_EXTERNAL_STORAGE) {
            dir = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/" + WhooConfig.ROOT_DIR());
            if (!dir.exists()) {
				Log.d(TAG, "error, not exist path: " + dir.getAbsolutePath());
				dir = null;
            } else {
            	dir = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/" + WhooConfig.ROOT_DIR() + "/" + person.getDirName());
            }
        }
        else {
            dir = mContext.getFileStreamPath(person.getName());
        }
		
        if (dir != null && !dir.exists()) {
			dir.delete();
        }
		
        mPersons.remove(person.getName());
	}

	public void deleteAll() {
        mPersons.clear();
	}

	public Vector<WFRFaceImage> getFaceImages() {
		Vector<WFRFaceImage> images = new Vector<WFRFaceImage>();

		Iterator iter = mPersons.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            WFRPerson person = (WFRPerson)entry.getValue();

			int count = person.getFaceImageCount();
			for (int ii = 0; ii < count; ii++) {
				images.add(person.getFaceImage(ii));
			}
        }

		return images;
	}
}
