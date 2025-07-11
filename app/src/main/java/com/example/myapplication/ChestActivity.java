package com.example.myapplication;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ChestActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "geode_prefs";
    private static final String[] GEODE_KEYS = {"bronze", "gold", "diamond", "galaxy"};
    private static final int[] GEODE_IMAGES = {R.drawable.geodabronce, R.drawable.geodaoro, R.drawable.geodadiamante, R.drawable.geodagalactica};
    private static final String[] GEODE_NAMES = {"Geoda de Bronce", "Geoda de Oro", "Geoda de Diamante", "Geoda Galáctica"};
    private static final String[] GEODE_DESCS = {
        "Común y resistente, símbolo de perseverancia.",
        "Brilla con valor y fortuna, menos común que la bronce.",
        "Rara y preciosa, representa logros excepcionales.",
        "La más misteriosa, solo para los más afortunados."};
    private static final String PREFS_RANGO = "rango_actual";
    private static final int RANGO_MAX = 9; // 0-cuarzo ... 9-alejandrita
    private static final String[] RANGO_NAMES = {"Cuarzo", "Ágata", "Amatista", "Topacio", "Ópalo", "Zafiro", "Esmeralda", "Rubí", "Diamante", "Alejandrita"};
    private TextView tvKeyCount, tvResult;
    private KeyManager keyManager;
    private FrameLayout geodeResultContainer;
    private MaterialButton btnUseKey, btnGallery;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest);
        
        keyManager = new KeyManager(this);
        geodeResultContainer = findViewById(R.id.geode_result_container);
        btnUseKey = findViewById(R.id.btn_use_key);
        btnGallery = findViewById(R.id.btn_gallery);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        tvKeyCount = findViewById(R.id.tv_key_count);
        tvResult = findViewById(R.id.tv_result);
        
        btnUseKey.setOnClickListener(v -> showRandomGeode());
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        btnGallery.setOnClickListener(v -> startActivity(new Intent(this, GalleryActivity.class)));
        
        updateKeyDisplay();
    }
    
    private void updateKeyDisplay() {
        int keys = keyManager.getKeyCount();
        tvKeyCount.setText("Llaves: " + keys);
    }
    
    private void useKey() {
        if (keyManager.useKey()) {
            // Obtener geoda aleatoria
            Geoda.Tipo geoda = Geoda.obtenerGeodaAleatoria();
            
            // Mostrar resultado
            String message = "¡Has obtenido una " + geoda.getNombre() + "!";
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("¡Geoda Encontrada!")
                    .setMessage(message)
                    .setPositiveButton("¡Genial!", (dialog, which) -> {
                        // Mostrar el resultado en la pantalla
                        tvResult.setText(geoda.getNombre());
                        tvResult.setVisibility(View.VISIBLE);
                    })
                    .setCancelable(false)
                    .show();
            
            updateKeyDisplay();
        } else {
            Toast.makeText(this, "No tienes llaves suficientes", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showRandomGeode() {
        int idx = getRandomGeodeIndex();
        Intent intent = new Intent(this, GeodeRewardActivity.class);
        intent.putExtra("geode_index", idx);
        startActivity(intent);
    }
    private int getRandomGeodeIndex() {
        double r = Math.random();
        if (r < 0.5) return 0; // Bronce 50%
        else if (r < 0.8) return 1; // Oro 30%
        else if (r < 0.95) return 2; // Diamante 15%
        else return 3; // Galaxia 5%
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateKeyDisplay();
    }
} 