package com.example.pandouland;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    // Variable du jeu
    private GameThread thread;
    private Pandou Pandou;
    private List<Fruit> fruits;
    private int screenWidth, screenHeight;
    private int score;
    private long gameTime;
    private boolean gameOver;  // Variable pour savoir si le jeu est terminé
    private Paint endGamePaint;  // Pour dessiner le texte de fin


    private Paint scorePaint;
    private Bitmap backgroundBitmap; // Image de fond

    private long lastFruitTime = 0; // Temps où le dernier fruit est apparu
    private static final int FRUIT_INTERVAL = 2000; // Intervalle de 2000ms (2 secondes)

    private static final int GAME_DURATION = 30000; // Durée totale du jeu en millisecondes (60 secondes)
    private Paint timerPaint; // Pour dessiner le cercle du timer
    private RectF timerRect; // Pour délimiter le cadre du cercle

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);

        score = 0;
        gameTime = System.currentTimeMillis();
        gameOver = false;

        // Affichage du score
        scorePaint = new Paint();
        scorePaint.setTextSize(60);
        scorePaint.setColor(0xFFFFFFFF); // Blanc

        // Initialisation de la peinture pour le timer circulaire
        timerPaint = new Paint();
        timerPaint.setColor(0xFFFF0000); // Rouge
        timerPaint.setStyle(Paint.Style.STROKE); // Pour dessiner uniquement le contour
        timerPaint.setStrokeWidth(20); // Largeur de la bordure

        // Initialisation de la zone où le cercle sera dessiné
        timerRect = new RectF(screenWidth - 300, 50, screenWidth - 50, 300);  // A ajuster si nécessaire

        // Dessiner un remplissage complet pour tester la visibilité du cercle
        timerPaint.setStyle(Paint.Style.FILL);
        timerPaint.setColor(0x88FF0000); // Rouge transparent pour mieux voir

        // Initialisation de la peinture pour le texte de fin
        endGamePaint = new Paint();
        endGamePaint.setTextSize(80);
        endGamePaint.setColor(0xFFFFFFFF);  // Blanc
        endGamePaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenWidth = getWidth();
        screenHeight = getHeight();

        // Charger et redimensionner l'image de fond pour qu'elle corresponde à la taille de l'écran
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.game_background);
        backgroundBitmap = Bitmap.createScaledBitmap(originalBitmap, screenWidth, screenHeight, false);

        // Crée le Pandou
        Pandou = new Pandou(getContext(), screenWidth, screenHeight);

        // Crée une liste de fruits
        fruits = new ArrayList<>();
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Pandou.setPosition((int) event.getX());
        }
        return true;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();

        // Vérifie si le jeu est encore en cours
        if(!gameOver) {
            // On veut que les fruits continuent à apparaître pendant les 60 premières secondes
            if (currentTime - gameTime < GAME_DURATION) {
                // Ajoute un fruit tous les 2 s
                if (currentTime - lastFruitTime > FRUIT_INTERVAL) {
                    fruits.add(new Fruit(getContext(), screenWidth, screenHeight));
                    lastFruitTime = currentTime; // Met à jour le temps de l'apparition du dernier fruit
                }

                // Mise à jour des fruits et score
                for (int i = 0; i < fruits.size(); i++) {
                    Fruit fruit = fruits.get(i);
                    fruit.update();
                    if (fruit.isCaughtBy(Pandou)) {
                        score++;
                        fruits.remove(fruit);
                    } else if (fruit.isOutOfScreen()) {
                        fruits.remove(fruit);
                    }
                }
            }
            else {
                // Le jeu est terminé
                gameOver = true;
                // debug // addPandouCoins(score);
            }
        }
    }

    /*private void endGame(int finalScore) {
        // Retourner le score à l'activité principale
        Intent resultIntent = new Intent();
        resultIntent.putExtra("finalScore", finalScore);
        setResult(RESULT_OK, resultIntent);
        finish(); // Termine l'activité du jeu et retourne à l'activité principale
    }*/


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas != null) {
            if(gameOver) {
                canvas.drawColor(0xFFFFA500); //Orange
                canvas.drawText("Score" + score,screenWidth / 2, screenHeight/2, endGamePaint);
                canvas.drawText("Pandou Coins gagnés : " + score, screenWidth /2, screenHeight/2 + 100, endGamePaint);
            }
            else {
                // Dessine l'image de fond
                canvas.drawBitmap(backgroundBitmap, 0, 0, null);

                // Dessine le panda roux
                Pandou.draw(canvas);

                // Dessine les fruits
                for (Fruit fruit : fruits) {
                    fruit.draw(canvas);
                }

                // Dessine le score
                canvas.drawText("Score: " + score, 50, 100, scorePaint);

                // Dessiner le timer circulaire
                long currentTime = System.currentTimeMillis(); //Calculer le temps écoulé
                long elapsedTime = currentTime - gameTime;
                float sweepAngle = (elapsedTime / (float) GAME_DURATION) * 360; //// Calculer la progression en degrés (0 à 360) pour le cercle
                canvas.drawArc(timerRect, -90, sweepAngle, true, timerPaint);
            }
        }
    }
}
