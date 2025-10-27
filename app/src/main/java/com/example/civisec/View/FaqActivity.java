package com.example.civisec.View;

import android.os.Bundle;
import android.view.View; // <-- Importar View
import android.widget.ScrollView; // <-- Importar ScrollView

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout; // <-- Importar
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.example.civisec.Controller.Controller;
import com.example.civisec.R;

public class FaqActivity extends AppCompatActivity {

    private Controller controller;
    private NestedScrollView faqScrollView;
    private ConstraintLayout takeoverLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faq);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar el controlador y los elementos de la vista
        controller = new Controller();
        faqScrollView = findViewById(R.id.faq_scroll_view);
        takeoverLayout = findViewById(R.id.takeover_layout);

        // Configurar la navegación inferior
        controller.setupBottomNavigation(this, R.id.nav_faq);

        // Comprobar la fase y actualizar la UI
        checkPhaseAndUpdateUI();
    }

    private void checkPhaseAndUpdateUI() {
        // 1. Obtener la fase actual a través del Controller
        int currentPhase = controller.getCurrentPhase(this);

        // 2. Decidir qué mostrar
        if (currentPhase < 3) {
            // Fases 1 y 2: Mostrar el FAQ normal
            faqScrollView.setVisibility(View.VISIBLE);
            takeoverLayout.setVisibility(View.GONE);
        } else {
            // Fase 3 o superior: Las máquinas han tomado el control
            faqScrollView.setVisibility(View.GONE);
            takeoverLayout.setVisibility(View.VISIBLE);
        }
    }
}