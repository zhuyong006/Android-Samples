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
Java_com_sunmi_openglcamera_MyGLSurfaceView_faceDetect(JNIEnv *env, jobject instance,
                                                       jlong src_addr, jlong dst_addr){

    // TODO
    //取到Java端的Mat对象
    Mat src =   *(Mat *)src_addr;
    Mat bitmap = *(Mat *)dst_addr;
    Mat gray;
    ALOGE("Face Detect");
    //转换为灰度图像减少计算量
    cvtColor(src , gray, COLOR_YUV420sp2GRAY);
    //竖屏，需要对图像旋转90度，才能识别
    rotate(gray,gray,ROTATE_90_COUNTERCLOCKWISE);

    std::vector<Rect> rects;
    cascade->detectMultiScale(gray,rects,1.3,5,0,Size(10,10),Size(0,0));
    if(rects.empty()) goto End;
    for(int i=0;i<rects.size();i++)
    {
        ALOGE("found face\n");
        rectangle(gray,rects[i],Scalar(255,0,0,0),2,8,0);
    }
End:
    rotate(gray,gray,ROTATE_90_COUNTERCLOCKWISE);
    //转换为bitmap的格式
    cvtColor(gray , bitmap, COLOR_GRAY2RGBA);
    flip(bitmap,bitmap,0);
    ALOGE("ndk : width = %d,height = %d", bitmap.rows,bitmap.cols);

    gray.release();
    return;
}