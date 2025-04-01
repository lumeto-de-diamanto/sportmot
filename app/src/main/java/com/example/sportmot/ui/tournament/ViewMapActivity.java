package com.example.sportmot.ui.tournament;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sportmot.DatabaseHelper;
import com.example.sportmot.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.api.IMapController;

public class ViewMapActivity extends AppCompatActivity {

    private static final String TAG = "ViewMapActivity";
    private MapView mapView;
    private IMapController mapController;
    private int tournamentID;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize DatabaseHelper, DatabaseHelper used to make SQLite DB
        dbHelper = new DatabaseHelper(this);

        // Retrieve the tournament ID from the intent
        tournamentID = getIntent().getIntExtra("TOURNAMENT_ID", -1);

        // Check if tournament ID is valid
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
            Log.e(TAG, "Error: mapView is NULL. Check if R.id.osmmap exists in activity_map.xml");
            return;
        }
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        mapController = mapView.getController();

        fetchTournamentLocation(tournamentID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(v -> {
            finish();
        });
    }

    //get the location using the tournamentId to find the location stored in the db
    private void fetchTournamentLocation(int tournamentId) {
        double[] location = dbHelper.getTournamentLocation(tournamentId);

        if (location != null && location.length == 2) {
            double latitude = location[0];
            double longitude = location[1];

            // Set map center and add marker
            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
            mapController.setCenter(geoPoint);
            mapController.setZoom(18.0);

            //costum football pin
            Drawable customPin = getResources().getDrawable(R.drawable.football_location);

            Marker marker = new Marker(mapView);
            marker.setPosition(geoPoint);
            marker.setTitle("Staðsetning móts");
            marker.setIcon(customPin);
            mapView.getOverlays().add(marker);
            mapView.invalidate();
        } else {
            // Fallback location if not found
            Log.e(TAG, "Tournament location not found. Using fallback location.");

            GeoPoint fallbackLocation = new GeoPoint(64.1395, -21.9506); // HÍ
            mapController.setCenter(fallbackLocation);

            Marker marker = new Marker(mapView);
            marker.setPosition(fallbackLocation);
            marker.setTitle("Tournament Location (not set)");
            mapView.getOverlays().add(marker);
            mapView.invalidate();

            Toast.makeText(this, "Tournament location is not available. Showing fallback location.", Toast.LENGTH_SHORT).show();
        }
    }
}
