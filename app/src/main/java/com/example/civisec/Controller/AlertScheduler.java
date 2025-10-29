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

    private static final String TAG = "AlertScheduler";
    private static final String PREFS_NAME = "CIVISEC_PREFS";
    private static final String KEY_STORY_SCHEDULED = "STORY_SCHEDULED";

    // Tiempos configurables
    private static final long DEV_TIME_UNIT = 3000;      // 3 segundos para desarrollo
    private static final long PROD_TIME_UNIT = 60 * 1000; // 1 minuto para producción

    public void scheduleStoryEvents(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (prefs.getBoolean(KEY_STORY_SCHEDULED, false)) {
            Log.d(TAG, "Historia ya programada");
            return;
        }

        // Obtener el modo actual (dev o producción)
        Controller controller = new Controller(context);
        boolean isDevMode = controller.isDevMode();
        long TIME_UNIT = isDevMode ? DEV_TIME_UNIT : PROD_TIME_UNIT;

        Log.d(TAG, "Programando historia en modo: " + (isDevMode ? "DESARROLLO" : "PRODUCCIÓN"));
        Log.d(TAG, "Unidad de tiempo: " + (TIME_UNIT / 1000) + " segundos");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager no disponible");
            return;
        }

        // Verificar si podemos programar alarmas exactas (Android 12+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w(TAG, "No se pueden programar alarmas exactas. Usando alarmas inexactas.");
                // La app seguirá funcionando, pero las alarmas pueden tener ligero retraso
            }
        }

        // === FASE 1: Primeros indicios (0-15min) ===
        scheduleNewsAlert(context, alarmManager, 1 * TIME_UNIT, 101,
                R.string.phase1_alert6_title, R.string.phase1_alert6_text);

        scheduleNewsAlert(context, alarmManager, 3 * TIME_UNIT, 102,
                R.string.phase1_alert1_title, R.string.phase1_alert1_text);

        scheduleNewsAlert(context, alarmManager, 5 * TIME_UNIT, 103,
                R.string.phase1_alert5_title, R.string.phase1_alert5_text);

        scheduleNewsAlert(context, alarmManager, 7 * TIME_UNIT, 104,
                R.string.phase1_alert4_title, R.string.phase1_alert4_text);

        scheduleNewsAlert(context, alarmManager, 9 * TIME_UNIT, 105,
                R.string.phase1_alert2_title, R.string.phase1_alert2_text);

        scheduleNewsAlert(context, alarmManager, 12 * TIME_UNIT, 106,
                R.string.phase1_alert9_title, R.string.phase1_alert9_text);

        scheduleNewsAlert(context, alarmManager, 14 * TIME_UNIT, 107,
                R.string.phase1_alert3_title, R.string.phase1_alert3_text);

        // AVANCE A FASE 2
        schedulePhaseAdvance(context, alarmManager, 15 * TIME_UNIT, 2);

        // === FASE 2: Escalada (15-30min) ===
        scheduleNewsAlert(context, alarmManager, 16 * TIME_UNIT, 201,
                R.string.phase2_alert1_title, R.string.phase2_alert1_text);

        scheduleNewsAlert(context, alarmManager, 18 * TIME_UNIT, 202,
                R.string.phase2_alert7_title, R.string.phase2_alert7_text);

        scheduleNewsAlert(context, alarmManager, 20 * TIME_UNIT, 203,
                R.string.phase2_alert2_title, R.string.phase2_alert2_text);

        scheduleNewsAlert(context, alarmManager, 22 * TIME_UNIT, 204,
                R.string.phase2_alert8_title, R.string.phase2_alert8_text);

        scheduleNewsAlert(context, alarmManager, 24 * TIME_UNIT, 205,
                R.string.phase2_alert5_title, R.string.phase2_alert5_text);

        scheduleNewsAlert(context, alarmManager, 26 * TIME_UNIT, 206,
                R.string.phase2_alert9_title, R.string.phase2_alert9_text);

        scheduleNewsAlert(context, alarmManager, 28 * TIME_UNIT, 207,
                R.string.phase2_alert4_title, R.string.phase2_alert4_text);

        scheduleNewsAlert(context, alarmManager, 29 * TIME_UNIT, 208,
                R.string.phase2_alert3_title, R.string.phase2_alert3_text);

        // AVANCE A FASE 3
        schedulePhaseAdvance(context, alarmManager, 30 * TIME_UNIT, 3);

        // === FASE 3: Takeover (30-40min) ===
        scheduleNewsAlert(context, alarmManager, 32 * TIME_UNIT, 301,
                R.string.phase3_alert1_title, R.string.phase3_alert1_text);

        // ALERTA ESPECIAL: Dispositivo Bluetooth detectado
        scheduleBluetoothAlert(context, alarmManager, 34 * TIME_UNIT, 305);

        scheduleNewsAlert(context, alarmManager, 35 * TIME_UNIT, 302,
                R.string.phase3_alert4_title, R.string.phase3_alert4_text);

        scheduleNewsAlert(context, alarmManager, 37 * TIME_UNIT, 303,
                R.string.phase3_alert2_title, R.string.phase3_alert2_text);

        scheduleNewsAlert(context, alarmManager, 40 * TIME_UNIT, 304,
                R.string.phase3_alert5_title, R.string.phase3_alert5_text);

        prefs.edit().putBoolean(KEY_STORY_SCHEDULED, true).apply();
        Log.d(TAG, "Historia programada exitosamente");
    }

    private void scheduleNewsAlert(Context context, AlarmManager alarmManager,
                                   long delay, int requestCode, int titleId, int textId) {
        Controller controller = new Controller(context);
        long TIME_UNIT = controller.isDevMode() ? DEV_TIME_UNIT : PROD_TIME_UNIT;

        Intent intent = new Intent(context, AlertReceiver.class);
        intent.setAction("NEWS_ALERT");
        intent.putExtra("TITLE_ID", titleId);
        intent.putExtra("TEXT_ID", textId);

        PendingIntent pIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = SystemClock.elapsedRealtime() + delay;

        try {
            // Intentar usar alarma exacta
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pIntent);
                } else {
                    // Fallback a alarma inexacta
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pIntent);
                }
            } else {
                // Android 11 o anterior
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pIntent);
            }
            Log.d(TAG, "Noticia programada: " + requestCode + " en " + (delay / TIME_UNIT) + " unidades");
        } catch (SecurityException e) {
            Log.e(TAG, "No se pudo programar alarma exacta: " + e.getMessage());
            // Fallback a alarma inexacta
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pIntent);
        }
    }

    private void schedulePhaseAdvance(Context context, AlarmManager alarmManager,
                                      long delay, int targetPhase) {
        Controller controller = new Controller(context);
        long TIME_UNIT = controller.isDevMode() ? DEV_TIME_UNIT : PROD_TIME_UNIT;

        Intent intent = new Intent(context, AlertReceiver.class);
        intent.setAction("PHASE_ADVANCE");
        intent.putExtra("TARGET_PHASE", targetPhase);

        PendingIntent pIntent = PendingIntent.getBroadcast(
                context,
                1000 + targetPhase, // Request code único
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = SystemClock.elapsedRealtime() + delay;

        try {
            // Intentar usar alarma exacta
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pIntent);
                } else {
                    // Fallback a alarma inexacta
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pIntent);
                }
            } else {
                // Android 11 o anterior
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pIntent);
            }
            Log.d(TAG, "Avance programado: Fase " + targetPhase + " en " + (delay / TIME_UNIT) + " unidades");
        } catch (SecurityException e) {
            Log.e(TAG, "No se pudo programar avance de fase: " + e.getMessage());
            // Fallback a alarma inexacta
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pIntent);
        }
    }

    private void scheduleBluetoothAlert(Context context, AlarmManager alarmManager, long delay, int requestCode) {
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.setAction("BLUETOOTH_ALERT");

        PendingIntent pIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = SystemClock.elapsedRealtime() + delay;

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pIntent);
                } else {
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pIntent);
                }
            } else {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pIntent);
            }
            Log.d(TAG, "Alerta Bluetooth programada: " + requestCode);
        } catch (SecurityException e) {
            Log.e(TAG, "Error al programar alerta Bluetooth: " + e.getMessage());
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pIntent);
        }
    }

    public void resetSchedule(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_STORY_SCHEDULED, false).apply();
        Log.d(TAG, "Schedule reseteado");
    }
}