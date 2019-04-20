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
    //将原图像转换为bitmap格式
    cvtColor(src ,bitmap, COLOR_YUV420sp2RGBA);

    //转换为灰度图像减少计算量
    cvtColor(src , gray, COLOR_YUV420sp2GRAY);
    //竖屏，需要对图像旋转90度，才能识别
    rotate(gray,gray,ROTATE_90_COUNTERCLOCKWISE);

    //检测目标
    std::vector<Rect> rects;
    cascade->detectMultiScale(gray,rects,1.3,5,0,Size(10,10),Size(0,0));
    //临时对象已经没用了，释放掉
    gray.release();

    //由于前置相机倒置，因此，如果是没有检测到目标，则垂直镜像后返回
    if(rects.empty()) {
        flip(bitmap,bitmap,1);
        return;
    }

    /*如果检测到目标，由于是用的旋转后的灰度图像去检测，灰度图像的坐标就会和bitmap的坐标有90度的差异
     * 无法直接画矩形，因此构造一个临时Mat对象，先把bitmap做90度翻转后，在翻转后的图像上做画，在这个
     * 包含检测结果的图像上，再次翻转90度并镜像后就可以得到需要的图像了
    */
    Mat temp;
    rotate(bitmap,temp,ROTATE_90_COUNTERCLOCKWISE);
    for(int i=0;i<rects.size();i++)
    {
        rectangle(temp,rects[i],Scalar(255.0,0),2,8,0);
    }
    rotate(temp,bitmap,ROTATE_90_COUNTERCLOCKWISE);
    temp.release();
    flip(bitmap,bitmap,0);

    return;
}