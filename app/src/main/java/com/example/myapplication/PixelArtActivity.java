package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;
import android.os.Environment;
import android.graphics.Bitmap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import androidx.appcompat.app.AppCompatActivity;
import yuku.ambilwarna.AmbilWarnaDialog;
import android.widget.SeekBar;
import android.widget.TextView;

public class PixelArtActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pixel_art);

        PixelArtView pixelArtView = findViewById(R.id.pixelArtView);
        Button btnColorRed = findViewById(R.id.btnColorRed);
        Button btnColorGreen = findViewById(R.id.btnColorGreen);
        Button btnColorBlue = findViewById(R.id.btnColorBlue);
        Button btnColorBlack = findViewById(R.id.btnColorBlack);
        Button btnClear = findViewById(R.id.btnClear);
        RadioGroup radioGroupFormat = findViewById(R.id.radioGroupFormat);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnColorPicker = findViewById(R.id.btnColorPicker);
        final int[] currentColor = {Color.BLACK};

        SeekBar seekBarGridSize = findViewById(R.id.seekBarGridSize);
        TextView textGridSize = findViewById(R.id.textGridSize);
        Button btnNewCanvas = findViewById(R.id.btnNewCanvas);

        seekBarGridSize.setMax(24); // 8 + 24 = 32
        seekBarGridSize.setProgress(8); // 16x16 por defecto
        textGridSize.setText("Tamaño de la cuadrícula: 16x16");

        seekBarGridSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int size = 8 + progress;
                textGridSize.setText("Tamaño de la cuadrícula: " + size + "x" + size);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnNewCanvas.setOnClickListener(v -> {
            int size = 8 + seekBarGridSize.getProgress();
            pixelArtView.setGridSize(size);
        });

        btnColorRed.setOnClickListener(v -> pixelArtView.setCurrentColor(Color.RED));
        btnColorGreen.setOnClickListener(v -> pixelArtView.setCurrentColor(Color.GREEN));
        btnColorBlue.setOnClickListener(v -> pixelArtView.setCurrentColor(Color.BLUE));
        btnColorBlack.setOnClickListener(v -> pixelArtView.setCurrentColor(Color.BLACK));
        btnClear.setOnClickListener(v -> pixelArtView.clear());

        btnColorPicker.setOnClickListener(v -> {
            AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, currentColor[0], new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    currentColor[0] = color;
                    pixelArtView.setCurrentColor(color);
                }
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                    // No hacer nada
                }
            });
            colorPicker.show();
        });

        btnSave.setOnClickListener(v -> {
            Bitmap bitmap = pixelArtView.getBitmap();
            int checkedId = radioGroupFormat.getCheckedRadioButtonId();
            String format = "PNG";
            Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
            String extension = ".png";
            if (checkedId == R.id.radioJpeg) {
                format = "JPEG";
                compressFormat = Bitmap.CompressFormat.JPEG;
                extension = ".jpg";
            }
            String fileName = "pixel_art_" + System.currentTimeMillis() + extension;
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (dir != null && !dir.exists()) dir.mkdirs();
            File file = new File(dir, fileName);
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(compressFormat, 100, out);
                Toast.makeText(this, "Guardado como " + format + ": " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
} 