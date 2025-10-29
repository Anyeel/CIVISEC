package com.example.civisec.View;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import com.example.civisec.Controller.Controller;
import com.example.civisec.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MapActivity";
    private Controller controller;
    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        controller = new Controller(this);
        controller.setupBottomNavigation(this, R.id.nav_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.map_view);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        centerMapOnUserLocation();
    }

    /**
     * Paso 1: Intenta obtener la ubicación del usuario y centrar el mapa.
     * Una vez centrado, llama al método para gestionar los marcadores.
     */
    private void centerMapOnUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                GeoPoint startPoint;
                if (location != null) {
                    startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    setupMyLocationOverlay(); // Muestra el punto azul
                } else {
                    Toast.makeText(this, "No se pudo obtener ubicación. Mostrando mapa general.", Toast.LENGTH_LONG).show();
                    startPoint = new GeoPoint(37.1773, -3.5986); // Fallback a Granada
                }
                MapController mapController = (MapController) mapView.getController();
                mapController.setZoom(15.0);
                mapController.setCenter(startPoint);
                manageShelterMarkers(startPoint); // Llama al siguiente paso
            });
        } else {
            GeoPoint startPoint = new GeoPoint(37.1773, -3.5986); // Fallback si no hay permiso
            MapController mapController = (MapController) mapView.getController();
            mapController.setZoom(14.0);
            mapController.setCenter(startPoint);
            manageShelterMarkers(startPoint); // Llama al siguiente paso
        }
    }

    /**
     * Paso 2: Decide si generar nuevos refugios o cargar los existentes.
     */
    private void manageShelterMarkers(GeoPoint center) {
        Set<String> savedShelters = controller.getShelterLocations();

        if (savedShelters.isEmpty()) {
            // No hay refugios guardados: los generamos, los guardamos y los mostramos.
            Log.d(TAG, "Generando y guardando nuevas ubicaciones de refugios.");
            Set<String> newShelterStrings = generateAndSaveNewShelters(center);
            displayMarkers(newShelterStrings);
        } else {
            // Ya tenemos refugios guardados: simplemente los mostramos.
            Log.d(TAG, "Cargando ubicaciones de refugios guardadas.");
            displayMarkers(savedShelters);
        }
    }

    /**
     * Paso 2.1 (Solo si es necesario): Genera nuevas ubicaciones aleatorias y las guarda.
     */
    private Set<String> generateAndSaveNewShelters(GeoPoint center) {
        int markerCount = 7;
        double radius = 0.05;
        Set<String> newLocations = new HashSet<>();
        Random random = new Random();

        for (int i = 0; i < markerCount; i++) {
            double lat = center.getLatitude() + (random.nextDouble() - 0.5) * radius * 2;
            double lon = center.getLongitude() + (random.nextDouble() - 0.5) * radius * 2;
            newLocations.add(lat + "," + lon);
        }
        controller.saveShelterLocations(newLocations);
        return newLocations;
    }

    /**
     * Paso 3: Muestra los marcadores en el mapa, adaptando el texto a la fase actual.
     */
    private void displayMarkers(Set<String> locations) {
        int currentPhase = controller.getCurrentPhase();
        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shelter_location, null);
        int i = 1;

        for (String locString : locations) {
            String[] parts = locString.split(",");
            GeoPoint location = new GeoPoint(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
            String title, snippet;

            if (currentPhase < 3) {
                title = "Refugio #" + i;
                snippet = "Ubicación segura designada";
            } else {
                title = String.format(getString(R.string.phase3_marker_title), i);
                snippet = getString(R.string.phase3_marker_snippet);
            }
            mapView.getOverlays().add(createMarker(location, title, snippet, icon));
            i++;
        }
        mapView.invalidate(); // Refresca el mapa para dibujar los marcadores
    }

    // --- Métodos de Ayuda ---
    private Marker createMarker(GeoPoint p, String title, String snippet, Drawable icon) {
        Marker marker = new Marker(mapView);
        marker.setPosition(p);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        marker.setSnippet(snippet);
        marker.setIcon(icon);
        return marker;
    }

    private void setupMyLocationOverlay() {
        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(this.locationOverlay);
    }

    // --- Métodos de Ciclo de Vida (obligatorios para osmdroid) ---
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (locationOverlay != null) locationOverlay.enableMyLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (locationOverlay != null) locationOverlay.disableMyLocation();
    }
}