package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class BluetoothGameActivity extends AppCompatActivity {
    
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSIONS = 2;
    private static final UUID APP_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothServerSocket serverSocket;
    
    private TextView tvQuestion;
    private TextView tvScore;
    private TextView tvOpponentScore;
    private Button btnAnswerA, btnAnswerB, btnAnswerC, btnAnswerD;
    private Button btnHost, btnJoin;
    
    private int myScore = 0;
    private int opponentScore = 0;
    private int currentQuestionIndex = 0;
    private boolean isHost = false;
    private boolean gameStarted = false;
    
    private Handler handler = new Handler(Looper.getMainLooper());
    
    // Preguntas de cultura general
    private List<Question> questions = new ArrayList<>();
    
    private boolean onlineMode = false;
    private String roomCode = null;
    private String role = null;
    private FirebaseFirestore db;
    private ListenerRegistration roomListener;
    private boolean finished = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_game);
        
        initializeViews();
        initializeQuestions();
        Intent intent = getIntent();
        onlineMode = intent.getBooleanExtra("online_mode", false);
        if (onlineMode) {
            roomCode = intent.getStringExtra("room_code");
            role = intent.getStringExtra("role");
            db = FirebaseFirestore.getInstance();
            setupOnlineGame();
        } else {
            setupBluetooth();
        }
    }
    
    private void initializeViews() {
        tvQuestion = findViewById(R.id.tv_question);
        tvScore = findViewById(R.id.tv_score);
        tvOpponentScore = findViewById(R.id.tv_opponent_score);
        btnAnswerA = findViewById(R.id.btn_answer_a);
        btnAnswerB = findViewById(R.id.btn_answer_b);
        btnAnswerC = findViewById(R.id.btn_answer_c);
        btnAnswerD = findViewById(R.id.btn_answer_d);
        btnHost = findViewById(R.id.btn_host);
        btnJoin = findViewById(R.id.btn_join);
        
        btnHost.setOnClickListener(v -> startHosting());
        btnJoin.setOnClickListener(v -> joinGame());
        
        btnAnswerA.setOnClickListener(v -> checkAnswer(0));
        btnAnswerB.setOnClickListener(v -> checkAnswer(1));
        btnAnswerC.setOnClickListener(v -> checkAnswer(2));
        btnAnswerD.setOnClickListener(v -> checkAnswer(3));
        
        updateScoreDisplay();
    }
    
    private void initializeQuestions() {
        questions.add(new Question("¿Cuál es la capital de Francia?", 
            new String[]{"Londres", "París", "Madrid", "Roma"}, 1));
        questions.add(new Question("¿En qué año comenzó la Primera Guerra Mundial?", 
            new String[]{"1914", "1915", "1913", "1916"}, 0));
        questions.add(new Question("¿Quién pintó la Mona Lisa?", 
            new String[]{"Van Gogh", "Da Vinci", "Picasso", "Monet"}, 1));
        questions.add(new Question("¿Cuál es el planeta más grande del sistema solar?", 
            new String[]{"Tierra", "Marte", "Júpiter", "Saturno"}, 2));
        questions.add(new Question("¿Cuál es el elemento químico más abundante en el universo?", 
            new String[]{"Helio", "Hidrógeno", "Carbono", "Oxígeno"}, 1));
        questions.add(new Question("¿En qué continente se encuentra Egipto?", 
            new String[]{"Asia", "Europa", "África", "América"}, 2));
        questions.add(new Question("¿Cuál es la lengua más hablada del mundo?", 
            new String[]{"Inglés", "Español", "Chino mandarín", "Hindi"}, 2));
        questions.add(new Question("¿Quién escribió 'Don Quijote'?", 
            new String[]{"Cervantes", "Shakespeare", "Dante", "Goethe"}, 0));
        questions.add(new Question("¿Cuál es la capital de Japón?", 
            new String[]{"Seúl", "Pekín", "Tokio", "Bangkok"}, 2));
        questions.add(new Question("¿En qué año terminó la Segunda Guerra Mundial?", 
            new String[]{"1944", "1945", "1946", "1943"}, 1));
        questions.add(new Question("¿Cuál es el río más largo del mundo?", 
            new String[]{"Amazonas", "Nilo", "Misisipi", "Yangtsé"}, 1));
        questions.add(new Question("¿Quién descubrió América?", 
            new String[]{"Vasco da Gama", "Cristóbal Colón", "Marco Polo", "Magallanes"}, 1));
        questions.add(new Question("¿Cuál es la montaña más alta del mundo?", 
            new String[]{"K2", "Monte Everest", "Kangchenjunga", "Lhotse"}, 1));
        questions.add(new Question("¿En qué año cayó el Imperio Romano de Occidente?", 
            new String[]{"476 d.C.", "410 d.C.", "1453 d.C.", "330 d.C."}, 0));
        questions.add(new Question("¿Cuál es el país más grande del mundo?", 
            new String[]{"China", "Estados Unidos", "Rusia", "Canadá"}, 2));
        questions.add(new Question("¿Quién escribió 'Romeo y Julieta'?", 
            new String[]{"Cervantes", "Shakespeare", "Dante", "Goethe"}, 1));
        questions.add(new Question("¿Cuál es la capital de Australia?", 
            new String[]{"Sídney", "Melbourne", "Canberra", "Brisbane"}, 2));
        questions.add(new Question("¿En qué año se fundó la ONU?", 
            new String[]{"1945", "1944", "1946", "1943"}, 0));
        questions.add(new Question("¿Cuál es el metal más abundante en la corteza terrestre?", 
            new String[]{"Hierro", "Aluminio", "Cobre", "Zinc"}, 1));
        questions.add(new Question("¿Quién fue el primer presidente de Estados Unidos?", 
            new String[]{"Thomas Jefferson", "John Adams", "George Washington", "Benjamin Franklin"}, 2));
    }
    
    private void setupBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth no está disponible", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    
    private void startHosting() {
        if (checkPermissions()) {
            isHost = true;
            btnHost.setEnabled(false);
            btnJoin.setEnabled(false);
            showWaitingDialog();
            
            new Thread(() -> {
                try {
                    serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothGame", APP_UUID);
                    bluetoothSocket = serverSocket.accept();
                    serverSocket.close();
                    
                    handler.post(() -> {
                        startGame();
                    });
                } catch (IOException e) {
                    handler.post(() -> {
                        Toast.makeText(this, "Error al conectar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        btnHost.setEnabled(true);
                        btnJoin.setEnabled(true);
                    });
                }
            }).start();
        }
    }
    
    private void joinGame() {
        if (checkPermissions()) {
            isHost = false;
            btnHost.setEnabled(false);
            btnJoin.setEnabled(false);
            showDeviceSelectionDialog();
        }
    }
    
    private void showWaitingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Esperando oponente...");
        builder.setMessage("Espera a que otro jugador se conecte");
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    private void showDeviceSelectionDialog() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        List<BluetoothDevice> pairedDevices = new ArrayList<>(bluetoothAdapter.getBondedDevices());
        String[] deviceNames = new String[pairedDevices.size()];
        
        for (int i = 0; i < pairedDevices.size(); i++) {
            deviceNames[i] = pairedDevices.get(i).getName();
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar dispositivo");
        builder.setItems(deviceNames, (dialog, which) -> {
            connectToDevice(pairedDevices.get(which));
        });
        builder.setCancelable(true);
        builder.setOnCancelListener(dialog -> {
            btnHost.setEnabled(true);
            btnJoin.setEnabled(true);
        });
        builder.show();
    }
    
    private void connectToDevice(BluetoothDevice device) {
        new Thread(() -> {
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(APP_UUID);
                bluetoothSocket.connect();
                
                handler.post(() -> {
                    startGame();
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Error al conectar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnHost.setEnabled(true);
                    btnJoin.setEnabled(true);
                });
            }
        }).start();
    }
    
    private void startGame() {
        gameStarted = true;
        btnHost.setVisibility(View.GONE);
        btnJoin.setVisibility(View.GONE);
        
        if (isHost) {
            showQuestion();
        }
        
        // Iniciar comunicación
        startCommunication();
    }
    
    private void startCommunication() {
        new Thread(() -> {
            try {
                InputStream inputStream = bluetoothSocket.getInputStream();
                OutputStream outputStream = bluetoothSocket.getOutputStream();
                byte[] buffer = new byte[1024];
                
                while (gameStarted) {
                    int bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        String message = new String(buffer, 0, bytes);
                        handleMessage(message);
                    }
                }
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Conexión perdida", Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        }).start();
    }
    
    private void handleMessage(String message) {
        String[] parts = message.split(":");
        String command = parts[0];
        
        switch (command) {
            case "QUESTION":
                if (!isHost) {
                    int questionIndex = Integer.parseInt(parts[1]);
                    handler.post(() -> showQuestion(questionIndex));
                }
                break;
            case "ANSWER":
                int playerAnswer = Integer.parseInt(parts[1]);
                int correctAnswer = Integer.parseInt(parts[2]);
                boolean isCorrect = playerAnswer == correctAnswer;
                
                if (isCorrect) {
                    if (isHost) {
                        opponentScore++;
                    } else {
                        myScore++;
                    }
                }
                
                handler.post(() -> {
                    updateScoreDisplay();
                    if (myScore >= 10 || opponentScore >= 10) {
                        endGame();
                    } else if (!isHost) {
                        showQuestion();
                    }
                });
                break;
        }
    }
    
    private void showQuestion() {
        if (onlineMode) {
            if (currentQuestionIndex >= questions.size()) {
                finishOnlineGame();
                return;
            }
            Question question = questions.get(currentQuestionIndex);
            tvQuestion.setText(question.getQuestion());
            btnAnswerA.setText("A) " + question.getAnswers()[0]);
            btnAnswerB.setText("B) " + question.getAnswers()[1]);
            btnAnswerC.setText("C) " + question.getAnswers()[2]);
            btnAnswerD.setText("D) " + question.getAnswers()[3]);
            enableAnswerButtons(true);
        } else {
            showQuestion(currentQuestionIndex);
        }
    }
    
    private void showQuestion(int questionIndex) {
        if (questionIndex >= questions.size()) {
            questionIndex = 0;
        }
        currentQuestionIndex = questionIndex;
        Question question = questions.get(questionIndex);

        tvQuestion.setText(question.getQuestion());
        btnAnswerA.setText("A) " + question.getAnswers()[0]);
        btnAnswerB.setText("B) " + question.getAnswers()[1]);
        btnAnswerC.setText("C) " + question.getAnswers()[2]);
        btnAnswerD.setText("D) " + question.getAnswers()[3]);
        enableAnswerButtons(true);

        // Enviar pregunta al oponente si soy host
        if (isHost) {
            sendMessage("QUESTION:" + questionIndex);
        }
    }
    
    private void checkAnswer(int selectedAnswer) {
        if (onlineMode) {
            Question question = questions.get(currentQuestionIndex);
            boolean isCorrect = selectedAnswer == question.getCorrectAnswer();
            if (isCorrect) {
                myScore++;
                Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Incorrecto. La respuesta correcta era: " + question.getAnswers()[question.getCorrectAnswer()], Toast.LENGTH_LONG).show();
            }
            updateScoreDisplay();
            enableAnswerButtons(false);
            currentQuestionIndex++;
            updateOnlineProgress();
            if (currentQuestionIndex >= questions.size()) {
                finishOnlineGame();
            } else {
                handler.postDelayed(this::showQuestion, 2000);
            }
        } else {
            Question question = questions.get(currentQuestionIndex);
            boolean isCorrect = selectedAnswer == question.getCorrectAnswer();
            
            if (isCorrect) {
                myScore++;
                Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Incorrecto. La respuesta correcta era: " + 
                    question.getAnswers()[question.getCorrectAnswer()], Toast.LENGTH_LONG).show();
            }
            
            updateScoreDisplay();
            enableAnswerButtons(false);
            
            // Enviar respuesta al oponente
            sendMessage("ANSWER:" + selectedAnswer + ":" + question.getCorrectAnswer());
            
            if (myScore >= 10) {
                endGame();
            } else {
                handler.postDelayed(() -> {
                    currentQuestionIndex++;
                    if (currentQuestionIndex >= questions.size()) {
                        currentQuestionIndex = 0;
                    }
                    showQuestion();
                }, 2000);
            }
        }
    }
    
    private void sendMessage(String message) {
        new Thread(() -> {
            try {
                if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
                    OutputStream outputStream = bluetoothSocket.getOutputStream();
                    outputStream.write(message.getBytes());
                }
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    private void enableAnswerButtons(boolean enable) {
        btnAnswerA.setEnabled(enable);
        btnAnswerB.setEnabled(enable);
        btnAnswerC.setEnabled(enable);
        btnAnswerD.setEnabled(enable);
    }
    
    private void updateScoreDisplay() {
        tvScore.setText(getString(R.string.score, myScore));
        tvOpponentScore.setText(getString(R.string.opponent_score, opponentScore));
        
        // Hacer que las puntuaciones sean más visibles
        tvScore.setTextColor(getResources().getColor(R.color.green_yellow_dark));
        tvOpponentScore.setTextColor(getResources().getColor(R.color.green_yellow_dark));
        
        // Agregar un pequeño efecto visual cuando cambia la puntuación
        tvScore.setScaleX(1.1f);
        tvScore.setScaleY(1.1f);
        tvOpponentScore.setScaleX(1.1f);
        tvOpponentScore.setScaleY(1.1f);
        
        handler.postDelayed(() -> {
            tvScore.setScaleX(1.0f);
            tvScore.setScaleY(1.0f);
            tvOpponentScore.setScaleX(1.0f);
            tvOpponentScore.setScaleY(1.0f);
        }, 200);
    }
    
    private void endGame() {
        gameStarted = false;
        boolean iWon = myScore >= 10;
        String result = iWon ? getString(R.string.you_won) : getString(R.string.you_lost);
        
        // Dar llaves según el resultado
        KeyManager keyManager = new KeyManager(this);
        if (iWon) {
            keyManager.addKeys(10);
            result += "\n\n¡Ganaste 10 llaves!";
        } else {
            keyManager.addKeys(1);
            result += "\n\n¡Ganaste 1 llave!";
        }
        // Guardar historial
        new GameHistoryManager(this).addGame(iWon ? "Ganado" : "Perdido", "Bluetooth");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fin del juego");
        builder.setMessage(result + "\nPuntuación final:\nTú: " + myScore + "\nOponente: " + opponentScore);
        builder.setPositiveButton("OK", (dialog, which) -> finish());
        builder.setCancelable(false);
        builder.show();
    }
    
    private boolean checkPermissions() {
        String[] permissions = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        };
        
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_PERMISSIONS);
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Bluetooth es necesario para jugar", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameStarted = false;
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (roomListener != null) roomListener.remove();
    }
    
    private void setupOnlineGame() {
        btnHost.setVisibility(View.GONE);
        btnJoin.setVisibility(View.GONE);
        enableAnswerButtons(false);
        // Ambos jugadores inician la partida
        currentQuestionIndex = 0;
        myScore = 0;
        opponentScore = 0;
        updateScoreDisplay();
        
        // El host inicia mostrando la primera pregunta
        if ("host".equals(role)) {
            showQuestion();
            updateOnlineProgress();
        }
        // El invitado espera a que el host inicie
        listenOnlineRoom();
    }

    private void listenOnlineRoom() {
        DocumentReference roomRef = db.collection("rooms").document(roomCode);
        roomListener = roomRef.addSnapshotListener((snapshot, error) -> {
            if (error != null || snapshot == null || !snapshot.exists()) return;
            // Leer progreso y puntaje de ambos jugadores
            Map<String, Object> data = snapshot.getData();
            if (data == null) return;
            
            Map<String, Object> hostProgress = (Map<String, Object>) data.get("hostProgress");
            Map<String, Object> guestProgress = (Map<String, Object>) data.get("guestProgress");
            
            // Si soy invitado y el host ya inició, mostrar la pregunta
            if ("guest".equals(role) && hostProgress != null && !Boolean.TRUE.equals(hostProgress.get("finished"))) {
                if (currentQuestionIndex == 0 && !btnAnswerA.isEnabled()) {
                    showQuestion();
                    updateOnlineProgress();
                }
            }
            
            // Actualizar puntuación del oponente en tiempo real
            if ("host".equals(role) && guestProgress != null) {
                int newOpponentScore = guestProgress.get("score") != null ? ((Long) guestProgress.get("score")).intValue() : 0;
                if (newOpponentScore != opponentScore) {
                    opponentScore = newOpponentScore;
                    updateScoreDisplay();
                }
            } else if ("guest".equals(role) && hostProgress != null) {
                int newOpponentScore = hostProgress.get("score") != null ? ((Long) hostProgress.get("score")).intValue() : 0;
                if (newOpponentScore != opponentScore) {
                    opponentScore = newOpponentScore;
                    updateScoreDisplay();
                }
            }
            
            // Verificar si ambos jugadores terminaron para mostrar el resultado final
            boolean hostFinished = hostProgress != null && Boolean.TRUE.equals(hostProgress.get("finished"));
            boolean guestFinished = guestProgress != null && Boolean.TRUE.equals(guestProgress.get("finished"));
            
            if (hostFinished && guestFinished && !finished) {
                int hostScore = hostProgress != null && hostProgress.get("score") != null ? ((Long) hostProgress.get("score")).intValue() : 0;
                int guestScore = guestProgress != null && guestProgress.get("score") != null ? ((Long) guestProgress.get("score")).intValue() : 0;
                
                boolean iWon = ("host".equals(role) && hostScore >= guestScore) || ("guest".equals(role) && guestScore >= hostScore);
                showEndDialog(iWon ? getString(R.string.you_won) : getString(R.string.you_lost), hostScore, guestScore);
            }
        });
    }

    private void updateOnlineProgress() {
        String progressField = "host".equals(role) ? "hostProgress" : "guestProgress";
        Map<String, Object> progress = new HashMap<>();
        progress.put("index", currentQuestionIndex);
        progress.put("score", myScore);
        progress.put("finished", false);
        db.collection("rooms").document(roomCode).update(progressField, progress);
    }

    private void finishOnlineGame() {
        finished = true;
        String progressField = "host".equals(role) ? "hostProgress" : "guestProgress";
        Map<String, Object> progress = new HashMap<>();
        progress.put("index", currentQuestionIndex);
        progress.put("score", myScore);
        progress.put("finished", true);
        db.collection("rooms").document(roomCode).update(progressField, progress);
        
        // Mostrar resultado inmediatamente cuando terminas
        showFinalResult();
    }
    
    private void showFinalResult() {
        // Obtener la puntuación actual del oponente
        db.collection("rooms").document(roomCode).get().addOnSuccessListener(snapshot -> {
            Map<String, Object> data = snapshot.getData();
            if (data == null) return;
            
            Map<String, Object> hostProgress = (Map<String, Object>) data.get("hostProgress");
            Map<String, Object> guestProgress = (Map<String, Object>) data.get("guestProgress");
            
            int hostScore = hostProgress != null && hostProgress.get("score") != null ? ((Long) hostProgress.get("score")).intValue() : 0;
            int guestScore = guestProgress != null && guestProgress.get("score") != null ? ((Long) guestProgress.get("score")).intValue() : 0;
            
            boolean iWon = ("host".equals(role) && hostScore >= guestScore) || ("guest".equals(role) && guestScore >= hostScore);
            String result = iWon ? getString(R.string.you_won) : getString(R.string.you_lost);
            
            // Dar llaves según el resultado
            KeyManager keyManager = new KeyManager(this);
            if (iWon) {
                keyManager.addKeys(10);
                result += "\n\n¡Ganaste 10 llaves!";
            } else {
                keyManager.addKeys(1);
                result += "\n\n¡Ganaste 1 llave!";
            }
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("¡Fin del juego!");
            builder.setMessage(result + "\n\nPuntuación final:\nTú: " + myScore + "\nOponente: " + (iWon ? (hostScore >= guestScore ? hostScore : guestScore) : (hostScore >= guestScore ? hostScore : guestScore)));
            builder.setPositiveButton("OK", (dialog, which) -> finish());
            builder.setCancelable(false);
            builder.show();
        });
    }



    private void showEndDialog(String result, int hostScore, int guestScore) {
        finished = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fin del juego");
        builder.setMessage(result + "\nPuntuación final:\nHost: " + hostScore + "\nInvitado: " + guestScore);
        builder.setPositiveButton("OK", (dialog, which) -> finish());
        builder.setCancelable(false);
        builder.show();
    }
} 