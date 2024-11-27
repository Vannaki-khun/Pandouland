package com.example.pandouland.ui.budget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class customPieChart extends View {

    private Paint paint = new Paint();
    private float[] dataValues = {40f, 30f, 20f, 10f}; // Pourcentage des segments

    private int[] colors = {
            Color.parseColor("#280540"), // Sorties
            Color.parseColor("#541689"), // Charges
            Color.parseColor("#BCA0522D"), // Crédit
            Color.parseColor("#DAA520")  // Épargne
    };

    private String centerText = "Reste à vivre\n452 €";
    private float[] values;
    private String[] labels;

    public customPieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (dataValues == null || dataValues.length == 0) {
            // Affiche un message indiquant que le graphique est vide
            paint.setColor(Color.GRAY);
            paint.setTextSize(40f);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Pas de données", getWidth() / 2f, getHeight() / 2f, paint);
            return;
        }

        // Définir le centre et le rayon du cercle
        int width = getWidth();
        int height = getHeight();
        float centerX = width / 2f;
        float centerY = height / 2f;
        float radius = Math.min(width, height) / 2f - 20f;

        // Dessiner le graphique
        float startAngle = 0f;
        for (int i = 0; i < dataValues.length; i++) {
            paint.setColor(colors[i]);
            float sweepAngle = (dataValues[i] / 100f) * 360f;
            canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius,
                    startAngle, sweepAngle, true, paint);
            startAngle += sweepAngle;
        }

        // Ajouter le texte au centre
        paint.setColor(Color.BLACK);
        paint.setTextSize(40f);
        paint.setTextAlign(Paint.Align.CENTER);

        String[] lines = centerText.split("\n");
        for (int i = 0; i < lines.length; i++) {
            canvas.drawText(lines[i], centerX, centerY + (i * 50) - 20, paint);
        }
    }

    // Méthode pour définir les données dynamiquement

    public void setData(float[] values, String[] labels) {
        this.dataValues = values; // Met à jour les valeurs utilisées pour dessiner
        this.labels = labels;
        invalidate(); // Force le redessin du graphique
    }

    public void clearData() {
        this.setData(new float[]{100f}, new String[]{""});
        this.centerText = "Le total des dépenses";
        invalidate();

    }

}

