package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class GeodeRewardActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "geode_prefs";
    private static final String[] GEODE_KEYS = {"bronze", "gold", "diamond", "galaxy"};
    private static final int[] GEODE_IMAGES = {R.drawable.geodabronce, R.drawable.geodaoro, R.drawable.geodadiamante, R.drawable.geodagalactica};
    private static final String[] GEODE_NAMES = {"Geoda de Bronce", "Geoda de Oro", "Geoda de Diamante", "Geoda Galáctica"};
    private static final String[] GEODE_DESCS = {
        "Común y resistente, símbolo de perseverancia.",
        "Brilla con valor y fortuna, menos común que la bronce.",
        "Rara y preciosa, representa logros excepcionales.",
        "La más misteriosa, solo para los más afortunados."};
    private int geodeIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geode_reward);
        geodeIndex = getIntent().getIntExtra("geode_index", 0);
        setupViews();
    }
    private void setupViews() {
        ImageView ivGeode = findViewById(R.id.iv_geode_image);
        TextView tvName = findViewById(R.id.tv_geode_name);
        TextView tvDesc = findViewById(R.id.tv_geode_desc);
        TextView tvCount = findViewById(R.id.tv_geode_count);
        MaterialButton btnCollect = findViewById(R.id.btn_collect_reward);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentCount = prefs.getInt(GEODE_KEYS[geodeIndex], 0);
        ivGeode.setImageResource(GEODE_IMAGES[geodeIndex]);
        tvName.setText(GEODE_NAMES[geodeIndex]);
        tvDesc.setText(GEODE_DESCS[geodeIndex]);
        tvCount.setText("Cantidad total: " + (currentCount + 1));
        btnCollect.setOnClickListener(v -> {
            RankUtils.collectGeodeAndCheckRank(this, geodeIndex);
            finish();
        });
    }
} 