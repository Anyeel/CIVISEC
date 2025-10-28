package com.example.civisec.View;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
        setContentView(R.layout.activity_faq);

        controller = new Controller(this);
        controller.setupBottomNavigation(this, R.id.nav_faq);

        faqScrollView = findViewById(R.id.faq_scroll_view);
        takeoverLayout = findViewById(R.id.takeover_layout);

        updateUIForPhase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIForPhase(); // Actualizar cuando volvemos a la pantalla
    }

    private void updateUIForPhase() {
        int currentPhase = controller.getCurrentPhase();

        if (currentPhase >= 3) {
            // Fase 3: La IA ha tomado el control
            faqScrollView.setVisibility(View.GONE);
            takeoverLayout.setVisibility(View.VISIBLE);
        } else {
            // Fases 1 y 2: Mostrar FAQ normal
            faqScrollView.setVisibility(View.VISIBLE);
            takeoverLayout.setVisibility(View.GONE);
        }
    }
}