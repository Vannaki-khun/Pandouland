package com.example.pandouland;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.pandouland.ui.game.GameFragment;
import com.example.pandouland.databinding.ActivityMainBinding;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;

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

        /* ------- Initialisation architecture application ------- */


        // --------- Top bar ---------
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });

        // Configuration du DrawerLayout
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView; // Passing each menu ID as a set of Ids because each

        // Configuration de l'AppBarConfiguration                                                 // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_shop, R.id.nav_skin,
                R.id.nav_calendar, R.id.nav_game, R.id.nav_healthcare, R.id.nav_budget)
                .setOpenableLayout(drawer)
                .build();
        // Configuration du NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        // Associer le NavController avec la Toolbar (ActionBar)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        // Associer le NavigationView (Drawer) avec le NavController
        NavigationUI.setupWithNavController(navigationView, navController);

        // Configuration de la BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Associer la BottomNavigationView avec le NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        /* ----- Fin initialisation architecture application ----- */

        // Initialiser SharedPreferences
        sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        // Charger les Pandou Coins
        pandouCoins = sharedPreferences.getInt(COINS_KEY, 0);
        // Mettre à jour l'affichage initial des coins si nécessaire
        pandouCoinsText = findViewById(R.id.pandouCoinsText);
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

    private void loadFragment(Fragment fragment) {
        // Vérifiez si le fragment actuel est différent du nouveau fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return; // Ne chargez pas un fragment déjà affiché
        }

        // Remplacez le fragment actuel par le nouveau fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
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

        if (requestCode == GameFragment.GAME_REQUEST_CODE && resultCode == RESULT_OK) {
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