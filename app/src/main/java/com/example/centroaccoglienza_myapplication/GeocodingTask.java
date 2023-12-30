package com.example.centroaccoglienza_myapplication;




import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GeocodingTask extends AsyncTask<String, Void, GeoPoint> {

    private static final String TAG = "GeocodingTask";
    private static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/search";
    private static final int MAX_RESULTS = 1;
    private MapView map;
    View view;

    public GeocodingTask(MapView mapView, View fragmentView) {
        this.map = mapView;
        this.view = fragmentView;
    }

    @Override
    protected GeoPoint doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String locationQuery = params[0];

        try {
            URL url = new URL(buildNominatimUrl(locationQuery));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try (InputStream inputStream = connection.getInputStream()) {
                String jsonResponse = new Scanner(inputStream).useDelimiter("\\A").next();
                return parseJsonResponse(jsonResponse);
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error during geocoding", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(GeoPoint geoPoint) {
        if (geoPoint != null) {
            // Handle the obtained GeoPoint
            Log.d(TAG, "Geocoded Location: " + geoPoint.getLatitude() + ", " + geoPoint.getLongitude());



            // Update the map's center with the obtained GeoPoint
            updateMapCenter(geoPoint);


        } else {
            // Handle geocoding failure
            Log.e(TAG, "Geocoding failed");
        }
    }

    private void updateMapCenter(GeoPoint centerPoint) {
        if (map != null) {
            BoundingBox boundingBox = map.getBoundingBox();
            IMapController mapController = map.getController();

            updateCoordinatesTextView(centerPoint);

            if (boundingBox != null && mapController != null) {
                GeoPoint currentCenter = boundingBox.getCenter();
                double zoomLevel = 20.0;  // Adjust the zoom level as needed

                // Set the desired zoom level
                mapController.zoomTo(zoomLevel);

                // Set the center point
                mapController.setCenter(centerPoint);

                // Optionally, animate to the new center
                mapController.animateTo(centerPoint);

            }
        }
    }

    private void updateCoordinatesTextView(GeoPoint geoPoint) {
        if (view != null) {
            TextView coordinatesTextView = view.findViewById(R.id.textCoordinate);
            if (coordinatesTextView != null) {
                String coordinatesText = "Latitudine:  " + geoPoint.getLatitude() + ", Longitudine:  " + geoPoint.getLongitude();
                coordinatesTextView.setText(coordinatesText);
            }
        }
    }

    private String buildNominatimUrl(String locationQuery) {
        return String.format("%s?format=json&q=%s&limit=%d", NOMINATIM_BASE_URL, locationQuery, MAX_RESULTS);
    }

    private GeoPoint parseJsonResponse(String jsonResponse) throws JSONException {
        JSONArray resultsArray = new JSONArray(jsonResponse);

        if (resultsArray.length() > 0) {
            JSONObject firstResult = resultsArray.getJSONObject(0);
            double latitude = firstResult.getDouble("lat");
            double longitude = firstResult.getDouble("lon");
            return new GeoPoint(latitude, longitude);
        }

        return null;
    }
}
