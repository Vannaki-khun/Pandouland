package com.example.pandouland;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;

public class Fruit {
    private Bitmap fruitBitmap;
    private int x, y;
    private int speed;
    private int screenWidth, screenHeight;
    private int fruitWidth, fruitHeight;

    public Fruit(Context context, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Génère des fruits à une vitesse et position aléatoire
        Random random = new Random();
        this.x = random.nextInt(screenWidth);
        this.y = 0;
        this.speed = random.nextInt(10) + 5;

        // Utilise le contexte pour accéder à l'image du fruit
        fruitBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_fruit);

        // Redimensionner l'image (par exemple, réduire la taille à 20% de sa taille d'origine)
        fruitWidth = fruitBitmap.getWidth() / 5;
        fruitHeight = fruitBitmap.getHeight() / 5;
        fruitBitmap = Bitmap.createScaledBitmap(fruitBitmap, fruitWidth, fruitHeight, false);
    }

    public void update() {
        y += speed;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(fruitBitmap, x, y, null);
    }

    public boolean isCaughtBy(Pandou Pandou) {
        Rect PandouRect = new Rect(Pandou.getX(), Pandou.getY(), Pandou.getX() + Pandou.getWidth(), Pandou.getY() + Pandou.getHeight());
        Rect fruitRect = new Rect(x, y, x + fruitBitmap.getWidth(), y + fruitBitmap.getHeight());
        return Rect.intersects(PandouRect, fruitRect);
    }

    public boolean isOutOfScreen() {
        return y > screenHeight;
    }
}
