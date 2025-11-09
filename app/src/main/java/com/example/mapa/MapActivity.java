package com.example.mapa;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private String businessName;
    private String businessAddress;
    private LatLng businessLocation;
    private Button directionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        businessName = getIntent().getStringExtra("businessName");
        businessAddress = getIntent().getStringExtra("businessAddress");

        if (businessAddress == null || businessAddress.isEmpty()) {
            Toast.makeText(this, "La dirección no está disponible", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        directionsButton = findViewById(R.id.directions_button);
        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDirections();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Error al cargar el mapa", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "MapFragment es null");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        
        if (mMap == null) {
            Toast.makeText(this, "Error al inicializar el mapa", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar que Geocoder esté disponible
        if (!Geocoder.isPresent()) {
            Toast.makeText(this, "El servicio de geocodificación no está disponible", Toast.LENGTH_SHORT).show();
            directionsButton.setEnabled(false);
            // Mostrar un mapa genérico como fallback
            LatLng defaultLocation = new LatLng(0, 0);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 2));
            return;
        }

        geocodeAddress();
    }

    private void geocodeAddress() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        
        try {
            Log.d(TAG, "Geocodificando dirección: " + businessAddress);
            List<Address> addresses = geocoder.getFromLocationName(businessAddress, 5);
            
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                businessLocation = new LatLng(address.getLatitude(), address.getLongitude());
                
                Log.d(TAG, "Dirección encontrada: " + businessLocation.toString());
                
                // Agregar marcador
                mMap.addMarker(new MarkerOptions()
                        .position(businessLocation)
                        .title(businessName != null ? businessName : "Negocio")
                        .snippet(businessAddress));
                
                // Mover cámara al negocio
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(businessLocation, 15));
                directionsButton.setEnabled(true);
                
                Toast.makeText(this, "Ubicación encontrada", Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "No se encontraron resultados para la dirección");
                Toast.makeText(this, 
                    "No se pudo encontrar la ubicación. Intenta con una dirección más específica.", 
                    Toast.LENGTH_LONG).show();
                directionsButton.setEnabled(false);
                
                // Mostrar mapa con una vista general como fallback
                // Podrías usar una ubicación por defecto de tu país/región
                LatLng defaultLocation = new LatLng(-34.6037, -58.3816); // Buenos Aires como ejemplo
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5));
            }
        } catch (IOException e) {
            Log.e(TAG, "Error al geocodificar dirección", e);
            Toast.makeText(this, 
                "Error al buscar la ubicación. Verifica tu conexión a internet.", 
                Toast.LENGTH_LONG).show();
            directionsButton.setEnabled(false);
            
            // Mostrar mapa con vista general como fallback
            LatLng defaultLocation = new LatLng(-34.6037, -58.3816);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5));
        }
    }

    private void openDirections() {
        if (businessLocation != null) {
            // Intentar abrir Google Maps app para obtener indicaciones
            String destination = businessLocation.latitude + "," + businessLocation.longitude;
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                // Abrir en la app de Google Maps
                startActivity(mapIntent);
            } else {
                // Si no tiene Google Maps instalado, abrir en navegador web con Google Maps
                Uri webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + destination);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, webUri);
                
                if (browserIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(browserIntent);
                } else {
                    Toast.makeText(this, 
                        "No se pudo abrir Google Maps. Por favor, instala Google Maps o verifica tu conexión a internet.", 
                        Toast.LENGTH_LONG).show();
                    Log.e(TAG, "No se pudo abrir Google Maps ni el navegador");
                }
            }
        } else {
            Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
        }
    }
}