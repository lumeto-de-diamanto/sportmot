package com.example.sportmot.ui.startpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportmot.R;
import com.example.sportmot.ui.homepage.homepageActivity;
import com.example.sportmot.ui.userpage.LoginActivity;
import com.example.sportmot.ui.userpage.SignupActivity;

public class startpageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String savedName = sharedPreferences.getString("user_name", "");
        String savedPassword = sharedPreferences.getString("user_password", "");
        System.out.println("Name: " + savedName);
        System.out.println("Password: " + savedPassword);
        if (!savedName.isEmpty() && !savedPassword.isEmpty()) {
            Intent intent = new Intent(startpageActivity.this, homepageActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_startpage);

        Button loginButton = findViewById(R.id.login_page);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(startpageActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        Button signupButton = findViewById(R.id.signup_page);
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(startpageActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String savedName = sharedPreferences.getString("user_name", "");
        String savedPassword = sharedPreferences.getString("user_password", "");
        System.out.println("Name: " + savedName);
        System.out.println("Password: " + savedPassword);
        if (!savedName.isEmpty() && !savedPassword.isEmpty()) {
            Intent intent = new Intent(startpageActivity.this, homepageActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
