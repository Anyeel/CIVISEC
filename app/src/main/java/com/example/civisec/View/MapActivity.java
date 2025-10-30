package com.example.civisec.View;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.civisec.Controller.Controller;
import com.example.civisec.R;
import com.google.android.gms.location.LocationServices;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

 //Pantalla del mapa que muestra refugios

public class MapActivity extends AppCompatActivity {

    private Controller controller;
    private MapView mapa;
    private MyLocationNewOverlay locationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        controller = new Controller(this);
        controller.setupBottomNavigation(this, R.id.nav_map);

        mapa = findViewById(R.id.map_view);
        mapa.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapa.setBuiltInZoomControls(true);
        mapa.setMultiTouchControls(true);

        centrarMapa();
    }


    // Centra el mapa en la ubicación del usuario
    private void centrarMapa() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            var clienteUbicacion = LocationServices.getFusedLocationProviderClient(this);
            clienteUbicacion.getLastLocation().addOnSuccessListener(ubicacion -> {
                GeoPoint centro;

                if (ubicacion != null) {
                    centro = new GeoPoint(ubicacion.getLatitude(), ubicacion.getLongitude());

                    // --- NUEVO: Configurar y añadir el "punto azul" ---
                    locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapa);
                    locationOverlay.enableMyLocation(); // Activa el seguimiento de la ubicación
                    mapa.getOverlays().add(locationOverlay); // Añade la capa del punto azul al mapa
                    // --- FIN DEL CÓDIGO NUEVO ---

                } else {
                    centro = new GeoPoint(37.1773, -3.5986);
                    Toast.makeText(this, "No se pudo obtener la ubicación. Mostrando mapa general.", Toast.LENGTH_LONG).show();
                }

                mapa.getController().setZoom(15.0);
                mapa.getController().setCenter(centro);
                gestionarRefugios(centro);
            });
        } else {
            GeoPoint centro = new GeoPoint(37.1773, -3.5986);
            mapa.getController().setZoom(14.0);
            mapa.getController().setCenter(centro);
            gestionarRefugios(centro);
        }
    }

     // Gestiona los refugios: los genera si no existen, o los carga
    private void gestionarRefugios(GeoPoint centro) {
        Set<String> refugiosGuardados = controller.getRefugios();

        if (refugiosGuardados.isEmpty()) {
            // Generar nuevos refugios
            refugiosGuardados = generarRefugios(centro);
            controller.guardarRefugios(refugiosGuardados);
        }

        // Mostrar en el mapa
        mostrarRefugios(refugiosGuardados);
    }

     //Genera 7 ubicaciones aleatorias alrededor del centro
    private Set<String> generarRefugios(GeoPoint centro) {
        Set<String> ubicaciones = new HashSet<>();
        Random random = new Random();

        for (int i = 0; i < 7; i++) {
            double lat = centro.getLatitude() + (random.nextDouble() - 0.5) * 0.1;
            double lon = centro.getLongitude() + (random.nextDouble() - 0.5) * 0.1;
            ubicaciones.add(lat + "," + lon);
        }

        return ubicaciones;
    }

    // Muestra los marcadores de refugios en el mapa
    private void mostrarRefugios(Set<String> ubicaciones) {
        int fase = controller.getFaseActual();
        int numero = 1;

        for (String ubicacionStr : ubicaciones) {
            String[] partes = ubicacionStr.split(",");
            double lat = Double.parseDouble(partes[0]);
            double lon = Double.parseDouble(partes[1]);
            GeoPoint punto = new GeoPoint(lat, lon);

            // Crear marcador
            Marker marcador = new Marker(mapa);
            marcador.setPosition(punto);
            marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            Drawable shelterIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shelter_location, null);
            marcador.setIcon(shelterIcon);

            // Texto según fase
            if (fase < 3) {
                marcador.setTitle("Refugio #" + numero);
                marcador.setSnippet("Ubicación segura designada");
            } else {
                marcador.setTitle(getString(R.string.phase3_marker_title, numero));
                marcador.setSnippet(getString(R.string.phase3_marker_snippet));
            }

            mapa.getOverlays().add(marcador);
            numero++;
        }

        mapa.invalidate(); // Redibujar mapa
    }

    // Metodos importantes para el mapa open source
    @Override
    protected void onResume() {
        super.onResume();
        mapa.onResume();
        // NUEVO: Asegurarse de que el seguimiento de la ubicación se reanuda
        if (locationOverlay != null) {
            locationOverlay.enableMyLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapa.onPause();
        // NUEVO: Asegurarse de que el seguimiento se pausa para ahorrar batería
        if (locationOverlay != null) {
            locationOverlay.disableMyLocation();
        }
    }
}
