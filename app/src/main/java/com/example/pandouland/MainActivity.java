package com.example.pandouland;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.example.pandouland.ui.gallery.GalleryFragment;
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
import java.util.HashMap;

import android.app.Dialog;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.pandouland.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    // Economie de l'appli : PANDOU COINS
    private int pandouCoins = 0; // Pandou Coins de l'utilisateur
    private TextView pandouCoinsText;
    private SharedPreferences sharedPreferences; // Pour stocker les coins
    private static final String PREFERENCES_NAME = "PANDA_PREFS";
    private static final String COINS_KEY = "pandouCoins";

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
        /* ----- Fin initialisation architecture application ----- */

        // Initialiser SharedPreferences
        sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        // Charger les Pandou Coins
        pandouCoins = sharedPreferences.getInt(COINS_KEY, 0);
        // Mettre à jour l'affichage initial des coins si nécessaire
        pandouCoinsText = findViewById(R.id.pandouCoinsText);
        updatePandouCoinsDisplay();
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
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    // Méthode pour mettre à jour l'affichage des coins
    private void updatePandouCoinsDisplay() {
        pandouCoinsText.setText("          : " + pandouCoins);
    }

    // Méthode pour ajouter des coins
    public void addPandouCoins(int amount) {
        pandouCoins += amount;
        updatePandouCoinsDisplay();
    }

    public void savePandouCoins(int coins) {
        SharedPreferences prefs = getSharedPreferences("pandouland_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("pandouCoins", coins);
        editor.apply(); // Sauvegarde de façon asynchrone
    }

    public int getPandouCoins() {
        return pandouCoins;
    }

    public void setPandouCoins(int coins) {
        pandouCoins = coins;
        sharedPreferences.edit().putInt(COINS_KEY, pandouCoins).apply();
        updatePandouCoinsDisplay();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == HomeFragment.GAME_REQUEST_CODE && resultCode == RESULT_OK) {
            int score = data.getIntExtra("finalScore", 0);
            pandouCoins += (score/2);  // Ajoute le score aux Pandou Coins

            SharedPreferences prefs = getSharedPreferences("PandouPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("pandouCoins", pandouCoins);
            editor.apply();

            updatePandouCoinsDisplay();
            savePandouCoins(pandouCoins);
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
        //handler.removeCallbacks(pandaRunnable);
        //handler.removeCallbacks(energyRegeneration);
    }
}