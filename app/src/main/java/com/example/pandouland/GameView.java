package com.example.pandouland;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private MainActivity mainActivity;

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

    // Bouton de retour au menu principal
    private RectF buttonRect;
    private Paint buttonPaint;
    private Paint buttonTextPaint;


    private long lastFruitTime = 0; // Temps où le dernier fruit est apparu
    private static final int FRUIT_INTERVAL = 2000; // Intervalle de 2000ms (2 secondes)
    private static final int GAME_DURATION = 30000; // Durée totale du jeu en millisecondes

    private Paint timerTextPaint; // Peinture pour le texte du timer
    private RectF timerRect; // Pour délimiter le cadre du cercle

    public GameView(Context context) {
        super(context);

        // Vérifiez si le contexte est une instance de MainActivity
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
        }

        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        score = 0;
        gameTime = System.currentTimeMillis();
        gameOver = false;

        // Affichage du score
        scorePaint = new Paint();
        scorePaint.setTextSize(60);
        scorePaint.setColor(0xFFFFFFFF); // Blanc

        // Initialisation de la peinture pour le texte du timer
        timerTextPaint = new Paint();
        timerTextPaint.setTextSize(60);
        timerTextPaint.setColor(0xFFFF0000); // Rouge
        timerTextPaint.setTextAlign(Paint.Align.RIGHT); // Aligné à droite

        // Initialisation de la zone où le cercle sera dessiné
        timerRect = new RectF(screenWidth - 300, 50, screenWidth - 50, 300);  // A ajuster si nécessaire

        // Initialisation de la peinture pour le texte de fin
        endGamePaint = new Paint();
        endGamePaint.setTextSize(80);
        endGamePaint.setColor(0xFFFFFFFF);  // Blanc
        endGamePaint.setTextAlign(Paint.Align.CENTER);

        // Paramètres pour le bouton de retour au menu principal
        buttonRect = new RectF();
        buttonPaint = new Paint();
        buttonPaint.setColor(0xFF008080); // Couleur du bouton (teal)

        buttonTextPaint = new Paint();
        buttonTextPaint.setTextSize(60);
        buttonTextPaint.setColor(0xFFFFFFFF); // Couleur du texte du bouton
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenWidth = getWidth();
        screenHeight = getHeight();

        // Charger et redimensionner l'image de fond pour qu'elle corresponde à la taille de l'écran
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.game_background);
        backgroundBitmap = Bitmap.createScaledBitmap(originalBitmap, screenWidth, screenHeight, false);


        Pandou = new Pandou(getContext(), screenWidth, screenHeight); // Crée le Pandou
        fruits = new ArrayList<>(); // Crée une liste de fruits
        thread.setRunning(true);
        thread.start();

        // Définir la position et la taille du bouton (centré dans l'écran)
        float buttonWidth = 400;
        float buttonHeight = 150;
        buttonRect.set((screenWidth - buttonWidth) / 2, (screenHeight / 2) + 200, (screenWidth + buttonWidth) / 2, (screenHeight / 2) + 200 + buttonHeight);
    }

    // GPT

    // Écouteur pour notifier la fin de jeu
    public interface GameViewListener {
        void onGameEnd(); // Appelle cette méthode quand le jeu se termine
    }

    private GameViewListener listener;

    public void setGameViewListener(GameViewListener listener) {
        this.listener = listener;
    }

    // GPT END


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
            if (!gameOver) {
                Pandou.setPosition((int) event.getX());
            }
        } else if (event.getAction() == MotionEvent.ACTION_DOWN && gameOver) {
            // Vérifie si l'utilisateur a cliqué dans la zone du bouton
            if (buttonRect.contains(event.getX(), event.getY())) {
                endGame(); // Retourne à MainActivity
            }
        }
        return true;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();

        // Vérifie si le jeu est encore en cours
        if(!gameOver) {
            // On veut que les fruits continuent à apparaître pendant la durée du jeu
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
            }
        }
    }

    private void endGame() {
        if (mainActivity != null) {
            mainActivity.setPandouCoins(mainActivity.getPandouCoins() + (score/2));
            // Message pour informer que les coins ont été sauvegardés
            Toast.makeText(this.getContext(), "Conversion de ton score en PandouCoins !", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this.getContext(), "ptit flop !", Toast.LENGTH_SHORT).show();
        }

        if (listener != null) {
            listener.onGameEnd();
        }

        // Retourner le score à l'activité principale
        Intent resultIntent = new Intent();
        resultIntent.putExtra("finalScore", score);
        ((GameActivity) getContext()).setResult(GameActivity.RESULT_OK, resultIntent);
        ((GameActivity) getContext()).finish(); // Termine l'activité du jeu
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas != null) {
            if(gameOver) {
                canvas.drawColor(0xFFFFA500); //Orange
                canvas.drawText("Score : " + score,screenWidth / 2, screenHeight/2, endGamePaint);
                canvas.drawText("Pandou Coins gagnés : " + score/2, screenWidth /2, screenHeight/2 + 100, endGamePaint);

                // Dessine le bouton de retour au menu
                canvas.drawRect(buttonRect, buttonPaint);
                canvas.drawText("Retour au menu", buttonRect.centerX(), buttonRect.centerY() + 20, buttonTextPaint);
            }
            else {
                // Dessine le déroulé du jeu (image de fond, panda roux et fruits)
                canvas.drawBitmap(backgroundBitmap, 0, 0, null); // Dessine l'image de fond
                Pandou.draw(canvas); // Dessine le panda roux
                for (Fruit fruit : fruits) {
                    fruit.draw(canvas);
                }

                // Dessine le score
                canvas.drawText("Score: " + score, 50, 100, scorePaint);

                // Récupération des timing pour l'affichage du compteur
                long currentTime = System.currentTimeMillis(); // Temps actuel
                long elapsedTime = currentTime - gameTime; // Temps écoulé
                long remainingTime = (GAME_DURATION - elapsedTime) / 1000; // Temps restant en secondes

                // Dessiner le temps restant
                String timerText = "Temps : " + Math.max(remainingTime, 0) + "s";
                canvas.drawText(timerText, screenWidth - 50, 100, timerTextPaint);
            }
        }
    }
}
