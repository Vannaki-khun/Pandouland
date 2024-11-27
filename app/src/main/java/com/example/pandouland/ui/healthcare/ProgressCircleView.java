package com.example.pandouland.ui.healthcare;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ProgressCircleView extends View {

    private int progress = 0; // Progression en pourcentage (0-100)
    private int circleColor = 0xFF00FF00; // Couleur du cercle (par défaut : vert)
    private int progressColor = 0xFFFF0000; // Couleur de la progression (par défaut : rouge)
    private Paint circlePaint;
    private Paint progressPaint;

    public ProgressCircleView(Context context) {
        super(context);
        init();
    }

    public ProgressCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setColor(circleColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(20);
        circlePaint.setAntiAlias(true);

        progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(20);
        progressPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Obtenir le centre et le rayon du cercle
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 20;

        // Dessiner le cercle de fond
        canvas.drawCircle(width / 2f, height / 2f, radius, circlePaint);

        // Dessiner l'arc de progression
        float sweepAngle = (360f * progress) / 100; // Angle correspondant à la progression
        canvas.drawArc(
                width / 2f - radius, // gauche
                height / 2f - radius, // haut
                width / 2f + radius, // droite
                height / 2f + radius, // bas
                -90, // Angle de départ (en haut)
                sweepAngle, // Angle balayé
                false, // Pas un cercle plein
                progressPaint
        );
    }

    // Méthode pour définir la progression (0-100)
    public void setProgress(int progress) {
        this.progress = Math.min(100, Math.max(0, progress)); // Limiter entre 0 et 100
        invalidate(); // Redessiner la vue
    }

    // Méthode pour définir la couleur du cercle
    public void setCircleColor(int color) {
        this.circleColor = color;
        circlePaint.setColor(color);
        invalidate();
    }

    // Méthode pour définir la couleur de la progression
    public void setProgressColor(int color) {
        this.progressColor = color;
        progressPaint.setColor(color);
        invalidate();
    }
}
