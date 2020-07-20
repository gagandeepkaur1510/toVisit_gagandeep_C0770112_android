package com.example.tovisit_gagandeep_c0770112_android;

import java.io.Serializable;

public class Place implements Serializable {
    int id;
    String title;
    double lat,lng;
    boolean visited;

    public Place(int id, String title, double lat, double lng, int visited) {
        this.id = id;
        this.title = title;
        this.lat = lat;
        this.lng = lng;
        this.visited = visited == 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
