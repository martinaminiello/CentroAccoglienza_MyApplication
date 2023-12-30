package com.example.centroaccoglienza_myapplication.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

import com.example.centroaccoglienza_myapplication.GeocodingTask;
import com.example.centroaccoglienza_myapplication.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.MapEventsOverlay;

import java.util.HashMap;
import java.util.Map;

public class PosizioneFragment extends Fragment {

    View view;
    MapView map = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String locationQuery = "";
    SearchView searchView;
    TextView coordinates;
    DocumentReference documentRef = db.collection("CentroAccoglienza").document("C001");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.posizione_servizi, container, false);
        searchView = view.findViewById(R.id.searchView);
        coordinates = view.findViewById(R.id.textCoordinate);

        Context ctx = getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string


        map = (MapView) view.findViewById(R.id.mapView);


        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                // Handle the single tap event here
                Log.d(TAG, "Map tapped at: " + p.getLatitude() + ", " + p.getLongitude());

                // Save the chosen position in Firestore
                saveChosenPositionToFirestore(p);
                updateCoordinatesTextView( p);


                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                // Handle the long press event here
                return false;
            }
        });
        map.getOverlays().add(0, mapEventsOverlay);

        retrieveStoredPositionFromFirestore();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Update the locationQuery when the user submits the query
                locationQuery = query;

                // Perform geocoding for the submitted query
                new GeocodingTask(map, view).execute(locationQuery);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text change if needed
                return false;
            }
        });
        return view;
    }

    private void saveChosenPositionToFirestore(GeoPoint chosenPosition) {
        // Assuming you have a document reference already defined
        Map<String, Object> data = new HashMap<>();
        data.put("latitude", chosenPosition.getLatitude());
        data.put("longitude", chosenPosition.getLongitude());

        documentRef.update(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Chosen position saved to Firestore successfully");
                    Toast.makeText(getContext(), "Chosen position saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving chosen position to Firestore", e);
                    Toast.makeText(getContext(), "Error saving chosen position", Toast.LENGTH_SHORT).show();
                });
    }
    private void retrieveStoredPositionFromFirestore() {
        documentRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double latitude = documentSnapshot.getDouble("latitude");
                        Double longitude = documentSnapshot.getDouble("longitude");

                        if (latitude != null && longitude != null) {
                            GeoPoint storedPosition = new GeoPoint(latitude, longitude);

                            // Initialize the map after setting the center
                            initializeMap(storedPosition);
                        } else {
                            // If no valid position is stored, initialize the map with a default position
                            initializeMap(null);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving stored position from Firestore", e);
                });
    }

    // Method to set up the map
    private void initializeMap(GeoPoint centerPoint) {
        map = view.findViewById(R.id.mapView);

        if (map != null) {
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);

            IMapController mapController = map.getController();
            mapController.setZoom(9.5);

            if (centerPoint != null) {
                mapController.setCenter(centerPoint);
            }
        }
    }

    private void updateCoordinatesTextView(GeoPoint geoPoint) {
        if (view != null) {
            TextView coordinatesTextView = view.findViewById(R.id.textCoordinate);
            if (coordinatesTextView != null) {
                String coordinatesText = "Latitudine:  " + geoPoint.getLatitude() + "\n"+"Longitudine:  " + geoPoint.getLongitude();
                coordinatesTextView.setText(coordinatesText);
            }
        }
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}
