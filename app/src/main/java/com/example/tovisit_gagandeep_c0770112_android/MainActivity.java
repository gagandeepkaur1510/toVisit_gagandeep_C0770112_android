package com.example.tovisit_gagandeep_c0770112_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //    ListView placesListView;
    RecyclerView recyclerView;
    DatabaseHelper databaseHelper;
    List<Place> places;
    PlaceAdapter placeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        places = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        placeAdapter = new PlaceAdapter(this, places);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(placeAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSimpleCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlaces();
        placeAdapter.setPlaces(places);
    }

    private void loadPlaces() {
        places.clear();
        Cursor cursor = databaseHelper.getAllPlaces();
        System.out.println(cursor);
        if (cursor.moveToFirst()) {
            do {
                Place place = new Place(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3), cursor.getInt(4));
                places.add(place);
                System.out.println(place.getTitle());
            }
            while (cursor.moveToNext());
            cursor.close();
        }
    }

    public void newClicked(View view) {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void nearbyClicked(View view) {
        Intent intent = new Intent(MainActivity.this, NearByPlacesActivity.class);
        startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback mSimpleCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Place place = placeAdapter.deletePlace(position);
            databaseHelper.deletePlace(place.getId());
        }
    };
}