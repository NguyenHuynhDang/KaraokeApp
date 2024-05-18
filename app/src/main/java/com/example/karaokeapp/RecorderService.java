package com.example.karaokeapp;

public class RecorderService {
    private int samplerate, buffersize;

    public RecorderService(int samplerate, int buffersize)
    {
        this.samplerate = samplerate;
        this.buffersize = buffersize;
    }

    public void startRecording()
    {
        Recorder(samplerate, buffersize);
    }

    public native void Recorder(int samplerate, int buffersize);
    public native void setEffectEnable(boolean value);
    public native void setAutotuneEnable(boolean value);
    public native void setEffectValue(int effectType, int value);
    public native void setMicVolume(float value);
    public native void stopRecording();
}
