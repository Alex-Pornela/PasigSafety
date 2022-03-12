package com.capstone.pasigsafety.Admin;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.capstone.pasigsafety.Fragments.MainHomeFragment;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.google.firebase.firestore.GeoPoint;
import com.google.protobuf.Any;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class FireStoreData  {

    private String brgy;
    private String street;
    private String date;
    private String  time;
    private Double latitude;
    private Double longitude;
    private String item;
    private String icon;
    private String crimeIcon;




    public FireStoreData(){

    }



    public FireStoreData(String brgy, String street, String date, String time, Double latitude, Double longitude,String item,String icon,String crimeIcon) {
        this.brgy = brgy;
        this.street = street;
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.item = item;
        this.icon= icon;
        this.crimeIcon= crimeIcon;

    }



    public String getBrgy() {
        return brgy;
    }

    public void setBrgy(String brgy) {
        this.brgy = brgy;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCrimeIcon() {
        return crimeIcon;
    }

    public void setCrimeIcon(String crimeIcon) {
        this.crimeIcon = crimeIcon;
    }
}
