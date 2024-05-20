package com.example.karaokeapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class PlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnTouchListener {
    boolean hasAllPermissions = false;
    boolean isHeadphoneConnected = false;
    boolean isFullScreen = false;
    private RecorderService recorderService;

    private FloatingButton settingBtn = new FloatingButton();
    private ImageButton closeSettingBtn;
    private YouTubePlayer youtubePlayer;
    private TextView videoTitleTv;
    private ScrollView settingView;
    private Switch micSwitch, effectSwitch, autotuneSwitch;
    private View micView, effectView;
    private SeekBar micVolumeSb;
    private SeekBar echoSb, reverbSb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        hasAllPermissions = checkPermission();
        if (!hasAllPermissions) return;

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFullScreen) {
                    // if the player is in fullscreen, exit fullscreen
                    youtubePlayer.toggleFullscreen();
                } else {
                    finish();
                }
            }
        });

        setupView();
        setYoutubePlayer();
        setRecorderService();
        setMicSwitchClick();
        setEffectSwitchClick();
        setAutotuneSwitchClick();
        setSeekbarListener();

        // start record
        if (checkHeadphoneConnected())
        {
            micSwitch.setChecked(true);
            micView.setVisibility(View.VISIBLE);
            autotuneSwitch.setVisibility(View.VISIBLE);
            effectSwitch.setVisibility(View.VISIBLE);
            recorderService.startRecording();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Please plug in headphones for a better experience.", Toast.LENGTH_LONG).show();
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

    private boolean checkPermission()
    {
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
        };
        for (String s : permissions) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                // Some permissions are not granted, ask the user.
                ActivityCompat.requestPermissions(this, permissions, 0);
                return false;
            }
        }

        return true;
    }

    private void setYoutubePlayer()
    {
        String videoId = getIntent().getStringExtra("videoId");
        String videoTitle = getIntent().getStringExtra("videoTitle");

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player);
        FrameLayout fullscreenViewContainer = findViewById(R.id.full_screen_view_container);

                getLifecycle().addObserver(youTubePlayerView);
        YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youtubePlayer = youTubePlayer;
                youTubePlayer.loadVideo(videoId, 0);
            }
        };
        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(1).fullscreen(1).build();
        youTubePlayerView.initialize(listener, options);

        youTubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View fullscreenView, @NonNull Function0<Unit> function0) {
                isFullScreen = true;

                //settingBtn.swapDimension();
                youTubePlayerView.setVisibility(View.GONE);
                videoTitleTv.setVisibility(View.GONE);
                fullscreenViewContainer.setVisibility(View.VISIBLE);
                fullscreenViewContainer.addView(fullscreenView);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onExitFullscreen() {
                isFullScreen = false;

                // the video will continue playing in the player
                //settingBtn.swapDimension();
                youTubePlayerView.setVisibility(View.VISIBLE);
                videoTitleTv.setVisibility(View.VISIBLE);
                fullscreenViewContainer.setVisibility(View.GONE);
                fullscreenViewContainer.removeAllViews();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

        MaterialTextView title = findViewById(R.id.tv_video_title);
        title.setText(videoTitle);
    }

    private void setupView()
    {
        settingView = findViewById(R.id.setting_view);
        videoTitleTv = findViewById(R.id.tv_video_title);
        micSwitch = findViewById(R.id.sw_mic);
        effectSwitch = findViewById(R.id.sw_effect);
        autotuneSwitch = findViewById(R.id.sw_autotune);
        micView = findViewById(R.id.view_mic);
        effectView = findViewById(R.id.view_effect);
        micVolumeSb = findViewById(R.id.sb_micVolume);
        echoSb = findViewById(R.id.sb_echo);
        reverbSb = findViewById(R.id.sb_reverb);

        closeSettingBtn = findViewById(R.id.closeSetting_btn);
        closeSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingView.setVisibility(View.GONE);
                settingBtn.floatingBtn.setVisibility(View.VISIBLE);
            }
        });

        settingBtn.floatingBtn = findViewById(R.id.setting_btn);
        settingBtn.floatingBtn.setOnTouchListener(this);
    }

    private void setMicSwitchClick()
    {
        micSwitch.setOnClickListener(v -> {
            if (micSwitch.isChecked())
            {
                if (!checkHeadphoneConnected())
                {
                    Toast.makeText(getApplicationContext(), "Please plug in headphones for a better experience.", Toast.LENGTH_LONG).show();
                    micSwitch.setChecked(false);
                    return;
                }

                if (!checkPermission())
                {
                    micSwitch.setChecked(false);
                    return;
                }
            }

            if (micSwitch.isChecked())
            {
                micView.setVisibility(View.VISIBLE);
                autotuneSwitch.setVisibility(View.VISIBLE);
                effectSwitch.setVisibility(View.VISIBLE);
                if (effectSwitch.isChecked())
                    effectView.setVisibility(View.VISIBLE);

                recorderService.startRecording();
                recorderService.setMicVolume(micVolumeSb.getProgress());
            }
            else
            {
                micView.setVisibility(View.GONE);
                autotuneSwitch.setVisibility(View.GONE);
                effectSwitch.setVisibility(View.GONE);
                effectView.setVisibility(View.GONE);
                recorderService.stopRecording();
            }
        });
    }

    private void setEffectSwitchClick()
    {
        effectSwitch.setOnClickListener(v -> {
            recorderService.setEffectEnable(effectSwitch.isChecked());
            if (effectSwitch.isChecked())
            {
                effectView.setVisibility(View.VISIBLE);
                setEffectValue();
            }
            else
            {
                effectView.setVisibility(View.GONE);
            }
        });
    }

    private void setAutotuneSwitchClick()
    {
        autotuneSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorderService.setAutotuneEnable(autotuneSwitch.isChecked());
            }
        });
    }

    private void setEffectValue()
    {
        recorderService.setEffectValue(1, echoSb.getProgress());
        recorderService.setEffectValue(2, reverbSb.getProgress());
    }

    private void setSeekbarListener()
    {
        micVolumeSb.setOnSeekBarChangeListener(this);
        echoSb.setOnSeekBarChangeListener(this);
        reverbSb.setOnSeekBarChangeListener(this);
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

    private boolean checkHeadphoneConnected(){
        isHeadphoneConnected = false;
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo devices[] = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        for(AudioDeviceInfo d : devices){
            if(d.getType() == AudioDeviceInfo.TYPE_USB_HEADSET || d.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                    || d.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET || d.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP){
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (micSwitch.isChecked())
            recorderService.stopRecording();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (micSwitch.isChecked())
        {
            if (!checkHeadphoneConnected())
            {
                Toast.makeText(getApplicationContext(), "Please plug in headphones for a better experience.", Toast.LENGTH_LONG).show();
                micSwitch.setChecked(false);
                return;
            }

            if (!checkPermission())
            {
                micSwitch.setChecked(false);
                return;
            }

            recorderService.startRecording();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (micSwitch.isChecked())
            recorderService.stopRecording();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.equals(micVolumeSb))
        {
            recorderService.setMicVolume(progress);
            return;
        }
        if (seekBar.equals(echoSb))
        {
            recorderService.setEffectValue(1, progress);
            return;
        }
        if (seekBar == reverbSb)
        {
            recorderService.setEffectValue(2, progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)view.getLayoutParams();

        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_DOWN) {

            settingBtn.downRawX = motionEvent.getRawX();
            settingBtn.downRawY = motionEvent.getRawY();
            settingBtn.dX = view.getX() - settingBtn.downRawX;
            settingBtn.dY = view.getY() - settingBtn.downRawY;

            return true; // Consumed

        }
        else if (action == MotionEvent.ACTION_MOVE) {

            int viewWidth = view.getWidth();
            int viewHeight = view.getHeight();

            View viewParent = (View)view.getParent();
            int parentWidth = viewParent.getWidth();
            int parentHeight = viewParent.getHeight();

            float newX = motionEvent.getRawX() + settingBtn.dX;
            newX = Math.max(layoutParams.leftMargin, newX); // Don't allow the FAB past the left hand side of the parent
            newX = Math.min(parentWidth - viewWidth - layoutParams.rightMargin, newX); // Don't allow the FAB past the right hand side of the parent

            float newY = motionEvent.getRawY() + settingBtn.dY;
            newY = Math.max(layoutParams.topMargin, newY); // Don't allow the FAB past the top of the parent
            newY = Math.min(parentHeight - viewHeight - layoutParams.bottomMargin, newY); // Don't allow the FAB past the bottom of the parent

            view.animate()
                    .x(newX)
                    .y(newY)
                    .setDuration(0)
                    .start();

            return true; // Consumed

        }
        else if (action == MotionEvent.ACTION_UP) {

            float upRawX = motionEvent.getRawX();
            float upRawY = motionEvent.getRawY();

            float upDX = upRawX - settingBtn.downRawX;
            float upDY = upRawY - settingBtn.downRawY;

            // Consumed
            if (Math.abs(upDX) < settingBtn.CLICK_DRAG_TOLERANCE && Math.abs(upDY) < settingBtn.CLICK_DRAG_TOLERANCE) { // A click
                //perform click
                // Perform click
                if (settingView.getVisibility() == View.GONE)
                {
                    settingView.setVisibility(View.VISIBLE);
                    settingBtn.floatingBtn.setVisibility(View.GONE);
                }
            }
            // A drag

            return true;

        }
        else {
            return super.onTouchEvent(motionEvent);
        }

    }

    private static class FloatingButton
    {
        private FloatingActionButton floatingBtn;
        private final static float CLICK_DRAG_TOLERANCE = 10; // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.
        private float downRawX, downRawY;
        private float dX, dY;

        private void swapDimension()
        {
            float temp = dX;
            dX = dY;
            dY = temp;

            temp = downRawX;
            downRawX = downRawY;
            downRawY = temp;
        }
    }
}