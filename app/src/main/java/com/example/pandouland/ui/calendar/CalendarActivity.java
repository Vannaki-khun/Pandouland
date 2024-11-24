package com.example.pandouland.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import com.example.pandouland.R;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private HashMap<String, String> notesMap = new HashMap<>();
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Initialize the CalendarView
        calendarView = findViewById(R.id.calendarView);

        // Set a listener for date selection
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            showNoteDialog(selectedDate);
        });
    }

    /**
     * Method to show a dialog with the note for the selected date.
     */
    private void showNoteDialog(String date) {
        // Inflate the custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_note, null);

        // Initialize UI elements in the dialog
        TextView dateTextView = dialogView.findViewById(R.id.dateTextView);
        EditText noteEditText = dialogView.findViewById(R.id.noteEditText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button closeButton = dialogView.findViewById(R.id.closeButton);

        // Set the date and the note in the dialog
        dateTextView.setText("Date : " + date);
        noteEditText.setText(notesMap.getOrDefault(date, ""));

        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false) // Prevent closing by tapping outside
                .create();

        // Handle the Save button
        saveButton.setOnClickListener(v -> {
            String note = noteEditText.getText().toString();
            notesMap.put(date, note); // Save the note
            Toast.makeText(this, "Note saved for " + date, Toast.LENGTH_SHORT).show();
            dialog.dismiss(); // Close the dialog
        });

        // Handle the Close button
        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }
}
