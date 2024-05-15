package com.example.karaokeapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textview.MaterialTextView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class PlayerActivity extends AppCompatActivity {
    boolean hasAllPermissions = false;
    private Boolean isRecording = false;
    private RecorderService recorderService;

    private Switch micSwitch, effectSwitch;
    private View micView, effectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        // Checking permissions.
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
        };
        for (String s : permissions) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                // Some permissions are not granted, ask the user.
                ActivityCompat.requestPermissions(this, permissions, 0);
                return;
            }
        }
        // Got all permissions
        hasAllPermissions = true;

        setupView();
        setYoutubePlayer();
        setRecorderService();
        setMicSwitchClick();
        setEffectSwitchClick();

        if (hasAllPermissions)
        {
            isRecording = true;
            micSwitch.setChecked(true);
            micView.setVisibility(View.VISIBLE);
            effectSwitch.setVisibility(View.VISIBLE);
            recorderService.startRecording();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Called when the user answers to the permission dialogs.
        if ((requestCode != 0) || (grantResults.length < 1) || (grantResults.length != permissions.length)) return;
        hasAllPermissions = true;

        for (int grantResult:grantResults) if (grantResult != PackageManager.PERMISSION_GRANTED) {
            hasAllPermissions = false;
            Toast.makeText(getApplicationContext(), "Please allow all permissions for the app.", Toast.LENGTH_LONG).show();
        }
    }

    private void setYoutubePlayer()
    {
        String videoId = getIntent().getStringExtra("videoId");
        String videoTitle = getIntent().getStringExtra("videoTitle");

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }
        });

        MaterialTextView title = findViewById(R.id.tv_video_title);
        title.setText(videoTitle);
    }

    private void setupView()
    {
        micSwitch = findViewById(R.id.sw_mic);
        effectSwitch = findViewById(R.id.sw_effect);
        micView = findViewById(R.id.view_mic);
        effectView = findViewById(R.id.view_effect);
    }

    private void setMicSwitchClick()
    {
        micSwitch.setOnClickListener(v -> {
            isRecording = !isRecording;
            if (isRecording)
            {
                micView.setVisibility(View.VISIBLE);
                effectSwitch.setVisibility(View.VISIBLE);
                recorderService.startRecording();
            }
            else
            {
                micView.setVisibility(View.GONE);
                effectSwitch.setVisibility(View.GONE);
                recorderService.stopRecording();
            }
        });
    }

    private void setEffectSwitchClick()
    {
        effectSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (effectSwitch.isChecked())
                {
                    effectView.setVisibility(View.VISIBLE);
                }
                else
                {
                    effectView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setRecorderService()
    {
        // Get the device's sample rate and buffer size to enable
        // low-latency Android audio output, if available.
        String samplerateString = null, buffersizeString = null;
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            samplerateString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            buffersizeString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        }
        if (samplerateString == null) samplerateString = "48000";
        if (buffersizeString == null) buffersizeString = "480";
        int samplerate = Integer.parseInt(samplerateString);
        int buffersize = Integer.parseInt(buffersizeString);

        recorderService = new RecorderService(samplerate, buffersize);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recorderService.stopRecording();
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player);
        youTubePlayerView.release();
    }
}