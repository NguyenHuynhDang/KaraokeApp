//
// Created by HuynhDang on 12/05/2024.
//

#ifndef KARAOKEAPP_KARAOKE_H
#define KARAOKEAPP_KARAOKE_H

#include <OpenSource/SuperpoweredAndroidAudioIO.h>
#include <SuperpoweredEcho.h>
#include <SuperpoweredReverb.h>

class Karaoke
{
private:
    bool isEffectEnable = false;
    float micVolume = .0f;
    SuperpoweredAndroidAudioIO *audioIO;
    Superpowered::Echo *echo;
    Superpowered::Reverb *reverb;
public:
    Karaoke(unsigned int samplerate, unsigned int bufferSize);
    bool process(short int* output, unsigned int numFrames, unsigned int samplerate);
    void setEffectEnable(bool value);
    void setEchoValue(int value);
    void setReverbValue(int value);
    void setMicVolume(float value);
    void stopRecord();
};

static Karaoke* karaoke = nullptr;


#endif //KARAOKEAPP_KARAOKE_H
