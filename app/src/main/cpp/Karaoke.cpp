//
// Created by HuynhDang on 12/05/2024.
//

#include <unistd.h>
#include <Superpowered.h>
#include <SuperpoweredSimple.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>
#include <math.h>
#include "Karaoke.h"

const float MIC_MAX_VOLUME = 0.005f;

bool audioProcessing(void* clientData, short int* audioIO, int numFrames, int sampleRate)
{
    return ((Karaoke*) clientData)->process(audioIO, (unsigned int)numFrames, (unsigned int)sampleRate);
}

Karaoke::Karaoke(unsigned int samplerate, unsigned int bufferSize)
{
    Superpowered::Initialize("ExampleLicenseKey-WillExpire-OnNextUpdate");
    isEffectEnable = true;

    audioIO = new SuperpoweredAndroidAudioIO (
            samplerate, // device sample rate
            bufferSize, // device buffer size
            true,   // enable input
            true,   // enable output
            audioProcessing,    // audio callback function
            this,   // client data
            SL_ANDROID_STREAM_SYSTEM,   // input stream type
            SL_ANDROID_RECORDING_PRESET_VOICE_RECOGNITION
            ); // output stream type

    echo = new Superpowered::Echo((unsigned int)samplerate);
    echo->setMix(0.0f);
    echo->enabled = isEffectEnable;

    reverb = new Superpowered::Reverb((unsigned int)samplerate);
    reverb->mix = 0.0f;
    reverb->enabled = isEffectEnable;
}

Karaoke::~Karaoke()
{
    delete karaoke;
}

bool Karaoke::process(short int *audio, unsigned int numFrames, unsigned int sampleRate)
{
    float floatBuffer[numFrames * 2];
    Superpowered::ShortIntToFloat(audio, floatBuffer, (unsigned int)numFrames);
    //Superpowered::ChangeVolumeAdd(floatBuffer, floatBuffer, 1.0f, micVolume, (unsigned int)numFrames);

    if (isEffectEnable) {
        echo->process(floatBuffer, floatBuffer, (unsigned int)numFrames);
        reverb->process(floatBuffer, floatBuffer, (unsigned int)numFrames);
    }

    Superpowered::FloatToShortInt(floatBuffer, audio, (unsigned int)numFrames);
    return true;
}

void Karaoke::setEffectEnable(bool value)
{
    isEffectEnable = value;
    echo->enabled = reverb->enabled = value;
}

void Karaoke::setEffectValue(int effectType, int value)
{
    switch (effectType)
    {
        case 1:
            echo->setMix(value / 100.0f);
            return;
        case 2:
            reverb->mix = value / 100.0f;
            return;
    }
}

void Karaoke::setMicVolume(float value)
{
    micVolume = sinf(value) * MIC_MAX_VOLUME;

}

void Karaoke::stopRecord()
{
    delete audioIO;
    delete echo;
    delete reverb;
}