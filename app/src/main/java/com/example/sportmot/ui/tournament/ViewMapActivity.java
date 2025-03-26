package com.example.sportmot.ui.tournament;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import android.content.Context;

import com.example.sportmot.R;

public class ViewMapActivity extends AppCompatActivity {

    private MapView mapView;
    private IMapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Context context = this;

        Configuration.getInstance().load(
                context,
                context.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
        );


        mapView = findViewById(R.id.osmmap);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Center the map on a location
        mapController = mapView.getController();
        mapController.setZoom(17.0);
        //Need to be able to set new point for each tournament
        mapController.setCenter(new org.osmdroid.util.GeoPoint(64.1029, -21.8997)); // Example: Fífan, Iceland


        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);


        addMarkerToMap(64.1029, -21.8997);  // Example coordinates (Fífan, Iceland)
    }

    private void addMarkerToMap(double latitude, double longitude) {
        // Create a GeoPoint for the given latitude and longitude
        GeoPoint point = new GeoPoint(latitude, longitude);

        // Create a new marker
        Marker marker = new Marker(mapView);
        marker.setIcon(getResources().getDrawable(R.drawable.football_location));
        marker.setPosition(point);  // Set the marker's position
        marker.setTitle("Pin Title");  // Optional: Set a title for the marker
        marker.setSnippet("This is a marker at " + latitude + ", " + longitude);

        // Add the marker to the map
        mapView.getOverlays().add(marker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume(); // Resume map when Activity is active
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause(); // Pause map when Activity is not visible
    }
}
