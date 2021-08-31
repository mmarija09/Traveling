package com.example.traveling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText username;
    EditText email;
    EditText password;
    EditText password2;

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
        setContentView(R.layout.activity_sign_up);


        initial();

        sliderView = findViewById(R.id.sliderView);

        sliderAdapter = new SliderAdapter(images);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();

    }

    public void initial() {
        username = findViewById(R.id.newUsername);
        email = findViewById(R.id.emailEt);
        password = findViewById(R.id.firstPassword);
        password2 = findViewById(R.id.secondPassword);


    }

    public void signUp(View view) {

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                "com.example.traveling",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();

        if (password.getText().toString().equals(password2.getText().toString()) && username.getText().toString().length() > 3) {

            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            String _email = email.getText().toString();
            String _password = password.getText().toString();

            auth.createUserWithEmailAndPassword(_email, _password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user = auth.getCurrentUser();
                        Log.e("Successfully create new account", "<<<<<<<<========");
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.e("in send email verification", "<<<<<<<<<<<<<<<<<");
                                if (task.isSuccessful()) {
                                    createUser();
                                    Log.e("Successfully create new account. Email was sent to your e-mail address", "<<<<<<<<========");
                                    Toast.makeText(SignUpActivity.this,
                                            "Successfully create new account. Email was sent to your e-mail address",
                                            Toast.LENGTH_LONG).show();
                                            finishSignInUp();
                                } else {
                                    Log.e("-------------->", "" + task.getException().toString());
                                }
                            }
                        });
                        Log.e("CURRENT USER", "" + user.getUid());


                    } else {
                        Log.e("LLLLLLLLLLLLLLL", task.getException().toString());
                    }
                }
            });
        }
    }

    private void finishSignInUp() {

        Intent intent = new Intent(SignUpActivity.this, SuccessfulSignInActivity.class);
        startActivity(intent);
    }


    public void createUser () {
        Map<String, Object> map = new HashMap<>();

        if (password.getText().toString().equals(password2.getText().toString()) && username.getText().toString().length() > 3) {

            map.put("username", username.getText().toString());
            map.put("e-mail", email.getText().toString());
            map.put("date of reg", Calendar.getInstance().getTime().toString());

            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            db.collection("Users").document("" + user.getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(SignUpActivity.this, "User Saved", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

