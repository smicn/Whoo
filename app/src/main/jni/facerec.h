#include <jni.h>

#ifndef WHOO_FACEREC_H
#define WHOO_FACEREC_H

#ifdef __cplusplus
extern "C" {
#endif

//
// these three interfaces are from contrib.hpp
//
//CV_EXPORTS_W Ptr<FaceRecognizer> createEigenFaceRecognizer(int num_components = 0, double threshold = DBL_MAX);
//CV_EXPORTS_W Ptr<FaceRecognizer> createFisherFaceRecognizer(int num_components = 0, double threshold = DBL_MAX);
//CV_EXPORTS_W Ptr<FaceRecognizer> createLBPHFaceRecognizer(int radius=1, int neighbors=8,
//                                                        int grid_x=8, int grid_y=8, double threshold = DBL_MAX);

/* 
 * Class:     com_lamar_cs_whoo_EigenFaceRecognizer
 * Method:    createEigenFaceRecognizer0
 * Signature: (J)L 
 */
JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_EigenFaceRecognizer_createEigenFaceRecognizer0(JNIEnv* env, jclass);

/* 
 * Class:     com_lamar_cs_whoo_EigenFaceRecognizer
 * Method:    createEigenFaceRecognizer
 * Signature: (JID)L 
 */
JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_EigenFaceRecognizer_createEigenFaceRecognizer
				(JNIEnv* env, jclass, jint num_components, jdouble threshold);

/* 
 * Class:     com_lamar_cs_whoo_FisherFaceRecognizer
 * Method:    createFisherFaceRecognizer0
 * Signature: (J)L 
 */
JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_FisherFaceRecognizer_createFisherFaceRecognizer0(JNIEnv* env, jclass);

/* 
 * Class:     com_lamar_cs_whoo_FisherFaceRecognizer
 * Method:    createFisherFaceRecognizer
 * Signature: (JID)L 
 */
JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_FisherFaceRecognizer_createFisherFaceRecognizer
				(JNIEnv* env, jclass, jint num_components, jdouble threshold);

/* 
 * Class:     com_lamar_cs_whoo_LBPHFaceRecognizer
 * Method:    createLBPHFaceRecognizer0
 * Signature: (J)L 
 */
JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_LBPHFaceRecognizer_createLBPHFaceRecognizer0(JNIEnv* env, jclass);

/* 
 * Class:     com_lamar_cs_whoo_LBPHFaceRecognizer
 * Method:    createLBPHFaceRecognizer1
 * Signature: (JI)L 
 */
JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_LBPHFaceRecognizer_createLBPHFaceRecognizer1(JNIEnv* env, jclass, jint radius);

/* 
 * Class:     com_lamar_cs_whoo_LBPHFaceRecognizer
 * Method:    createLBPHFaceRecognizer2
 * Signature: (JII)L 
 */
JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_LBPHFaceRecognizer_createLBPHFaceRecognizer2(JNIEnv* env, jclass, jint radius, jint neighbours);

/* 
 * Class:     com_lamar_cs_whoo_LBPHFaceRecognizer
 * Method:    createLBPHFaceRecognizer
 * Signature: (JIIIID)L 
 */
JNIEXPORT jlong JNICALL Java_com_lamar_cs_whoo_LBPHFaceRecognizer_createLBPHFaceRecognizer
				(JNIEnv* env, jclass, jint radius, jint neighbours, jint grid_x, jint grid_y, jdouble threshold);

#ifdef __cplusplus
}
#endif

#endif