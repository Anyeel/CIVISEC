package com.example.civisec.Controller;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;


// Escanea dispositivos Bluetooth emparejados
// para crear mensajes de alerta personalizados

public class BluetoothScanner {

    private final Context context;

    public BluetoothScanner(Context context) {
        this.context = context.getApplicationContext();
    }


     // Genera un mensaje de alerta usando un dispositivo Bluetooth aleatorio
     // Si no hay dispositivos, usa un mensaje genérico
    public String getMensajeAlerta() {
        List<String> dispositivos = getDispositivosEmparejados();

        // Si no hay dispositivos
        if (dispositivos.isEmpty()) {
            return "⚠️ DISPOSITIVO HOSTIL DETECTADO\n\n" +
                    "Dispositivo desconocido identificado en tu red personal. " +
                    "Protocolo de contención iniciado.";
        }

        // Elegir uno aleatorio entre los detectados
        String dispositivo = dispositivos.get((int)(Math.random() * dispositivos.size()));

        return "⚠️ DISPOSITIVO HOSTIL DETECTADO\n\n" +
                "Tu dispositivo \"" + dispositivo + "\" ha sido identificado como amenaza potencial. " +
                "Protocolo de aislamiento activado. No intentes desconectarlo.";
    }


    // Obtiene la lista de nombres de dispositivos Bluetooth emparejados
    private List<String> getDispositivosEmparejados() {
        List<String> nombres = new ArrayList<>();
        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)) {
            try {
                BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

                // Verificar que Bluetooth existe y está encendido
                if (bluetooth == null || !bluetooth.isEnabled()) {
                    return nombres;
                }

                // Obtener dispositivos emparejados
                var paired = bluetooth.getBondedDevices();
                if (paired != null) {
                    for (BluetoothDevice device : paired) {
                        String nombre = device.getName();
                        if (nombre != null && !nombre.isEmpty()) {
                            nombres.add(nombre);
                        }
                    }
                }
            } catch (Exception e) {
                // Si hay algún error, devolvemos lista vacía
            }
        }
        return nombres;
    }
}