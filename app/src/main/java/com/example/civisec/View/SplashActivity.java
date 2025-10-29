package com.example.civisec.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.civisec.Controller.AlertScheduler;
import com.example.civisec.Controller.Controller;
import com.example.civisec.R;
import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private Controller controller;
    private AlertScheduler alertScheduler;

    private Button userModeButton;
    private Button devModeButton;

    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                Log.d(TAG, "Permisos respondidos, navegando a MainActivity");
                startMainActivity();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        alertScheduler = new AlertScheduler();
        controller = new Controller(this);
        Button enterButton = findViewById(R.id.enter_button);
        enterButton.setOnClickListener(v -> checkAndRequestPermissions());

        addDevButtons();
    }

    private void addDevButtons() {
        userModeButton = findViewById(R.id.user_mode_button);
        devModeButton = findViewById(R.id.dev_mode_button);
        Button resetButton = findViewById(R.id.reset_button);

        userModeButton.setOnClickListener(v -> {
            controller.setDevMode(false);
            updateModeButtons();
            Toast.makeText(this, "Modo NORMAL activado: 1 min/unidad", Toast.LENGTH_SHORT).show();
        });

        devModeButton.setOnClickListener(v -> {
            controller.setDevMode(true);
            updateModeButtons();
            Toast.makeText(this, "Modo DEV activado: 3 seg/unidad", Toast.LENGTH_SHORT).show();
        });

        resetButton.setBackgroundColor(getResources().getColor(R.color.red_alert));
        resetButton.setTextColor(getResources().getColor(android.R.color.white));
        resetButton.setOnClickListener(v -> {
            controller.resetApp();
            alertScheduler.resetSchedule(this);
            Toast.makeText(this, "RESET COMPLETO. Reinicia la app para reprogramar la historia.", Toast.LENGTH_LONG).show();
            finish();
        });

        updateModeButtons();
    }

    private void updateModeButtons() {
        boolean isDevMode = controller.isDevMode();
        if (isDevMode) {
            devModeButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
            devModeButton.setTextColor(getResources().getColor(android.R.color.white));
            userModeButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            userModeButton.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            userModeButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            userModeButton.setTextColor(getResources().getColor(android.R.color.white));
            devModeButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            devModeButton.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    private void checkAndRequestPermissions() {
        ArrayList<String> permissionsToRequest = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permissionsToRequest.isEmpty()) {
            Log.d(TAG, "Todos los permisos concedidos");
            startMainActivity();
        } else {
            Log.d(TAG, "Solicitando permisos: " + permissionsToRequest);
            requestPermissionsLauncher.launch(permissionsToRequest.toArray(new String[0]));
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}