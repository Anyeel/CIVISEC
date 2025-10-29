package com.example.civisec.Controller;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.civisec.R;
import com.example.civisec.View.MainActivity;
import com.example.civisec.View.MapActivity;
import com.example.civisec.View.TipsActivity;
import com.example.civisec.View.FaqActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.HashSet;
import java.util.Set;

public class Controller {

    private static final String TAG = "Controller";
    private static final String PREFS_NAME = "CIVISEC_PREFS";
    private static final String KEY_CURRENT_PHASE = "CURRENT_PHASE";
    private static final String KEY_TRIGGERED_NEWS = "TRIGGERED_NEWS";
    private static final String KEY_DEV_MODE = "DEV_MODE";
    private static final String CHANNEL_ID = "CIVISEC_ALERTS";
    private static final String CHANNEL_NAME = "CIVISEC System Alerts";
    private static final String CHANNEL_DESC = "Notifications for critical CIVISEC alerts";

    private final Context context;
    private final SharedPreferences prefs;

    public Controller(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        createNotificationChannel();
    }

    // ============ NAVEGACIÓN ============

    public void setupBottomNavigation(Activity activity, int currentItemId) {
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottom_navigation);
        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(currentItemId);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == currentItemId) return false;

            Intent intent = getIntentForMenuItem(activity, item.getItemId());
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                activity.finish();
                return true;
            }
            return false;
        });
    }

    private Intent getIntentForMenuItem(Context context, int itemId) {
        if (itemId == R.id.nav_alerts) {
            return new Intent(context, MainActivity.class);
        } else if (itemId == R.id.nav_map) {
            return new Intent(context, MapActivity.class);
        } else if (itemId == R.id.nav_tips) {
            return new Intent(context, TipsActivity.class);
        } else if (itemId == R.id.nav_faq) {
            return new Intent(context, FaqActivity.class);
        }
        return null;
    }

    // ============ GESTIÓN DE FASES ============

    public int getCurrentPhase() {
        return prefs.getInt(KEY_CURRENT_PHASE, 1);
    }

    public void saveCurrentPhase(int phase) {
        prefs.edit().putInt(KEY_CURRENT_PHASE, phase).apply();
        Log.d(TAG, "Fase guardada: " + phase);
    }

    public void advanceToPhase(int newPhase) {
        if (newPhase <= getCurrentPhase()) {
            Log.d(TAG, "No se avanza. Ya estamos en fase " + getCurrentPhase());
            return;
        }

        Log.d(TAG, "Avanzando a fase " + newPhase);
        saveCurrentPhase(newPhase);

        // Notificar el cambio de fase
        String title = "";
        String message = "";

        switch (newPhase) {
            case 2:
                title = "⚠️ ALERTA NIVEL 2";
                message = "Escalada de amenazas detectada. Revise las nuevas directivas.";
                break;
            case 3:
                title = "🚨 DIRECTIVA OBLIGATORIA";
                message = "El sistema ha sido comprometido. Protocolo de emergencia activado.";
                break;
        }

        if (!title.isEmpty()) {
            sendNotification(title, message);
        }
    }

    // ============ GESTIÓN DE NOTICIAS ============

    public void triggerNewsAlert(int titleResId, int textResId) {
        // Guardar noticia
        Set<String> news = new HashSet<>(prefs.getStringSet(KEY_TRIGGERED_NEWS, new HashSet<>()));
        news.add(titleResId + "|" + textResId);
        prefs.edit().putStringSet(KEY_TRIGGERED_NEWS, news).apply();

        // Enviar notificación
        String title = context.getString(titleResId);
        String text = context.getString(textResId);
        sendNotification(title, text);

        Log.d(TAG, "Noticia activada: " + title);
    }

    public Set<String> getTriggeredNews() {
        return new HashSet<>(prefs.getStringSet(KEY_TRIGGERED_NEWS, new HashSet<>()));
    }

    // ============ NOTIFICACIONES ============

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void sendNotification(String title, String message) {
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Sin permiso de notificaciones");
            return;
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_civisec_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());
    }

    /**
     * Envía una notificación personalizada (para alerta de Bluetooth)
     */
    public void sendCustomNotification(String title, String message) {
        sendNotification(title, message);
    }

    // ============ MODO DESARROLLADOR ============
    public void setDevMode(boolean enabled) { prefs.edit().putBoolean(KEY_DEV_MODE, enabled).apply(); }
    public void toggleDevMode() { setDevMode(!isDevMode()); }
    public void resetApp() { prefs.edit().clear().apply(); }
    public boolean isDevMode() {
        return prefs.getBoolean(KEY_DEV_MODE, false);
    }
}