package com.example.civisec.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlertReceiver extends BroadcastReceiver {

    private static final String TAG = "AlertReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            Log.w(TAG, "Intent nulo o sin acción");
            return;
        }

        // IMPORTANTE: Pasar el context al constructor
        Controller controller = new Controller(context.getApplicationContext());
        String action = intent.getAction();

        Log.d(TAG, "Acción recibida: " + action);

        if ("NEWS_ALERT".equals(action)) {
            handleNewsAlert(context, controller, intent);
        } else if ("PHASE_ADVANCE".equals(action)) {
            handlePhaseAdvance(context, controller, intent);
        }
    }

    private void handleNewsAlert(Context context, Controller controller, Intent intent) {
        int titleResId = intent.getIntExtra("TITLE_ID", 0);
        int textResId = intent.getIntExtra("TEXT_ID", 0);

        if (titleResId != 0 && textResId != 0) {
            controller.triggerNewsAlert(titleResId, textResId);
            Log.d(TAG, "Noticia activada: " + titleResId);
        } else {
            Log.w(TAG, "IDs de recursos inválidos");
        }
    }

    private void handlePhaseAdvance(Context context, Controller controller, Intent intent) {
        int targetPhase = intent.getIntExtra("TARGET_PHASE", 0);

        if (targetPhase >= 2 && targetPhase <= 3) {
            controller.advanceToPhase(targetPhase);
            Log.d(TAG, "Avance a fase: " + targetPhase);
        } else {
            Log.w(TAG, "Fase inválida: " + targetPhase);
        }
    }
}