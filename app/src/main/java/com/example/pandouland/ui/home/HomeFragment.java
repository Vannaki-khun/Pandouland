package com.example.pandouland.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

// Pour le GIF
import android.widget.Toast;
import com.bumptech.glide.Glide;

import com.example.pandouland.GameActivity;
import com.example.pandouland.MainActivity;
import com.example.pandouland.R;

import androidx.core.content.ContextCompat;

public class HomeFragment extends Fragment implements SensorEventListener {

    private TextView timeTextView, temperatureTextView, weatherTextView, stepsTextView;
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "e1af8a29512be4179bf479b2f2cf07c4"; // Clé API
    private ProgressBar budgetProgressBar;
    private ImageView pandaImageView;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private boolean isCounterSensorAvailable;
    private int stepCount = 0;
    private Handler handler = new Handler();

    // Déclaration du lanceur de demande de permission
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission accordée
                    Toast.makeText(requireContext(), "Permission accordée !", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission refusée
                    Toast.makeText(requireContext(), "Permission refusée !", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vérification et demande de la permission ACTIVITY_RECOGNITION
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            // Demande la permission si elle n'est pas accordée
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
        }
        
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Charger le layout du fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialiser les vues
        timeTextView = rootView.findViewById(R.id.timeTextView);
        temperatureTextView = rootView.findViewById(R.id.temperatureTextView);
        weatherTextView = rootView.findViewById(R.id.weatherTextView);
        stepsTextView = rootView.findViewById(R.id.stepsTextView);
        ImageView gifImageView = rootView.findViewById(R.id.gifImageView);

        // Charger le GIF dans l'ImageView
        Glide.with(requireContext())
                .asGif()
                .load(R.drawable.hello) // Nom du GIF
                .into(gifImageView);

        // Ajouter un listener pour détecter un appui court
        gifImageView.setOnClickListener(view -> {
            // Afficher un Toast avec le message "Hello"
            Toast.makeText(requireContext(), "Coucou M. Benabou", Toast.LENGTH_SHORT).show();
        });

        // Initialiser le SensorManager
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);

        // Initialisation de Retrofit pour récupérer la météo
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherApiService apiService = retrofit.create(WeatherApiService.class);

        // Faire une requête pour obtenir la météo
        Call<WeatherResponse> call = apiService.getWeather("Paris", API_KEY, "metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();

                    String translatedDescription = translateWeatherDescription(weatherResponse.weather[0].description);

                    // Mise à jour de l'interface utilisateur
                    temperatureTextView.setText(String.format("%.1f°C", weatherResponse.main.temperature));
                    weatherTextView.setText(translatedDescription);
                } else {
                    Log.e("API_ERROR", "Erreur dans la réponse");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("API_ERROR", "Échec de la requête", t);
            }
        });

        // Vérifier et gérer les permissions pour l'ACTIVITY_RECOGNITION
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 100);
        }

        // Récupérer le capteur de pas
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            if (stepCounterSensor != null) {
                isCounterSensorAvailable = true;
                sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
            } else {
                isCounterSensorAvailable = false;
                stepsTextView.setText("Capteur de pas non disponible");
            }
        }

        // Mettre à jour l'heure
        updateTime();

        return rootView;
    }

    private void updateTime() {
        handler.postDelayed(() -> {
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            timeTextView.setText(currentTime);
            updateTime(); // Mise à jour en boucle
        }, 1000);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isCounterSensorAvailable) {
            stepCount = (int) event.values[0];
            stepsTextView.setText("Nombre de pas : " + stepCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isCounterSensorAvailable) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isCounterSensorAvailable) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    // Traduction des descriptions de météo
    private String translateWeatherDescription(String description) {
        HashMap<String, String> translations = new HashMap<>();
        translations.put("clear sky", "ciel dégagé");
        translations.put("few clouds", "quelques nuages");
        translations.put("scattered clouds", "nuages épars");
        translations.put("broken clouds", "nuages fragmentés");
        translations.put("shower rain", "pluie d'averses");
        translations.put("rain", "pluie");
        translations.put("thunderstorm", "orage");
        translations.put("snow", "neige");
        translations.put("mist", "brume");
        translations.put("overcast clouds", "nuages couverts");
        translations.put("light rain", "pluie légère");
        translations.put("light intensity drizzle", "bruine de faible intensité");
        translations.put("heavy rain", "pluie forte");
        translations.put("moderate rain", "pluie modérée");


        // Retourne la traduction ou l'original si non trouvé
        return translations.getOrDefault(description, description);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requireActivity().recreate(); // Relancer le fragment pour enregistrer les capteurs
        }
    }
}
