package com.example.pandouland.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pandouland.ui.healthcare.HealthcareFragment;
import com.example.pandouland.ui.game.GameFragment;

public class FragmentAdapter extends FragmentStateAdapter  {
    // Constructor for the adapter
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HealthcareFragment();
            case 1:
                return new GameFragment();
                //return new PandagochiFragment();
            case 2:
                //return new HomeFragment();
            case 3:
                //return new CalendarFragment();
            case 4:
                //return new BudgetFragment();
            default:
                return new GameFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5; // 5 fragments
    }
}

