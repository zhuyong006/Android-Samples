#include <jni.h>
#include <android/log.h>
#define ALOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"Jon",FORMAT,##__VA_ARGS__);

extern "C"
JNIEXPORT jstring JNICALL
Java_com_sunmi_openglcamera_MyGLSurfaceView_stringFromC(JNIEnv *env, jobject instance){
    ALOGE("Java_sunmi_opencv_camera_MainActivity_stringFromC");
    return env->NewStringUTF("hello NDK From C");
}
