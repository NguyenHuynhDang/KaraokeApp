#include <jni.h>
#include <string>
#include <android/log.h>
#include "Karaoke.h"

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
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_karaokeapp_RecorderService_setEffectValue(JNIEnv *env, jobject thiz,
                                                           jint effect_type, jint value) {
    if (karaoke) karaoke->setEffectValue(effect_type, value);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_karaokeapp_RecorderService_setMicVolume(JNIEnv *env, jobject thiz, jfloat value) {
    if (karaoke) karaoke->setMicVolume(value);
}