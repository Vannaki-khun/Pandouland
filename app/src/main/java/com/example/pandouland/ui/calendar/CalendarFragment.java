package com.example.pandouland.ui.calendar;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pandouland.R;
import com.example.pandouland.databinding.FragmentCalendarBinding;


public class CalendarFragment extends Fragment {
    private FragmentCalendarBinding binding;
    public CalendarFragment() {
        // Constructeur vide obligatoire
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate le layout du fragment


        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }
}
