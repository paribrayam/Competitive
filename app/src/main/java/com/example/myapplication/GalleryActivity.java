package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GalleryActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "geode_prefs";
    private static final String[] GEODE_KEYS = {"bronze", "gold", "diamond", "galaxy"};
    private static final int[] GEODE_IMAGES = {R.drawable.geodabronce, R.drawable.geodaoro, R.drawable.geodadiamante, R.drawable.geodagalactica};
    private static final String[] GEODE_NAMES = {"Geoda de Bronce", "Geoda de Oro", "Geoda de Diamante", "Geoda Galáctica"};
    private static final String[] GEODE_DESCS = {
        "Común y resistente, símbolo de perseverancia.",
        "Brilla con valor y fortuna, menos común que la bronce.",
        "Rara y preciosa, representa logros excepcionales.",
        "La más misteriosa, solo para los más afortunados."};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        LinearLayout container = findViewById(R.id.gallery_container);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        LayoutInflater inflater = LayoutInflater.from(this);
        // Mostrar rango actual
        TextView tvRango = new TextView(this);
        tvRango.setTextSize(22);
        tvRango.setTextColor(0xFFFFD600);
        int rango = prefs.getInt("rango_actual", 0);
        String[] nombres = {"Cuarzo", "Ágata", "Amatista", "Topacio", "Ópalo", "Zafiro", "Esmeralda", "Rubí", "Diamante", "Alejandrita"};
        tvRango.setText("Rango actual: " + nombres[rango]);
        container.addView(tvRango);
        for (int i = 0; i < GEODE_KEYS.length; i++) {
            int count = prefs.getInt(GEODE_KEYS[i], 0);
            if (count > 0) {
                // Mostrar solo si tiene al menos una
                LinearLayout item = (LinearLayout) inflater.inflate(R.layout.view_geode_gallery_item, container, false);
                ((ImageView) item.findViewById(R.id.iv_geode_image)).setImageResource(GEODE_IMAGES[i]);
                ((TextView) item.findViewById(R.id.tv_geode_name)).setText(GEODE_NAMES[i]);
                ((TextView) item.findViewById(R.id.tv_geode_desc)).setText(GEODE_DESCS[i]);
                ((TextView) item.findViewById(R.id.tv_geode_count)).setText("Cantidad: " + count);
                container.addView(item);
            }
        }
    }
} 