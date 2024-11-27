package com.example.pandouland.ui.game.JumpGame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.example.pandouland.GameView;

public class GameThread2 extends Thread {
    private SurfaceHolder surfaceHolder;
    private GameView gameView2;
    private boolean running;

    public GameThread2(SurfaceHolder surfaceHolder, GameView gameView) {
        this.surfaceHolder = surfaceHolder;
        this.gameView2 = gameView2;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while (running) {
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    gameView2.update();
                    gameView2.draw(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
