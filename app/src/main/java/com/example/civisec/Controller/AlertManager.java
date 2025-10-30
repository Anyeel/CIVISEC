package com.example.civisec.Controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import com.example.civisec.R;

public class AlertManager extends BroadcastReceiver {

    // Tiempos: 10 segundos para desarrollo, 1 minuto para producción
    private static final long TIEMPO_DEV = 10000;
    private static final long TIEMPO_PROD = 60000;

    // RECEPTOR DE EVENTOS


    // Recibe las alarmas programadas y las ejecuta
    // Funciona incluso con el telefono apagado
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        Controller controller = new Controller(context);
        String accion = intent.getAction();

        if ("NOTICIA".equals(accion)) {
            // Activar una noticia
            int titulo = intent.getIntExtra("TITULO", 0);
            int texto = intent.getIntExtra("TEXTO", 0);
            controller.activarNoticia(titulo, texto);

        } else if ("FASE".equals(accion)) {
            // Avanzar de fase
            int fase = intent.getIntExtra("FASE", 0);
            controller.avanzarFase(fase);

        } else if ("BLUETOOTH".equals(accion)) {
            // Alerta especial de dispositivo Bluetooth
            BluetoothScanner scanner = new BluetoothScanner(context);
            String mensaje = scanner.getMensajeAlerta();
            controller.enviarNotificacion("AMENAZA DETECTADA", mensaje);
        }
    }

    // PROGRAMACIÓN DE EVENTOS

     // Programa toda la historia de eventos desde el inicio
    public void programarHistoria(Context context) {
        // Verificar si ya está programada
        var prefs = context.getSharedPreferences("CIVISEC_PREFS", Context.MODE_PRIVATE);
        //no usamos un booleano normal porque la app se cierra y se perderia el valor
        if (prefs.getBoolean("PROGRAMADA", false)) return;

        Controller controller = new Controller(context);
        long tiempo = controller.isModoDesarrollo() ? TIEMPO_DEV : TIEMPO_PROD;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // FASE 1
        programarNoticia(context, alarmManager, 1 * tiempo, 101,
                R.string.phase1_alert6_title, R.string.phase1_alert6_text);
        programarNoticia(context, alarmManager, 3 * tiempo, 102,
                R.string.phase1_alert1_title, R.string.phase1_alert1_text);
        programarNoticia(context, alarmManager, 5 * tiempo, 103,
                R.string.phase1_alert5_title, R.string.phase1_alert5_text);
        programarNoticia(context, alarmManager, 7 * tiempo, 104,
                R.string.phase1_alert4_title, R.string.phase1_alert4_text);
        programarNoticia(context, alarmManager, 9 * tiempo, 105,
                R.string.phase1_alert2_title, R.string.phase1_alert2_text);
        programarNoticia(context, alarmManager, 12 * tiempo, 106,
                R.string.phase1_alert9_title, R.string.phase1_alert9_text);
        programarNoticia(context, alarmManager, 14 * tiempo, 107,
                R.string.phase1_alert3_title, R.string.phase1_alert3_text);

        // Avanzar a fase 2
        programarFase(context, alarmManager, 15 * tiempo, 2);

        //FASE 2
        programarNoticia(context, alarmManager, 16 * tiempo, 201,
                R.string.phase2_alert1_title, R.string.phase2_alert1_text);
        programarNoticia(context, alarmManager, 18 * tiempo, 202,
                R.string.phase2_alert7_title, R.string.phase2_alert7_text);
        programarNoticia(context, alarmManager, 20 * tiempo, 203,
                R.string.phase2_alert2_title, R.string.phase2_alert2_text);
        programarNoticia(context, alarmManager, 22 * tiempo, 204,
                R.string.phase2_alert8_title, R.string.phase2_alert8_text);
        programarNoticia(context, alarmManager, 24 * tiempo, 205,
                R.string.phase2_alert5_title, R.string.phase2_alert5_text);
        programarNoticia(context, alarmManager, 26 * tiempo, 206,
                R.string.phase2_alert9_title, R.string.phase2_alert9_text);
        programarNoticia(context, alarmManager, 28 * tiempo, 207,
                R.string.phase2_alert4_title, R.string.phase2_alert4_text);
        programarNoticia(context, alarmManager, 29 * tiempo, 208,
                R.string.phase2_alert3_title, R.string.phase2_alert3_text);

        // Avanzar a fase 3
        programarFase(context, alarmManager, 30 * tiempo, 3);

        //FASE 3
        programarNoticia(context, alarmManager, 32 * tiempo, 301,
                R.string.phase3_alert1_title, R.string.phase3_alert1_text);
        programarBluetooth(context, alarmManager, 34 * tiempo, 305);
        programarNoticia(context, alarmManager, 35 * tiempo, 302,
                R.string.phase3_alert4_title, R.string.phase3_alert4_text);
        programarNoticia(context, alarmManager, 37 * tiempo, 303,
                R.string.phase3_alert2_title, R.string.phase3_alert2_text);
        programarNoticia(context, alarmManager, 40 * tiempo, 304,
                R.string.phase3_alert5_title, R.string.phase3_alert5_text);

        // Marcar como programada
        prefs.edit().putBoolean("PROGRAMADA", true).apply();
    }

    // Programa una noticia para que aparezca en el futuro
    private void programarNoticia(Context context, AlarmManager alarmManager,
                                  long retardo, int codigo, int tituloId, int textoId) {
        // Creamos un Intent con la información de la noticia futura.
        Intent intent = new Intent(context, AlertManager.class);
        intent.setAction("NOTICIA");
        intent.putExtra("TITULO", tituloId);
        intent.putExtra("TEXTO", textoId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, codigo, intent, PendingIntent.FLAG_IMMUTABLE
        );

        long momento = SystemClock.elapsedRealtime() + retardo;
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, momento, pendingIntent);
    }

    // Programa un cambio de fase
    private void programarFase(Context context, AlarmManager alarmManager,
                               long retardo, int fase) {
        // Creamos un Intent con la información de la fase.
        Intent intent = new Intent(context, AlertManager.class);
        intent.setAction("FASE");
        intent.putExtra("FASE", fase);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 1000 + fase, intent, PendingIntent.FLAG_IMMUTABLE
        );

        long momento = SystemClock.elapsedRealtime() + retardo;
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, momento, pendingIntent);
    }

    // Programa la alerta especial de Bluetooth
    private void programarBluetooth(Context context, AlarmManager alarmManager,
                                    long retardo, int codigo) {
        Intent intent = new Intent(context, AlertManager.class);
        intent.setAction("BLUETOOTH");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, codigo, intent, PendingIntent.FLAG_IMMUTABLE
        );

        long momento = SystemClock.elapsedRealtime() + retardo;
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, momento, pendingIntent);
    }

    // Reinicia la programación (para volver a empezar)
    // Coge las preferencias y pone nuestro Bool a false
    public void reiniciar(Context context) {
        var prefs = context.getSharedPreferences("CIVISEC_PREFS", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("PROGRAMADA", false).apply();
    }
}