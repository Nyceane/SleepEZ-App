package com.sleepez.android;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public final class VolumeAdjuster {
    private static final String TAG = VolumeAdjuster.class.getName();
    private static final int VOLUME_FLAGS = 0;

    private AudioManager mAudioManager;
    private final int mStreamType;
    private final int mOriginalVolume;
    private final int mMaxVolume;
    private final int mAmpInterval;
    private int currentSound = 0;

    public VolumeAdjuster(AudioManager audioManager, int streamType) {
        mAudioManager = audioManager;
        mStreamType = streamType;
        mOriginalVolume = mAudioManager.getStreamVolume(mStreamType);
        mMaxVolume = mAudioManager.getStreamMaxVolume(mStreamType);
        mAmpInterval = NoiseMeter.METER_LIMIT / mMaxVolume;
        mAudioManager.setStreamVolume(mStreamType, 4, VOLUME_FLAGS);

    }

    public int adjustVolume(int noiseAmplitude) {
        //Adjustting volumn of the ambient music and set the brightness to the ambient night light

        int adjustedVolumeLevel = Math.max(1, noiseAmplitude / mAmpInterval);

        if(currentSound > adjustedVolumeLevel)
        {
            //mAudioManager.adjustStreamVolume(mStreamType, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            SetBrightness(99 - (int)((double)(mAudioManager.getStreamVolume(mStreamType) + AudioManager.ADJUST_LOWER)/(double)15*99) * 2);
        }
        else if (currentSound < adjustedVolumeLevel)
        {
            //mAudioManager.adjustStreamVolume(mStreamType, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            SetBrightness(99 - (int)((double)(mAudioManager.getStreamVolume(mStreamType) + AudioManager.ADJUST_RAISE)/(double)15*99) * 2);
        }
        else if (mAudioManager.getStreamVolume(mStreamType) < 4)
        {
            //adjustedVolumeLevel = 4;
            //mAudioManager.setStreamVolume(mStreamType, adjustedVolumeLevel, VOLUME_FLAGS);
            SetBrightness(99);
        }


        Log.d(TAG, "noise amplitude: " + noiseAmplitude);
        Log.d(TAG, "volume level: " + adjustedVolumeLevel);
        Log.e(TAG, "current volume: " + mAudioManager.getStreamVolume(mStreamType));
        currentSound = mAudioManager.getStreamVolume(mStreamType);
        return currentSound;
    }

    public void restoreVolume() {
        mAudioManager.setStreamVolume(mStreamType, mOriginalVolume, VOLUME_FLAGS);
    }

    public void mute() {
        mAudioManager.setStreamVolume(mStreamType, 0, VOLUME_FLAGS);
    }

    public enum SensitivityLevel {
        LOW,
        MEDIUM,
        HIGH
    }

    public static final String SENSITIVITY_PREFS_NAME = "SensitivityPrefs";
    public static final String SENSITIVITY_PREFS_KEY = "sensitivityLevel";

    public static SensitivityLevel getUsersSensitivityLevel(Context context) {
        String enumValueName = context
                .getSharedPreferences(SENSITIVITY_PREFS_NAME, Context.MODE_PRIVATE)
                .getString(SENSITIVITY_PREFS_KEY, VolumeAdjuster.SensitivityLevel.MEDIUM.name());

        // enumValueName may be null, use MEDIUM as default in this case
        SensitivityLevel sensitivityLevel = SensitivityLevel.MEDIUM;
        if (enumValueName != null) {
            sensitivityLevel = Enum.valueOf(VolumeAdjuster.SensitivityLevel.class, enumValueName);
        }

        return sensitivityLevel;
    }

    protected void SetBrightness(int brightness)
    {
        //LED part for setting up brightness

    }

}
