package com.example.civisec.View;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import com.example.civisec.Controller.Controller;
import com.example.civisec.R;

public class TipsActivity extends AppCompatActivity {

    private Controller controller;
    private NestedScrollView tipsScrollView;
    private LinearLayout tipsTakeoverLayout;
    private TextView tip1Title, tip1Text, tip2Title, tip2Text, tip3Title, tip3Text, tip4Title, tip4Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        controller = new Controller(this);
        controller.setupBottomNavigation(this, R.id.nav_tips);

        initializeViews();
        updateUIForPhase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIForPhase(); // Actualizar cuando volvemos a la pantalla
    }

    private void initializeViews() {
        tipsScrollView = findViewById(R.id.scroll_view);
        tipsTakeoverLayout = findViewById(R.id.tips_takeover_layout);
        tip1Title = findViewById(R.id.tip1_title);
        tip1Text = findViewById(R.id.tip1_text);
        tip2Title = findViewById(R.id.tip2_title);
        tip2Text = findViewById(R.id.tip2_text);
        tip3Title = findViewById(R.id.tip3_title);
        tip3Text = findViewById(R.id.tip3_text);
        tip4Title = findViewById(R.id.tip4_title);
        tip4Text = findViewById(R.id.tip4_text);
    }

    private void updateUIForPhase() {
        int currentPhase = controller.getCurrentPhase();

        if (currentPhase >= 3) {
            // Fase 3: Mostrar takeover de la IA
            tipsScrollView.setVisibility(View.GONE);
            tipsTakeoverLayout.setVisibility(View.VISIBLE);
        } else {
            // Fases 1 y 2: Mostrar consejos normales
            tipsScrollView.setVisibility(View.VISIBLE);
            tipsTakeoverLayout.setVisibility(View.GONE);

            if (currentPhase == 1) {
                setPhase1Tips();
            } else {
                setPhase2Tips();
            }
        }
    }

    private void setPhase1Tips() {
        tip1Title.setText(R.string.tip1_title_phase1);
        tip1Text.setText(R.string.tip1_text_phase1);
        tip2Title.setText(R.string.tip2_title_phase1);
        tip2Text.setText(R.string.tip2_text_phase1);
        tip3Title.setText(R.string.tip3_title_phase1);
        tip3Text.setText(R.string.tip3_text_phase1);
        tip4Title.setText(R.string.tip4_title_phase1);
        tip4Text.setText(R.string.tip4_text_phase1);
    }

    private void setPhase2Tips() {
        tip1Title.setText(R.string.tip1_title_phase2);
        tip1Text.setText(R.string.tip1_text_phase2);
        tip2Title.setText(R.string.tip2_title_phase2);
        tip2Text.setText(R.string.tip2_text_phase2);
        tip3Title.setText(R.string.tip3_title_phase2);
        tip3Text.setText(R.string.tip3_text_phase2);
        tip4Title.setText(R.string.tip4_title_phase2);
        tip4Text.setText(R.string.tip4_text_phase2);
    }
}