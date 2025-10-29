package com.example.civisec.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.civisec.Controller.AlertScheduler;
import com.example.civisec.Controller.Controller;
import com.example.civisec.R;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Controller controller;
    private AlertScheduler alertScheduler;
    private LinearLayout alertsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new Controller(this);
        alertScheduler = new AlertScheduler();
        alertsContainer = findViewById(R.id.alerts_container);

        controller.setupBottomNavigation(this, R.id.nav_alerts);
        alertScheduler.scheduleStoryEvents(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayTriggeredNews();
    }

    private void displayTriggeredNews() {
        // Limpiar tarjetas anteriores
        removeAllNewsCards();

        // Obtener y mostrar noticias
        Set<String> triggeredNews = controller.getTriggeredNews();
        List<NewsItem> newsList = parseNewsItems(triggeredNews);

        // Ordenar por ID (las más recientes primero)
        newsList.sort((a, b) -> Integer.compare(b.titleResId, a.titleResId));

        // Crear tarjetas
        for (NewsItem news : newsList) {
            createNewsCard(news.titleResId, news.textResId);
        }

        // Mostrar mensaje si no hay noticias
        if (newsList.isEmpty()) {
            showNoNewsMessage();
        }
    }

    private void removeAllNewsCards() {
        for (int i = alertsContainer.getChildCount() - 1; i >= 0; i--) {
            View child = alertsContainer.getChildAt(i);
            if ("news_card".equals(child.getTag())) {
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
                    // Ignorar entrada malformada
                }
            }
        }
        return items;
    }

    private void createNewsCard(int titleResId, int textResId) {
        // Crear tarjeta
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = dpToPx(16);
        cardParams.setMargins(0, 0, 0, margin);
        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(12));
        card.setCardElevation(dpToPx(4));
        card.setCardBackgroundColor(getResources().getColor(android.R.color.white, null));
        card.setTag("news_card");

        // Layout interior
        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = dpToPx(16);
        innerLayout.setPadding(padding, padding, padding, padding);

        // Título (titleResId es el título)
        TextView titleView = new TextView(this);
        titleView.setText(titleResId); // titleResId = título
        titleView.setTextSize(16);
        titleView.setTextColor(getResources().getColor(R.color.red_alert, null));
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(0, 0, 0, dpToPx(8));
        titleView.setLayoutParams(titleParams);

        // Texto (textResId es la descripción)
        TextView textView = new TextView(this);
        textView.setText(textResId); // textResId = texto largo
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(android.R.color.black, null));
        textView.setLineSpacing(0, 1.1f);

        // Ensamblar
        innerLayout.addView(titleView);
        innerLayout.addView(textView);
        card.addView(innerLayout);

        // Añadir al final del contenedor
        alertsContainer.addView(card);
    }

    private void showNoNewsMessage() {
        TextView noNews = new TextView(this);
        noNews.setText("No hay alertas activas en este momento.");
        noNews.setTextSize(14);
        noNews.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
        noNews.setPadding(dpToPx(16), dpToPx(24), dpToPx(16), dpToPx(16));
        noNews.setTag("news_card");
        alertsContainer.addView(noNews);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    // Clase auxiliar para manejar noticias
    private static class NewsItem {
        int titleResId;
        int textResId;

        NewsItem(int titleResId, int textResId) {
            this.titleResId = titleResId;
            this.textResId = textResId;
        }
    }
}