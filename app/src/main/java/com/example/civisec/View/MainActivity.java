package com.example.civisec.View;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.civisec.Controller.Controller;
import com.example.civisec.R;
import com.google.android.material.card.MaterialCardView;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Controller controller;

    /**
     * Este es el "escuchador" que se activa cuando el sistema Android anuncia
     * que un dispositivo Bluetooth se ha conectado.
     */
    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                // Comprobamos la fase actual a través del Controller
                int currentPhase = controller.getCurrentPhase(context);

                // Solo actuamos si estamos en la Fase 3 o superior
                if (currentPhase >= 3) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    try {
                        String deviceName = device.getName();
                        if (deviceName != null) {
                            Log.d("CIVISEC_BT", "Fase 3: Dispositivo hostil detectado: " + deviceName);
                            // Le decimos al Controller que gestione la alerta y la notificación
                            controller.triggerHostileDeviceAlert(context, deviceName);
                        }
                    } catch (SecurityException e) {
                        // Este error no debería ocurrir si el permiso se concedió en el Splash,
                        // pero es una buena práctica de seguridad registrarlo.
                        Log.e("CIVISEC_BT", "Falta el permiso BLUETOOTH_CONNECT para obtener el nombre.");
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializamos el controlador y configuramos la navegación inferior.
        // Ya no pedimos permisos aquí.
        controller = new Controller();
        controller.setupBottomNavigation(this, R.id.nav_alerts);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cuando la app vuelve a estar en primer plano:
        // 1. Registramos nuestro "escuchador" de Bluetooth para que esté activo.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(bluetoothStateReceiver, filter);

        // 2. Actualizamos la pantalla para mostrar cualquier nueva alerta que se haya guardado.
        displaySavedAlerts();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cuando la app pasa a segundo plano, dejamos de escuchar para no gastar batería.
        // Es muy importante hacer esto para evitar "memory leaks".
        unregisterReceiver(bluetoothStateReceiver);
    }

    /**
     * Lee las alertas guardadas desde el Controller y las muestra en la pantalla
     * creando tarjetas de alerta dinámicamente.
     */
    private void displaySavedAlerts() {
        LinearLayout container = findViewById(R.id.alerts_container);
        Set<String> alerts = controller.getAlerts(this);

        if (alerts != null && !alerts.isEmpty()) {
            for (String alertMessage : alerts) {
                // Para evitar añadir la misma tarjeta varias veces, comprobamos si ya existe
                // una vista con el mismo "tag" (que será el mensaje de la alerta).
                if (container.findViewWithTag(alertMessage) == null) {
                    // --- Creamos la tarjeta de alerta desde cero ---

                    // 1. El contenedor exterior de la tarjeta
                    MaterialCardView card = new MaterialCardView(this);
                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    int marginInDp = 16;
                    int marginInPixels = (int) (marginInDp * getResources().getDisplayMetrics().density);
                    cardParams.setMargins(0, 0, 0, marginInPixels);
                    card.setLayoutParams(cardParams);
                    card.setRadius(12 * getResources().getDisplayMetrics().density);
                    card.setCardElevation(4 * getResources().getDisplayMetrics().density);
                    card.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    card.setTag(alertMessage); // Asignamos el tag para poder encontrarla después

                    // 2. El texto que va dentro de la tarjeta
                    TextView textView = new TextView(this);
                    int paddingInPixels = (int) (16 * getResources().getDisplayMetrics().density);
                    textView.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels);
                    textView.setText(alertMessage);
                    textView.setTextColor(getResources().getColor(R.color.red_alert));
                    textView.setTextSize(16);

                    // 3. Añadimos el texto a la tarjeta y la tarjeta al contenedor
                    card.addView(textView);
                    container.addView(card);
                }
            }
        }
    }
}