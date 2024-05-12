#include <jni.h>
#include <string>
#include <android/log.h>
#include "Recorder.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_example_karaokeapp_RecorderService_Recorder(JNIEnv *env, jobject thiz, jint samplerate,
                                                     jint buffersize, jint destinationfd) {
    recorder = new Recorder((unsigned int)samplerate, (unsigned int)buffersize, destinationfd);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_karaokeapp_RecorderService_ToggleRecorder(JNIEnv *env, jobject thiz) {
    if (recorder) recorder->toggleRecorder();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_karaokeapp_RecorderService_CleanUp(JNIEnv *env, jobject thiz) {
    if (recorder)
    {
        recorder->clean();
        delete recorder;
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_karaokeapp_RecorderService_StopRecording(JNIEnv *env, jobject thiz) {
    if (recorder)
    {
        recorder->stopRecord();
        __android_log_print(ANDROID_LOG_DEBUG, "Recorder", "Finished recording.");
    }
}