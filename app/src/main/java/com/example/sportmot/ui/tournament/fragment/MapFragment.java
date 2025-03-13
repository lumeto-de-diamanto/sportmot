package com.example.sportmot.ui.tournament.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.sportmot.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapFragment extends Fragment {

    private MapView mapView;
    private IMapController mapController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Context context = requireActivity();

        Configuration.getInstance().load(
                context,
                context.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
        );

        View view = inflater.inflate(R.layout.fragment_mapview, container, false);

        // Initialize the MapView
        mapView = view.findViewById(R.id.osmmap);
        mapView.setTileSource(TileSourceFactory.MAPNIK); // Use OpenStreetMap tiles
        mapView.setMultiTouchControls(true); // Enable touch gestures

        // Center the map on a location
        mapController = mapView.getController();
        mapController.setZoom(12.0);
        mapController.setCenter(new org.osmdroid.util.GeoPoint(64.1029, -21.8997)); // Example: Fífan, Iceland

        // Add a "My Location" overlay
        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume(); // Resume map when Fragment is active
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause(); // Pause map when Fragment is not visible
    }
}
