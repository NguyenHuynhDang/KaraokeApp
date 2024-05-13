package com.example.karaokeapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

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
    public native void setEffectValue(int effectType, int value);
    public native void setMicVolume(float value);
    public native void stopRecording();
}
