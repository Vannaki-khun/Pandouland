package com.example.pandouland.ui.game.JumpGame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Player {
    private int x, y;
    private int velocityY;
    private static final int GRAVITY = 2;
    private static final int JUMP_STRENGTH = -30;
    private static final int PLAYER_SIZE = 50;

    public Player() {
        x = 100;
        y = 300;
        velocityY = 0;
    }

    public void jump() {
        velocityY = JUMP_STRENGTH;
    }

    public void update() {
        velocityY += GRAVITY;
        y += velocityY;

        if (y > 300) { // Sol fictif
            y = 300;
            velocityY = 0;
        }
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawRect(x, y, x + PLAYER_SIZE, y + PLAYER_SIZE, paint);
    }

    public boolean collidesWith(Obstacle obstacle) {
        return x < obstacle.getX() + Obstacle.OBSTACLE_WIDTH &&
                x + PLAYER_SIZE > obstacle.getX() &&
                y < obstacle.getY() + Obstacle.OBSTACLE_HEIGHT &&
                y + PLAYER_SIZE > obstacle.getY();
    }
}
