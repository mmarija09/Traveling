package com.example.traveling.Models;

public class MarkerModel {

    String markerName;
    String markerLat;
    String markerLong;
    String markerAltitude;
    String category;
    String details;
    // photo
    // user reference


    public MarkerModel() {
    }

    public MarkerModel(String markerName, String markerLat, String markerLong, String markerAltitude, String category, String details) {
        this.markerName = markerName;
        this.markerLat = markerLat;
        this.markerLong = markerLong;
        this.markerAltitude = markerAltitude;
        this.category = category;
        this.details = details;
    }

    public String getMarkerAltitude() {
        return markerAltitude;
    }

    public void setMarkerAltitude(String markerAltitude) {
        this.markerAltitude = markerAltitude;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public String getMarkerLat() {
        return markerLat;
    }

    public void setMarkerLat(String markerLat) {
        this.markerLat = markerLat;
    }

    public String getMarkerLong() {
        return markerLong;
    }

    public void setMarkerLong(String markerLong) {
        this.markerLong = markerLong;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
