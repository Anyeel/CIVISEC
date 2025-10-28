package com.example.civisec.View;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.civisec.Controller.AlertScheduler;
import com.example.civisec.Controller.Controller;
import com.example.civisec.R;
import com.google.android.material.card.MaterialCardView;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Controller controller;
    private AlertScheduler alertScheduler;
    private LinearLayout alertsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de los componentes lógicos
        controller = new Controller();
        alertScheduler = new AlertScheduler();

        // Referencia al contenedor de las alertas en el layout
        alertsContainer = findViewById(R.id.alerts_container);

        // Configuración de la navegación inferior
        controller.setupBottomNavigation(this, R.id.nav_alerts);

        // Iniciar el programador de la historia. Solo se ejecuta la primera vez que la app se instala.
        alertScheduler.scheduleStoryEvents(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cada vez que la actividad vuelve a ser visible, actualizamos la lista de noticias.
        displayTriggeredNews();
    }

    /**
     * Lee la lista de noticias que ya han ocurrido desde SharedPreferences
     * y las muestra en pantalla, creando una tarjeta para cada una.
     */
    private void displayTriggeredNews() {
        // 1. Primero, limpiamos todas las tarjetas de noticias que ya estaban en pantalla
        // para evitar duplicarlas cada vez que volvemos a esta actividad.
        for (int i = alertsContainer.getChildCount() - 1; i >= 0; i--) {
            if ("news_card".equals(alertsContainer.getChildAt(i).getTag())) {
                alertsContainer.removeViewAt(i);
            }
        }

        // 2. Obtenemos la lista de noticias del controlador.
        Set<String> triggeredNews = controller.getTriggeredNews(this);

        // 3. Recorremos la lista y creamos una tarjeta para cada noticia.
        for (String newsIds : triggeredNews) {
            String[] ids = newsIds.split("\\|");
            if (ids.length < 2) continue; // Ignorar datos mal formados

            try {
                int titleResId = Integer.parseInt(ids[0]);
                int textResId = Integer.parseInt(ids[1]);
                createNewsCard(titleResId, textResId);
            } catch (NumberFormatException e) {
                // Si los IDs no son números, ignoramos esta entrada.
            }
        }
    }

    /**
     * Crea una vista de MaterialCardView programáticamente para una noticia específica
     * y la añade al contenedor principal.
     * @param titleResId El ID del recurso de string para el título.
     * @param textResId El ID del recurso de string para el texto.
     */
    private void createNewsCard(int titleResId, int textResId) {
        // --- Contenedor exterior de la tarjeta ---
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginInPixels = (int) (16 * getResources().getDisplayMetrics().density);
        cardParams.setMargins(0, 0, 0, marginInPixels);
        card.setLayoutParams(cardParams);
        card.setRadius(12 * getResources().getDisplayMetrics().density);
        card.setCardElevation(4 * getResources().getDisplayMetrics().density);
        card.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        card.setTag("news_card"); // Tag para poder identificarlas y borrarlas después.

        // --- Layout interior para organizar el título y el texto ---
        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        int paddingInPixels = (int) (16 * getResources().getDisplayMetrics().density);
        innerLayout.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels);

        // --- TextView para el título ---
        TextView titleView = new TextView(this);
        titleView.setText(titleResId);
        titleView.setTextSize(16);
        titleView.setTextColor(getResources().getColor(R.color.red_alert)); // Color rojo de alerta
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(0, 0, 0, (int) (8 * getResources().getDisplayMetrics().density));
        titleView.setLayoutParams(titleParams);

        // --- TextView para el cuerpo de la noticia ---
        TextView textView = new TextView(this);
        textView.setText(textResId);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setLineSpacing(0, 1.1f);

        // --- Ensamblaje: Añadimos todo en orden ---
        innerLayout.addView(titleView);
        innerLayout.addView(textView);
        card.addView(innerLayout);

        // Finalmente, añadimos la tarjeta completa al contenedor en la pantalla.
        // El '2' indica que se añadirá en la tercera posición (después de los dos títulos).
        alertsContainer.addView(card, 2);
    }
}