package com.example.tovisit_gagandeep_c0770112_android.volley;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class GetByVolley {

    public static void getDirection(JSONObject jsonObject, GoogleMap googleMap, LatLng latLng) {
        try
        {
            HashMap<String, String> distances = null;
            VolleyParser directionParser = new VolleyParser();
            distances = directionParser.parseDistance(jsonObject);

            String distance = distances.get("distance");
            String duration = distances.get("duration");

            String[] directionsList;
            directionsList = directionParser.parseDirections(jsonObject);
            displayDirection(directionsList, distance, duration, googleMap, latLng);
        }
        catch (NullPointerException e)
        {

        }
    }

    private static void displayDirection(String[] directionsList, String distance, String duration, GoogleMap googleMap, LatLng latLng) {
        googleMap.clear();
        MarkerOptions options = new MarkerOptions().position(latLng)
                .title("Duration : " + duration)
                .snippet("Distance : " + distance)
                .draggable(true);
        googleMap.addMarker(options);
        for (int i=0; i<directionsList.length; i++) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(Color.RED)
                    .width(10)
                    .addAll(PolyUtil.decode(directionsList[i]));
            googleMap.addPolyline(polylineOptions);
        }
    }

    public static void getNearbyPlaces(JSONObject jsonObject, GoogleMap googleMap) {
        List<HashMap<String, String>> nearbyPlaces = null;
        VolleyParser dataParser = new VolleyParser();
        nearbyPlaces = dataParser.parsePlace(jsonObject);
        showNearbyPlaces(nearbyPlaces, googleMap);
    }

    private static void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaces, GoogleMap googleMap) {
        googleMap.clear();
        for (int i=0; i<nearbyPlaces.size(); i++) {
            HashMap<String, String> googlePlace = nearbyPlaces.get(i);
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble(googlePlace.get("latitude"));
            double lng = Double.parseDouble(googlePlace.get("longitude"));

            LatLng latLng = new LatLng(lat, lng);

            MarkerOptions options = new MarkerOptions().position(latLng)
                    .title(placeName + " : " + vicinity)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            googleMap.addMarker(options);
        }
    }
}



















