package com.sleepez.android;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import org.codeandmagic.android.gauge.GaugeView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{
    private static final int POLLING_RATE = 1000;

    private Handler mHandler;
    private Runnable mRunnable;
    private ImageButton mTestRingerButton;
    private MediaPlayer mMediaPlayer;
    private NoiseMeter mNoiseMeter;
    private VolumeAdjuster mVolumeAdjuster;
    private VolumeAdjuster.SensitivityLevel mSensitivityLevel;
    private GaugeView mGaugeView1;

    Timer mTimer=null;

    String Tag = "Get";
    boolean isActive;

    private int currentSound = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMediaPlayer = MediaPlayer.create(this, R.raw.ambient);

        mNoiseMeter = new NoiseMeter();
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mVolumeAdjuster = new VolumeAdjuster(audioManager, AudioManager.STREAM_MUSIC);

        HandlerThread thread = new HandlerThread("NoiseMeterThread");
        thread.start();
        mHandler = new Handler(thread.getLooper());

        mGaugeView1 = (GaugeView) findViewById(R.id.gauge_view1);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                final int noiseAmplitude = mNoiseMeter.getMaxAmplitude();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public synchronized void run() {
                        if (isActive) {
                            mGaugeView1.setTargetValue((float) ((double) noiseAmplitude / (double) NoiseMeter.METER_LIMIT) * 100);
                        }
                    }
                });

                mVolumeAdjuster.adjustVolume(noiseAmplitude);

                try {
                    int newsound = currentSound;
                    int noise = (int)(((double) noiseAmplitude / (double) NoiseMeter.METER_LIMIT) * 100);

                    if(noise + 20 > newsound)
                    {
                        newsound = Math.min(newsound + 20, 100);
                    }
                    else if(noise - 20 < newsound)
                    {
                        newsound = Math.max(0, newsound - 20);
                    }
                    else
                    {
                        newsound = noise;
                    }
                    dimLight(newsound);
                    currentSound = newsound;
                    Log.e("foobar", String.valueOf(noise) + ":" + String.valueOf(currentSound));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.postDelayed(mRunnable, POLLING_RATE);

            }
        };

        mTestRingerButton = (ImageButton) findViewById(R.id.test_ringer_button);
        mTestRingerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMediaPlayer.isPlaying()) {
                    startRingtone();
                } else {
                    stopRingtone();
                }
            }
        });


        setInitialRadioClicked();
        isActive = true;

        setTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRingtone();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        mNoiseMeter.stop();
        isActive = false;
        System.exit(0);
    }

    public void onSensitivityClicked(View view) {
        switch (view.getId()) {
            case R.id.low_button:
                mSensitivityLevel = VolumeAdjuster.SensitivityLevel.LOW;
                break;
            case R.id.med_button:
                mSensitivityLevel = VolumeAdjuster.SensitivityLevel.MEDIUM;
                break;
            case R.id.high_button:
                mSensitivityLevel = VolumeAdjuster.SensitivityLevel.HIGH;
                break;
        }
    }

    public void onSaveClicked(View view) {
        getSharedPreferences(VolumeAdjuster.SENSITIVITY_PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(VolumeAdjuster.SENSITIVITY_PREFS_KEY, mSensitivityLevel.name())
                .commit();

        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
    }

    public void dimLight(int level) throws Exception {
    }

    private void startRingtone() {
        if (!mMediaPlayer.isPlaying()) {
            mTestRingerButton.setImageResource(R.mipmap.button_blue_stop);
            mMediaPlayer.start();
            mNoiseMeter.start();
            mHandler.postDelayed(mRunnable, POLLING_RATE);
        }
    }

    private void stopRingtone() {
        if (mMediaPlayer.isPlaying()) {
            mTestRingerButton.setImageResource(R.mipmap.media_playback_start);
            mMediaPlayer.pause();
            mNoiseMeter.stop();
            mHandler.removeCallbacks(mRunnable);
            mVolumeAdjuster.restoreVolume();
        }
    }

    private void setInitialRadioClicked() {
        mSensitivityLevel = VolumeAdjuster.getUsersSensitivityLevel(this);

        int radioButtonId;
        switch (mSensitivityLevel) {
            case LOW:
                radioButtonId = R.id.low_button;
                break;
            case HIGH:
                radioButtonId = R.id.high_button;
                break;
            default:
                radioButtonId = R.id.med_button;
                break;
        }

        RadioButton radioButton = (RadioButton) findViewById(radioButtonId);
        radioButton.setChecked(true);
    }


    public void setTimer()
    {
        if(mTimer!=null)
            return;

        mTimer=new Timer();
        TimerTask task=new TimerTask()
        {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public synchronized void run() {
                        if (isActive) {

                        }
                    }
                });
            }
        };
        mTimer.schedule(task, 1000, 1500);
    }

    private void cancelTimer()
    {
        if(mTimer!=null)
        {
            mTimer.cancel();
            mTimer=null;
        }
    }

}
