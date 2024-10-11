package com.example.pandouland;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pandouland.databinding.ActivityMainBinding;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    // Economie de l'appli
    private int pandouCoins = 0; // Pandou Coins de l'utilisateur
    private TextView pandouCoinsText;
    private SharedPreferences sharedPreferences; // Pour stocker les coins

    // États du panda roux
    private int hunger = 100; // 100 = plein, 0 = affamé
    private int happiness = 100; // 100 = très heureux, 0 = malheureux
    private int energy = 100; // 100 = plein d'énergie, 0 = fatigué

    // Timer pour dégrader l'état du panda avec le temps
    private Handler handler = new Handler();
    private Runnable pandaRunnable;

    // Liste pour stocker les items de nourriture
    private ArrayList<String> inventory = new ArrayList<>();

    // UI Elements
    private ProgressBar hungerBar, happinessBar, energyBar;
    private TextView statusText;
    private ImageView pandaImage;
    private Button feedButton, playButton, sleepButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Initialiser SharedPreferences
        sharedPreferences = getSharedPreferences("PANDA_PREFS", MODE_PRIVATE);
        // Récupérer les Pandou Coins sauvegardés
        pandouCoins = sharedPreferences.getInt("pandouCoins", 0);
        // Afficher les Pandou Coins
        pandouCoinsText = findViewById(R.id.pandouCoinsText);
        updatePandouCoinsDisplay();

        // Lier les éléments de l'interface utilisateur
        hungerBar = findViewById(R.id.hungerBar);
        happinessBar = findViewById(R.id.happinessBar);
        energyBar = findViewById(R.id.energyBar);
        statusText = findViewById(R.id.statusText);
        pandaImage = findViewById(R.id.pandaImage);
        feedButton = findViewById(R.id.feedButton);
        playButton = findViewById(R.id.playButton);
        sleepButton = findViewById(R.id.sleepButton);

        // Démarrer le cycle du panda roux
        startPandaLifeCycle();

        // Ajouter de la nourriture pour tester l'inventaire (plus tard, ce sera via un shop)
        inventory.add("Bambou");
        inventory.add("Fruits exotiques");

        // Actions des boutons
        feedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedPanda("Bambou");
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playWithPanda();

                // Rediriger vers l'activité de jeu
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePandaSleep();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // Méthode pour mettre à jour l'affichage des coins
    private void updatePandouCoinsDisplay() {
        pandouCoinsText.setText("Pandou Coins: " + pandouCoins);
    }

    // Méthode pour ajouter des coins
    private void addPandouCoins(int amount) {
        pandouCoins += amount;
        updatePandouCoinsDisplay();
    }

    // Démarrage du cycle de vie du panda (baisse des états toutes les 10 secondes)
    private void startPandaLifeCycle() {
        pandaRunnable = new Runnable() {
            @Override
            public void run() {
                // Réduire les états avec le temps
                hunger -= 2;
                happiness -= 1;
                energy -= 3;

                // Assure que les valeurs ne deviennent pas négatives
                if (hunger < 0) hunger = 0;
                if (happiness < 0) happiness = 0;
                if (energy < 0) energy = 0;

                // Mise à jour de l'interface utilisateur
                updateUI();

                // Si l'état est critique, afficher un message
                if (hunger <= 0 || happiness <= 0 || energy <= 0) {
                    statusText.setText("Le panda roux a besoin de votre attention !");
                } else {
                    statusText.setText("Le panda roux est en bonne forme !");
                }

                // Répéter la mise à jour toutes les 10 secondes
                handler.postDelayed(this, 10000);
            }
        };

        handler.post(pandaRunnable);
    }

    // Nourrir le panda roux
    /*private void feedPanda() {
        if (hunger < 100) {
            hunger += 20; // Augmenter la faim
            if (hunger > 100) hunger = 100; // Limiter la valeur à 100
        }
        updateUI();
    }*/
    private void feedPanda(String foodItem) {
        if (inventory.contains(foodItem)) {
            switch (foodItem) {
                case "Bambou":
                    hunger += 15; // Nourriture qui remplit modérément
                    break;
                case "Fruits exotiques":
                    hunger += 30; // Nourriture qui remplit plus
                    break;
            }

            if (hunger > 100) hunger = 100; // Limiter la barre de faim à 100

            inventory.remove(foodItem); // Retirer l'item après utilisation
            updateUI(); // Mise à jour de l'interface utilisateur
        } else {
            // Afficher un message si l'item n'est pas dans l'inventaire
            Toast.makeText(this, "Nourriture non disponible dans l'inventaire", Toast.LENGTH_SHORT).show();
        }
    }


    // Jouer avec le panda roux
    private void playWithPanda() {
        if (happiness < 100) {
            happiness += 30; // Augmenter le bonheur
            if (happiness > 100) happiness = 100; // Limiter la valeur à 100
        }
        energy -= 10; // Jouer fatigue le panda
        if (energy < 0) energy = 0;
        updateUI();
    }

    // Faire dormir le panda roux
    private void makePandaSleep() {
        energy += 50; // Augmenter l'énergie
        if (energy > 100) energy = 100; // Limiter la valeur à 100
        updateUI();
    }

    // Obtenir une couleur basée sur le niveau de progression
    private ColorStateList getProgressColor(int progress) {
        int color;
        if (progress >= 75) {
            color = Color.parseColor("#4CAF50"); // Vert pour un niveau élevé
        } else if (progress >= 50) {
            color = Color.parseColor("#FFEB3B"); // Jaune pour un niveau moyen
        } else if (progress >= 25) {
            color = Color.parseColor("#FF9800"); // Orange pour un niveau faible
        } else {
            color = Color.parseColor("#F44336"); // Rouge pour un niveau très bas
        }
        return ColorStateList.valueOf(color); // Retourne une ColorStateList
    }

    // Mettre à jour les barres de progression et l'interface utilisateur
    private void updateUI() {
        hungerBar.setProgress(hunger);
        happinessBar.setProgress(happiness);
        energyBar.setProgress(energy);

        // Appliquer les couleurs dynamiques en fonction des niveaux
        hungerBar.setProgressTintList(getProgressColor(hunger));
        happinessBar.setProgressTintList(getProgressColor(happiness));
        energyBar.setProgressTintList(getProgressColor(energy));

        // Mise à jour de l'image du panda (fatigue, bonheur, etc.)
        if (energy < 20) {
            pandaImage.setImageResource(R.drawable.redpanda_sleeping); // Image d'un panda endormi
        } else if (happiness < 20) {
            pandaImage.setImageResource(R.drawable.redpanda_sad); // Panda triste
        } else {
            pandaImage.setImageResource(R.drawable.redpanda_happy); // Panda heureux
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Sauvegarder les Pandou Coins dans SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("pandouCoins", pandouCoins);
        editor.apply(); // Sauvegarde asynchrone (non bloquante)

        // Message pour informer que les coins ont été sauvegardés
        Toast.makeText(this, "Pandou Coins sauvegardés", Toast.LENGTH_SHORT).show();

        // Arrêter le cycle de vie du panda lorsque l'activité est détruite
        handler.removeCallbacks(pandaRunnable);
    }
}