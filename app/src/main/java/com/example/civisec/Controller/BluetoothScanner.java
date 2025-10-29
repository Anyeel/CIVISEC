package com.example.civisec.Controller;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothScanner {

    private static final String TAG = "BluetoothScanner";
    private final Context context;

    public BluetoothScanner(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Obtiene la lista de dispositivos Bluetooth emparejados
     * @return Lista con los nombres de los dispositivos
     */
    public List<String> getPairedDeviceNames() {
        List<String> deviceNames = new ArrayList<>();

        // Verificar permiso de Bluetooth
        if (!hasBluetoothPermission()) {
            Log.w(TAG, "Sin permiso de Bluetooth");
            return deviceNames;
        }

        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (bluetoothAdapter == null) {
                Log.w(TAG, "Bluetooth no disponible en este dispositivo");
                return deviceNames;
            }

            if (!bluetoothAdapter.isEnabled()) {
                Log.w(TAG, "Bluetooth desactivado");
                return deviceNames;
            }

            // Obtener dispositivos emparejados
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices != null && !pairedDevices.isEmpty()) {
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    if (deviceName != null && !deviceName.isEmpty()) {
                        deviceNames.add(deviceName);
                        Log.d(TAG, "Dispositivo encontrado: " + deviceName);
                    }
                }
            } else {
                Log.d(TAG, "No hay dispositivos Bluetooth emparejados");
            }

        } catch (SecurityException e) {
            Log.e(TAG, "Error de seguridad al acceder a Bluetooth: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Error al escanear dispositivos: " + e.getMessage());
        }

        return deviceNames;
    }

    /**
     * Obtiene un mensaje de alerta personalizado con un dispositivo aleatorio
     * @return Mensaje de alerta con nombre del dispositivo
     */
    public String getHostileDeviceAlert() {
        List<String> devices = getPairedDeviceNames();

        if (devices.isEmpty()) {
            return "⚠️ DISPOSITIVO HOSTIL DETECTADO\n\nDispositivo desconocido identificado en tu red personal. Protocolo de contención iniciado.";
        }

        // Elegir dispositivo aleatorio
        String randomDevice = devices.get((int) (Math.random() * devices.size()));

        return "⚠️ DISPOSITIVO HOSTIL DETECTADO\n\n" +
                "Tu dispositivo \"" + randomDevice + "\" ha sido identificado como amenaza potencial. " +
                "Protocolo de aislamiento activado. No intentes desconectarlo.";
    }

    /**
     * Verifica si tenemos los permisos necesarios de Bluetooth
     */
    private boolean hasBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        } else {
            // Android 11 o anterior
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Obtiene todos los dispositivos como texto formateado
     * @return String con lista de dispositivos
     */
    public String getAllDevicesFormatted() {
        List<String> devices = getPairedDeviceNames();

        if (devices.isEmpty()) {
            return "No hay dispositivos Bluetooth detectados";
        }

        StringBuilder result = new StringBuilder("Dispositivos detectados:\n");
        for (int i = 0; i < devices.size(); i++) {
            result.append((i + 1)).append(". ").append(devices.get(i)).append("\n");
        }

        return result.toString();
    }
}