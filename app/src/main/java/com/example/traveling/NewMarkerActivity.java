package com.example.traveling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewMarkerActivity extends AppCompatActivity {

    Spinner spinner;
    Intent intent;

    EditText latitude;
    EditText longitude;
    EditText altitude;

    EditText details;
    EditText nameOfMarker;

    ImageView image;

    ParseFile file;

    TextView photo;

    Bitmap bitmapImage;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_marker);



        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        } else {
            //getPhoto();
        }

        intent = getIntent();

        init();
        setInit();

        List<String> list = new ArrayList<>();
        list.add("Изберете категорија");
        list.add("Водопади и извори");
        list.add("Верски објекти");
        list.add("Национален парк");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);



    }

    public void init() {

        latitude = findViewById(R.id.latitudeEditText);
        longitude = findViewById(R.id.longitudeEditText);
        altitude = findViewById(R.id.altitudeEditText);

        spinner = findViewById(R.id.spinnerCategories);

        details = findViewById(R.id.opisEditText);
        nameOfMarker = findViewById(R.id.nameOfMarker);

        image = findViewById(R.id.imageView);
        photo = findViewById(R.id.photo);

    }

    public void setInit(){

        latitude.setText(String.valueOf(intent.getDoubleExtra("latitude", 0.0)));
        longitude.setText(String.valueOf(intent.getDoubleExtra("longitude", 0.0)));
        altitude.setText(String.valueOf(intent.getDoubleExtra("altitude", 0.0)));

    }


    public void saveNewMarker(View view) {

        db = FirebaseFirestore.getInstance();
        Map<String, Object> markerMap = new HashMap<>();

        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(
                intent.getDoubleExtra("latitude", 0.0), intent.getDoubleExtra("longitude", 0.0)));

        //markerMap.put("locationName", nameOfMarker.getText().toString());

        markerMap.put("latitude", intent.getDoubleExtra("latitude", 0.0));
        markerMap.put("longitude", intent.getDoubleExtra("longitude", 0.0));
        markerMap.put("locationAlatitude", intent.getDoubleExtra("altitude", 0.0));
        markerMap.put("locationDetails", details.getText().toString());
        markerMap.put("locationCategory", spinner.getSelectedItem().toString());

        db.collection("Locations").document("" + nameOfMarker.getText().toString())
                .set(markerMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(NewMarkerActivity.this, "New marker was created", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(NewMarkerActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

//    public void saveNewMarker(View view){
//
//        ParseObject locationObject = new ParseObject("Locations");
//        ParseGeoPoint parseGeoPoint = new ParseGeoPoint(intent.getDoubleExtra("latitude", 0.0), intent.getDoubleExtra("longitude", 0.0));
//
//        locationObject.put("Username", ParseUser.getCurrentUser().getUsername());
//        locationObject.put("locationName", nameOfMarker.getText().toString());
//        locationObject.put("locationCoordinats", parseGeoPoint);
//        locationObject.put("locationAlatitude", intent.getDoubleExtra("altitude", 0.0));
//        locationObject.put("locationDetails", details.getText().toString());
//        locationObject.put("locationCategory", spinner.getSelectedItem().toString());
//        if(bitmapImage != null) {
//            Log.e("Bitmap Image", "" + bitmapImage);
//
//            ByteArrayOutputStream baos = new  ByteArrayOutputStream();
//            bitmapImage.compress(Bitmap.CompressFormat.PNG,100, baos);
//            byte [] b = baos.toByteArray();
//            String temp = Base64.encodeToString(b, Base64.DEFAULT);
//
//            locationObject.put("locationImage", temp);
//        } else {
//            Log.e("Bitmap Image", " it's empty");
//        }
//
//        locationObject.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if(e == null) {
//                    Log.e("saved", "in background");
//                    Toast.makeText(getApplicationContext(), "Успешно креиран маркер", Toast.LENGTH_LONG).show();
//                    Intent intent2 = new Intent(NewMarkerActivity.this, AllMarkersActivity.class);
//                    startActivity(intent2);
//
//                } else {
//                    Log.e("ERROR", e.getMessage());
//                }
//            }
//        });
//    }

    public void openGallery(View view) {

        photo.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                bitmapImage = bitmap;
                Log.i("Photo", "Received");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                image.setImageBitmap(bitmap);

                //file = new ParseFile("image.png", byteArray);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}