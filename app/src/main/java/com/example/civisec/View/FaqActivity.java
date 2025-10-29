package com.example.civisec.View;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.civisec.Controller.Controller;
import com.example.civisec.R;


 //Pantalla de FAQ que se reemplaza por takeover en fase 3

public class FaqActivity extends AppCompatActivity {

    private Controller controller;
    private View contenidoNormal;
    private View contenidoTakeover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        controller = new Controller(this);
        controller.setupBottomNavigation(this, R.id.nav_faq);

        contenidoNormal = findViewById(R.id.faq_scroll_view);
        contenidoTakeover = findViewById(R.id.takeover_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarPantalla();
    }

     // Muestra FAQ normal o takeover según la fase

    private void actualizarPantalla() {
        if (controller.getFaseActual() >= 3) {
            // Fase 3: Takeover
            contenidoNormal.setVisibility(View.GONE);
            contenidoTakeover.setVisibility(View.VISIBLE);
        } else {
            // Fases 1 y 2: FAQ normal
            contenidoNormal.setVisibility(View.VISIBLE);
            contenidoTakeover.setVisibility(View.GONE);
        }
    }
}