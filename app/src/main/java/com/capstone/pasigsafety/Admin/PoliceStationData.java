package com.capstone.pasigsafety.Admin;

public class PoliceStationData {

    private String name;
    private String number;
    private Double latitude;
    private Double longitude;
    private String address;
    private String healthIcon;

    public PoliceStationData() {

    }

    public PoliceStationData(String name, String number, Double latitude, Double longitude, String address,String healthIcon) {
        this.name = name;
        this.number = number;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.healthIcon = healthIcon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHealthIcon() {
        return healthIcon;
    }

    public void setHealthIcon(String healthIcon) {
        this.healthIcon = healthIcon;
    }
}
