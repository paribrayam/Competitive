package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class KeyManager {
    private static final String PREF_NAME = "KeyManager";
    private static final String KEY_COUNT = "key_count";
    
    private SharedPreferences preferences;
    
    public KeyManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public int getKeyCount() {
        return preferences.getInt(KEY_COUNT, 0);
    }
    
    public void addKeys(int count) {
        int currentKeys = getKeyCount();
        preferences.edit().putInt(KEY_COUNT, currentKeys + count).apply();
    }
    
    public boolean useKey() {
        int currentKeys = getKeyCount();
        if (currentKeys > 0) {
            preferences.edit().putInt(KEY_COUNT, currentKeys - 1).apply();
            return true;
        }
        return false;
    }
    
    public void resetKeys() {
        preferences.edit().putInt(KEY_COUNT, 0).apply();
    }
} 