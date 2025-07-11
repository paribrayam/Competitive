package com.example.myapplication;

import android.content.Intent;
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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.SharedPreferences;

public class OnlineGameActivity extends AppCompatActivity {
    private TextView tvQuestion;
    private TextView tvScore;
    private TextView tvOpponentScore;
    private Button btnAnswerA, btnAnswerB, btnAnswerC, btnAnswerD;

    private int myScore = 0;
    private int opponentScore = 0;
    private int currentQuestionIndex = 0;
    private boolean finished = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private List<Question> questions = new ArrayList<>();

    private String roomCode = null;
    private String role = null;
    private FirebaseFirestore db;
    private ListenerRegistration roomListener;

    private static final String PREFS_RANGO = "rango_actual";
    private static final int RANGO_MAX = 9;
    private static final String[] RANGO_NAMES = {"Cuarzo", "Ágata", "Amatista", "Topacio", "Ópalo", "Zafiro", "Esmeralda", "Rubí", "Diamante", "Alejandrita"};
    private static final String PREFS_WINS = "partidas_ganadas";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_game);
        initializeViews();
        initializeQuestions();
        Intent intent = getIntent();
        roomCode = intent.getStringExtra("room_code");
        role = intent.getStringExtra("role");
        db = FirebaseFirestore.getInstance();
        setupOnlineGame();
    }

    private void initializeViews() {
        tvQuestion = findViewById(R.id.tv_question);
        tvScore = findViewById(R.id.tv_score);
        tvOpponentScore = findViewById(R.id.tv_opponent_score);
        btnAnswerA = findViewById(R.id.btn_answer_a);
        btnAnswerB = findViewById(R.id.btn_answer_b);
        btnAnswerC = findViewById(R.id.btn_answer_c);
        btnAnswerD = findViewById(R.id.btn_answer_d);

        btnAnswerA.setOnClickListener(v -> checkAnswer(0));
        btnAnswerB.setOnClickListener(v -> checkAnswer(1));
        btnAnswerC.setOnClickListener(v -> checkAnswer(2));
        btnAnswerD.setOnClickListener(v -> checkAnswer(3));

        updateScoreDisplay();
    }

    private void initializeQuestions() {
        questions.add(new Question("¿Cuál es la capital de Francia?", new String[]{"Londres", "París", "Madrid", "Roma"}, 1));
        questions.add(new Question("¿En qué año comenzó la Primera Guerra Mundial?", new String[]{"1914", "1915", "1913", "1916"}, 0));
        questions.add(new Question("¿Quién pintó la Mona Lisa?", new String[]{"Van Gogh", "Da Vinci", "Picasso", "Monet"}, 1));
        questions.add(new Question("¿Cuál es el planeta más grande del sistema solar?", new String[]{"Tierra", "Marte", "Júpiter", "Saturno"}, 2));
        questions.add(new Question("¿Cuál es el elemento químico más abundante en el universo?", new String[]{"Helio", "Hidrógeno", "Carbono", "Oxígeno"}, 1));
        questions.add(new Question("¿En qué continente se encuentra Egipto?", new String[]{"Asia", "Europa", "África", "América"}, 2));
        questions.add(new Question("¿Cuál es la lengua más hablada del mundo?", new String[]{"Inglés", "Español", "Chino mandarín", "Hindi"}, 2));
        questions.add(new Question("¿Quién escribió 'Don Quijote'?", new String[]{"Cervantes", "Shakespeare", "Dante", "Goethe"}, 0));
        questions.add(new Question("¿Cuál es la capital de Japón?", new String[]{"Seúl", "Pekín", "Tokio", "Bangkok"}, 2));
        questions.add(new Question("¿En qué año terminó la Segunda Guerra Mundial?", new String[]{"1944", "1945", "1946", "1943"}, 1));
    }

    private void setupOnlineGame() {
        enableAnswerButtons(false);
        // El host inicia la partida
        if ("host".equals(role)) {
            currentQuestionIndex = 0;
            myScore = 0;
            opponentScore = 0;
            updateScoreDisplay();
            showQuestion();
            updateOnlineProgress();
        }
        listenOnlineRoom();
    }

    private void listenOnlineRoom() {
        DocumentReference roomRef = db.collection("rooms").document(roomCode);
        roomListener = roomRef.addSnapshotListener((snapshot, error) -> {
            if (error != null || snapshot == null || !snapshot.exists()) return;
            Map<String, Object> data = snapshot.getData();
            if (data == null) return;
            Map<String, Object> hostProgress = (Map<String, Object>) data.get("hostProgress");
            Map<String, Object> guestProgress = (Map<String, Object>) data.get("guestProgress");
            if ("host".equals(role) && guestProgress != null && Boolean.TRUE.equals(guestProgress.get("finished"))) {
                int guestScore = guestProgress.get("score") != null ? ((Long) guestProgress.get("score")).intValue() : 0;
                if (!finished) {
                    showEndDialog(guestScore > myScore ? getString(R.string.you_lost) : getString(R.string.you_won), guestScore, myScore);
                }
            } else if ("guest".equals(role) && hostProgress != null && Boolean.TRUE.equals(hostProgress.get("finished"))) {
                int hostScore = hostProgress.get("score") != null ? ((Long) hostProgress.get("score")).intValue() : 0;
                if (!finished) {
                    showEndDialog(hostScore > myScore ? getString(R.string.you_lost) : getString(R.string.you_won), hostScore, myScore);
                }
            }
        });
    }

    private void showQuestion() {
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
    }

    private void checkAnswer(int selectedAnswer) {
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
        handler.postDelayed(this::checkOnlineWinner, 1000);
    }

    private void checkOnlineWinner() {
        db.collection("rooms").document(roomCode).get().addOnSuccessListener(snapshot -> {
            Map<String, Object> data = snapshot.getData();
            if (data == null) return;
            Map<String, Object> hostProgress = (Map<String, Object>) data.get("hostProgress");
            Map<String, Object> guestProgress = (Map<String, Object>) data.get("guestProgress");
            int hostScore = hostProgress != null && hostProgress.get("score") != null ? ((Long) hostProgress.get("score")).intValue() : 0;
            int guestScore = guestProgress != null && guestProgress.get("score") != null ? ((Long) guestProgress.get("score")).intValue() : 0;
            boolean hostFinished = hostProgress != null && Boolean.TRUE.equals(hostProgress.get("finished"));
            boolean guestFinished = guestProgress != null && Boolean.TRUE.equals(guestProgress.get("finished"));
            if (hostFinished && guestFinished) {
                boolean iWon = ("host".equals(role) && hostScore >= guestScore) || ("guest".equals(role) && guestScore >= hostScore);
                showEndDialog(iWon ? getString(R.string.you_won) : getString(R.string.you_lost), hostScore, guestScore);
            } else {
                Toast.makeText(this, "Esperando al oponente...", Toast.LENGTH_SHORT).show();
            }
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

    private void enableAnswerButtons(boolean enable) {
        btnAnswerA.setEnabled(enable);
        btnAnswerB.setEnabled(enable);
        btnAnswerC.setEnabled(enable);
        btnAnswerD.setEnabled(enable);
    }

    private void updateScoreDisplay() {
        tvScore.setText(getString(R.string.score, myScore));
        tvOpponentScore.setText(getString(R.string.opponent_score, opponentScore));
    }

    private void onGameWon() {
        RankUtils.onGameWon(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (roomListener != null) roomListener.remove();
    }
} 