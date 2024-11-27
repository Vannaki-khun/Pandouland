package com.example.pandouland.ui.game;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.example.pandouland.R;

public class SkinFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    public SkinFragment() {
        super(R.layout.fragment_skin); // Utilise le layout skin.xml
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Charger la vue pour ce fragment
        View view = inflater.inflate(R.layout.fragment_skin, container, false);

        // Identifier les images cliquables
        ImageView skinMinecraft = view.findViewById(R.id.skin_default);
        ImageView skinForest = view.findViewById(R.id.skin_snk);
        ImageView skinGalaxy = view.findViewById(R.id.skin_esiee);

        // Ajouter des listeners pour enregistrer les préférences de l'utilisateur
        skinMinecraft.setOnClickListener(v -> saveSelectedSkin(R.drawable.background_minecraft));
        skinForest.setOnClickListener(v -> saveSelectedSkin(R.drawable.background_snk));
        skinGalaxy.setOnClickListener(v -> saveSelectedSkin(R.drawable.background_esiee));

        return view;
    }

    private void saveSelectedSkin(int skinResourceId) {
        // Enregistrer l'image sélectionnée dans les SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("SELECTED_SKIN", skinResourceId);
        editor.apply();

        // Afficher un message
        Toast.makeText(getContext(), "Thème appliqué avec succès !", Toast.LENGTH_SHORT).show();
    }
}
