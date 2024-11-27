package com.example.pandouland.ui.game;;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FlappyGameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Thread gameThread;
    private boolean isPlaying = true;
    private int screenWidth, screenHeight;
    private Paint paint;
    private Rect bird;
    private int birdY, birdVelocity;
    private int gravity = 3;
    private int jumpPower = -25;
    private int obstacleX, obstacleWidth, obstacleHeight;
    private int obstacleSpeed = 10;

    public FlappyGameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        paint = new Paint();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenWidth = getWidth();
        screenHeight = getHeight();
        bird = new Rect(100, screenHeight / 2, 200, screenHeight / 2 + 100);
        birdY = bird.top;
        obstacleX = screenWidth;
        obstacleWidth = 200;
        obstacleHeight = screenHeight / 3;

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            try {
                Thread.sleep(16); // Environ 60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        // Mouvements de l'oiseau
        birdY += birdVelocity;
        birdVelocity += gravity;

        if (birdY + bird.height() > screenHeight || birdY < 0) {
            isPlaying = false;
        }

        bird.offsetTo(bird.left, birdY);

        // Mouvements des obstacles
        obstacleX -= obstacleSpeed;
        if (obstacleX + obstacleWidth < 0) {
            obstacleX = screenWidth;
        }

        // Vérifie les collisions
        if (Rect.intersects(bird, new Rect(obstacleX, screenHeight - obstacleHeight, obstacleX + obstacleWidth, screenHeight))) {
            isPlaying = false;
        }
    }

    private void draw() {
        Canvas canvas = getHolder().lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.CYAN); // Fond bleu clair
            paint.setColor(Color.YELLOW);
            canvas.drawRect(bird, paint); // Dessine l'oiseau
            paint.setColor(Color.GREEN);
            canvas.drawRect(obstacleX, screenHeight - obstacleHeight, obstacleX + obstacleWidth, screenHeight, paint); // Dessine les obstacles
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            birdVelocity = jumpPower;
        }
        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
