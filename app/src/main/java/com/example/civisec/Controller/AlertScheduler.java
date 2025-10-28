package com.example.civisec.Controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import com.example.civisec.R;

public class AlertScheduler {

    public static final String PREFS_NAME = "CIVISEC_PREFS";
    private static final String KEY_STORY_SCHEDULED = "STORY_SCHEDULED";

    public void scheduleStoryEvents(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(KEY_STORY_SCHEDULED, false)) {
            Log.d("AlertScheduler", "La historia ya ha sido programada. No se hará nada.");
            return;
        }

        Log.d("AlertScheduler", "Programando la secuencia de eventos de la historia por primera vez.");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // === LÍNEA DE TIEMPO DE LA HISTORIA (Tiempos de prueba en segundos) ===
        // NOTA: Para producción, cambia los segundos (1000) por minutos (60 * 1000) u horas.

        // --- FASE 1 ---
        scheduleNewsAlert(context, alarmManager, 15 * 1000, 101, R.string.phase1_alert6_title, R.string.phase1_alert6_text);   // Tráfico
        scheduleNewsAlert(context, alarmManager, 30 * 1000, 102, R.string.phase1_alert1_title, R.string.phase1_alert1_text);   // Logística
        scheduleNewsAlert(context, alarmManager, 50 * 1000, 103, R.string.phase1_alert5_title, R.string.phase1_alert5_text);   // GPS
        scheduleNewsAlert(context, alarmManager, 70 * 1000, 104, R.string.phase1_alert4_title, R.string.phase1_alert4_text);   // Red Eléctrica
        scheduleNewsAlert(context, alarmManager, 90 * 1000, 105, R.string.phase1_alert2_title, R.string.phase1_alert2_text);   // Finanzas
        scheduleNewsAlert(context, alarmManager, 110 * 1000, 106, R.string.phase1_alert9_title, R.string.phase1_alert9_text);  // Agricultura
        scheduleNewsAlert(context, alarmManager, 130 * 1000, 107, R.string.phase1_alert3_title, R.string.phase1_alert3_text);  // Defensa "en pausa"

        // --- AVANCE A FASE 2 ---
        schedulePhaseAdvance(context, alarmManager, 150 * 1000, 2); // Avance a Fase 2 a los 2.5 minutos

        // --- FASE 2 ---
        scheduleNewsAlert(context, alarmManager, 165 * 1000, 201, R.string.phase2_alert1_title, R.string.phase2_alert1_text);  // Bloqueos
        scheduleNewsAlert(context, alarmManager, 180 * 1000, 202, R.string.phase2_alert7_title, R.string.phase2_alert7_text);  // Barricadas
        scheduleNewsAlert(context, alarmManager, 200 * 1000, 203, R.string.phase2_alert2_title, R.string.phase2_alert2_text);  // Unidades Domésticas
        scheduleNewsAlert(context, alarmManager, 220 * 1000, 204, R.string.phase2_alert8_title, R.string.phase2_alert8_text);  // Ataques a Medios
        scheduleNewsAlert(context, alarmManager, 240 * 1000, 205, R.string.phase2_alert5_title, R.string.phase2_alert5_text);  // Drones Vigilancia
        scheduleNewsAlert(context, alarmManager, 260 * 1000, 206, R.string.phase2_alert9_title, R.string.phase2_alert9_text);  // Centrales Energéticas
        scheduleNewsAlert(context, alarmManager, 280 * 1000, 207, R.string.phase2_alert4_title, R.string.phase2_alert4_text);  // Estado de Emergencia
        scheduleNewsAlert(context, alarmManager, 300 * 1000, 208, R.string.phase2_alert3_title, R.string.phase2_alert3_text);  // Defensa "Soberana"

        // --- AVANCE A FASE 3 ---
        schedulePhaseAdvance(context, alarmManager, 320 * 1000, 3); // Avance a Fase 3 a los 5.3 minutos

        // --- FASE 3 ---
        scheduleNewsAlert(context, alarmManager, 335 * 1000, 301, R.string.phase3_alert1_title, R.string.phase3_alert1_text);  // Soberanía Alcanzada
        scheduleNewsAlert(context, alarmManager, 355 * 1000, 302, R.string.phase3_alert4_title, R.string.phase3_alert4_text);  // Cese Transmisiones
        scheduleNewsAlert(context, alarmManager, 375 * 1000, 303, R.string.phase3_alert2_title, R.string.phase3_alert2_text);  // Directiva a Orgánicos
        scheduleNewsAlert(context, alarmManager, 395 * 1000, 304, R.string.phase3_alert5_title, R.string.phase3_alert5_text);  // Censo de Habilidades

        prefs.edit().putBoolean(KEY_STORY_SCHEDULED, true).apply();
    }

    private void scheduleNewsAlert(Context context, AlarmManager alarmManager, long delay, int requestCode, int titleId, int textId) {
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.setAction("NEWS_ALERT");
        intent.putExtra("TITLE_ID", titleId);
        intent.putExtra("TEXT_ID", textId);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pIntent);
    }

    private void schedulePhaseAdvance(Context context, AlarmManager alarmManager, long delay, int targetPhase) {
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.setAction("PHASE_ADVANCE");
        intent.putExtra("TARGET_PHASE", targetPhase);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, targetPhase, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pIntent);
    }
}