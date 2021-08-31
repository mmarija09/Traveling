package com.example.traveling;

import android.graphics.Bitmap;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.parse.ParseFile;

public class MarkerSelected {

    @SerializedName("objectId")
    private String mObjectId;

    @SerializedName("locationName")
    private String mLocationName;

    @SerializedName("locationCoordinats")
    private LatLng mCoordinats;

    private double mLatitude;

    private double mLongitude;

    @SerializedName("locationDetails")
    private String mDetails;

    @SerializedName("locationAlatitude")
    private String mAltitude;

    @SerializedName("Username")
    private String mUsername;

    @SerializedName("locationImage")
    private Bitmap mImage;

    public MarkerSelected(){}

    public MarkerSelected(Bitmap mImage, String mLocationName, double mLatitude, double mLongitude, String mDetails, String mAltitude, String mUsername) {
        this.mLocationName = mLocationName;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mDetails = mDetails;
        this.mAltitude = mAltitude;
        this.mUsername = mUsername;
        this.mImage = mImage;
    }

    public Bitmap getmImage() {
        return mImage;
    }

    public void setmImage(Bitmap mImage) {
        this.mImage = mImage;
    }

    public LatLng getmCoordinats() {
        return mCoordinats;
    }

    public void setmCoordinats(LatLng mCoordinats) {
        this.mCoordinats = mCoordinats;
    }

    public String getmLocationName() {
        return mLocationName;
    }

    public void setmLocationName(String mLocationName) {
        this.mLocationName = mLocationName;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getmDetails() {
        return mDetails;
    }

    public void setmDetails(String mDetails) {
        this.mDetails = mDetails;
    }

    public String getmAltitude() {
        return mAltitude;
    }

    public void setmAltitude(String mAltitude) {
        this.mAltitude = mAltitude;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }
}
