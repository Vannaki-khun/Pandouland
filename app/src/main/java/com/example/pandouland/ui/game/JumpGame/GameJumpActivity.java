package com.example.pandouland.ui.game.JumpGame;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pandouland.R;

public class GameJumpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamejump);

        // Ajoutez une interaction pour quitter via le bouton
        findViewById(R.id.exit_button).setOnClickListener(v -> finish());
    }
}
