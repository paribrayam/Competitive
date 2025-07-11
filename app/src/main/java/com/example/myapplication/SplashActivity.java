package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView icon = findViewById(R.id.splash_icon);
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setPadding(0, 0, 0, 0);
        RotateAnimation rotate = new RotateAnimation(
                0f, 360f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(RotateAnimation.INFINITE);
        rotate.setFillAfter(true);
        icon.startAnimation(rotate);

        ProgressBar progressBar = findViewById(R.id.progress_bar);
        final int splashDuration = 4000;
        final int interval = 40; // ms
        final int max = 100;
        progressBar.setMax(max);
        new Thread(() -> {
            for (int i = 0; i <= max; i++) {
                int progress = i;
                runOnUiThread(() -> progressBar.setProgress(progress));
                try {
                    Thread.sleep(splashDuration / max);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 4000);
    }
} 