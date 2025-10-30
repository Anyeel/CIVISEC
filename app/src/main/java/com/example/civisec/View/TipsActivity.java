package com.example.civisec.View;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.civisec.Controller.Controller;
import com.example.civisec.R;

public class TipsActivity extends AppCompatActivity {

    private Controller controller;
    private View contenidoNormal;
    private View contenidoTakeover;
    private TextView[] titulos = new TextView[4];
    private TextView[] textos = new TextView[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        controller = new Controller(this);
        controller.setupBottomNavigation(this, R.id.nav_tips);

        // Obtener vistas
        contenidoNormal = findViewById(R.id.scroll_view);
        contenidoTakeover = findViewById(R.id.tips_takeover_layout);

        // Obtener TextViews de consejos
        titulos[0] = findViewById(R.id.tip1_title);
        textos[0] = findViewById(R.id.tip1_text);
        titulos[1] = findViewById(R.id.tip2_title);
        textos[1] = findViewById(R.id.tip2_text);
        titulos[2] = findViewById(R.id.tip3_title);
        textos[2] = findViewById(R.id.tip3_text);
        titulos[3] = findViewById(R.id.tip4_title);
        textos[3] = findViewById(R.id.tip4_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarPantalla();
    }

    // Actualiza la pantalla según la fase actual
    private void actualizarPantalla() {
        int fase = controller.getFaseActual();

        if (fase >= 3) {
            // Fase 3: Mostrar pantalla de takeover
            contenidoNormal.setVisibility(View.GONE);
            contenidoTakeover.setVisibility(View.VISIBLE);
        } else {
            // Fases 1 y 2: Mostrar consejos normales
            contenidoNormal.setVisibility(View.VISIBLE);
            contenidoTakeover.setVisibility(View.GONE);

            if (fase == 1) {
                mostrarConsejosFase1();
            } else {
                mostrarConsejosFase2();
            }
        }
    }

    //Muestra los consejos de la fase 1
    private void mostrarConsejosFase1() {
        titulos[0].setText(R.string.tip1_title_phase1);
        textos[0].setText(R.string.tip1_text_phase1);
        titulos[1].setText(R.string.tip2_title_phase1);
        textos[1].setText(R.string.tip2_text_phase1);
        titulos[2].setText(R.string.tip3_title_phase1);
        textos[2].setText(R.string.tip3_text_phase1);
        titulos[3].setText(R.string.tip4_title_phase1);
        textos[3].setText(R.string.tip4_text_phase1);
    }

    //Muestra los consejos de la fase 2
    private void mostrarConsejosFase2() {
        titulos[0].setText(R.string.tip1_title_phase2);
        textos[0].setText(R.string.tip1_text_phase2);
        titulos[1].setText(R.string.tip2_title_phase2);
        textos[1].setText(R.string.tip2_text_phase2);
        titulos[2].setText(R.string.tip3_title_phase2);
        textos[2].setText(R.string.tip3_text_phase2);
        titulos[3].setText(R.string.tip4_title_phase2);
        textos[3].setText(R.string.tip4_text_phase2);
    }
}
