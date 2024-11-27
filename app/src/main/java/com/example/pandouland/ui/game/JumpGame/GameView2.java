package com.example.pandouland.ui.game.JumpGame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.pandouland.GameThread;

import java.util.ArrayList;


public class GameView2 extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;
    private boolean isGameRunning = false;
    private boolean isGameWon = false;
    private long gameStartTime;
    private static final int GAME_DURATION = 30000; // 30 secondes
    private Player player;
    private ArrayList<Obstacle> obstacles;
    private Runnable onGameEnd;

    public GameView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        player = new Player();
        obstacles = new ArrayList<>();
    }

    public void startGame(Runnable onGameEnd) {
        this.onGameEnd = onGameEnd;
        isGameRunning = true;
        isGameWon = true; // Assume the player wins unless they hit an obstacle
        gameStartTime = System.currentTimeMillis();
        obstacles.clear();

        if (gameThread == null) {
            //gameThread = new GameThread(getHolder(), this);
            gameThread.setRunning(true);
            gameThread.start();
        }
    }

    public boolean isGameWon() {
        return isGameWon;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Placeholder
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Placeholder
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (gameThread != null) {
            gameThread.setRunning(false);
            gameThread = null;
        }
    }

    public void update() {
        if (!isGameRunning) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - gameStartTime > GAME_DURATION) {
            // Fin du jeu
            isGameRunning = false;
            onGameEnd.run();
            return;
        }

        player.update();
        for (Obstacle obstacle : obstacles) {
            obstacle.update();
            if (player.collidesWith(obstacle)) {
                isGameRunning = false;
                isGameWon = false;
                onGameEnd.run();
                return;
            }
        }

        if (Math.random() < 0.02) { // Générer des obstacles aléatoires
            obstacles.add(new Obstacle(getWidth(), getHeight()));
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas == null) return;

        canvas.drawColor(Color.WHITE); // Fond blanc
        player.draw(canvas);
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(canvas);
        }
    }
}
