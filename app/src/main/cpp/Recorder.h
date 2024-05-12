//
// Created by HuynhDang on 12/05/2024.
//

#ifndef KARAOKEAPP_RECORDER_H
#define KARAOKEAPP_RECORDER_H

#include <OpenSource/SuperpoweredAndroidAudioIO.h>
#include <SuperpoweredRecorder.h>

class Recorder
{
private:
    SuperpoweredAndroidAudioIO *audioIO;
    Superpowered::Recorder *recorder;
    bool isRecording = false;

public:
    Recorder(unsigned int samplerate, unsigned int bufferSize, int destinationfd);
    ~Recorder();
    bool process(short int* output, unsigned int numFrames, unsigned int samplerate);
    void toggleRecorder();
    void stopRecord();
    void clean();
};

static Recorder* recorder = nullptr;


#endif //KARAOKEAPP_RECORDER_H
