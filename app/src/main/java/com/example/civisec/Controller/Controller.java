package com.example.civisec.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;

import com.example.civisec.R;
import com.example.civisec.View.MainActivity;
import com.example.civisec.View.MapActivity;
import com.example.civisec.View.TipsActivity;
import com.example.civisec.View.FaqActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Controller {

    public static final String PREFS_NAME = "CIVISEC_PREFS";
    public static final String KEY_CURRENT_PHASE = "CURRENT_PHASE";
    public static final String KEY_BT_DEVICE_NAME = "BT_DEVICE_NAME"; // Constante para bluetooth

    public static final String KEY_SAVED_ALERTS = "SAVED_ALERTS"; // Para la lista de alertas
    private static final String CHANNEL_ID = "CIVISEC_ALERTS";
    private static final String CHANNEL_NAME = "CIVISEC System Alerts";
    private static final String CHANNEL_DESC = "Notifications for critical CIVISEC alerts";
    public void setupBottomNavigation(Activity activity, int currentItemId) {
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_navigation);

        // Marcar el ítem actual como seleccionado para que visualmente sea correcto
        bottomNavigationView.setSelectedItemId(currentItemId);

        // Establecer el listener para manejar los clics
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int selectedItemId = item.getItemId();

                // Si el usuario vuelve a pulsar el ítem en el que ya está, no hacemos nada
                if (selectedItemId == currentItemId) {
                    return false;
                }

                Intent intent = null;

                if (selectedItemId == R.id.nav_alerts) {
                    intent = new Intent(activity, MainActivity.class);
                } else if (selectedItemId == R.id.nav_map) {
                    intent = new Intent(activity, MapActivity.class);
                }
                else if (selectedItemId == R.id.nav_tips) {
                    intent = new Intent(activity, TipsActivity.class);
                } else if (selectedItemId == R.id.nav_faq) {
                    intent = new Intent(activity, FaqActivity.class);
                }

                if (intent != null) {
                    activity.startActivity(intent);
                    // Usamos flags para limpiar el stack y evitar acumular Activities
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.overridePendingTransition(0, 0); // Transición instantánea
                    activity.finish(); // Cierra la actividad actual
                }

                return true;
            }
        });

    }

    public void saveCurrentPhase(Context context, int phase) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CURRENT_PHASE, phase);
        editor.apply(); // apply() guarda los datos en segundo plano.
    }

    public int getCurrentPhase(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // El segundo parámetro de getInt es el valor por defecto que se devuelve
        // si la clave no existe. Empezamos en la fase 1.
        return prefs.getInt(KEY_CURRENT_PHASE, 1);
    }

    public void saveLastConnectedBluetoothDevice(Context context, String deviceName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_BT_DEVICE_NAME, deviceName);
        editor.apply();
    }

    public String getLastConnectedBluetoothDevice(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_BT_DEVICE_NAME, null);
    }

    public void saveAlert(Context context, String alertMessage) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // Usamos un Set<String> para almacenar las alertas y evitar duplicados.
        Set<String> alerts = new HashSet<>(prefs.getStringSet(KEY_SAVED_ALERTS, new HashSet<>()));
        alerts.add(alertMessage);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(KEY_SAVED_ALERTS, alerts);
        editor.apply();
    }

    public Set<String> getAlerts(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return new HashSet<>(prefs.getStringSet(KEY_SAVED_ALERTS, new HashSet<>()));
    }

    public void triggerHostileDeviceAlert(Context context, String deviceName) {
        // 1. Crear el mensaje de la alerta.
        String alertMessage = "Dispositivo hostil detectado: [" + deviceName + "]. Protocolo de contención iniciado.";

        // 2. Guardar la alerta para que aparezca en el menú principal.
        saveAlert(context, alertMessage);

        // 3. Enviar la notificación al sistema.
        sendNotification(context, "ALERTA DE SEGURIDAD CIVISEC", alertMessage);
    }

    private void sendNotification(Context context, String title, String message) {
        // Crear el canal de notificación (necesario para Android 8.0 Oreo y superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Crear un Intent para que al pulsar la notificación se abra MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_civisec_logo) // Asegúrate de tener un icono en drawable
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message)) // Para texto largo
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent) // Acción al pulsar
                .setAutoCancel(true); // La notificación desaparece al pulsarla

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        // El ID de la notificación (101) puede ser cualquier número.
        // Si usas el mismo ID, las notificaciones se reemplazarán. Si usas IDs diferentes, se apilarán.
        managerCompat.notify(101, builder.build());
    }
}