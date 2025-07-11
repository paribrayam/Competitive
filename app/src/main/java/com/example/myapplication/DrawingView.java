package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {
    private List<DrawingPath> paths;
    private DrawingPath currentPath;
    private Paint paint;
    private int currentColor = Color.BLACK;
    private float strokeWidth = 5f;
    private boolean isEraser = false;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paths = new ArrayList<>();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(currentColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Dibujar todos los paths guardados
        for (DrawingPath path : paths) {
            paint.setColor(path.getColor());
            paint.setStrokeWidth(path.getStrokeWidth());
            paint.setStyle(path.isEraser() ? Paint.Style.STROKE : Paint.Style.STROKE);
            canvas.drawPath(path.getPath(), paint);
        }
        
        // Dibujar el path actual
        if (currentPath != null) {
            paint.setColor(currentPath.getColor());
            paint.setStrokeWidth(currentPath.getStrokeWidth());
            paint.setStyle(currentPath.isEraser() ? Paint.Style.STROKE : Paint.Style.STROKE);
            canvas.drawPath(currentPath.getPath(), paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new DrawingPath(currentColor, strokeWidth, isEraser);
                currentPath.getPath().moveTo(x, y);
                return true;

            case MotionEvent.ACTION_MOVE:
                currentPath.getPath().lineTo(x, y);
                break;

            case MotionEvent.ACTION_UP:
                currentPath.getPath().lineTo(x, y);
                paths.add(currentPath);
                currentPath = null;
                break;

            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setColor(int color) {
        currentColor = color;
        isEraser = false;
    }

    public void setEraser(boolean eraser) {
        isEraser = eraser;
        if (eraser) {
            currentColor = Color.WHITE;
        }
    }

    public void setStrokeWidth(float width) {
        strokeWidth = width;
    }

    public void clear() {
        paths.clear();
        currentPath = null;
        invalidate();
    }

    public void undo() {
        if (!paths.isEmpty()) {
            paths.remove(paths.size() - 1);
            invalidate();
        }
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        draw(canvas);
        return bitmap;
    }

    private static class DrawingPath {
        private Path path;
        private int color;
        private float strokeWidth;
        private boolean isEraser;

        public DrawingPath(int color, float strokeWidth, boolean isEraser) {
            this.path = new Path();
            this.color = color;
            this.strokeWidth = strokeWidth;
            this.isEraser = isEraser;
        }

        public Path getPath() { return path; }
        public int getColor() { return color; }
        public float getStrokeWidth() { return strokeWidth; }
        public boolean isEraser() { return isEraser; }
    }
} 