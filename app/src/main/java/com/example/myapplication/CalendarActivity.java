package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {
    private TextView tvMonthYear;
    private GridLayout calendarGrid;
    private Calendar currentDate;
    private Calendar selectedDate;
    private Map<String, List<Event>> events;
    private Gson gson;
    private static final String PREF_NAME = "CalendarEvents";
    private static final String KEY_EVENTS = "events";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        
        tvMonthYear = findViewById(R.id.tv_month_year);
        calendarGrid = findViewById(R.id.calendar_grid);
        
        currentDate = Calendar.getInstance();
        selectedDate = Calendar.getInstance();
        gson = new Gson();
        
        loadEvents();
        
        findViewById(R.id.btn_prev_month).setOnClickListener(v -> previousMonth());
        findViewById(R.id.btn_next_month).setOnClickListener(v -> nextMonth());
        findViewById(R.id.btn_add_event).setOnClickListener(v -> showAddEventDialog());
        
        updateCalendar();
    }
    
    private void loadEvents() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String eventsJson = prefs.getString(KEY_EVENTS, "{}");
        Type type = new TypeToken<Map<String, List<Event>>>(){}.getType();
        events = gson.fromJson(eventsJson, type);
        if (events == null) {
            events = new HashMap<>();
        }
    }
    
    private void saveEvents() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String eventsJson = gson.toJson(events);
        prefs.edit().putString(KEY_EVENTS, eventsJson).apply();
    }
    
    private void updateCalendar() {
        updateMonthYear();
        populateCalendar();
    }
    
    private void updateMonthYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        tvMonthYear.setText(sdf.format(currentDate.getTime()));
    }
    
    private void populateCalendar() {
        calendarGrid.removeAllViews();
        
        Calendar firstDayOfMonth = (Calendar) currentDate.clone();
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        
        int firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Agregar días vacíos al inicio
        for (int i = 1; i < firstDayOfWeek; i++) {
            addDayView("");
        }
        
        // Agregar días del mes
        for (int day = 1; day <= daysInMonth; day++) {
            addDayView(String.valueOf(day), day);
        }
        
        // Agregar días vacíos al final para completar 6 semanas
        int totalCells = 42; // 6 semanas * 7 días
        int filledCells = firstDayOfWeek - 1 + daysInMonth;
        for (int i = filledCells; i < totalCells; i++) {
            addDayView("");
        }
    }
    
    private void addDayView(String dayText) {
        addDayView(dayText, -1);
    }
    
    private void addDayView(String dayText, int day) {
        MaterialButton dayButton = new MaterialButton(this);
        dayButton.setText(dayText);
        dayButton.setTextSize(16);
        dayButton.setCornerRadius(24);
        dayButton.setBackgroundTintList(getColorStateList(R.color.green_yellow_primary));
        dayButton.setTextColor(getColorStateList(R.color.green_yellow_dark));
        
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(2, 2, 2, 2);
        dayButton.setLayoutParams(params);
        
        if (day > 0) {
            final int finalDay = day;
            dayButton.setOnClickListener(v -> {
                selectedDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), finalDay);
                showDayEvents(finalDay);
            });
            
            // Marcar si hay eventos
            String dateKey = getDateKey(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), finalDay);
            if (events.containsKey(dateKey) && !events.get(dateKey).isEmpty()) {
                dayButton.setBackgroundTintList(getColorStateList(R.color.green_yellow_secondary));
            }
        }
        
        calendarGrid.addView(dayButton);
    }
    
    private void previousMonth() {
        currentDate.add(Calendar.MONTH, -1);
        updateCalendar();
    }
    
    private void nextMonth() {
        currentDate.add(Calendar.MONTH, 1);
        updateCalendar();
    }
    
    private void showAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Evento");
        
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null);
        EditText etTitle = dialogView.findViewById(R.id.et_event_title);
        EditText etDescription = dialogView.findViewById(R.id.et_event_description);
        TextView tvDate = dialogView.findViewById(R.id.tv_event_date);
        TextView tvTime = dialogView.findViewById(R.id.tv_event_time);
        
        final Calendar eventDate = Calendar.getInstance();
        final Calendar eventTime = Calendar.getInstance();
        
        updateDateText(tvDate, eventDate);
        updateTimeText(tvTime, eventTime);
        
        tvDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                eventDate.set(year, month, dayOfMonth);
                updateDateText(tvDate, eventDate);
            }, eventDate.get(Calendar.YEAR), eventDate.get(Calendar.MONTH), eventDate.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });
        
        tvTime.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                eventTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                eventTime.set(Calendar.MINUTE, minute);
                updateTimeText(tvTime, eventTime);
            }, eventTime.get(Calendar.HOUR_OF_DAY), eventTime.get(Calendar.MINUTE), true);
            timePicker.show();
        });
        
        builder.setView(dialogView);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            
            if (!title.isEmpty()) {
                Event event = new Event(title, description, eventDate.getTime(), eventTime.getTime());
                addEvent(event);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    
    private void updateDateText(TextView tv, Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tv.setText(sdf.format(cal.getTime()));
    }
    
    private void updateTimeText(TextView tv, Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tv.setText(sdf.format(cal.getTime()));
    }
    
    private void addEvent(Event event) {
        String dateKey = getDateKey(event.getDate());
        if (!events.containsKey(dateKey)) {
            events.put(dateKey, new ArrayList<>());
        }
        events.get(dateKey).add(event);
        saveEvents();
        updateCalendar();
        Toast.makeText(this, "Evento guardado", Toast.LENGTH_SHORT).show();
    }
    
    private void showDayEvents(int day) {
        String dateKey = getDateKey(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), day);
        List<Event> dayEvents = events.get(dateKey);
        
        if (dayEvents == null || dayEvents.isEmpty()) {
            Toast.makeText(this, "No hay eventos para este día", Toast.LENGTH_SHORT).show();
            return;
        }
        
        StringBuilder message = new StringBuilder();
        for (Event event : dayEvents) {
            SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            message.append("• ").append(event.getTitle()).append(" (").append(timeSdf.format(event.getTime())).append(")\n");
            if (!event.getDescription().isEmpty()) {
                message.append("  ").append(event.getDescription()).append("\n");
            }
            message.append("\n");
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eventos del " + day + "/" + (currentDate.get(Calendar.MONTH) + 1));
        builder.setMessage(message.toString());
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private String getDateKey(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getDateKey(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }
    
    private String getDateKey(int year, int month, int day) {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
    }
    
    public static class Event {
        private String title;
        private String description;
        private Date date;
        private Date time;
        
        public Event(String title, String description, Date date, Date time) {
            this.title = title;
            this.description = description;
            this.date = date;
            this.time = time;
        }
        
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public Date getDate() { return date; }
        public Date getTime() { return time; }
    }
} 