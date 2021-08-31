package com.example.traveling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartingActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null && currentUser.isEmailVerified()) {
            Intent intent = new Intent(StartingActivity.this, AllMarkersActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(StartingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}