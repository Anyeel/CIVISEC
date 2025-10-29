package com.example.civisec.View;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.civisec.Controller.AlertScheduler;
import com.example.civisec.Controller.BluetoothScanner;
import com.example.civisec.Controller.Controller;
import com.example.civisec.R;
import com.google.android.material.card.MaterialCardView;
import org.osmdroid.config.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Controller controller;
    private AlertScheduler alertScheduler;
    private BluetoothScanner bluetoothScanner;
    private LinearLayout alertsContainer;
    private TextView subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, android.preference.PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_main);

        controller = new Controller(this);
        alertScheduler = new AlertScheduler();
        bluetoothScanner = new BluetoothScanner(this);
        alertsContainer = findViewById(R.id.alerts_container);
        subtitle = findViewById(R.id.alerts_subtitle);

        controller.setupBottomNavigation(this, R.id.nav_alerts);
        alertScheduler.scheduleStoryEvents(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIForCurrentPhase();
    }

    /**
     * Comprueba la fase actual y actualiza la UI para mostrar todo el contenido relevante.
     */
    private void updateUIForCurrentPhase() {
        int phase = controller.getCurrentPhase();
        removeAllDynamicCards();

        // --- LÓGICA CORREGIDA ---

        // 1. Siempre mostramos las noticias programadas que han ocurrido, sin importar la fase.
        subtitle.setVisibility(View.VISIBLE);
        displayTriggeredNews();

        // 2. SI ADEMÁS estamos en Fase 3 o superior, añadimos la tarjeta especial de Bluetooth.
        if (phase >= 3) {
            displayBluetoothTakeover();
        }
    }

    /**
     * Muestra la alerta de la Fase 3, utilizando el BluetoothScanner para obtener
     * un mensaje personalizado. ESTA TARJETA SE AÑADE A LAS DEMÁS.
     */
    private void displayBluetoothTakeover() {
        String alertMessage = bluetoothScanner.getHostileDeviceAlert();

        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dpToPx(16));
        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(12));
        card.setCardElevation(dpToPx(4));
        card.setCardBackgroundColor(getResources().getColor(android.R.color.black, null));
        card.setTag("dynamic_card");

        TextView textView = new TextView(this);
        int padding = dpToPx(24);
        textView.setPadding(padding, padding, padding, padding);
        textView.setText(alertMessage);
        textView.setTextColor(getResources().getColor(R.color.red_alert, null));
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER);
        textView.setLineSpacing(dpToPx(4), 1.2f);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);

        card.addView(textView);
        // La añadimos en la primera posición (índice 2, después de los dos títulos) para que sea lo primero que se vea.
        alertsContainer.addView(card, 2);
    }

    /**
     * Obtiene las noticias que han ocurrido y crea una tarjeta para cada una.
     */
    private void displayTriggeredNews() {
        Set<String> triggeredNews = controller.getTriggeredNews();
        List<NewsItem> newsList = parseNewsItems(triggeredNews);

        newsList.sort((a, b) -> Integer.compare(b.titleResId, a.titleResId));

        for (NewsItem news : newsList) {
            createNewsCard(news.titleResId, news.textResId);
        }

        if (newsList.isEmpty()) {
            showNoNewsMessage();
        }
    }

    private void removeAllDynamicCards() {
        for (int i = alertsContainer.getChildCount() - 1; i >= 0; i--) {
            View child = alertsContainer.getChildAt(i);
            if ("dynamic_card".equals(child.getTag())) {
                alertsContainer.removeViewAt(i);
            }
        }
    }

    private List<NewsItem> parseNewsItems(Set<String> newsSet) {
        List<NewsItem> items = new ArrayList<>();
        for (String newsIds : newsSet) {
            String[] ids = newsIds.split("\\|");
            if (ids.length == 2) {
                try {
                    int titleResId = Integer.parseInt(ids[0]);
                    int textResId = Integer.parseInt(ids[1]);
                    items.add(new NewsItem(titleResId, textResId));
                } catch (NumberFormatException e) {
                    // Ignorar
                }
            }
        }
        return items;
    }

    private void createNewsCard(int titleResId, int textResId) {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dpToPx(16));
        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(12));
        card.setCardElevation(dpToPx(4));
        card.setCardBackgroundColor(getResources().getColor(android.R.color.white, null));
        card.setTag("dynamic_card");

        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = dpToPx(16);
        innerLayout.setPadding(padding, padding, padding, padding);

        TextView titleView = new TextView(this);
        titleView.setText(titleResId);
        titleView.setTextSize(16);
        titleView.setTextColor(getResources().getColor(R.color.red_alert, null));
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(0, 0, 0, dpToPx(8));
        titleView.setLayoutParams(titleParams);

        TextView textView = new TextView(this);
        textView.setText(textResId);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(android.R.color.black, null));
        textView.setLineSpacing(0, 1.1f);

        innerLayout.addView(titleView);
        innerLayout.addView(textView);
        card.addView(innerLayout);

        alertsContainer.addView(card);
    }

    private void showNoNewsMessage() {
        TextView noNews = new TextView(this);
        noNews.setText("No hay alertas activas en este momento.");
        noNews.setTextSize(14);
        noNews.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
        noNews.setPadding(dpToPx(16), dpToPx(24), dpToPx(16), dpToPx(16));
        noNews.setTag("dynamic_card");
        alertsContainer.addView(noNews);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private static class NewsItem {
        int titleResId;
        int textResId;

        NewsItem(int titleResId, int textResId) {
            this.titleResId = titleResId;
            this.textResId = textResId;
        }
    }
}