package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class RankUtils {
    public static final String PREFS_NAME = "geode_prefs";
    public static final String PREFS_RANGO = "rango_actual";
    public static final int RANGO_MAX = 9;
    public static final String[] RANGO_NAMES = {"Cuarzo", "Ágata", "Amatista", "Topacio", "Ópalo", "Zafiro", "Esmeralda", "Rubí", "Diamante", "Alejandrita"};
    public static final String[] GEODE_KEYS = {"bronze", "gold", "diamond", "galaxy"};

    // Lógica para subir de rango por geodas
    public static void collectGeodeAndCheckRank(Context context, int idx) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int count = prefs.getInt(GEODE_KEYS[idx], 0) + 1;
        prefs.edit().putInt(GEODE_KEYS[idx], count).apply();
        int rango = prefs.getInt(PREFS_RANGO, 0);
        boolean subioRango = false;
        if (rango < RANGO_MAX) {
            if (idx == 0 && count >= 20) subioRango = true; // bronce
            if (idx == 1 && count >= 10) subioRango = true; // oro
            if (idx == 2 && count >= 5) subioRango = true; // diamante
            if (idx == 3 && count >= 1) subioRango = true; // galáctica
        }
        if (subioRango) {
            prefs.edit().putInt(PREFS_RANGO, rango + 1).apply();
            Toast.makeText(context, "¡Subiste de rango! Ahora eres: " + RANGO_NAMES[rango + 1], Toast.LENGTH_LONG).show();
        }
    }

    // Lógica para subir de rango por partidas ganadas
    public static void onGameWon(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int wins = prefs.getInt("partidas_ganadas", 0) + 1;
        prefs.edit().putInt("partidas_ganadas", wins).apply();
        int rango = prefs.getInt(PREFS_RANGO, 0);
        boolean subioRango = false;
        if (rango < RANGO_MAX) {
            if (rango < 6 && wins % 5 == 0) subioRango = true; // hasta esmeralda
            if (rango >= 6 && wins % 20 == 0) subioRango = true; // de esmeralda a alejandrita
        }
        if (subioRango) {
            prefs.edit().putInt(PREFS_RANGO, rango + 1).apply();
            Toast.makeText(context, "¡Subiste de rango! Ahora eres: " + RANGO_NAMES[rango + 1], Toast.LENGTH_LONG).show();
        }
    }
} 