package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button CreateAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        CreateAccountButton = findViewById(R.id.register_btn);
        InputName = findViewById(R.id.register_username_input);
        InputPassword = findViewById(R.id.register_password_input);
        InputPhoneNumber = findViewById(R.id.register_phone_number_input);
        loadingBar = new ProgressDialog(this);
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });


    }

    private void CreateAccount() {
        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        if (TextUtils.isEmpty(phone) || phone.length() < 11) {
            Toast.makeText(this, "Enter a valid Phone Number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(name)) {

            Toast.makeText(this, "Enter a valid name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password) || password.length() < 6) {
            Toast.makeText(this, "Enter a valid  password", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Creating account");
            loadingBar.setMessage("Please wait while creating account");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            ValidatePhoneNumber(name, phone, password);
        }
    }

    private void ValidatePhoneNumber(final String name, final String phone, final String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(phone).exists())) {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phone);
                    userDataMap.put("password", password);
                    userDataMap.put("name", name);
                    rootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this,
                                                "Account created successfully"
                                                , Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this
                                                , LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this,
                                                "Error, Try again later",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error, Try again later"
                            , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
