package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class DrawingActivity extends AppCompatActivity {
    private DrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        
        drawingView = findViewById(R.id.drawing_view);
        
        // Herramientas
        findViewById(R.id.btn_pen).setOnClickListener(v -> {
            drawingView.setEraser(false);
            drawingView.setColor(android.graphics.Color.BLACK);
        });
        
        findViewById(R.id.btn_eraser).setOnClickListener(v -> {
            drawingView.setEraser(true);
        });
        
        // Colores
        findViewById(R.id.btn_red).setOnClickListener(v -> {
            drawingView.setColor(android.graphics.Color.RED);
        });
        
        findViewById(R.id.btn_blue).setOnClickListener(v -> {
            drawingView.setColor(android.graphics.Color.BLUE);
        });
        
        findViewById(R.id.btn_green).setOnClickListener(v -> {
            drawingView.setColor(android.graphics.Color.GREEN);
        });
        
        findViewById(R.id.btn_black).setOnClickListener(v -> {
            drawingView.setColor(android.graphics.Color.BLACK);
        });
        
        // Acciones
        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            drawingView.clear();
        });
        
        findViewById(R.id.btn_undo).setOnClickListener(v -> {
            drawingView.undo();
        });
        
        findViewById(R.id.btn_save).setOnClickListener(v -> {
            saveDrawing();
        });
        
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            finish();
        });
    }
    
    private void saveDrawing() {
        try {
            Bitmap bitmap = drawingView.getBitmap();
            
            // Crear nombre de archivo con timestamp
            String fileName = "drawing_" + System.currentTimeMillis() + ".png";
            
            // Guardar en galería
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
                
                Toast.makeText(this, "Dibujo guardado en galería", Toast.LENGTH_SHORT).show();
            }
            
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
} 