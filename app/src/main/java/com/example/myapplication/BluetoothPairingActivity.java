package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BluetoothPairingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_pairing);

        TextView tvInstructions = findViewById(R.id.tv_instructions);
        Button btnHost = findViewById(R.id.btn_pair_host);
        Button btnJoin = findViewById(R.id.btn_pair_join);

        tvInstructions.setText("Elige si quieres ser anfitrión o unirte a una partida por Bluetooth. Ambos jugadores deben estar cerca y tener Bluetooth activado.");

        btnHost.setOnClickListener(v -> {
            Intent intent = new Intent(this, BluetoothGameActivity.class);
            intent.putExtra("bluetooth_mode", "host");
            startActivity(intent);
        });
        btnJoin.setOnClickListener(v -> {
            Intent intent = new Intent(this, BluetoothGameActivity.class);
            intent.putExtra("bluetooth_mode", "join");
            startActivity(intent);
        });
    }
} 