package com.example.civisec.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.civisec.R;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    // ÚNICO LANZADOR para solicitar TODOS los permisos necesarios a la vez.
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                // Este bloque se ejecuta DESPUÉS de que el usuario responde al diálogo de permisos.
                // No importa si los aceptó o no, la app debe continuar.
                Log.d("CIVISEC_PERMISSIONS", "Resultado de permisos recibido. Navegando a MainActivity.");
                startMainActivity();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button enterButton = findViewById(R.id.enter_button);

        enterButton.setOnClickListener(v -> {
            // Al pulsar "Entrar", comprobamos los permisos y los pedimos si es necesario.
            checkAndRequestPermissions();
        });
    }

    private void checkAndRequestPermissions() {
        // 1. Crear una lista de los permisos que vamos a necesitar pedir.
        ArrayList<String> permissionsToRequest = new ArrayList<>();

        // 2. Comprobar el permiso de BLUETOOTH_CONNECT (solo para Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }

        // 3. Comprobar el permiso de POST_NOTIFICATIONS (solo para Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        // 4. Decidir qué hacer.
        if (permissionsToRequest.isEmpty()) {
            // Si la lista está vacía, es que ya tenemos todos los permisos. Vamos a la app.
            Log.d("CIVISEC_PERMISSIONS", "Todos los permisos ya están concedidos.");
            startMainActivity();
        } else {
            // Si faltan permisos, lanzamos el diálogo para pedirlos.
            Log.d("CIVISEC_PERMISSIONS", "Solicitando permisos: " + permissionsToRequest);
            requestPermissionsLauncher.launch(permissionsToRequest.toArray(new String[0]));
        }
    }

    /**
     * Método centralizado para iniciar MainActivity y cerrar el Splash.
     */
    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}