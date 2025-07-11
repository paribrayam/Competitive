package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GameHistoryManager {
    private static final String PREF_NAME = "GameHistory";
    private static final String KEY_HISTORY = "history";
    private SharedPreferences preferences;

    public GameHistoryManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addGame(String result, String mode) {
        JSONArray history = getHistoryArray();
        JSONObject entry = new JSONObject();
        try {
            entry.put("result", result);
            entry.put("mode", mode);
            entry.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));
            history.put(entry);
            preferences.edit().putString(KEY_HISTORY, history.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<GameEntry> getHistory() {
        List<GameEntry> list = new ArrayList<>();
        JSONArray history = getHistoryArray();
        for (int i = 0; i < history.length(); i++) {
            JSONObject obj = history.optJSONObject(i);
            if (obj != null) {
                String result = obj.optString("result", "");
                String mode = obj.optString("mode", "");
                String date = obj.optString("date", "");
                list.add(new GameEntry(result, mode, date));
            }
        }
        return list;
    }

    private JSONArray getHistoryArray() {
        String json = preferences.getString(KEY_HISTORY, "[]");
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public static class GameEntry {
        public final String result;
        public final String mode;
        public final String date;
        public GameEntry(String result, String mode, String date) {
            this.result = result;
            this.mode = mode;
            this.date = date;
        }
    }
} 