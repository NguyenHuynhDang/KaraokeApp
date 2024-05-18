//
// Created by HuynhDang on 12/05/2024.
//

#ifndef KARAOKEAPP_KARAOKE_H
#define KARAOKEAPP_KARAOKE_H

#include <OpenSource/SuperpoweredAndroidAudioIO.h>
#include <SuperpoweredEcho.h>
#include <SuperpoweredReverb.h>
#include <SuperpoweredAutomaticVocalPitchCorrection.h>
#include <memory>

class Karaoke
{
private:
    bool isEffectEnable = false;
    bool isAutotuneEnable = false;
    float micVolume = .0f;
    SuperpoweredAndroidAudioIO *audioIO;
    Superpowered::Echo *echo;
    Superpowered::Reverb *reverb;
    Superpowered::AutomaticVocalPitchCorrection *autotune;
public:
    Karaoke(unsigned int samplerate, unsigned int bufferSize);
    bool process(short int* output, unsigned int numFrames, unsigned int samplerate);
    void setEffectEnable(bool value);
    void setAutotuneEnable(bool value);
    void setEchoValue(int value);
    void setReverbValue(int value);
    void setMicVolume(float value);
    void stopRecord();
};

static std::unique_ptr<Karaoke> karaoke;

#endif //KARAOKEAPP_KARAOKE_H
