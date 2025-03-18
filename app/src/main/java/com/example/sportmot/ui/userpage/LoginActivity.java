package com.example.sportmot.ui.userpage;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import android.os.Bundle;
import com.example.sportmot.R;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.content.SharedPreferences;

import com.example.sportmot.api.RetrofitClient;
import com.example.sportmot.api.TournamentApiService;
import com.example.sportmot.ui.homepage.homepageActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, signUpRedirectButton;
    private TournamentApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpRedirectButton = findViewById(R.id.signUpRedirectButton);

        apiService = RetrofitClient.getClient().create(TournamentApiService.class);

        // Login button click listener
        loginButton.setOnClickListener(v -> attemptLogin());

        // Move to SignUpActivity
        signUpRedirectButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        TextView statusTextView = findViewById(R.id.statusTextView);

        if (email.isEmpty() || password.isEmpty()) {
            statusTextView.setText("Please enter both email and password");
            return;
        }

        Call<String> call = apiService.login(email, password);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    statusTextView.setText(response.body());
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_name", email);
                    editor.putString("user_password", password);
                    editor.apply();
                    LoginActivity.this.finish();
                    //navigateToHomePage();
                } else {
                    statusTextView.setText("Invalid credentials. Try again.");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                statusTextView.setText("Error: " + t.getMessage());
            }
        });
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(LoginActivity.this, homepageActivity.class);
        startActivity(intent);
        finish(); // Close LoginActivity
    }
}