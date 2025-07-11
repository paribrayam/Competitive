package com.example.myapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TranslatorActivity extends AppCompatActivity {
    private Spinner spinnerFrom, spinnerTo;
    private EditText etFrom, etTo;
    private ExecutorService executor;
    
    private static final String[] LANGUAGES = {
        "Español", "Inglés", "Francés", "Alemán", "Italiano", "Portugués", 
        "Ruso", "Chino", "Japonés", "Coreano", "Árabe", "Hindi", "Turco", "Holandés"
    };
    
    private static final String[] LANGUAGE_CODES = {
        "es", "en", "fr", "de", "it", "pt", "ru", "zh", "ja", "ko", "ar", "hi", "tr", "nl"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);
        
        spinnerFrom = findViewById(R.id.spinner_from);
        spinnerTo = findViewById(R.id.spinner_to);
        etFrom = findViewById(R.id.et_from);
        etTo = findViewById(R.id.et_to);
        
        executor = Executors.newSingleThreadExecutor();
        
        setupSpinners();
        setupButtons();
    }
    
    private void setupSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, LANGUAGES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
        
        // Configurar idiomas por defecto
        spinnerFrom.setSelection(0); // Español
        spinnerTo.setSelection(1);   // Inglés
    }
    
    private void setupButtons() {
        findViewById(R.id.btn_translate).setOnClickListener(v -> translate());
        findViewById(R.id.btn_swap).setOnClickListener(v -> swapLanguages());
        findViewById(R.id.btn_copy).setOnClickListener(v -> copyTranslation());
        findViewById(R.id.btn_share).setOnClickListener(v -> shareTranslation());
        findViewById(R.id.btn_clear).setOnClickListener(v -> clearText());
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
    
    private void translate() {
        String text = etFrom.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "Ingresa texto para traducir", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String fromLang = LANGUAGE_CODES[spinnerFrom.getSelectedItemPosition()];
        String toLang = LANGUAGE_CODES[spinnerTo.getSelectedItemPosition()];
        
        // Mostrar indicador de carga
        etTo.setText("Traduciendo...");
        
        executor.execute(() -> {
            try {
                String translation = translateText(text, fromLang, toLang);
                runOnUiThread(() -> {
                    etTo.setText(translation);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    etTo.setText("Error en la traducción");
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private String translateText(String text, String fromLang, String toLang) throws Exception {
        // Usar Google Translate API (versión gratuita)
        String encodedText = URLEncoder.encode(text, "UTF-8");
        String url = String.format(
            "https://translate.googleapis.com/translate_a/single?client=gtx&sl=%s&tl=%s&dt=t&q=%s",
            fromLang, toLang, encodedText
        );
        
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            // Parsear respuesta de Google Translate
            String result = response.toString();
            // Extraer la traducción del JSON
            int start = result.indexOf("\"") + 1;
            int end = result.indexOf("\"", start);
            if (start > 0 && end > start) {
                return result.substring(start, end);
            }
        }
        
        throw new Exception("Error en la traducción");
    }
    
    private void swapLanguages() {
        int fromPos = spinnerFrom.getSelectedItemPosition();
        int toPos = spinnerTo.getSelectedItemPosition();
        
        spinnerFrom.setSelection(toPos);
        spinnerTo.setSelection(fromPos);
        
        // Intercambiar texto
        String tempText = etFrom.getText().toString();
        etFrom.setText(etTo.getText().toString());
        etTo.setText(tempText);
    }
    
    private void copyTranslation() {
        String translation = etTo.getText().toString();
        if (!translation.isEmpty() && !translation.equals("Traduciendo...")) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Traducción", translation);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Traducción copiada", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void shareTranslation() {
        String original = etFrom.getText().toString();
        String translation = etTo.getText().toString();
        
        if (!translation.isEmpty() && !translation.equals("Traduciendo...")) {
            String shareText = "Original: " + original + "\n\nTraducción: " + translation;
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Compartir traducción"));
        }
    }
    
    private void clearText() {
        etFrom.setText("");
        etTo.setText("");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
} 