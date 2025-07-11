package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {
    private TextView tvDisplay;
    private String currentNumber = "";
    private String operator = "";
    private double firstNumber = 0;
    private boolean isNewNumber = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        
        tvDisplay = findViewById(R.id.tv_display);
        
        // Números
        findViewById(R.id.btn_0).setOnClickListener(v -> appendNumber("0"));
        findViewById(R.id.btn_1).setOnClickListener(v -> appendNumber("1"));
        findViewById(R.id.btn_2).setOnClickListener(v -> appendNumber("2"));
        findViewById(R.id.btn_3).setOnClickListener(v -> appendNumber("3"));
        findViewById(R.id.btn_4).setOnClickListener(v -> appendNumber("4"));
        findViewById(R.id.btn_5).setOnClickListener(v -> appendNumber("5"));
        findViewById(R.id.btn_6).setOnClickListener(v -> appendNumber("6"));
        findViewById(R.id.btn_7).setOnClickListener(v -> appendNumber("7"));
        findViewById(R.id.btn_8).setOnClickListener(v -> appendNumber("8"));
        findViewById(R.id.btn_9).setOnClickListener(v -> appendNumber("9"));
        findViewById(R.id.btn_dot).setOnClickListener(v -> appendNumber("."));
        
        // Operadores
        findViewById(R.id.btn_plus).setOnClickListener(v -> setOperator("+"));
        findViewById(R.id.btn_minus).setOnClickListener(v -> setOperator("-"));
        findViewById(R.id.btn_multiply).setOnClickListener(v -> setOperator("×"));
        findViewById(R.id.btn_divide).setOnClickListener(v -> setOperator("÷"));
        findViewById(R.id.btn_equals).setOnClickListener(v -> calculate());
        
        // Funciones especiales
        findViewById(R.id.btn_clear).setOnClickListener(v -> clear());
        findViewById(R.id.btn_plus_minus).setOnClickListener(v -> toggleSign());
        findViewById(R.id.btn_percent).setOnClickListener(v -> calculatePercent());
    }
    
    private void appendNumber(String number) {
        if (isNewNumber) {
            currentNumber = number;
            isNewNumber = false;
        } else {
            if (number.equals(".") && currentNumber.contains(".")) {
                return; // No permitir múltiples puntos decimales
            }
            currentNumber += number;
        }
        updateDisplay();
    }
    
    private void setOperator(String op) {
        if (!currentNumber.isEmpty()) {
            if (!operator.isEmpty()) {
                calculate();
            }
            firstNumber = Double.parseDouble(currentNumber);
            operator = op;
            isNewNumber = true;
        }
    }
    
    private void calculate() {
        if (!currentNumber.isEmpty() && !operator.isEmpty()) {
            double secondNumber = Double.parseDouble(currentNumber);
            double result = 0;
            
            switch (operator) {
                case "+":
                    result = firstNumber + secondNumber;
                    break;
                case "-":
                    result = firstNumber - secondNumber;
                    break;
                case "×":
                    result = firstNumber * secondNumber;
                    break;
                case "÷":
                    if (secondNumber != 0) {
                        result = firstNumber / secondNumber;
                    } else {
                        tvDisplay.setText("Error");
                        return;
                    }
                    break;
            }
            
            currentNumber = String.valueOf(result);
            if (currentNumber.endsWith(".0")) {
                currentNumber = currentNumber.substring(0, currentNumber.length() - 2);
            }
            operator = "";
            isNewNumber = true;
            updateDisplay();
        }
    }
    
    private void clear() {
        currentNumber = "";
        operator = "";
        firstNumber = 0;
        isNewNumber = true;
        tvDisplay.setText("0");
    }
    
    private void toggleSign() {
        if (!currentNumber.isEmpty()) {
            if (currentNumber.startsWith("-")) {
                currentNumber = currentNumber.substring(1);
            } else {
                currentNumber = "-" + currentNumber;
            }
            updateDisplay();
        }
    }
    
    private void calculatePercent() {
        if (!currentNumber.isEmpty()) {
            double number = Double.parseDouble(currentNumber);
            number = number / 100;
            currentNumber = String.valueOf(number);
            if (currentNumber.endsWith(".0")) {
                currentNumber = currentNumber.substring(0, currentNumber.length() - 2);
            }
            updateDisplay();
        }
    }
    
    private void updateDisplay() {
        if (currentNumber.isEmpty()) {
            tvDisplay.setText("0");
        } else {
            tvDisplay.setText(currentNumber);
        }
    }
} 