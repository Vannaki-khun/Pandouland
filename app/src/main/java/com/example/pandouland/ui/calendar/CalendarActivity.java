package com.example.pandouland.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.pandouland.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashMap;
import java.util.HashSet;

public class CalendarActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private HashMap<CalendarDay, String> notesMap = new HashMap<>();
    private HashSet<CalendarDay> notedDates = new HashSet<>();
    private Button addNoteButton;
    private TextView selectedDateNoteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        addNoteButton = findViewById(R.id.add_note_button);
        selectedDateNoteTextView = findViewById(R.id.selected_date_note_text_view);

        // Configure the calendar
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);

        // Add the decorator for dates with notes
        calendarView.addDecorator(new NoteDecorator(notedDates, this));

        // Handle date selection
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (notesMap.containsKey(date)) {
                // Display the note for the selected date
                selectedDateNoteTextView.setText("Note : " + notesMap.get(date));
            } else {
                // No note for the selected date
                selectedDateNoteTextView.setText("Aucune note pour cette date.");
            }
        });

        // Button to add a note
        addNoteButton.setOnClickListener(v -> {
            CalendarDay selectedDate = calendarView.getSelectedDate();
            if (selectedDate != null) {
                showNoteDialog(selectedDate);
            } else {
                Toast.makeText(this, "Veuillez sélectionner une date", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNoteDialog(CalendarDay date) {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_note, null);

        TextView dateTextView = dialogView.findViewById(R.id.dateTextView);
        EditText noteEditText = dialogView.findViewById(R.id.noteEditText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button closeButton = dialogView.findViewById(R.id.closeButton);

        dateTextView.setText("Date : " + date.toString());
        noteEditText.setText(notesMap.getOrDefault(date, ""));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        saveButton.setOnClickListener(v -> {
            String note = noteEditText.getText().toString();
            if (!note.isEmpty()) {
                notesMap.put(date, note); // Save the note
                notedDates.add(date); // Add the date to the decorated dates
                calendarView.invalidateDecorators(); // Refresh decorators
                selectedDateNoteTextView.setText("Note : " + note); // Update the displayed note
                Toast.makeText(this, "Note enregistrée pour " + date, Toast.LENGTH_SHORT).show();
            } else {
                notesMap.remove(date); // Remove the note if empty
                notedDates.remove(date); // Remove the date from decorated dates
                calendarView.invalidateDecorators(); // Refresh decorators
                selectedDateNoteTextView.setText("Aucune note pour cette date."); // Clear the displayed note
                Toast.makeText(this, "Note supprimée pour " + date, Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private static class NoteDecorator implements DayViewDecorator {
        private final HashSet<CalendarDay> dates;
        private final CalendarActivity calendarActivity;

        public NoteDecorator(HashSet<CalendarDay> dates, CalendarActivity calendarActivity) {
            this.dates = dates;
            this.calendarActivity = calendarActivity;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            // Use the context from the activity
            view.setBackgroundDrawable(ContextCompat.getDrawable(calendarActivity, R.drawable.circle_red));
        }
    }
}
