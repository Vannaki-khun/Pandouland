package com.example.pandouland;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Pandou {
    private Bitmap pandaBitmap;
    private int x, y;
    private int screenWidth;
    private int pandaWidth, pandaHeight;

    public Pandou(Context context, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.x = screenWidth / 2;
        this.y = screenHeight - 200;

        // Utilise le contexte pour accéder à l'image du panda
        pandaBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_pandou);

        // Redimensionner le panda si nécessaire (par exemple, le réduire à 30%)
        pandaWidth = pandaBitmap.getWidth() / 3;
        pandaHeight = pandaBitmap.getHeight() / 3;
        pandaBitmap = Bitmap.createScaledBitmap(pandaBitmap, pandaWidth, pandaHeight, false);

        // Ajuste la position pour qu'il ne soit pas trop bas
        x = screenWidth / 2 - pandaWidth / 2;
        y = screenHeight - pandaHeight - 100;  // Ajoute une marge par rapport au bas
    }

    public void setPosition(int newX) {
        // S'assurer que le panda reste dans les limites de l'écran
        x = Math.max(0, Math.min(newX - pandaWidth / 2, screenWidth - pandaWidth));
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(pandaBitmap, x, y, null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return pandaWidth;
    }

    public int getHeight() {
        return pandaHeight;
    }
}
