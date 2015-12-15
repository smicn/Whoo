# Whoo
A face recognition application based on OpenCV and Android

## Brief
  The Android application Whoo is the part of the author's thesis, MS of
computer science in 2015. The main purpose is easy and straightforward:
to develop an Android application based on OpenCV so that it has the
features of face detection and face recognition. OpenCV has supported
three face recognition algorithms and this software does not develop new
algorithms. However, it really did some careful design and optimizations
to make the face recognition easy and friendly to use. Just take pictures
to your friends and yourself, and hope you have fun from it.

## Compilation
* Make sure that Android Studio, Android SDK and NDK are installed on your PC.
* Download this project to your PC; You do not need to install OpenCV SDK or 
runtime by yourself because this project already contains the most ensential
parts of OpenCV-android-sdk.
* You must modify the file local.properties and set the correct directories for
Android SDK and NDK:
  * sdk.dir=C\:\\Users\\Samuel\\AppData\\Local\\Android\\sdk<br>
  * ndk.dir=C\:\\Users\\Samuel\\AppData\\Local\\Android\\android-ndk-r10e
* Now, try to build the project by yourself using Android Studio.

## Execution and Test
* Before installing the Whoo.apk to your Android device, you need to download 
and install "OpenCV Manager" from Google-play store.
* Launch the application Whoo, take pictures to your friends and yourself, tell
the software their names (by typing for the first time and later selecting from
the name list), and hope you find this software works.

## The least required environments to compile and run this project
* Android SDK 4.4.2 (API 19)
* Samsung Galaxy S4 (Display Resolution: 1920x1080 pixels, Android 4.4.2 (Kitkat))<br>
  Well, the too low resolution is not to cause compatibility issues but totally not
working of face recognition. Sorry for that, because I have not got enough time
to handle this during my thesis. Hopefully, most of the Android phones have at least
1920x1080 pixels these days.

## The snapshots of Whoo GUIs
![](https://raw.githubusercontent.com/smicn/Whoo/master/docs/pics/whoo.png)

## Documents and References
Please refer to the dirctory ./docs for more information, where I put my thesis 
defense slides and the abstract of my thesis paper.<br>
Also, please feel free to write to me: smicn@foxmail.com, if you are interested in this topic.
