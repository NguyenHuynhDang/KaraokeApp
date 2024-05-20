#include <jni.h>
#include <string>
#include <android/log.h>
#include "Karaoke.h"
#define TAG "KARAOKE_NATIVE_RECORDING"

extern "C"
JNIEXPORT void JNICALL
Java_com_example_karaokeapp_RecorderService_Recorder(JNIEnv *env, jobject thiz, jint samplerate,
                                                     jint buffersize) {
    karaoke = new Karaoke((unsigned int)samplerate, (unsigned int)buffersize);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_karaokeapp_RecorderService_stopRecording(JNIEnv *env, jobject thiz) {
    if (karaoke)
    {
        karaoke->stopRecord();
        __android_log_print(ANDROID_LOG_DEBUG, "Karaoke", "Finished recording.");
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_karaokeapp_RecorderService_setEffectEnable(JNIEnv *env, jobject thiz,
                                                            jboolean value) {
    if (karaoke) karaoke->setEffectEnable(value);
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "set effect enable = %d", value);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_karaokeapp_RecorderService_setAutotuneEnable(JNIEnv *env, jobject thiz,
                                                              jboolean value) {
    if (karaoke) karaoke->setAutotuneEnable(value);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_karaokeapp_RecorderService_setEffectValue(JNIEnv *env, jobject thiz,
                                                           jint effect_type, jint value) {
    if (karaoke)
    {
        switch (effect_type) {
            case 1:
                karaoke->setEchoValue(value);
                __android_log_print(ANDROID_LOG_DEBUG, TAG, "set echo = %d", value);

                return;
            case 2:
                karaoke->setReverbValue(value);
                __android_log_print(ANDROID_LOG_DEBUG, TAG, "set reverb = %d", value);
                return;
        }
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_karaokeapp_RecorderService_setMicVolume(JNIEnv *env, jobject thiz, jfloat value) {
    if (karaoke) karaoke->setMicVolume(value);
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "setMicVolume = %f", value);
}
