package com.example.myapplication;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class AjustesActivity extends AppCompatActivity {
    private SeekBar seekBarVolume;
    private TextView tvVolumeValue;
    private SwitchMaterial switchMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        seekBarVolume = findViewById(R.id.seekbar_volume);
        tvVolumeValue = findViewById(R.id.tv_volume_value);
        switchMusic = findViewById(R.id.switch_music);

        // Cargar configuración actual
        float currentVolume = MusicManager.getVolume();
        boolean musicEnabled = MusicManager.isMusicEnabled(this);
        
        seekBarVolume.setProgress((int) (currentVolume * 100));
        tvVolumeValue.setText((int) (currentVolume * 100) + "%");
        switchMusic.setChecked(musicEnabled);

        // Configurar listeners
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                tvVolumeValue.setText(progress + "%");
                MusicManager.saveVolume(AjustesActivity.this, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MusicManager.saveMusicEnabled(AjustesActivity.this, isChecked);
        });
    }
} 