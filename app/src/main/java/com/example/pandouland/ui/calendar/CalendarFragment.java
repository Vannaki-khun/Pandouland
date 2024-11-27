package com.example.pandouland.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.pandouland.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashMap;
import java.util.HashSet;

public class CalendarFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private HashMap<CalendarDay, String> notesMap = new HashMap<>();
    private HashSet<CalendarDay> notedDates = new HashSet<>();
    private Button addNoteButton;
    private TextView selectedDateNoteTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = rootView.findViewById(R.id.calendarView);
        addNoteButton = rootView.findViewById(R.id.add_note_button);
        selectedDateNoteTextView = rootView.findViewById(R.id.selected_date_note_text_view);

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        calendarView.addDecorator(new NoteDecorator(notedDates, this));

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (notesMap.containsKey(date)) {
                selectedDateNoteTextView.setText("Note : " + notesMap.get(date));
            } else {
                selectedDateNoteTextView.setText("Aucune note pour cette date.");
            }
        });

        addNoteButton.setOnClickListener(v -> {
            CalendarDay selectedDate = calendarView.getSelectedDate();
            if (selectedDate != null) {
                showNoteDialog(selectedDate);
            } else {
                Toast.makeText(getContext(), "Veuillez sélectionner une date", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void showNoteDialog(CalendarDay date) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_note, null);

        TextView dateTextView = dialogView.findViewById(R.id.dateTextView);
        EditText noteEditText = dialogView.findViewById(R.id.noteEditText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button closeButton = dialogView.findViewById(R.id.closeButton);

        dateTextView.setText("Date : " + date.toString());
        noteEditText.setText(notesMap.getOrDefault(date, ""));

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        saveButton.setOnClickListener(v -> {
            String note = noteEditText.getText().toString();
            if (!note.isEmpty()) {
                notesMap.put(date, note);
                notedDates.add(date);
                calendarView.invalidateDecorators();
                selectedDateNoteTextView.setText("Note : " + note);
                Toast.makeText(getContext(), "Note enregistrée pour " + date, Toast.LENGTH_SHORT).show();
            } else {
                notesMap.remove(date);
                notedDates.remove(date);
                calendarView.invalidateDecorators();
                selectedDateNoteTextView.setText("Aucune note pour cette date.");
                Toast.makeText(getContext(), "Note supprimée pour " + date, Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private static class NoteDecorator implements DayViewDecorator {
        private final HashSet<CalendarDay> dates;
        private final Fragment fragment;

        public NoteDecorator(HashSet<CalendarDay> dates, Fragment fragment) {
            this.dates = dates;
            this.fragment = fragment;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(ContextCompat.getDrawable(fragment.requireContext(), R.drawable.circle_red));
        }
    }
}
