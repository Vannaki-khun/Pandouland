package com.example.pandouland.ui.game.JumpGame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Obstacle {
    private int x, y;
    private static final int SPEED = 10;
    public static final int OBSTACLE_WIDTH = 50;
    public static final int OBSTACLE_HEIGHT = 50;

    public Obstacle(int screenWidth, int screenHeight) {
        x = screenWidth;
        y = screenHeight - OBSTACLE_HEIGHT - 50; // Position au sol
    }

    public void update() {
        x -= SPEED;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawRect(x, y, x + OBSTACLE_WIDTH, y + OBSTACLE_HEIGHT, paint);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
