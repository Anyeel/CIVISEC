package com.example.civisec.View;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.example.civisec.Controller.AlertManager;
import com.example.civisec.Controller.BluetoothScanner;
import com.example.civisec.Controller.Controller;
import com.example.civisec.R;
import com.google.android.material.card.MaterialCardView;

import org.osmdroid.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Controller controller;
    private LinearLayout contenedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Configuracion de OpenStreetMap
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_main);

        controller = new Controller(this);
        contenedor = findViewById(R.id.alerts_container);

        // Configurar navegación
        controller.setupBottomNavigation(this, R.id.nav_alerts);

        // Programar eventos de la historia (solo la primera vez)
        new AlertManager().programarHistoria(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarPantalla();
    }

    // Actualiza la pantalla según la fase actual
    private void actualizarPantalla() {
        limpiarTarjetas();

        // En fase 3, añadir tarjeta especial de Bluetooth PRIMERO
        if (controller.getFaseActual() >= 3) {
            mostrarAlertaBluetooth();
        }

        // Después mostrar noticias activadas
        mostrarNoticias();
    }

    // Muestra todas las noticias activadas
    private void mostrarNoticias() {
        List<Noticia> noticias = parsearNoticias();

        // Ordenar de más reciente a más antigua (orden DESCENDENTE por codigo)
        noticias.sort((a, b) -> Integer.compare(b.codigo, a.codigo));

        // Crear tarjeta para cada noticia
        for (Noticia noticia : noticias) {
            crearTarjetaNoticia(noticia.tituloId, noticia.textoId);
        }
    }

    // Muestra la alerta especial de la fase 3 con dispositivo Bluetooth

    private void mostrarAlertaBluetooth() {
        BluetoothScanner scanner = new BluetoothScanner(this);
        String mensaje = scanner.getMensajeAlerta();

        // Crear tarjeta negra especial
        MaterialCardView tarjeta = new MaterialCardView(this);
        tarjeta.setRadius(dpAPx(12));
        tarjeta.setCardBackgroundColor(getResources().getColor(android.R.color.black, null));
        tarjeta.setTag("dinamica");

        TextView texto = new TextView(this);
        int padding = dpAPx(24);
        texto.setPadding(padding, padding, padding, padding);
        texto.setText(mensaje);
        texto.setTextColor(getResources().getColor(R.color.red_alert, null));
        texto.setTextSize(18);
        texto.setTypeface(null, android.graphics.Typeface.BOLD);

        tarjeta.addView(texto);

        // Añadir al principio (después de los títulos - índice 2)
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpAPx(16));
        tarjeta.setLayoutParams(params);

        contenedor.addView(tarjeta, 2); // Posición 2: después del título y subtítulo
    }


    // Crea una tarjeta blanca para una noticia

    private void crearTarjetaNoticia(int tituloId, int textoId) {
        MaterialCardView tarjeta = new MaterialCardView(this);
        tarjeta.setRadius(dpAPx(12));
        tarjeta.setCardBackgroundColor(getResources().getColor(android.R.color.white, null));
        tarjeta.setTag("dinamica");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = dpAPx(16);
        layout.setPadding(padding, padding, padding, padding);

        // Título
        TextView titulo = new TextView(this);
        titulo.setText(tituloId);
        titulo.setTextSize(16);
        titulo.setTextColor(getResources().getColor(R.color.red_alert, null));
        titulo.setTypeface(null, android.graphics.Typeface.BOLD);

        // Texto
        TextView texto = new TextView(this);
        texto.setText(textoId);
        texto.setTextSize(14);
        texto.setTextColor(getResources().getColor(android.R.color.black, null));

        layout.addView(titulo);
        layout.addView(texto);
        tarjeta.addView(layout);

        // Añadir al contenedor
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpAPx(16));
        tarjeta.setLayoutParams(params);

        contenedor.addView(tarjeta);
    }


    // Convierte las noticias guardadas a objetos Noticia
    private List<Noticia> parsearNoticias() {
        List<Noticia> lista = new ArrayList<>();
        for (String noticiaStr : controller.getNoticias()) {
            String[] partes = noticiaStr.split("\\|");
            // Compatibilidad con formato antiguo (2 valores) y nuevo (3 valores)
            if (partes.length == 3) {
                int codigo = Integer.parseInt(partes[0]);
                int titulo = Integer.parseInt(partes[1]);
                int texto = Integer.parseInt(partes[2]);
                lista.add(new Noticia(codigo, titulo, texto));
            } else if (partes.length == 2) {
                // Formato antiguo: asignar codigo = tituloId
                int titulo = Integer.parseInt(partes[0]);
                int texto = Integer.parseInt(partes[1]);
                lista.add(new Noticia(titulo, titulo, texto));
            }
        }
        return lista;
    }

    // Elimina todas las tarjetas dinámicas
    private void limpiarTarjetas() {
        for (int i = contenedor.getChildCount() - 1; i >= 0; i--) {
            View hijo = contenedor.getChildAt(i);
            if ("dinamica".equals(hijo.getTag())) {
                contenedor.removeViewAt(i);
            }
        }
    }

    // Convierte dp a píxeles
    private int dpAPx(int dp) {
        return (int)(dp * getResources().getDisplayMetrics().density);
    }

    // Clase auxiliar para almacenar noticias
    private static class Noticia {
        int codigo, tituloId, textoId;
        Noticia(int codigo, int titulo, int texto) {
            this.codigo = codigo;
            this.tituloId = titulo;
            this.textoId = texto;
        }
    }
}