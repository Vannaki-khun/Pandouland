package com.example.pandouland.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pandouland.R;
import com.example.pandouland.databinding.FragmentBudgetBinding;


public class BudgetFragment extends Fragment {
    private FragmentBudgetBinding binding;
    public BudgetFragment() {
        // Constructeur vide obligatoire
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate le layout du fragment


        return inflater.inflate(R.layout.fragment_budget, container, false);
    }
}
