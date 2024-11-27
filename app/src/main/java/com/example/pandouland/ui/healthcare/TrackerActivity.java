package com.example.pandouland.ui.healthcare;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.pandouland.R;

public class TrackerActivity extends AppCompatActivity {

    private static final String TAG = "TrackerActivity";

    private TextView tvStepsValue, tvDurationValue, tvCaloriesValue;
    private ProgressCircleView circleSteps, circleDuration, circleCalories;
    private ImageView activityAnimation;

    private Button btnSetStepsGoal, btnSetDurationGoal, btnSetCaloriesGoal;

    private int stepGoal = 6000;
    private int durationGoal = 90; // en minutes
    private int caloriesGoal = 300;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int initialSteps = 0; // Nombre initial de pas
    private boolean isSensorInitialized = false;

    private int simulatedSteps = 0; // Pour suivre les pas simulés via ADB

    // BroadcastReceiver pour écouter les pas simulés via ADB
    public static class StepSimulationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (context instanceof TrackerActivity && intent != null) {
                TrackerActivity activity = (TrackerActivity) context;

                // Vérification du nom de l'action
                if ("com.example.app.MOTION_STEP".equals(intent.getAction())) {
                    int steps = intent.getIntExtra("steps", 0);

                    // Log pour confirmer la réception de l'intent
                    Log.d(TAG, "Intent reçu : steps = " + steps);

                    activity.handleSimulatedSteps(steps);
                } else {
                    Log.w(TAG, "Action inattendue : " + intent.getAction());
                }
            }
        }
    }

    private StepSimulationReceiver stepSimulationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        // Initialiser les vues
        tvStepsValue = findViewById(R.id.tv_steps_value);
        tvDurationValue = findViewById(R.id.tv_duration_value);
        tvCaloriesValue = findViewById(R.id.tv_calories_value);
        activityAnimation = findViewById(R.id.activity_animation);

        circleSteps = findViewById(R.id.circle_steps);
        circleDuration = findViewById(R.id.circle_duration);
        circleCalories = findViewById(R.id.circle_calories);

        btnSetStepsGoal = findViewById(R.id.btn_set_steps_goal);
        btnSetDurationGoal = findViewById(R.id.btn_set_duration_goal);
        btnSetCaloriesGoal = findViewById(R.id.btn_set_calories_goal);

        // Charger le GIF animé dans l'ImageView
        Glide.with(this)
                .asGif()
                .load(R.drawable.ic_panda_gif)
                .into(activityAnimation);

        // Initialiser le SensorManager pour récupérer les pas
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepSensor == null) {
            tvStepsValue.setText("Capteur de pas indisponible");
            Log.e(TAG, "Capteur TYPE_STEP_COUNTER non disponible sur cet appareil.");
        } else {
            sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_UI);
            Log.d(TAG, "Capteur TYPE_STEP_COUNTER enregistré avec succès.");
        }

        // Ajouter des écouteurs pour changer les objectifs
        btnSetStepsGoal.setOnClickListener(v -> showGoalInputDialog("Objectif de pas", stepGoal, value -> {
            stepGoal = value;
            updateUI(0, 0, 0);
        }));

        btnSetDurationGoal.setOnClickListener(v -> showGoalInputDialog("Objectif de durée (min)", durationGoal, value -> {
            durationGoal = value;
            updateUI(0, 0, 0);
        }));

        btnSetCaloriesGoal.setOnClickListener(v -> showGoalInputDialog("Objectif de calories (kcal)", caloriesGoal, value -> {
            caloriesGoal = value;
            updateUI(0, 0, 0);
        }));

        // Initialiser le BroadcastReceiver
        stepSimulationReceiver = new StepSimulationReceiver();

        // Mettre à jour l'interface utilisateur avec des valeurs initiales
        updateUI(0, 0, 0);
    }

    private final SensorEventListener stepListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int steps = (int) event.values[0];

            if (!isSensorInitialized) {
                initialSteps = steps;
                isSensorInitialized = true;
            }

            int currentSteps = steps - initialSteps;
            int caloriesBurned = (int) (currentSteps * 0.04);
            int activityDuration = currentSteps / 100;

            updateUI(currentSteps, activityDuration, caloriesBurned);

            // Log pour déboguer le capteur
            Log.d(TAG, "Capteur de pas : currentSteps = " + currentSteps);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Pas nécessaire pour ce capteur
        }
    };

    private void updateUI(int steps, int duration, int calories) {
        tvStepsValue.setText(steps + " / " + stepGoal);
        tvDurationValue.setText(duration + " / " + durationGoal + " min");
        tvCaloriesValue.setText(calories + " / " + caloriesGoal + " kcal");

        circleSteps.setProgress((int) ((float) steps / stepGoal * 100));
        circleDuration.setProgress((int) ((float) duration / durationGoal * 100));
        circleCalories.setProgress((int) ((float) calories / caloriesGoal * 100));

        // Log pour confirmer la mise à jour de l'UI
        Log.d(TAG, "UI mise à jour : steps = " + steps + ", duration = " + duration + ", calories = " + calories);
    }

    private void handleSimulatedSteps(int steps) {
        simulatedSteps += steps;

        // Log pour déboguer les pas simulés
        Log.d(TAG, "handleSimulatedSteps : simulatedSteps = " + simulatedSteps);

        int caloriesBurned = (int) (simulatedSteps * 0.04);
        int activityDuration = simulatedSteps / 100;

        updateUI(simulatedSteps, activityDuration, caloriesBurned);
    }

    private void showGoalInputDialog(String title, int currentValue, GoalInputListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Valeur actuelle : " + currentValue);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String inputValue = input.getText().toString();
            if (!inputValue.isEmpty()) {
                int newValue = Integer.parseInt(inputValue);
                listener.onGoalChanged(newValue);
            }
        });
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stepSensor != null) {
            sensorManager.unregisterListener(stepListener);
        }

        // Désenregistrement sécurisé du BroadcastReceiver
        try {
            unregisterReceiver(stepSimulationReceiver);
            Log.d(TAG, "BroadcastReceiver désenregistré.");
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "BroadcastReceiver non enregistré : " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepSensor != null) {
            sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }

        // Enregistrement sécurisé du BroadcastReceiver
        IntentFilter filter = new IntentFilter("com.example.app.MOTION_STEP");
        registerReceiver(stepSimulationReceiver, filter, Context.RECEIVER_EXPORTED);
        Log.d(TAG, "BroadcastReceiver enregistré.");
    }

    private interface GoalInputListener {
        void onGoalChanged(int value);
    }
}
