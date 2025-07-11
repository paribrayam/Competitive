package com.example.myapplication.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.BluetoothGameActivity;
import com.example.myapplication.ChestActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import android.content.SharedPreferences;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration roomListener;
    private String currentRoomCode;
    private String currentRole; // "host" o "guest"

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        
        // Configurar el botón de competencia Bluetooth
        binding.btnBluetoothCompetition.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.example.myapplication.BluetoothPairingActivity.class);
            startActivity(intent);
        });
        
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() == null) {
            mAuth.signInAnonymously();
        }
        
        // Usar binding directamente en lugar de findViewById
        binding.btnCreateOnlineRoom.setOnClickListener(v -> createOnlineRoom());
        binding.btnJoinOnlineRoom.setOnClickListener(v -> joinOnlineRoom(binding.etRoomCode.getText().toString().trim()));
        binding.btnOpenChest.setOnClickListener(v -> openChest());
        binding.btnInfo.setOnClickListener(v -> showInfoDialog());
        binding.btnRangos.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.example.myapplication.RangosActivity.class);
            startActivity(intent);
        });
        binding.btnAjustes.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.example.myapplication.AjustesActivity.class);
            startActivity(intent);
        });
        
        // Mostrar rango actual
        TextView tvRangoActual = new TextView(getContext());
        tvRangoActual.setTextSize(20);
        tvRangoActual.setTextColor(0xFFFFD600);
        tvRangoActual.setText("Rango actual: " + getRangoActual());
        ((ViewGroup) binding.textHome.getParent()).addView(tvRangoActual, 1);
        
        return root;
    }

    private void createOnlineRoom() {
        if (mAuth.getCurrentUser() == null) {
            mAuth.signInAnonymously().addOnSuccessListener(authResult -> {
                actuallyCreateOnlineRoom();
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error de autenticación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            actuallyCreateOnlineRoom();
        }
    }

    private void actuallyCreateOnlineRoom() {
        String roomCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Error: usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> room = new HashMap<>();
        Map<String, Object> host = new HashMap<>();
        host.put("uid", user.getUid());
        host.put("nombre", "Host");
        room.put("host", host);
        room.put("status", "waiting");
        room.put("createdAt", System.currentTimeMillis());
        db.collection("rooms").document(roomCode)
            .set(room)
            .addOnSuccessListener(aVoid -> {
                currentRoomCode = roomCode;
                currentRole = "host";
                
                // Verificar que el fragmento aún está activo antes de mostrar Toast o navegar
                if (getContext() != null && isAdded() && !isDetached()) {
                    Toast.makeText(getContext(), getString(R.string.room_created, roomCode), Toast.LENGTH_LONG).show();
                    
                    // Navegar a la sala de espera solo si la actividad existe y el fragmento está adjunto
                    if (getActivity() != null && !getActivity().isFinishing() && !getActivity().isDestroyed()) {
                        try {
                            Intent intent = new Intent(getActivity(), com.example.myapplication.WaitingRoomActivity.class);
                            intent.putExtra("room_code", roomCode);
                            intent.putExtra("role", "host");
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Error al abrir la sala de espera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "No se pudo abrir la sala de espera", Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .addOnFailureListener(e -> {
                if (getContext() != null && isAdded() && !isDetached()) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void joinOnlineRoom(String roomCode) {
        if (roomCode.isEmpty()) {
            Toast.makeText(getContext(), R.string.room_code_hint, Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Error: usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference roomRef = db.collection("rooms").document(roomCode);
        roomRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                if (getContext() != null && isAdded() && !isDetached()) {
                    Toast.makeText(getContext(), R.string.room_not_found, Toast.LENGTH_SHORT).show();
                }
                return;
            }
            Map<String, Object> data = snapshot.getData();
            if (data != null && data.containsKey("guest")) {
                if (getContext() != null && isAdded() && !isDetached()) {
                    Toast.makeText(getContext(), R.string.room_full, Toast.LENGTH_SHORT).show();
                }
                return;
            }
            Map<String, Object> guest = new HashMap<>();
            guest.put("uid", user.getUid());
            guest.put("nombre", "Invitado");
            roomRef.update("guest", guest, "status", "playing")
                .addOnSuccessListener(aVoid -> {
                    currentRoomCode = roomCode;
                    currentRole = "guest";
                    
                    if (getContext() != null && isAdded() && !isDetached()) {
                        Toast.makeText(getContext(), R.string.room_joined, Toast.LENGTH_SHORT).show();
                        
                        if (getActivity() != null && !getActivity().isFinishing() && !getActivity().isDestroyed()) {
                            try {
                                Intent intent = new Intent(getActivity(), com.example.myapplication.WaitingRoomActivity.class);
                                intent.putExtra("room_code", roomCode);
                                intent.putExtra("role", "guest");
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "Error al abrir la sala de espera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "No se pudo abrir la sala de espera", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null && isAdded() && !isDetached()) {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        }).addOnFailureListener(e -> {
            if (getContext() != null && isAdded() && !isDetached()) {
                Toast.makeText(getContext(), "Error al buscar la sala: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenRoom(String roomCode) {
        if (roomListener != null) roomListener.remove();
        DocumentReference roomRef = db.collection("rooms").document(roomCode);
        roomListener = roomRef.addSnapshotListener((snapshot, error) -> {
            if (error != null || snapshot == null || !snapshot.exists()) return;
            Map<String, Object> data = snapshot.getData();
            if (data == null) return;
            String status = (String) data.get("status");
            if ("waiting".equals(status)) {
                Toast.makeText(getContext(), R.string.waiting_for_opponent, Toast.LENGTH_SHORT).show();
            } else if ("playing".equals(status)) {
                Toast.makeText(getContext(), R.string.game_started, Toast.LENGTH_SHORT).show();
                // Iniciar la pantalla de juego online
                Intent intent = new Intent(getActivity(), com.example.myapplication.OnlineGameActivity.class);
                intent.putExtra("room_code", roomCode);
                intent.putExtra("role", currentRole);
                startActivity(intent);
            }
        });
    }

    private void openChest() {
        Intent intent = new Intent(getActivity(), ChestActivity.class);
        startActivity(intent);
    }

    private void showInfoDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Información de llaves")
                .setMessage("Si ganas una partida, obtienes 10 llaves.\nSi pierdes, obtienes 1 llave.\n\n¡Usa tus llaves en el cofre para ganar geodas!")
                .setPositiveButton("OK", null)
                .show();
    }

    private String getRangoActual() {
        SharedPreferences prefs = requireContext().getSharedPreferences("geode_prefs", 0);
        int rango = prefs.getInt("rango_actual", 0);
        String[] nombres = {"Cuarzo", "Ágata", "Amatista", "Topacio", "Ópalo", "Zafiro", "Esmeralda", "Rubí", "Diamante", "Alejandrita"};
        return nombres[rango];
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (roomListener != null) roomListener.remove();
        binding = null;
    }
}