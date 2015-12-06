//
// facerec.cpp, this goes under src/main/native
//
// https://gist.githubusercontent.com/berak/9830239/raw/
// 4d0f2f3939d766b1af4f5405c8d30839db312291/gistfile1.txt
//

#include "facerec.h"
#include "opencv2/contrib/contrib.hpp"

using namespace cv;

JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_EigenFaceRecognizer_createEigenFaceRecognizer0(JNIEnv* env, jclass) {
    try {
        Ptr<FaceRecognizer> model = createEigenFaceRecognizer(0, DBL_MAX);
		model.addref();
        return (jlong)model.obj;
    } catch (...) {
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "sorry, failed to create eigen face recognizer(0)!");
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_EigenFaceRecognizer_createEigenFaceRecognizer
				(JNIEnv* env, jclass, jint num_components, jdouble threshold) {
	try {
        Ptr<FaceRecognizer> model = createEigenFaceRecognizer((int)num_components, (double)threshold);
		model.addref();
        return (jlong)model.obj;
    } catch (...) {
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "sorry, failed to create eigen face recognizer!");
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_FisherFaceRecognizer_createFisherFaceRecognizer0(JNIEnv* env, jclass) {
    try {
        Ptr<FaceRecognizer> model = createFisherFaceRecognizer(0, DBL_MAX);
        model.addref();
        return (jlong)model.obj;
    } catch (...) {
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "sorry, failed to create fisher face recognizer(0)!");
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_FisherFaceRecognizer_createFisherFaceRecognizer
				(JNIEnv* env, jclass, jint num_components, jdouble threshold) {
	try {
        Ptr<FaceRecognizer> model = createFisherFaceRecognizer((int)num_components, (double)threshold);
        model.addref();
        return (jlong)model.obj;
    } catch (...) {
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "sorry, failed to create fisher face recognizer!");
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_LBPHFaceRecognizer_createLBPHFaceRecognizer0(JNIEnv* env, jclass) {
    try {
        Ptr<FaceRecognizer> model = createLBPHFaceRecognizer(1, 8, 8, 8, DBL_MAX);
        model.addref();
        return (jlong)model.obj;
    } catch (...) {
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "sorry, failed to create LBPH face recognizer(0)!");
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_LBPHFaceRecognizer_createLBPHFaceRecognizer1(JNIEnv* env, jclass, jint radius) {
    try {
        Ptr<FaceRecognizer> model = createLBPHFaceRecognizer((int)radius, 8, 8, 8, DBL_MAX);
        model.addref();
        return (jlong)model.obj;
    } catch (...) {
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "sorry, failed to create LBPH face recognizer(1)!");
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_LBPHFaceRecognizer_createLBPHFaceRecognizer2(JNIEnv* env, jclass, jint radius, jint neighbours) {
    try {
        Ptr<FaceRecognizer> model = createLBPHFaceRecognizer((int)radius,(int)neighbours, 8, 8, 200/*DBL_MAX*/);
        model.addref();
        return (jlong)model.obj;
    } catch (...) {
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "sorry, failed to create LBPH face recognizer(2)!");
    } 
    return 0;
}

JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_LBPHFaceRecognizer_createLBPHFaceRecognizer
				(JNIEnv* env, jclass, jint radius, jint neighbours, jint grid_x, jint grid_y, jdouble threshold) {
	try {
        Ptr<FaceRecognizer> model = createLBPHFaceRecognizer((int)radius,(int)neighbours, (int)grid_x, (int)grid_y, (double)threshold);
        model.addref();
        return (jlong)model.obj;
    } catch (...) {
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "sorry, failed to create LBPH face recognizer!");
    } 
    return 0;
}