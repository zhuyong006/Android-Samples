#include <jni.h>
#include <string>
#include <fstream>
#include "leak_tracer/include/MemoryTrace.hpp"

#ifdef ANDROID

#include <android/log.h>

#define TAG "Jon"

#define ALOGE(fmt, ...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##__VA_ARGS__)
#define ALOGI(fmt, ...) __android_log_print(ANDROID_LOG_INFO, TAG, fmt, ##__VA_ARGS__)
#define ALOGD(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##__VA_ARGS__)
#define ALOGW(fmt, ...) __android_log_print(ANDROID_LOG_WARN, TAG, fmt, ##__VA_ARGS__)
#else
#define ALOGE printf
#define ALOGI printf
#define ALOGD printf
#define ALOGW printf
#endif
#ifdef __cplusplus
extern "C"{
#endif
    void mem_leak(void);
#ifdef __cplusplus
};
#endif

extern "C"
jstring
Java_com_sunmi_mmleak_MainActivity_NativeMmLeak(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    leaktracer::MemoryTrace::GetInstance().startMonitoringAllThreads();
    mem_leak();
    leaktracer::MemoryTrace::GetInstance().stopAllMonitoring();


    leaktracer::MemoryTrace::GetInstance().writeLeaksToFile("/data/leak.out");


    return env->NewStringUTF(hello.c_str());
}
