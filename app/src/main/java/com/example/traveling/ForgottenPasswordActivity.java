package com.example.traveling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgottenPasswordActivity extends AppCompatActivity {

    FirebaseAuth auth;

    EditText email;
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.emailEditText);
        reset = findViewById(R.id.resetButton);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().length() > 0) {
                    String emailAddress = email.getText().toString();
                    auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Log.e("CHANGING PASSWORD", "IS COMPLITED");
                                //add alert dialog that would take the user to the MainActivity
                            } else  {
                                Log.e("CHANGING PASSWORD", "IS NOT COMPLITED");
                            }
                        }
                    });
                }
            }
        });
    }

    public void backToMain(View view) {
        Intent intent = new Intent(ForgottenPasswordActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}