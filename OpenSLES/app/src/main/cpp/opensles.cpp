#include <jni.h>
#include <string>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <android/log.h>
#define LOGD(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"ywl5320",FORMAT,##__VA_ARGS__);

//1 创建引擎
static SLObjectItf  engineSL = NULL;
static SLPlayItf iplayer = NULL;

SLEngineItf CreateSL()
{
    SLresult re;
    SLEngineItf en;
    re = slCreateEngine(&engineSL,0,0,0,0,0);
    if(re != SL_RESULT_SUCCESS) return NULL;
    re = (*engineSL)->Realize(engineSL,SL_BOOLEAN_FALSE);
    if(re != SL_RESULT_SUCCESS) return NULL;
    re = (*engineSL)->GetInterface(engineSL,SL_IID_ENGINE,&en);
    if(re != SL_RESULT_SUCCESS) return NULL;
    return en;
}

void PcmCall(SLAndroidSimpleBufferQueueItf bf,void *contex)
{
    LOGD("PcmCall");
    static FILE *fp = NULL;
    static char *buf = NULL;
    if(!buf)
    {
        buf = new char[1024*1024];
    }
    if(!fp)
    {
        fp = fopen("/data/xusong-shzj2.wav","rb");
    }
    if(!fp)return;
    if(feof(fp) == 0)
    {
        int len = fread(buf,1,1024,fp);
        if(len > 0)
            (*bf)->Enqueue(bf,buf,len);
    }



}


extern "C"
JNIEXPORT void JNICALL
Java_com_sunmi_opensles_OpenSLES_OpenSlesInit(JNIEnv *env, jobject instance) {

    // TODO
    // TODO
    //1 创建引擎
    SLEngineItf eng = CreateSL();
    if(eng){
        LOGD("CreateSL success！ ");
    }else{
        LOGD("CreateSL failed！ ");
    }

    //2 创建混音器
    SLObjectItf mix = NULL;
    SLresult re = 0;
    re = (*eng)->CreateOutputMix(eng,&mix,0,0,0);
    if(re !=SL_RESULT_SUCCESS )
    {
        LOGD("SL_RESULT_SUCCESS failed!");
    }
    re = (*mix)->Realize(mix,SL_BOOLEAN_FALSE);
    if(re !=SL_RESULT_SUCCESS )
    {
        LOGD("(*mix)->Realize failed!");
    }
    SLDataLocator_OutputMix outmix = {SL_DATALOCATOR_OUTPUTMIX,mix};
    SLDataSink audioSink= {&outmix,0};

    //3 配置音频信息
    //缓冲队列
    SLDataLocator_AndroidSimpleBufferQueue que = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,10};
    //音频格式
    SLDataFormat_PCM pcm = {
            SL_DATAFORMAT_PCM,
            2,//    声道数
            SL_SAMPLINGRATE_44_1,
            SL_PCMSAMPLEFORMAT_FIXED_16,
            SL_PCMSAMPLEFORMAT_FIXED_16,
            SL_SPEAKER_FRONT_LEFT|SL_SPEAKER_FRONT_RIGHT,
            SL_BYTEORDER_LITTLEENDIAN //字节序，小端
    };
    SLDataSource ds = {&que,&pcm};


    //4 创建播放器
    SLObjectItf player = NULL;
    SLAndroidSimpleBufferQueueItf pcmQue = NULL;
    const SLInterfaceID ids[] = {SL_IID_BUFFERQUEUE};
    const SLboolean req[] = {SL_BOOLEAN_TRUE};
    re = (*eng)->CreateAudioPlayer(eng,&player,&ds,&audioSink,sizeof(ids)/sizeof(SLInterfaceID),ids,req);
    if(re !=SL_RESULT_SUCCESS )
    {
        LOGD("CreateAudioPlayer failed!");
    } else{
        LOGD("CreateAudioPlayer success!");
    }
    (*player)->Realize(player,SL_BOOLEAN_FALSE);
    //获取player接口
    re = (*player)->GetInterface(player,SL_IID_PLAY,&iplayer);
    if(re !=SL_RESULT_SUCCESS )
    {
        LOGD("GetInterface SL_IID_PLAY failed!");
    }
    re = (*player)->GetInterface(player,SL_IID_BUFFERQUEUE,&pcmQue);
    if(re !=SL_RESULT_SUCCESS )
    {
        LOGD("GetInterface SL_IID_BUFFERQUEUE failed!");
    }

    //设置回调函数，播放队列空调用
    (*pcmQue)->RegisterCallback(pcmQue,PcmCall,0);

    //设置为播放状态
    (*iplayer)->SetPlayState(iplayer,SL_PLAYSTATE_STOPPED);

    //启动队列回调
    (*pcmQue)->Enqueue(pcmQue,"",1);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sunmi_opensles_OpenSLES_PlaySound(JNIEnv *env, jobject instance) {

    (*iplayer)->SetPlayState(iplayer,SL_PLAYSTATE_PLAYING);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sunmi_opensles_OpenSLES_PauseSound(JNIEnv *env, jobject instance) {

    // TODO
    (*iplayer)->SetPlayState(iplayer,SL_PLAYSTATE_PAUSED);
}
