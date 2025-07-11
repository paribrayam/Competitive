package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GeminiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_gemini); // Layout eliminado
        // findViewById(R.id.btn_open_gemini).setOnClickListener(v -> openGemini()); // Botón eliminado
        // findViewById(R.id.btn_open_assistant).setOnClickListener(v -> openGoogleAssistant()); // Botón eliminado
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
    
    private void openGemini() {
        // Intent para activar el asistente de voz (como mantener presionado el botón de inicio)
        Intent voiceIntent = new Intent(Intent.ACTION_VOICE_COMMAND);
        voiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        try {
            startActivity(voiceIntent);
            Toast.makeText(this, "Activando asistente de voz...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Fallback: intentar abrir Google Assistant
            openGoogleAssistant();
        }
    }
    
    private void openGoogleAssistant() {
        // Intent para activar el asistente de voz con Google Assistant específicamente
        Intent assistantIntent = new Intent(Intent.ACTION_VOICE_COMMAND);
        assistantIntent.setPackage("com.google.android.googlequicksearchbox");
        assistantIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        // Verificar si Google Assistant está disponible
        if (isAppInstalled("com.google.android.googlequicksearchbox")) {
            try {
                startActivity(assistantIntent);
                Toast.makeText(this, "Activando Google Assistant...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // Fallback: intentar abrir la app de Google
                try {
                    Intent googleIntent = new Intent(Intent.ACTION_MAIN);
                    googleIntent.setPackage("com.google.android.googlequicksearchbox");
                    googleIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(googleIntent);
                } catch (Exception e2) {
                    // Último fallback: abrir en navegador
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://assistant.google.com"));
                    startActivity(browserIntent);
                    Toast.makeText(this, "Abriendo Google Assistant en el navegador", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // Si no está disponible, abrir Google en navegador
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://assistant.google.com"));
            startActivity(browserIntent);
            Toast.makeText(this, "Abriendo Google Assistant en el navegador", Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean isAppInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
} 