package com.example.pandouland.ui.game;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pandouland.GameActivity;
import com.example.pandouland.MainActivity;
import com.example.pandouland.R;
import com.example.pandouland.databinding.FragmentGameBinding;
import com.example.pandouland.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class GameFragment extends Fragment {
    // bind
    private FragmentGameBinding binding;

    // Video game
    public static final int GAME_REQUEST_CODE = 1;

    // Economie
    private TextView pandouCoinsText;
    private MainActivity mainActivity;

    // États du panda roux
    private int hunger = 100; // 100 = plein, 0 = affamé
    private int happiness = 100; // 100 = très heureux, 0 = malheureux
    private int energy = 100; // 100 = plein d'énergie, 0 = fatigué

    // Timer pour dégrader l'état du panda avec le temps
    private Handler handler = new Handler();
    private Runnable pandaRunnable;

    // Timer pour le mode sleep
    private Runnable energyRegeneration;
    private boolean isSleeping = false;

    // HashMap pour stocker le type de fruit et leur quantité
    private HashMap<String, Integer> inventory;

    // Liste fictive des fruits que peut manger le panda
    String[] my_fruits = {"Cerise", "Pomme", "Pêche", "Bambou"};

    // UI Elements
    private ProgressBar hungerBar, happinessBar, energyBar;
    private TextView statusText;
    private ImageView pandaImage;
    private Button playButton, sleepButton;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GameViewModel gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        binding = FragmentGameBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // TO DELETE
        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Initialisation de l'inventaire avec quelques fruits
        inventory = new HashMap<>();
        inventory.put("Cerise", 5);
        inventory.put("Pomme", 2);
        inventory.put("Pêche", 1);
        inventory.put("Bambou", 0);

        // Lier les éléments de l'interface utilisateur
        hungerBar = root.findViewById(R.id.hungerBar);
        happinessBar = root.findViewById(R.id.happinessBar);
        energyBar = root.findViewById(R.id.energyBar);
        statusText = root.findViewById(R.id.statusText);
        pandaImage = root.findViewById(R.id.pandaImage);
        ImageView inventoryButton = root.findViewById(R.id.inventoryButton);
        playButton = root.findViewById(R.id.playButton);
        sleepButton = root.findViewById(R.id.sleepButton);

        // Démarrer le cycle du panda roux
        startPandaLifeCycle();

        // ------------- Actions des boutons ----------------

        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher le dialog d'inventaire
                showInventoryDialog();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playWithPanda();

                // Rediriger vers l'activité de jeu
                Intent gameIntent = new Intent(requireContext(), GameActivity.class);
                startActivityForResult(gameIntent, GAME_REQUEST_CODE);
            }
        });

        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePandaSleepMode();
            }
        });

        // Définir le Runnable pour régénérer l'énergie
        energyRegeneration = new Runnable() {
            @Override
            public void run() {
                if (isSleeping && energy < 100) {  // Régénération uniquement si le panda est en sommeil
                    energy += 1;
                    energyBar.setProgress(energy);
                    if (energy == 100) {
                        Toast.makeText(requireContext(), "Énergie maximale atteinte !", Toast.LENGTH_SHORT).show();
                    } else {
                        handler.postDelayed(this, 5000); // Appeler toutes les 5 secondes
                    }
                }
            }
        };
        // Récupérer l'instance de MainActivity
        mainActivity = (MainActivity) requireActivity();

        // Obtenir le TextView de GameFragment pour afficher les coins
        pandouCoinsText = root.findViewById(R.id.pandouCoinsText);

        // Afficher le nombre de Pandou Coins depuis MainActivity
        displayPandouCoins();

        return root;
    }

    // Démarrage du cycle de vie du panda (baisse des états toutes les 10 secondes)
    private void startPandaLifeCycle() {
        pandaRunnable = new Runnable() {
            @Override
            public void run() {
                // Réduire les états avec le temps quand évéillé
                if(!isSleeping) {
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
                    handler.postDelayed(this, 15000);
                }
            }
        };

        handler.post(pandaRunnable);
    }

    // Méthode pour afficher le Dialog de l'inventaire
    private void showInventoryDialog() {
        // Créer un nouveau Dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_inventory);

        // Référence à la ListView dans le dialog
        ListView inventoryList = dialog.findViewById(R.id.inventoryList);

        // Créer une liste qui affichera chaque fruit avec sa quantité
        ArrayList<String> fruitList = new ArrayList<>();
        for (String fruit : inventory.keySet()) {
            fruitList.add(fruit + " : x" + inventory.get(fruit));
        }

        // Adapter pour afficher les fruits et leurs quantités dans la ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, fruitList);
        inventoryList.setAdapter(adapter);

        // Gérer le clic sur un élément de la liste
        inventoryList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFruit = (String) parent.getItemAtPosition(position);
            String fruitName = selectedFruit.split(" :")[0]; // Obtenir juste le nom du fruit

            if (inventory.get(fruitName) > 0) {
                // Si l'utilisateur a des fruits de ce type, on peut nourrir le panda
                feedPanda(fruitName);

                // Réafficher l'inventaire mis à jour
                updateInventoryList(adapter);

                Toast.makeText(requireContext(), "Le panda a mangé : " + fruitName, Toast.LENGTH_SHORT).show();
            } else {
                // Si pas de fruit disponible
                Toast.makeText(requireContext(), "Pas assez de " + fruitName + " pour nourrir le panda", Toast.LENGTH_SHORT).show();
            }
        });

        // Afficher le dialog
        dialog.show();
    }

    // Méthode pour mettre à jour l'inventaire affiché dans la liste après une modification
    private void updateInventoryList(ArrayAdapter<String> adapter) {
        ArrayList<String> updatedFruitList = new ArrayList<>();
        for (String fruit : inventory.keySet()) {
            updatedFruitList.add(fruit + " : x" + inventory.get(fruit));
        }
        adapter.clear();
        adapter.addAll(updatedFruitList);
        adapter.notifyDataSetChanged();
    }

    // Méthode pour afficher les Pandou Coins en utilisant le MainActivity
    private void displayPandouCoins() {
        if (mainActivity != null) {
            int pandouCoins = mainActivity.getPandouCoins();
            pandouCoinsText.setText("         : " + pandouCoins);
        }
    }

    // Nourrir le panda roux
    private void feedPanda(String foodItem) {
        if (inventory.containsKey(foodItem)) {
            // Consomme le fruit
            inventory.put(foodItem, inventory.get(foodItem) - 1);

            // Afficher un message pour dire que le panda a mangé le fruit
            Toast.makeText(requireContext(), "Le panda a mangé : " + foodItem, Toast.LENGTH_SHORT).show();

            // Attribue les valeurs nutritionnelles
            switch (foodItem) {
                case "Cerise":
                    hunger += 10; // Nourriture qui remplit modérément
                    break;
                case "Pomme":
                    hunger += 25; // Nourriture qui remplit plus
                    break;
                case "Pêche":
                    hunger += 40; // Nourriture qui remplit plus
                    break;
                case "Bambou":
                    hunger += 100;
                    break;
            }

            if (hunger > 100) hunger = 100; // Limiter la barre de faim à 100

            updateUI(); // Mise à jour de l'interface utilisateur
        } else {
            // Afficher un message si l'item n'est pas dans l'inventaire
            Toast.makeText(requireContext(), "Nourriture non disponible dans l'inventaire", Toast.LENGTH_SHORT).show();
        }
    }

    // TO DO : Méthode pour ajouter des fruits dans l'inventaire (peut être appelée lorsque le joueur en ramasse)
    public void addFruit(String fruitName, int amount) {
        if (inventory.containsKey(fruitName)) {
            inventory.put(fruitName, inventory.get(fruitName) + amount);
        } else {
            inventory.put(fruitName, amount);
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

    // Méthode pour activer/désactiver le mode sommeil
    private void togglePandaSleepMode() {
        if (isSleeping) {
            // Réveille le panda
            isSleeping = false;
            pandaImage.setImageResource(R.drawable.redpanda_happy);
            sleepButton.setText("Dormir");
            Toast.makeText(requireContext(), "Le panda est réveillé !", Toast.LENGTH_SHORT).show();

            // Arrête la régénération d'énergie
            handler.removeCallbacks(energyRegeneration);
        } else {
            // Active le mode sommeil
            isSleeping = true;
            pandaImage.setImageResource(R.drawable.redpanda_sleeping);
            sleepButton.setText("Réveiller");
            Toast.makeText(requireContext(), "Le panda dort...", Toast.LENGTH_SHORT).show();

            // Commence la régénération d'énergie
            handler.postDelayed(energyRegeneration, 5000); // Démarre après 5 secondes
        }
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
        if (hunger < 20) {
            pandaImage.setImageResource(R.drawable.redpanda_hungry);
        } else if(energy < 20) {
            pandaImage.setImageResource(R.drawable.redpanda_sleepy); // Image d'un panda endormi
        } else if (happiness < 20) {
            pandaImage.setImageResource(R.drawable.redpanda_sad); // Panda triste
        } else {
            pandaImage.setImageResource(R.drawable.redpanda_happy); // Panda heureux
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().requestFocus(); // Récupère le focus pour le fragment
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}