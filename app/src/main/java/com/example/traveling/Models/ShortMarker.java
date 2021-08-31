package com.example.traveling.Models;

public class ShortMarker {

    String markerName;
    String markerLat;
    String markerLong;
    String markerCategory;

    public ShortMarker() {
    }

    public ShortMarker(String markerName, String markerLat, String markerLong, String markerCategory) {
        this.markerName = markerName;
        this.markerLat = markerLat;
        this.markerLong = markerLong;
        this.markerCategory = markerCategory;
    }

    public String getMarkerCategory() {
        return markerCategory;
    }

    public void setMarkerCategory(String markerCategory) {
        this.markerCategory = markerCategory;
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
}
