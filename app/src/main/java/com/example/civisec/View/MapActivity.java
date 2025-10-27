package com.example.civisec.View;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.civisec.Controller.Controller; // <-- Importa el Controller
import com.example.civisec.R;

public class MapActivity extends AppCompatActivity {

    private Controller controller; // <-- Crea una variable para el Controller

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- LÓGICA DE NAVEGACIÓN CENTRALIZADA ---
        controller = new Controller();
        // Llamamos al metodo, pasándole esta Activity y el ID de SU item de menú
        controller.setupBottomNavigation(this, R.id.nav_map);
    }
}