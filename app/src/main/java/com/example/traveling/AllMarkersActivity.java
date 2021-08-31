package com.example.traveling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.traveling.Models.MarkerModel;
import com.example.traveling.Models.ShortMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Paths.get;

public class AllMarkersActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationListener locationListener;
    LocationManager locationManager;

    List<String> list;

    Toolbar toolbar;

    Double lat;
    Double lon;
    Double alt;

    Spinner spinner;

    LatLng currentLocation;

    static String markerClickedId;

    FirebaseFirestore db;
    FirebaseAuth auth;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_markers);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        spinner = findViewById(R.id.spinner);

        fab = findViewById(R.id.fabAdd);

        toolbar = findViewById(R.id.incToolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_all_markers);

        //fillWithMarkers();

        list = new ArrayList<>();
        list.add("Сите");
        list.add("Водопади и извори");
        list.add("Верски објекти");
        list.add("Национален парк");
        list.add("Мои маркери");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0 ) {
                    fillWithMarkers();
                }else if(i == list.size() - 1) {
                    showMyMarkers();
                } else {
                    fillWithMarkers(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewMarker(v);
            }
        });

    }

    public void showMyMarkers() {

        final ParseQuery<ParseObject> myQuery = ParseQuery.getQuery("Locations");
        myQuery.whereExists("locationCategory");
        myQuery.whereEqualTo("Username", ParseUser.getCurrentUser().getUsername());

        mMap.clear();

        myQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size() > 0) {
                    for(ParseObject object : objects) {

                        ParseGeoPoint mLocation = (ParseGeoPoint) object.get("locationCoordinats");
                        LatLng mLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                        if(object.get("locationCategory").equals("Водопади и извори")) {
                            mMap.addMarker(new MarkerOptions().position(mLatLng).title((String) object.get("locationName")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        } else if (object.get("locationCategory").equals("Верски објекти")){
                            mMap.addMarker(new MarkerOptions().position(mLatLng).title((String) object.get("locationName")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        } else if (object.get("locationCategory").equals("Национален парк")) {
                            mMap.addMarker(new MarkerOptions().position(mLatLng).title((String) object.get("locationName")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        }
                    }
                }
            }
        });

    }


    public void fillWithMarkers(int i) {

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Locations");
        query.whereEqualTo("locationCategory", list.get(i));

        db.collection("Locations").whereEqualTo("locationCategory", list.get(i))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    Log.e("+++++++++++++++>>>", task.getResult().toString());
                    //make object from ShortModel
                    //call make marker
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.d(TAG, document.getId() + " => " + document.getData());
                        ShortMarker marker = new ShortMarker(
                                document.getId(),
                                document.get("latitude").toString(),
                                document.get("longitude").toString(),
                                document.get("locationCategory").toString()
                        );
                        makeMarkers(marker);
                    }
                } else {
                    Log.e(">>>>>>>>>>>>>>>>>>", task.getException().toString());
                }
            }
        });

        mMap.clear();

//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//                if(e == null && objects.size() > 0) {
//                    for (final ParseObject object : objects) {
//                        Log.e("Object name", object.get("locationName").toString());
//                        if (object.get("locationCategory").equals("Водопади и извори")) {
//                            ParseGeoPoint mLocation = (ParseGeoPoint) object.get("locationCoordinats");
//                            //Location mLocation = (Location) object.get("locationCoordinats");
//                            LatLng mLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
//                            mMap.addMarker(new MarkerOptions().position(mLatLng).title((String) object.get("locationName")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
////                       mMap.addMarker(new MarkerOptions().position(mLatLng).title((String) object.get("locationName")).icon(BitmapDescriptorFactory.fromResource(R.drawable.airplanemode)));
////                       Failed to decode image. The provided image must be a Bitmap.
//                        } else if (object.get("locationCategory").equals("Верски објекти")) {
//                            ParseGeoPoint mLocation = (ParseGeoPoint) object.get("locationCoordinats");
//                            //Location mLocation = (Location) object.get("locationCoordinats");
//                            LatLng mLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
//                            mMap.addMarker(new MarkerOptions().position(mLatLng).title((String) object.get("locationName")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
////                       mMap.addMarker(new MarkerOptions().position(mLatLng).title((String) object.get("locationName")).icon(BitmapDescriptorFactory.fromResource(R.drawable.airplanemode)));
////                       Failed to decode image. The provided image must be a Bitmap.
//                        } else if (object.get("locationCategory").equals("Национален парк")) {
//                            ParseGeoPoint mLocation = (ParseGeoPoint) object.get("locationCoordinats");
//                            LatLng mLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
//                            mMap.addMarker(new MarkerOptions().position(mLatLng).title((String) object.get("locationName")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//                       }
//                   }
//                } else {
//                    Log.e("Location category ", e.getMessage());
//                }
//            }
//        });
    }

    public void fillWithMarkers(){

        mMap.clear();

        db = FirebaseFirestore.getInstance();
        db.collection("Locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                db.collection("Locations").document("" + documentSnapshot.getId())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();
                                            ShortMarker marker = new ShortMarker(doc.getId(),
                                                    doc.get("latitude").toString(),
                                                    doc.get("longitude").toString(),
                                                    doc.get("locationCategory").toString());
                                            makeMarkers(marker);
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.e("----------", task.getException().toString());
                        }
                    }

                });
    }

    public void addNewMarker(View view){

        boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(statusOfGPS == true) {
            Intent intent = new Intent(AllMarkersActivity.this, NewMarkerActivity.class);
            intent.putExtra("latitude", lat);
            intent.putExtra("longitude", lon);
            intent.putExtra("altitude", alt);
            startActivity(intent);
        } else {
            Toast.makeText(this, "YOUR GPS IN TURNED OFF. PLEASE TURN IT ON", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateMyLocation(location);
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            mMap.setMyLocationEnabled(true);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                updateMyLocation(lastKnownLocation);
            }
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                markerClickedId = marker.getTitle();
                db.collection("Locations").document("" + marker.getTitle())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            MarkerModel marker = new MarkerModel(doc.getId(),
                                    doc.get("latitude").toString(),
                                    doc.get("longitude").toString(),
                                    doc.get("locationAlatitude").toString(),
                                    doc.get("locationCategory").toString(),
                                    doc.get("locationDetails").toString());

                            showDetailsMarkerModel(marker);

                        }
                    }
                });
                return true;
            }

            //AlertDialog.Builder dialog = new AlertDialog.Builder(AllMarkersActivity.this);
        });
    }

    public void showDetailsMarkerModel(MarkerModel marker) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AllMarkersActivity.this);

        LayoutInflater inflater = getLayoutInflater();
       // View dialoglayout = inflater.inflate(R.layout.imageview, null);

        ImageView image = findViewById(R.id.img);

//        if(object.getmImage() != null) {
//            Log.e("BITMAP", " <<<<===========");
//            image.setImageBitmap(object.getmImage());
//        } else {
//            Log.e("EMPTY BITMAP", " <<<<===========");
//            image.setImageResource(R.drawable.ic_baseline_build_24);
//        }

        dialog.setTitle("детали за :" + marker.getMarkerName())
                .setMessage("Оддалеченост : " +  "  " + "\n"+ "\n" + "Надморска висина : " + "  " + marker.getMarkerAltitude() + "m" + "\n" + "\n" + "Детали : " + "    " + marker.getDetails())
                //.setView(dialoglayout)
                .setNegativeButton("ОТКАЖИ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("ОДВЕДИ МЕ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //go to google maps and make a route

                        Intent directionsIntent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" + currentLocation.latitude + "," + currentLocation.longitude
                                        + "&daddr=" + marker.getMarkerLat() + "," + marker.getMarkerLong()));
                        startActivity(directionsIntent);

                    }
                });

        dialog.show();
    }


    public void makeMarkers(ShortMarker marker) {


            LatLng mLatLng = new LatLng(Double.parseDouble(marker.getMarkerLat()), Double.parseDouble(marker.getMarkerLong()));
            //Log.e("SNIPPED FRM CRT MARKER", "" + object.getObjectId());

        if(marker.getMarkerCategory().equals("Водопади и извори")){
            mMap.addMarker(new MarkerOptions()
                    .position(mLatLng)
                    .title((String) marker.getMarkerName())
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE)));
        } else if(marker.getMarkerCategory().equals("Верски објекти")){
            mMap.addMarker(new MarkerOptions()
                    .position(mLatLng)
                    .title((String) marker.getMarkerName())
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_ORANGE)));
        } else if(marker.getMarkerCategory().equals("Национален парк")){
            mMap.addMarker(new MarkerOptions()
                    .position(mLatLng)
                    .title((String) marker.getMarkerName())
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_CYAN)));
        }
    }


    public void updateMyLocation(Location location) {

        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(myLatLng).title("MY LOCATION"));

        currentLocation = myLatLng;

        lat = location.getLatitude();
        lon = location.getLongitude();
        alt = location.getAltitude();

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 10));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_all_markers, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.addNewLocation) {
            Intent i = new Intent(AllMarkersActivity.this, ChangePasswordActivity.class);
            startActivity(i);
        } else if(item.getItemId() == R.id.logout) {
            //Log out the current user
            auth = FirebaseAuth.getInstance();
            auth.signOut();
            Intent i = new Intent(AllMarkersActivity.this, MainActivity.class);
            startActivity(i);

        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1 ) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if(lastKnownLocation != null ) {
                        //updateListView(lastKnownLocation);
                        //savingDriversLocation(lastKnownLocation);

                    }

                }

            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        
    }
}