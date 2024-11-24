package com.example.pandouland.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandouland.R;
import com.example.pandouland.MainActivity;

import java.util.ArrayList;
import java.util.List;

import com.example.pandouland.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    private RecyclerView shopRecyclerView;
    private TextView coinsTextView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        coinsTextView = root.findViewById(R.id.coinsTextView);

        // Vérifie d'abord si l'activité est une instance de MainActivity
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            coinsTextView.setText("Pandou Coins: " + mainActivity.getPandouCoins());
        }

        shopRecyclerView = root.findViewById(R.id.shopRecyclerView);
        shopRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ShopItem> shopItems = new ArrayList<>();
        shopItems.add(new ShopItem("Cherry", 10, R.drawable.food_cherry));
        shopItems.add(new ShopItem("Apple", 15, R.drawable.food_apple));
        shopItems.add(new ShopItem("Peach", 20, R.drawable.food_peach));
        shopItems.add(new ShopItem("Bamboo", 30, R.drawable.food_bamboo));

        ShopAdapter adapter = new ShopAdapter(shopItems, getActivity());
        shopRecyclerView.setAdapter(adapter);

        return root;
    }
}