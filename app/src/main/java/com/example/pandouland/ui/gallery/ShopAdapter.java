package com.example.pandouland.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandouland.MainActivity;
import com.example.pandouland.R;
import com.example.pandouland.ui.gallery.ShopItem;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private List<ShopItem> items;
    private Context context;

    public ShopAdapter(List<ShopItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shop, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        ShopItem item = items.get(position);
        holder.itemName.setText(item.getName());
        holder.itemPrice.setText(item.getPrice() + " Coins");
        holder.itemImage.setImageResource(item.getImageResource());

        holder.itemView.setOnClickListener(v -> {
            // Vérifie que le contexte est bien une instance de MainActivity
            if (context instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) context;

                // Vérifie que l'utilisateur a assez de Pandou Coins
                if (mainActivity.getPandouCoins() >= item.getPrice()) {
                    // Déduit le coût de l'achat
                    mainActivity.setPandouCoins(mainActivity.getPandouCoins() - item.getPrice());

                    // Affiche un message de succès
                    Toast.makeText(context, item.getName() + " acheté!", Toast.LENGTH_SHORT).show();

                    // Met à jour l'affichage des Pandou Coins
                    TextView coinsTextView = ((MainActivity) context).findViewById(R.id.coinsTextView);
                    if (coinsTextView != null) {
                        coinsTextView.setText("Pandou Coins: " + mainActivity.getPandouCoins());
                    }
                } else {
                    // Message si l'utilisateur n'a pas assez de Pandou Coins
                    Toast.makeText(context, "Pas assez de Pandou Coins!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ShopViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice;
        ImageView itemImage;

        ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemImage = itemView.findViewById(R.id.itemImage);
        }
    }
}
