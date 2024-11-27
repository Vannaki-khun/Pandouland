package com.example.pandouland.ui.budget;

import android.os.Bundle;
import android.text.InputType;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.pandouland.R;
import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BudgetFragment extends Fragment {

    private TextView monthlyBudgetTextView;
    private TextView weeklyBudgetTextView;
    private Button setBudgetButton;
    private customPieChart pieChart; // Graphique personnalisé
    private Button addExpenseButton; // Bouton pour ajouter une dépense de sortie
    private Map<String, Float> expenseData; // Données pour le graphique
    private Button addChargesButton; // Bouton pour ajouter une charge
    private Map<String, Float> chargesData; // Données pour le graphique
    private Button addCreditButton; // Bouton pour ajouter une charge
    private Map<String, Float> creditData; // Données pour le graphique
    private Button addEpargButton; // Bouton pour ajouter une charge
    private Map<String, Float> epargData; // Données pour le graphique
    private float monthlyBudget; // Déclaration de la variable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflater le layout pour ce fragment
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        // Initialisation des vues
        ImageView gifView = view.findViewById(R.id.bottomGifView);
        Glide.with(this)
                .asGif()
                .load(R.drawable.duo)
                .into(gifView);

        monthlyBudgetTextView = view.findViewById(R.id.monthlyBudgetTextView);
        weeklyBudgetTextView = view.findViewById(R.id.weeklyBudgetTextView);

        // Initialisations des boutons et graphique
        pieChart = view.findViewById(R.id.pieChart);
        setBudgetButton = view.findViewById(R.id.setBudgetButton);
        addExpenseButton = view.findViewById(R.id.addExpenseButton);
        addChargesButton = view.findViewById(R.id.addChargesButton);
        addCreditButton = view.findViewById(R.id.addCreditButton);
        addEpargButton = view.findViewById(R.id.addEpargButton);

        // Ajout d'un écouteur sur le bouton
        setBudgetButton.setOnClickListener(v -> showBudgetInputDialog());
        // Initialisation des données du graphique
        expenseData = new HashMap<>();
        expenseData.put("Sorties", 0f);

        chargesData = new HashMap<>();
        chargesData.put("Charges", 0f);

        creditData = new HashMap<>();
        creditData.put("Crédit", 0f);

        epargData = new HashMap<>();
        epargData.put("Epargne", 0f);

        // Dessiner le graphique avec les données initiales
        updatePieChart();

        // Gestion du clic sur les boutons
        addExpenseButton.setOnClickListener(v -> showExpenseInputDialog());
        addChargesButton.setOnClickListener(v -> showChargesInputDialog());
        addCreditButton.setOnClickListener(v -> showCreditInputDialog());
        addEpargButton.setOnClickListener(v -> showEpargInputDialog());

        return view;
    }

    private void showBudgetInputDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Entrez votre budget mensuel")
                .setView(input)
                .setPositiveButton("Valider", (dialog, which) -> {
                    String inputText = input.getText().toString();

                    if (!inputText.isEmpty()) {
                        try {
                            this.monthlyBudget = Float.parseFloat(inputText);

                            // Calcul du budget hebdomadaire
                            double weeklyBudget = monthlyBudget / 4;

                            // Mettre à jour l'affichage
                            monthlyBudgetTextView.setText(String.format("Budget mensuel : %.2f €", monthlyBudget));
                            weeklyBudgetTextView.setText(String.format("Budget par semaine : %.2f €", weeklyBudget));

                        } catch (NumberFormatException e) {
                            Toast.makeText(requireContext(), "Veuillez entrer un nombre valide", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Champ vide, veuillez réessayer", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null);

        builder.show();
    }

    private void showExpenseInputDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Entrez le montant de vos dépenses (Sorties)")
                .setView(input)
                .setPositiveButton("Valider", (dialog, which) -> {
                    String inputText = input.getText().toString();
                    if (!inputText.isEmpty()) {
                        try {
                            float newExpense = Float.parseFloat(inputText);
                            float currentExpense = expenseData.get("Sorties");
                            expenseData.put("Sorties", currentExpense + newExpense);
                            updatePieChart();
                        } catch (NumberFormatException e) {
                            Toast.makeText(requireContext(), "Veuillez entrer un nombre valide", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Champ vide, veuillez réessayer", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null);

        builder.show();
    }

    private void showChargesInputDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Entrez le montant de vos charges")
                .setView(input)
                .setPositiveButton("Valider", (dialog, which) -> {
                    String inputText = input.getText().toString();
                    if (!inputText.isEmpty()) {
                        try {
                            float newCharges = Float.parseFloat(inputText);
                            float currentCharges = chargesData.get("Charges");
                            chargesData.put("Charges", currentCharges + newCharges);
                            updatePieChart();
                        } catch (NumberFormatException e) {
                            Toast.makeText(requireContext(), "Veuillez entrer un nombre valide", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Champ vide, veuillez réessayer", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null);

        builder.show();
    }

    private void showCreditInputDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Entrez le montant de votre crédit")
                .setView(input)
                .setPositiveButton("Valider", (dialog, which) -> {
                    String inputText = input.getText().toString();
                    if (!inputText.isEmpty()) {
                        try {
                            float newCredit = Float.parseFloat(inputText);
                            float currentCredit = creditData.get("Crédit");
                            creditData.put("Crédit", currentCredit + newCredit);
                            updatePieChart();
                        } catch (NumberFormatException e) {
                            Toast.makeText(requireContext(), "Veuillez entrer un nombre valide", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Champ vide, veuillez réessayer", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null);

        builder.show();
    }

    private void showEpargInputDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Entrez le montant de votre épargne")
                .setView(input)
                .setPositiveButton("Valider", (dialog, which) -> {
                    String inputText = input.getText().toString();
                    if (!inputText.isEmpty()) {
                        try {
                            float newEparg = Float.parseFloat(inputText);
                            float currentEparg = epargData.getOrDefault("Epargne", 0f);
                            epargData.put("Epargne", currentEparg + newEparg);
                            updatePieChart();
                        } catch (NumberFormatException e) {
                            Toast.makeText(requireContext(), "Veuillez entrer un nombre valide", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Champ vide, veuillez réessayer", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null);

        builder.show();
    }

    private void setPieChartData(Map<String, Float> data) {
        // Calculer la somme totale des valeurs
        float total = 0f;
        for (float value : data.values()) {
            total += value;
        }

        // Si le total est 0, éviter de diviser par zéro
        if (total == 0) {
            pieChart.clearData(); // Efface le graphique si pas de données
            return;
        }

        // Préparer les valeurs et labels pour le graphique
        int size = data.size();
        float[] values = new float[size];
        String[] labels = new String[size];
        int index = 0;

        for (Map.Entry<String, Float> entry : data.entrySet()) {
            values[index] = (entry.getValue() / total) * 100; // Convertir en pourcentage
            labels[index] = entry.getKey();
            index++;
        }

        // Mettre à jour le graphique avec les valeurs et labels
        pieChart.setData(values, labels); // Assurez-vous que votre méthode accepte les labels sous forme de tableau
    }

    private void updatePieChart() {
        if (monthlyBudget <= 0) {
            // Si le budget est invalide, effacez le graphique
            pieChart.clearData();
            return;
        }

        // Combinez les données des différentes catégories
        float sorties = expenseData.getOrDefault("Sorties", 0f);
        float charges = chargesData.getOrDefault("Charges", 0f);
        float credit = creditData.getOrDefault("Crédit", 0f);
        float epargne = epargData.getOrDefault("Epargne", 0f);

        // Calculez les pourcentages
        float[] values = {
                (sorties / monthlyBudget) * 100f,
                (charges / monthlyBudget) * 100f,
                (credit / monthlyBudget) * 100f,
                (epargne / monthlyBudget) * 100f
        };

        String[] labels = {"Sorties", "Charges", "Crédit", "Epargne"};

        // Mettez à jour le graphique
        pieChart.setData(values, labels);
    }
}
