package com.example.karaokeapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textview.MaterialTextView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.FileNotFoundException;

public class PlayerActivity extends AppCompatActivity {
    private Boolean isRecording = false;
    private ActivityResultLauncher<Intent> fileBrowserLauncher = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFileBrowserLauncher();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String videoId = getIntent().getStringExtra("videoId");
        String videoTitle = getIntent().getStringExtra("videoTitle");

        MaterialTextView title = findViewById(R.id.tv_video_title);
        title.setText(videoTitle);

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }
        });

        ImageButton microphoneBtn = findViewById(R.id.microphoneBtn);
        microphoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = !isRecording;
                startService("toggle");
                if (isRecording)
                    microphoneBtn.setImageResource(R.drawable.baseline_mic_24);
                else
                    microphoneBtn.setImageResource(R.drawable.baseline_mic_off_24);
            }
        });

        startRecord();
    }

    private void setFileBrowserLauncher()
    {
        fileBrowserLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // Handle the return of the save as dialog.
            if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                Intent resultData = result.getData();
                if (resultData != null) {
                    Uri u = resultData.getData();
                    try {
                        ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(u, "w");
                        if (pfd != null) {
                            Intent serviceIntent = new Intent(this, RecorderService.class);
                            serviceIntent.putExtra("fileDescriptor", pfd.detachFd());
                            ContextCompat.startForegroundService(this, serviceIntent);
                            isRecording = true;
                        } else Log.d("Recorder", "File descriptor is null.");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void startRecord()
    {
        // Open the file browser to pick a destination.
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_TITLE, "recording.wav");
        fileBrowserLauncher.launch(intent);
    }

    private void startService(String action)
    {
        Intent serviceIntent = new Intent(this, RecorderService.class);
        serviceIntent.setAction(action);
        startService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService("stop");
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player);
        youTubePlayerView.release();
    }
}