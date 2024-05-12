//
// Created by HuynhDang on 12/05/2024.
//

#include <unistd.h>
#include <Superpowered.h>
#include <SuperpoweredSimple.h>
#include "Recorder.h"

bool audioProcessing(void* clientData, short int* audioIO, int numFrames, int sampleRate)
{
    Recorder* k = (Recorder*) clientData;
    return k->process(audioIO, (unsigned int)numFrames, (unsigned int)sampleRate);
}

Recorder::Recorder(unsigned int samplerate, unsigned int bufferSize, int destinationfd)
{
    Superpowered::Initialize("ExampleLicenseKey-WillExpire-OnNextUpdate");

    isRecording = true;
    recorder = new Superpowered::Recorder(NULL);
    recorder->preparefd(destinationfd, 0, samplerate, true, 2);

    audioIO = new SuperpoweredAndroidAudioIO(samplerate, bufferSize, true, true, audioProcessing,
                                             this);

}

Recorder::~Recorder()
{
    delete audioIO; // always delete first
    delete recorder;
}

bool Recorder::process(short int *audio, unsigned int numFrames, unsigned int sampleRate)
{
    //if (!isRecording) return false;

    float outputBuffer[numFrames * 2];
    Superpowered::ShortIntToFloat(audio, outputBuffer, (unsigned int)numFrames);
    recorder->recordInterleaved(outputBuffer, (unsigned int)numFrames);

    return true;
}

void Recorder::toggleRecorder()
{
    isRecording = !isRecording;
}

void Recorder::clean()
{
    delete audioIO;
    delete recorder;
}

void Recorder::stopRecord()
{
    recorder->stop();
    while (!recorder->isFinished()) usleep(100000); // 0.1 second
}