#include <jni.h>
#include <android/log.h>
#include <jni.h>
#include<opencv2/opencv.hpp>
#include <android/bitmap.h>
#include<iostream>
#include <vector>
using namespace cv;
using namespace std;

#include <android/log.h>
#define ALOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"Jon",FORMAT,##__VA_ARGS__);

CascadeClassifier *cascade = nullptr;

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_sunmi_openglcamera_MyGLSurfaceView_initCascade(JNIEnv *env, jobject instance,
                                                        jstring path_) {
    ALOGE("Java_sunmi_opencv_camera_MainActivity_stringFromC");
    const char *cascadePath = env->GetStringUTFChars(path_,0);

    //cascade = new CascadeClassifier(cascadePath);
    cascade = new CascadeClassifier("/data/haarcascade_eye_tree_eyeglasses.xml");
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
    Mat gray;
    ALOGE("face detect enter E\n");

    cvtColor(obj,gray,COLOR_YUV420sp2GRAY);
    std::vector<Rect> rects;
    cascade->detectMultiScale(gray,rects,2,5,0,Size(10,10),Size(300,300));
    if(rects.empty()) return;
    for(int i=0;i<rects.size();i++)
    {
        ALOGE("found face\n");
        rectangle(obj,rects[i],Scalar(255,0,0),2,8,0);
    }
    return;
}