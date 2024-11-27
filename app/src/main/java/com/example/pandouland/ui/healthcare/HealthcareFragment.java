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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.pandouland.R;

public class HealthcareFragment extends Fragment {

    private static final String TAG = "HealthcareFragment";

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

    private StepSimulationReceiver stepSimulationReceiver;
    private boolean isReceiverRegistered = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_healthcare, container, false);

        // Initialiser les vues
        tvStepsValue = rootView.findViewById(R.id.tv_steps_value);
        tvDurationValue = rootView.findViewById(R.id.tv_duration_value);
        tvCaloriesValue = rootView.findViewById(R.id.tv_calories_value);
        activityAnimation = rootView.findViewById(R.id.activity_animation);

        circleSteps = rootView.findViewById(R.id.circle_steps);
        circleDuration = rootView.findViewById(R.id.circle_duration);
        circleCalories = rootView.findViewById(R.id.circle_calories);

        btnSetStepsGoal = rootView.findViewById(R.id.btn_set_steps_goal);
        btnSetDurationGoal = rootView.findViewById(R.id.btn_set_duration_goal);
        btnSetCaloriesGoal = rootView.findViewById(R.id.btn_set_calories_goal);

        Glide.with(this).asGif().load(R.drawable.ic_panda_gif).into(activityAnimation);

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepSensor == null) {
            tvStepsValue.setText("Capteur de pas indisponible");
            Log.e(TAG, "Capteur TYPE_STEP_COUNTER non disponible.");
        } else {
            sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_UI);
            Log.d(TAG, "Capteur TYPE_STEP_COUNTER enregistré.");
        }

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

        stepSimulationReceiver = new StepSimulationReceiver(this);
        updateUI(0, 0, 0);

        return rootView;
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
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void updateUI(int steps, int duration, int calories) {
        Log.d(TAG, "Mise à jour de l'UI : Steps = " + steps + ", Duration = " + duration + ", Calories = " + calories);

        tvStepsValue.setText(steps + " / " + stepGoal);
        tvDurationValue.setText(duration + " / " + durationGoal + " min");
        tvCaloriesValue.setText(calories + " / " + caloriesGoal + " kcal");

        circleSteps.setProgress((int) ((float) steps / stepGoal * 100));
        circleDuration.setProgress((int) ((float) duration / durationGoal * 100));
        circleCalories.setProgress((int) ((float) calories / caloriesGoal * 100));
    }

    private void handleSimulatedSteps(int steps) {
        simulatedSteps += steps;
        int caloriesBurned = (int) (simulatedSteps * 0.04);
        int activityDuration = simulatedSteps / 100;

        Log.d(TAG, "Simulated steps = " + simulatedSteps);
        Log.d(TAG, "Calories brûlées = " + caloriesBurned);
        Log.d(TAG, "Durée de l'activité = " + activityDuration);

        updateUI(simulatedSteps, activityDuration, caloriesBurned);
    }

    private void showGoalInputDialog(String title, int currentValue, GoalInputListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Valeur actuelle : " + currentValue);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String inputValue = input.getText().toString().trim();
            if (!inputValue.isEmpty() && inputValue.matches("\\d+")) {
                int newValue = Integer.parseInt(inputValue);
                listener.onGoalChanged(newValue);
            } else {
                Toast.makeText(requireContext(), "Veuillez entrer un nombre valide", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private interface GoalInputListener {
        void onGoalChanged(int value);
    }

    public static class StepSimulationReceiver extends BroadcastReceiver {
        private final HealthcareFragment fragment;

        public StepSimulationReceiver(HealthcareFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "com.example.app.MOTION_STEP".equals(intent.getAction())) {
                try {
                    String stepsString = intent.getStringExtra("steps");
                    int steps = Integer.parseInt(stepsString); // Convertir la chaîne en entier
                    Log.d(TAG, "Intent reçu : steps = " + steps);
                    if (fragment != null) {
                        fragment.handleSimulatedSteps(steps);
                    }
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Erreur lors de la conversion des pas simulés : " + e.getMessage());
                }
            } else {
                Log.w(TAG, "Action inattendue ou intent null : " + (intent != null ? intent.getAction() : "null"));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (stepSensor != null) {
            sensorManager.unregisterListener(stepListener);
        }
        if (isReceiverRegistered) {
            try {
                requireContext().unregisterReceiver(stepSimulationReceiver);
                isReceiverRegistered = false;
                Log.d(TAG, "BroadcastReceiver désenregistré");
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "BroadcastReceiver non enregistré : " + e.getMessage());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stepSensor != null) {
            sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
        if (!isReceiverRegistered) {
            IntentFilter filter = new IntentFilter("com.example.app.MOTION_STEP");
            requireContext().registerReceiver(stepSimulationReceiver, filter, Context.RECEIVER_EXPORTED);
            isReceiverRegistered = true;
            Log.d(TAG, "BroadcastReceiver enregistré");
        }
    }
}
