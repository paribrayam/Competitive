package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.view.ScaleGestureDetector;

public class PixelArtView extends View {
    private int numCells = 16;
    private int[][] pixels = new int[numCells][numCells];
    private int currentColor = Color.BLACK;
    private Paint paint = new Paint();
    private float scaleFactor = 1.0f;
    private ScaleGestureDetector scaleDetector;
    private float lastFocusX = 0, lastFocusY = 0, offsetX = 0, offsetY = 0;
    private float lastTouchX = 0, lastTouchY = 0;
    private boolean isPanning = false;

    public PixelArtView(Context context) {
        super(context);
        initView(context);
    }
    public PixelArtView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public PixelArtView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private void initView(Context context) {
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        resetPixels(numCells);
    }
    private void resetPixels(int size) {
        pixels = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                pixels[i][j] = Color.WHITE;
            }
        }
        numCells = size;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scaleFactor, scaleFactor);
        int cellWidth = getWidth() / numCells;
        int cellHeight = getHeight() / numCells;
        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                paint.setColor(pixels[i][j]);
                canvas.drawRect(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight, paint);
                paint.setColor(Color.LTGRAY);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight, paint);
                paint.setStyle(Paint.Style.FILL);
            }
        }
        canvas.restore();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        if (scaleDetector.isInProgress()) {
            // No pan mientras se hace pinch zoom
            isPanning = false;
            return true;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                isPanning = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (scaleFactor > 1.0f) {
                    float dx = event.getX() - lastTouchX;
                    float dy = event.getY() - lastTouchY;
                    if (!isPanning && (Math.abs(dx) > 5 || Math.abs(dy) > 5)) {
                        isPanning = true;
                    }
                    if (isPanning) {
                        offsetX += dx;
                        offsetY += dy;
                        // Limitar el pan para no salirse del área
                        float maxOffsetX = (scaleFactor - 1) * getWidth();
                        float maxOffsetY = (scaleFactor - 1) * getHeight();
                        offsetX = Math.max(-maxOffsetX, Math.min(offsetX, maxOffsetX));
                        offsetY = Math.max(-maxOffsetY, Math.min(offsetY, maxOffsetY));
                        invalidate();
                    }
                    lastTouchX = event.getX();
                    lastTouchY = event.getY();
                } else {
                    // Dibujo normal
                    if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
                        float x = (event.getX() - offsetX) / scaleFactor;
                        float y = (event.getY() - offsetY) / scaleFactor;
                        int cellWidth = getWidth() / numCells;
                        int cellHeight = getHeight() / numCells;
                        int i = (int) (x / cellWidth);
                        int j = (int) (y / cellHeight);
                        if (i >= 0 && i < numCells && j >= 0 && j < numCells) {
                            pixels[i][j] = currentColor;
                            invalidate();
                        }
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isPanning = false;
                break;
        }
        return true;
    }
    public void setCurrentColor(int color) {
        this.currentColor = color;
    }
    public void clear() {
        resetPixels(numCells);
    }
    public void setGridSize(int size) {
        resetPixels(size);
    }
    public int getGridSize() {
        return numCells;
    }
    public Bitmap getBitmap() {
        int size = Math.min(getWidth(), getHeight());
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 8.0f));
            invalidate();
            return true;
        }
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            lastFocusX = detector.getFocusX();
            lastFocusY = detector.getFocusY();
            return true;
        }
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            // No-op
        }
    }
} 