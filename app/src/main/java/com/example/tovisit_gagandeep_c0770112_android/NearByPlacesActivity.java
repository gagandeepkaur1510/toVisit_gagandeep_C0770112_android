package com.example.tovisit_gagandeep_c0770112_android;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tovisit_gagandeep_c0770112_android.volley.GetByVolley;
import com.example.tovisit_gagandeep_c0770112_android.volley.VolleySingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NearByPlacesActivity extends FragmentActivity implements OnMapReadyCallback, TabLayout.OnTabSelectedListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowLongClickListener {

    private static final int REQUEST_CODE = 1;
    private static final int RADIUS = 1500;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private DatabaseHelper databaseHelper;
    private List<Place> placeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_places);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ((TabLayout) findViewById(R.id.tab)).addOnTabSelectedListener(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        checkPermissions();
        databaseHelper = new DatabaseHelper(this);
        placeList = new ArrayList<>();
    }

    private void checkPermissions() {
        if (!hasLocationPermission()) {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
                showLaunchNearby();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        mMap.setMyLocationEnabled(true);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
        showLaunchNearby();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
//        Log.d(TAG, "onTabSelected: " + tab.getPosition());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String url = "";
        switch (tab.getPosition()) {
            case 0:
                url = getPlaceUrl(location.getLatitude(), location.getLongitude(), "hospital");
                break;
            case 1:
                url = getPlaceUrl(location.getLatitude(), location.getLongitude(), "restaurant");
                break;
            case 2:
                url = getPlaceUrl(location.getLatitude(), location.getLongitude(), "museum");
                break;
            case 3:
                url = getPlaceUrl(location.getLatitude(), location.getLongitude(), "cafe");
                break;
        }

        showNearbyPlaces(url);
    }

    private void showLaunchNearby()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String url = getPlaceUrl(location.getLatitude(), location.getLongitude(), "hospital");
        showNearbyPlaces(url);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private String getPlaceUrl(double latitude, double longitude, String placeType) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append(("&radius=" + RADIUS));
        googlePlaceUrl.append("&type=" + placeType);
        googlePlaceUrl.append("&key=" + getString(R.string.google_maps_key));
//        Log.d(TAG, "getDirectionUrl: " + googlePlaceUrl);
        return googlePlaceUrl.toString();
    }

    private void showNearbyPlaces(String url) {

        /*By Volley Library*/
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        GetByVolley.getNearbyPlaces(response, mMap);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Object o = marker.getTag();
        if (o == null) {
            boolean cond = databaseHelper.insertPlace(marker.getTitle(), marker.getPosition().latitude, marker.getPosition().longitude, false);
            System.out.println("cond: " + cond);
            marker.setTag("favourite place");
            marker.hideInfoWindow();
            marker.setSnippet("Favourite Place");
            marker.showInfoWindow();
            loadPlaces();
        } else {
            Place place = getPlace(marker);
            System.out.println(place);
            if (place != null) {
                databaseHelper.deletePlace(place.getId());
                marker.hideInfoWindow();
                marker.setSnippet("");
                marker.showInfoWindow();
                loadPlaces();
            }
        }
    }

    private Place getPlace(Marker marker) {
        for (Place place1 : placeList) {
            if (place1.getLat() == marker.getPosition().latitude && place1.getLng() == marker.getPosition().longitude) {
                return place1;
            }
        }
        return null;
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        Place place = getPlace(marker);
        if (place != null) {
            marker.hideInfoWindow();
            String o = (String) marker.getTag();
            if (place.isVisited()) {
                marker.setSnippet(marker.getSnippet().replace("visited", ""));
                place.setVisited(false);
                o = o.replace("visited", "");
            } else {
                place.setVisited(true);
                o += " visited";
                marker.setSnippet(marker.getSnippet() + " visited");
            }
            databaseHelper.updatePlace(place.getId(), place.getTitle(), place.getLat(), place.getLng(), place.isVisited());
            marker.showInfoWindow();
        }
    }

    private void loadPlaces() {
        placeList.clear();
        Cursor cursor = databaseHelper.getAllPlaces();
        if (cursor.moveToFirst()) {
            do {
                placeList.add(new Place(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3), cursor.getInt(4)));
            }
            while (cursor.moveToNext());
            cursor.close();
        }
    }
}