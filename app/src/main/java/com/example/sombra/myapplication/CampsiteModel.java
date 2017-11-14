package com.example.sombra.myapplication;

/**
 * Created by Sombra on 2017-11-14.
 */

public class CampsiteModel {

    int id;
    String location;
    String coordinates;
    String type;
    String fee;
    int capacity;
    String availability;
    String description;

    public CampsiteModel(int id, String location, String coordinates, String type, String fee,
                         int capacity, String availability, String description){
        this.id = id;
        this.location = location;
        this.coordinates = coordinates;
        this.type = type;
        this.fee = fee;
        this.capacity = capacity;
        this.availability = availability;
        this.description = description;
    }


}
