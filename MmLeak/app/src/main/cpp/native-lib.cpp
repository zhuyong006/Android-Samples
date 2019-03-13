#include <jni.h>
#include <string>
#include "leak_tracer/include/MemoryTrace.hpp"
#include <fstream>

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


char *mm = NULL;
extern "C"
jstring
Java_com_sunmi_mmleak_MainActivity_NativeMmLeak(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    leaktracer::MemoryTrace::GetInstance().startMonitoringAllThreads();
    mm = (char *)malloc(4096);
    memset(mm,0x0,4096);
    leaktracer::MemoryTrace::GetInstance().stopAllMonitoring();

    std::ofstream out;
    out.open("/data/leak.out", std::ios_base::out);
    if (out.is_open()) {
        leaktracer::MemoryTrace::GetInstance().writeLeaks(out);
    } else {
        ALOGE("Failed to write to \"leaks.out\"\n");
    }

    return env->NewStringUTF(hello.c_str());
}
