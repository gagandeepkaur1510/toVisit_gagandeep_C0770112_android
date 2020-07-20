package com.example.tovisit_gagandeep_c0770112_android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder>{

    Context context;
    List<Place> placeList;

    public PlaceAdapter(Context context, List<Place> placeList) {
        this.context = context;
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.custom_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Place place = placeList.get(position);
        holder.titletextview.setText(place.getTitle());
        if(place.isVisited())
        {
            holder.linearLayout.setBackgroundColor(Color.LTGRAY);
        }
        else
        {
            holder.linearLayout.setBackgroundColor(Color.WHITE);
        }
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("place",place);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public void setPlaces(List<Place> places) {
        this.placeList = places;
        this.notifyDataSetChanged();
    }

    public Place deletePlace(int position) {
        Place place = placeList.remove(position);
        this.notifyItemRemoved(position);
        return place;
    }

    public void addPlace(Place place, int position) {
        placeList.add(position,place);
        this.notifyItemInserted(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView titletextview;
        LinearLayout linearLayout;
        public ViewHolder(View itemView)
        {
            super(itemView);
            titletextview = itemView.findViewById(R.id.title_textview);
            linearLayout = itemView.findViewById(R.id.layout);
        }
    }
}
