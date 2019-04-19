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
Java_com_sunmi_openglcamera_MyGLSurfaceView_faceDetect(JNIEnv *env, jobject instance,
                                                       jbyteArray data_, jint srcFrameWidth,
                                                       jint srcFrameHeight,jlong addr){

    // TODO
    Mat gray;
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    Mat image = *(Mat *)addr;

    ALOGE("face detect enter E\n");

    cvtColor(image,gray,COLOR_YUV420p2GRAY);
    std::vector<Rect> rects;
    cascade->detectMultiScale(gray,rects,1.1,5,0,Size(10,10),Size(300,300));
    if(rects.empty()) {
        env->ReleaseByteArrayElements(data_, data, 0);
        return ;
    }

    for(int i=0;i<rects.size();i++)
    {
        ALOGE("found face\n");
       // rectangle(image,rects[i],Scalar(255,0,0),2,8,0);
    }
    env->ReleaseByteArrayElements(data_, data, 0);

    return ;

}