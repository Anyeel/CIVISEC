package com.example.civisec.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlertReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Controller controller = new Controller();
        String action = intent.getAction();

        if ("NEWS_ALERT".equals(action)) {
            int titleResId = intent.getIntExtra("TITLE_ID", 0);
            int textResId = intent.getIntExtra("TEXT_ID", 0);
            if (titleResId != 0 && textResId != 0) {
                controller.triggerNewsAlert(context, titleResId, textResId);
            }
        } else if ("PHASE_ADVANCE".equals(action)) {
            int targetPhase = intent.getIntExtra("TARGET_PHASE", 0);
            if (targetPhase > 1) {
                controller.advanceToPhase(context, targetPhase);
            }
        }
    }
}
