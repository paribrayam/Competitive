package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class WaitingRoomActivity extends AppCompatActivity {
    private TextView tvRoomCode, tvWaitingStatus;
    private Button btnCancelRoom;
    private String roomCode, role;
    private FirebaseFirestore db;
    private ListenerRegistration roomListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);
        tvRoomCode = findViewById(R.id.tv_room_code);
        tvWaitingStatus = findViewById(R.id.tv_waiting_status);
        btnCancelRoom = findViewById(R.id.btn_cancel_room);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        roomCode = intent.getStringExtra("room_code");
        role = intent.getStringExtra("role");
        tvRoomCode.setText(roomCode);

        btnCancelRoom.setOnClickListener(v -> {
            // El host puede eliminar la sala, el guest solo sale
            if ("host".equals(role)) {
                db.collection("rooms").document(roomCode).delete();
            }
            finish();
        });

        listenRoomStatus();
    }

    private void listenRoomStatus() {
        DocumentReference roomRef = db.collection("rooms").document(roomCode);
        roomListener = roomRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) return;
            if (snapshot == null || !snapshot.exists()) {
                Toast.makeText(this, "La sala fue eliminada", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            String status = snapshot.getString("status");
            if ("waiting".equals(status)) {
                tvWaitingStatus.setText("Esperando oponente...");
            } else if ("playing".equals(status)) {
                tvWaitingStatus.setText("¡Oponente unido! Iniciando juego...");
                // Navegar a la actividad de juego online
                Intent intent = new Intent(this, OnlineGameActivity.class);
                intent.putExtra("room_code", roomCode);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (roomListener != null) roomListener.remove();
    }
} 