package com.example.pandouland;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.pandouland.ui.game.FlappyGameView;

public class GameActivity extends AppCompatActivity {

    public static final String GAME_TYPE_KEY = "game_type";
    public static final String GAME_TYPE_CATCH = "catch_game";
    public static final String GAME_TYPE_FLAPPY = "flappy_game";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Récupère le type de jeu depuis les extras
        String gameType = getIntent().getStringExtra(GAME_TYPE_KEY);

        if (GAME_TYPE_CATCH.equals(gameType)) {
            // Affiche la vue pour le jeu de saut
            setContentView(new GameView(this));
        } else if (GAME_TYPE_FLAPPY.equals(gameType)) {
            // Affiche la vue pour le jeu Flappy
            setContentView(new FlappyGameView(this));
        } else {
            // Si aucun type n'est spécifié, affiche par défaut une erreur ou une vue neutre
            throw new IllegalArgumentException("Invalid game type provided");
        }
    }
}