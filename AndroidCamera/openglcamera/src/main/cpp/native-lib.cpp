#include <jni.h>
#include <android/log.h>
#include <jni.h>
#include<opencv2/opencv.hpp>
#include <android/bitmap.h>
#include<iostream>
#include <vector>
#include <unistd.h>
using namespace cv;
using namespace std;

#include <android/log.h>
#include <zconf.h>

#define ALOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"Jon",FORMAT,##__VA_ARGS__);

CascadeClassifier *cascade = nullptr;

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_sunmi_openglcamera_MyGLSurfaceView_initCascade(JNIEnv *env, jobject instance,
                                                        jstring path_) {
    const char *cascadePath = env->GetStringUTFChars(path_,0);
    ALOGE("Cascade Path : %s\n",cascadePath);
    cascade = new CascadeClassifier(cascadePath);
    if(!cascade)
    {
        ALOGE("CascadeClassifier Creat Failed\n");
        env->ReleaseStringUTFChars(path_, cascadePath);
        return false;
    }
    env->ReleaseStringUTFChars(path_, cascadePath);
    return true;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sunmi_openglcamera_MyGLSurfaceView_faceDetect(JNIEnv *env, jobject instance, jlong addr) {

    // TODO
    //取到Java端的Mat对象
    Mat obj = *(Mat *)addr;

    std::vector<Rect> rects;
    cascade->detectMultiScale(obj,rects,1.3,5,0,Size(10,10),Size(0,0));
    if(rects.empty()) return;
    for(int i=0;i<rects.size();i++)
    {
        ALOGE("found face\n");
        rectangle(obj,rects[i],Scalar(255,0,0,0),2,8,0);
    }
    return;
}