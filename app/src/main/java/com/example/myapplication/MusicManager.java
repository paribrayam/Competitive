package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

public class MusicManager {
    private static MediaPlayer mediaPlayer;
    private static boolean isPlaying = false;
    private static float currentVolume = 1.0f;
    private static final String PREFS_NAME = "music_prefs";
    private static final String KEY_VOLUME = "volume";
    private static final String KEY_MUSIC_ENABLED = "music_enabled";

    public static void initialize(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.fondomusical);
            mediaPlayer.setLooping(true);
            loadSettings(context);
            if (isMusicEnabled(context)) {
                start(context);
            }
        }
    }

    public static void start(Context context) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    public static void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying = false;
        }
    }

    public static void setVolume(float volume) {
        currentVolume = volume;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    public static float getVolume() {
        return currentVolume;
    }

    public static boolean isPlaying() {
        return isPlaying;
    }

    public static void saveVolume(Context context, float volume) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putFloat(KEY_VOLUME, volume).apply();
        setVolume(volume);
    }

    public static void saveMusicEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_MUSIC_ENABLED, enabled).apply();
        if (enabled) {
            start(context);
        } else {
            pause();
        }
    }

    public static boolean isMusicEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_MUSIC_ENABLED, true);
    }

    private static void loadSettings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        currentVolume = prefs.getFloat(KEY_VOLUME, 1.0f);
        setVolume(currentVolume);
    }

    public static void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
} 