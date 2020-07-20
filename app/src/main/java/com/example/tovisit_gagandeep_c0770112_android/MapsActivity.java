package com.example.tovisit_gagandeep_c0770112_android;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

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

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowLongClickListener, RadioGroup.OnCheckedChangeListener, GoogleMap.OnMarkerDragListener {

    private static final int REQUEST_CODE = 1;
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Marker marker;
    private DatabaseHelper databaseHelper;
    private List<Place> placeList;
    private RadioGroup radioGroup;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkPermissions();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        databaseHelper = new DatabaseHelper(this);
        radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(this);
        placeList = new ArrayList<>();
        place = (Place) getIntent().getSerializableExtra("place");
        loadPlaces();
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
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        setPlace();

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        mMap.setMyLocationEnabled(true);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));
    }

    private void setPlace() {
        if (place != null) {
            String snippet = "Favourite Place";
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(place.getLat(), place.getLng())).draggable(true).title(place.getTitle()).snippet(snippet);
            marker = mMap.addMarker(markerOptions);
            marker.setTag("favourite place");
            if (place.isVisited())
            {
                marker.setSnippet(marker.getSnippet() + " visited");
            }
            marker.showInfoWindow();
        }
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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(marker != null)
        {
            marker.remove();
            place = null;
        }
        String title;
        try {
            Geocoder geocoder = new Geocoder(this);
            Address address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
            title = Util.getTitle(address);
        } catch (Exception e) {
            e.printStackTrace();
            title = Util.getTitle(null);
        }
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title).draggable(true);
        marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();
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
            place = getPlace(marker);
        } else {
            Place place = getPlace(marker);
            System.out.println(place);
            if (place != null) {
                databaseHelper.deletePlace(place.getId());
                marker.hideInfoWindow();
                marker.setSnippet("");
                marker.showInfoWindow();
                loadPlaces();
                this.place = null;
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
        if (place != null)
        {
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

    public void navigateClicked(View view) {
        if(marker != null)
        {
            final LatLng latLng = marker.getPosition();
            if (latLng != null) {

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        getDirectionUrl(latLng), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        GetByVolley.getDirection(response, mMap, latLng);
                    }
                }, null);
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
            } else {
                Toast.makeText(MapsActivity.this, "Please choose a destination", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private String getDirectionUrl(LatLng location) {
        Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        StringBuilder googleDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionUrl.append("origin=" + location1.getLatitude() + "," + location1.getLongitude());
        googleDirectionUrl.append(("&destination=" + location.latitude + "," + location.longitude));
        googleDirectionUrl.append("&key=" + getString(R.string.google_maps_key));
        Log.d(TAG, "getDirectionUrl: " + googleDirectionUrl);
        return googleDirectionUrl.toString();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        try {
            marker.hideInfoWindow();
            Geocoder geocoder = new Geocoder(this);
            Address address = geocoder.getFromLocation(marker.getPosition().latitude,marker.getPosition().longitude,1).get(0);
            String title = Util.getTitle(address);
            marker.setTitle(title);
            if(place != null)
            {
                place.setTitle(title);
                place.setLat(marker.getPosition().latitude);
                place.setLng(marker.getPosition().longitude);
                databaseHelper.updatePlace(place.getId(),title,marker.getPosition().latitude,marker.getPosition().longitude,place.isVisited());
            }
            marker.showInfoWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}