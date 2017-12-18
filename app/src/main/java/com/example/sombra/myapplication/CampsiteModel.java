package com.example.sombra.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sombra on 2017-11-14.
 */

public class CampsiteModel implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CampsiteModel createFromParcel(Parcel in) {
            return new CampsiteModel(in);
        }

        public CampsiteModel[] newArray(int size) {
            return new CampsiteModel[size];
        }
    };

    String id;
    String location;
    String name;
    double lat;
    double lng;
    String type;
    String fee;
    String capacity;
    String availability;
    String description;
    double rating;
    int views;
    String username;

    public CampsiteModel(String id, String location, String name, double lat, double lng, String type, String fee,
                         String capacity, String availability, String description, double rating, int views, String username){
        this.id = id;
        this.location = location;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.fee = fee;
        this.capacity = capacity;
        this.availability = availability;
        this.description = description;
        this.rating = rating;
        this.views = views;
        this.username = username;
    }

    public CampsiteModel(Parcel in) {
        this.id = in.readString();
        this.location = in.readString();
        this.name = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.type = in.readString();
        this.fee = in.readString();
        this.capacity = in.readString();
        this.availability = in.readString();
        this.description = in.readString();
        this.rating = in.readDouble();
        this.views = in.readInt();
        this.username = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.location);
        parcel.writeString(this.name);
        parcel.writeDouble(this.lat);
        parcel.writeDouble(this.lng);
        parcel.writeString(this.type);
        parcel.writeString(this.fee);
        parcel.writeString(this.capacity);
        parcel.writeString(this.availability);
        parcel.writeString(this.description);
        parcel.writeDouble(this.rating);
        parcel.writeInt(this.views);
        parcel.writeString(this.username);
    }
}
