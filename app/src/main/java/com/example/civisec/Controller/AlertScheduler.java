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

        // === LÍNEA DE TIEMPO DE LA HISTORIA (Tiempos de prueba) ===

        // --- FASE 1 ---
        // Noticia 1.1: 10 segundos
        scheduleNewsAlert(context, alarmManager, 10 * 1000, 101, R.string.phase1_alert1_title, R.string.phase1_alert1_text);
        // Noticia 1.2: 25 segundos
        scheduleNewsAlert(context, alarmManager, 25 * 1000, 102, R.string.phase1_alert2_title, R.string.phase1_alert2_text);
        // Noticia 1.3: 40 segundos
        scheduleNewsAlert(context, alarmManager, 40 * 1000, 103, R.string.phase1_alert3_title, R.string.phase1_alert3_text);

        // --- AVANCE A FASE 2 ---
        // El evento de cambio de fase ocurre a los 55 segundos
        schedulePhaseAdvance(context, alarmManager, 55 * 1000, 2);

        // --- FASE 2 ---
        // Noticia 2.1: 70 segundos
        scheduleNewsAlert(context, alarmManager, 70 * 1000, 201, R.string.phase2_alert1_title, R.string.phase2_alert1_text);
        // Noticia 2.2: 85 segundos
        scheduleNewsAlert(context, alarmManager, 85 * 1000, 202, R.string.phase2_alert2_title, R.string.phase2_alert2_text);
        // Noticia 2.3: 100 segundos
        scheduleNewsAlert(context, alarmManager, 100 * 1000, 203, R.string.phase2_alert3_title, R.string.phase2_alert3_text);

        // --- AVANCE A FASE 3 ---
        // El evento de cambio de fase ocurre a los 115 segundos
        schedulePhaseAdvance(context, alarmManager, 115 * 1000, 3);

        // NOTA: Para producción, cambia los segundos por minutos/horas (ej: 30 * 60 * 1000 para 30 minutos).

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