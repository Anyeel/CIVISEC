package com.example.civisec.View;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.civisec.Controller.AlertManager;
import com.example.civisec.Controller.Controller;
import com.example.civisec.R;
import java.util.ArrayList;


// Pantalla inicial de la app con controles de desarrollador

public class SplashActivity extends AppCompatActivity {

    private Controller controller;
    private Button botonUsuario, botonDev, botonReset;

    // Lanzador de solicitud de permisos
    private final ActivityResultLauncher<String[]> solicitarPermisos =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    resultado -> irAMainActivity());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        controller = new Controller(this);

        // Botón principal de entrada
        Button botonEntrar = findViewById(R.id.enter_button);
        botonEntrar.setOnClickListener(v -> verificarPermisos());

        // Botones de desarrollador
        configurarBotonesDesarrollador();
    }

     // Configura los botones de modo desarrollo
    private void configurarBotonesDesarrollador() {
        botonUsuario = findViewById(R.id.user_mode_button);
        botonDev = findViewById(R.id.dev_mode_button);
        botonReset = findViewById(R.id.reset_button);

        // Modo Usuario (1 minuto por evento)
        botonUsuario.setOnClickListener(v -> {
            controller.setModoDesarrollo(false);
            actualizarBotones();
            Toast.makeText(this, "Modo NORMAL: 1 min/evento", Toast.LENGTH_SHORT).show();
        });

        // Modo Desarrollo (3 segundos por evento)
        botonDev.setOnClickListener(v -> {
            controller.setModoDesarrollo(true);
            actualizarBotones();
            Toast.makeText(this, "Modo DEV: 3 seg/evento", Toast.LENGTH_SHORT).show();
        });

        // Reset completo
        botonReset.setOnClickListener(v -> {
            controller.reiniciarApp();
            new AlertManager().reiniciar(this);
            Toast.makeText(this, "RESET COMPLETO. Reinicia la app.", Toast.LENGTH_LONG).show();
            finish();
        });

        actualizarBotones();
    }

     // Actualiza el color de los botones según el modo activo

    private void actualizarBotones() {
        if (controller.isModoDesarrollo()) {
            // Modo desarrollo activo
            botonDev.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
            botonUsuario.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            // Modo usuario activo
            botonUsuario.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            botonDev.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

     // Verifica y solicita los permisos necesarios

    private void verificarPermisos() {
        ArrayList<String> permisosFaltantes = new ArrayList<>();

        // Bluetooth (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permisosFaltantes.add(Manifest.permission.BLUETOOTH_CONNECT);
        }

        // Notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisosFaltantes.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        // Ubicación
        permisosFaltantes.add(Manifest.permission.ACCESS_FINE_LOCATION);

        // Solicitar permisos o ir directamente a MainActivity
        if (permisosFaltantes.isEmpty()) {
            irAMainActivity();
        } else {
            solicitarPermisos.launch(permisosFaltantes.toArray(new String[0]));
        }
    }

    // Navega a la pantalla principal
    private void irAMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}