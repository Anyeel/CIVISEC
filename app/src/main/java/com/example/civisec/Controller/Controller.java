package com.example.civisec.Controller;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.civisec.R;
import com.example.civisec.View.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

/**
 * Controlador principal que gestiona:
 * - Navegación entre pantallas
 * - Fases de la historia
 * - Notificaciones
 * - Almacenamiento de datos
 */
public class Controller {

    private final Context context;
    private final SharedPreferences prefs;

    // Constantes
    private static final String PREFS_NAME = "CIVISEC_PREFS";
    private static final String CHANNEL_ID = "CIVISEC_ALERTS";

    public Controller(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        crearCanalNotificaciones();
    }

    // ============ NAVEGACIÓN ============


    // Configura la barra de navegación inferior

    public void setupBottomNavigation(Activity activity, int currentItemId) {
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottom_navigation);
        if (bottomNav == null) return; // Comprobación de seguridad

        // 1. Marca el ítem del menú actual como seleccionado
        bottomNav.setSelectedItemId(currentItemId);

        // 2. Configura el listener para reaccionar a los clics
        bottomNav.setOnItemSelectedListener(item -> {
            // 3. Si el usuario vuelve a pulsar el ítem en el que ya está, no hacemos nada
            if (item.getItemId() == currentItemId) {
                return false;
            }

            // 4. Obtiene el Intent correcto para el ítem pulsado
            Intent intent = getIntentForMenuItem(activity, item.getItemId());

            // 5. Si el Intent es válido, inicia la nueva actividad y cierra la actual
            if (intent != null) {
                // Estas flags ayudan a gestionar el historial para no apilar actividades
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                // Elimina la animación de transición para una sensación más fluida
                activity.overridePendingTransition(0, 0);
                activity.finish(); // Cierra la actividad actual
                return true; // Indica que hemos manejado el evento
            }

            return false; // Indica que no hemos manejado el evento
        });
    }

    /**
     * Método de ayuda que devuelve el Intent correcto para un ID de menú específico.
     * @param context El contexto actual.
     * @param itemId El ID del ítem del menú que se ha pulsado.
     * @return un Intent para la actividad correspondiente, o null si no se reconoce el ID.
     */
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
        return null; // Devuelve null si no se encuentra una coincidencia
    }

    // ============ FASES ============

    // Obtiene la fase actual (1, 2 o 3)
    public int getFaseActual() {
        return prefs.getInt("FASE", 1);
    }

    // Avanza a una nueva fase y envía notificación
    public void avanzarFase(int nuevaFase) {
        if (nuevaFase <= getFaseActual()) return; // No retroceder

        prefs.edit().putInt("FASE", nuevaFase).apply();

        // Notificar cambio de fase
        if (nuevaFase == 2) {
            enviarNotificacion("⚠️ ALERTA NIVEL 2",
                    "Escalada de amenazas detectada. Revise las nuevas directivas.");
        } else if (nuevaFase == 3) {
            enviarNotificacion("🚨 DIRECTIVA OBLIGATORIA",
                    "El sistema ha sido comprometido. Protocolo de emergencia activado.");
        }
    }

    // ============ NOTICIAS ============


     // Guarda una noticia activada y envía notificación
    public void activarNoticia(int tituloId, int textoId) {
        // Guardar en preferencias
        Set<String> noticias = getNoticias();
        noticias.add(tituloId + "|" + textoId);
        prefs.edit().putStringSet("NOTICIAS", noticias).apply();

        // Enviar notificación
        String titulo = context.getString(tituloId);
        String texto = context.getString(textoId);
        enviarNotificacion(titulo, texto);
    }

    // Obtiene todas las noticias activadas
    public Set<String> getNoticias() {
        return new HashSet<>(prefs.getStringSet("NOTICIAS", new HashSet<>()));
    }

    // ============ NOTIFICACIONES ============

    // Crea el canal de notificaciones (necesario en Android 8+)
    private void crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alertas CIVISEC",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    // Envía una notificación al usuario
    public void enviarNotificacion(String titulo, String mensaje) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_civisec_logo)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        var notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    // ============ REFUGIOS (MAPA) ============

    // Guarda las ubicaciones de los refugios
    public void guardarRefugios(Set<String> ubicaciones) {
        prefs.edit().putStringSet("REFUGIOS", ubicaciones).apply();
    }

    // Obtiene las ubicaciones guardadas de los refugios */
    public Set<String> getRefugios() {
        return prefs.getStringSet("REFUGIOS", new HashSet<>());
    }

    // ============ MODO DESARROLLADOR ============

    // Activa/desactiva el modo desarrollo (eventos cada 3 seg vs 1 min)
    public void setModoDesarrollo(boolean activar) {
        prefs.edit().putBoolean("DEV_MODE", activar).apply();
    }

    // Verifica si el modo desarrollo está activo
    public boolean isModoDesarrollo() {
        return prefs.getBoolean("DEV_MODE", false);
    }

    // Reinicia completamente la app (borra todos los datos)
    public void reiniciarApp() {
        prefs.edit().clear().apply();
    }
}