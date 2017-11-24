package com.example.sombra.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sombra on 2017-11-14.
 */

public class CampsiteModel implements Parcelable {

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

    public CampsiteModel(Parcel in) {
        this.id = in.readString();
        this.location = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.type = in.readString();
        this.fee = in.readString();
        this.capacity = in.readInt();
        this.availability = in.readString();
        this.description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.location);
        parcel.writeDouble(this.lat);
        parcel.writeDouble(this.lng);
        parcel.writeString(this.type);
        parcel.writeString(this.fee);
        parcel.writeInt(this.capacity);
        parcel.writeString(this.availability);
        parcel.writeString(this.description);
    }
}
