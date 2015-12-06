LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# OpenCV
OPENCV_CAMERA_MODULES:=off
OPENCV_INSTALL_MODULES:=off
include ../../../../opencv/sdk/native/jni/OpenCV.mk

LOCAL_C_INCLUDES += ../../../../opencv/sdk/native/jni/include

LOCAL_MODULE    := facerec
LOCAL_SRC_FILES := facerec.cpp
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)