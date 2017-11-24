package com.example.sombra.myapplication;

/**
 * Created by Sombra on 2017-11-14.
 */

public class CampsiteModel {

    String id;
    String location;
    double lat;
    double lng;
    String type;
    String fee;
    int capacity;
    String availability;
    String description;

    public CampsiteModel(String id, String location, double lat, double lng, String type, String fee,
                         int capacity, String availability, String description){
        this.id = id;
        this.location = location;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.fee = fee;
        this.capacity = capacity;
        this.availability = availability;
        this.description = description;
    }
}
