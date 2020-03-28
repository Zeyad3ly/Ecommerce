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

import com.example.ecommerce.Model.Users;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText InputPassword, InputPhoneNumber;
    ProgressDialog loadingBar;
    View v;
    private String parentDbName = "Users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginButton = findViewById(R.id.login_button);
        InputPassword = findViewById(R.id.login_password_input);
        InputPhoneNumber = findViewById(R.id.login_phone_number_input);
        v = findViewById(R.id.content);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

    }

    private void LoginUser() {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        if (TextUtils.isEmpty(phone) || phone.length() != 11) {
            InputPhoneNumber.setError("Enter a valid phone number");
        } else if (TextUtils.isEmpty(password) || password.length() < 6) {
            InputPassword.setError("Enter a valid password");
        } else {
            loadingBar.setTitle("Logging");
            loadingBar.setMessage("Please wait while logging you in...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            AllowAccessAccount(phone, password);
        }
    }

    private void AllowAccessAccount(final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists()) {

                    Users usersData = dataSnapshot.child(parentDbName).child(phone)
                            .getValue(Users.class);
                    if (usersData.getPhone().equals(phone)) {
                        if (usersData.getPassword().equals(password)) {
                            Toast.makeText(LoginActivity.this,
                                    "logged in successfully", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(LoginActivity.this,
                                    HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                } else {
                    Snackbar.make(v, "Phone do not exists, try again",
                            Snackbar.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
