package com.example.traveling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    TextView errorLabel;

    FirebaseFirestore db;

    FirebaseAuth auth;
    FirebaseUser user;

    SliderView sliderView;
    int[] images = {
           R.drawable.mount2, R.drawable.orange, R.drawable.water
    };

    SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null ) {
            if(!currentUser.isEmailVerified()) {
                Log.e("Already singed in user", "<<<<<<<<<<<<<<<");
                Log.e("Already singed in user", currentUser.getUid() + "");
                //Intent intent = new Intent(MainActivity.this, AllMarkersActivity.class);
                //startActivity(intent);
            }
        }

        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);

        sliderView = findViewById(R.id.sliderViewMain);

        sliderAdapter = new SliderAdapter(images);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();

    }

    public void logIn(View view) {


        db = FirebaseFirestore.getInstance();

        String email = username.getText().toString();
        String _password = password.getText().toString();

        if(email.length() >0 && _password.length() > 0) {
            auth.signInWithEmailAndPassword(email, _password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    user = auth.getCurrentUser();
//                    Log.e("Current user is", "" + user.getUid());
                    if (task.isSuccessful()) {
                        if(user.isEmailVerified()) {
                            Intent intent = new Intent(MainActivity.this, AllMarkersActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Please verify you account", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Log.e("ERROR ++++++++>", "" + task.getException());
                    }
                }
            });
        } else {
            Log.e("++++++++>", "Please enter your email and password");
            Toast.makeText(MainActivity.this, "Please enter your email and password", Toast.LENGTH_LONG).show();
        }
    }

    public void toSignUp(View view) {

        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);

    }

    public void retrieveForgottenPasswordAccount(View view) {
        Intent intent = new Intent(MainActivity.this, ForgottenPasswordActivity.class);
        startActivity(intent);
    }

}