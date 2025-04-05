package com.example.sportmot.ui.tournament;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sportmot.DatabaseHelper;
import com.example.sportmot.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.api.IMapController;
import android.location.Location;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This activity uses Open Street Map to display a map of the world. We have in addLocationFragment
 * put a location for the tournament and this is fetched from the local DB to put a pin on the
 * map to show the location. We also ask the user for permission to locate the device and if you
 * get approval you show the way between the users device and the tournament location.
 * */

public class ViewMapActivity extends AppCompatActivity {

    private static final String TAG = "ViewMapActivity";
    private static final int LOCATION_PERMISSION_REQUEST = 1;

    private MapView mapView;
    private IMapController mapController;
    private int tournamentID;
    private DatabaseHelper dbHelper;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize DatabaseHelper (local db)
        dbHelper = new DatabaseHelper(this);

        // Retrieve tournament ID from intent
        tournamentID = getIntent().getIntExtra("TOURNAMENT_ID", -1);
        if (tournamentID == -1) {
            Log.e(TAG, "Error: Tournament ID not passed correctly");
            Toast.makeText(this, "Invalid Tournament ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize map
        Configuration.getInstance().load(this, getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE));
        mapView = findViewById(R.id.osmmap);
        if (mapView == null) {
            Log.e(TAG, "Error: mapView is NULL.");
            return;
        }

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapController.setZoom(18.0);

        fetchTournamentLocation(tournamentID);

        // Back button to go back to previous page
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(v -> finish());

        // Request location permission
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkLocationPermission();
    }

    //Ask for location permission from user
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            requestLocationUpdates();
        }
    }

    // Handle permission result. If we get the permission we fetch the user location otherwise we
    // only show the tournament location.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();  // User granted permission, so we can fetch the user location
            } else {
                Toast.makeText(this, "Location permission denied! Showing tournament location.", Toast.LENGTH_SHORT).show();
                // User denied permission, only show tournament location
                fetchTournamentLocation(tournamentID);
            }
        }
    }

    /*Get the location latitude and longitude associated to the tournamentID. Put a marker
    * to the location. If there is no location we get a pin at HÍ*/
    private void fetchTournamentLocation(int tournamentId) {
        double[] location = dbHelper.getTournamentLocation(tournamentId);
        if (location != null && location.length == 2) {
            double latitude = location[0];
            double longitude = location[1];

            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
            mapController.setCenter(geoPoint);

            Marker marker = new Marker(mapView);
            marker.setPosition(geoPoint);
            marker.setTitle("Tournament Location");
            mapView.getOverlays().add(marker);
            mapView.invalidate();
        } else {
            Log.e(TAG, "Tournament location not found. Using fallback location.");
            GeoPoint fallbackLocation = new GeoPoint(64.1395, -21.9506); // HÍ fallback location
            mapController.setCenter(fallbackLocation);

            Marker marker = new Marker(mapView);
            marker.setPosition(fallbackLocation);
            marker.setTitle("Tournament Location (not set)");
            mapView.getOverlays().add(marker);
            mapView.invalidate();

            Toast.makeText(this, "Tournament location is not available. Showing fallback location HÍ.", Toast.LENGTH_SHORT).show();
        }
    }

    //Request and handle location updates from the device's GPS
    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double userLat = location.getLatitude();
                    double userLon = location.getLongitude();
                    Log.d("Location", "User Location: " + userLat + ", " + userLon);

                    // Move the map to show both locations
                    GeoPoint userLocation = new GeoPoint(userLat, userLon);
                    mapController.setCenter(userLocation);
                    mapController.setZoom(15.0);

                    // Add a marker for the user location
                    Marker userMarker = new Marker(mapView);
                    userMarker.setPosition(userLocation);
                    userMarker.setTitle("You are here");
                    mapView.getOverlays().add(userMarker);

                    // Retrieve the tournament location
                    double[] tournamentLocation = dbHelper.getTournamentLocation(tournamentID);
                    if (tournamentLocation != null && tournamentLocation.length == 2) {
                        double tournamentLat = tournamentLocation[0];
                        double tournamentLon = tournamentLocation[1];
                        GeoPoint tournamentGeoPoint = new GeoPoint(tournamentLat, tournamentLon);

                        // Ensure tournament location remains
                        Marker tournamentMarker = new Marker(mapView);
                        tournamentMarker.setPosition(tournamentGeoPoint);
                        tournamentMarker.setTitle("Tournament Location");
                        mapView.getOverlays().add(tournamentMarker);

                        // Draw the route
                        drawRoute(userLocation, tournamentGeoPoint);
                    }

                    mapView.invalidate();
                }


                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}
            });
        }
    }

    //Draw route between the user location and tournament location
    private void drawRoute(GeoPoint start, GeoPoint end) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            RoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "com.example.sportmot/1.0");

            ArrayList<GeoPoint> waypoints = new ArrayList<>();
            waypoints.add(start);
            waypoints.add(end);

            Road road = roadManager.getRoad(waypoints);

            handler.post(() -> {
                if (road != null) {
                    Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                    mapView.getOverlays().add(roadOverlay);
                    mapView.invalidate();
                } else {
                    Toast.makeText(ViewMapActivity.this, "Failed to fetch route.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

}
