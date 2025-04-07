package com.example.sportmot.ui.userpage;

import com.example.sportmot.R;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.example.sportmot.api.RetrofitClient;
import com.example.sportmot.api.TournamentApiService;
import com.example.sportmot.data.entities.User;

import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, nameEditText;
    private Button signUpButton, loginRedirectButton;
    private RadioGroup roleRadioGroup;
    private TournamentApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        signUpButton = findViewById(R.id.signUpButton);
        loginRedirectButton = findViewById(R.id.loginRedirectButton);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);

        apiService = RetrofitClient.getClient().create(TournamentApiService.class);

        // SignUp button click listener
        signUpButton.setOnClickListener(v -> attemptSignUp());

        // Move to LoginActivity
        loginRedirectButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void attemptSignUp() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();

        int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRoleButton = findViewById(selectedRoleId);
        String role = selectedRoleButton.getTag().toString().toLowerCase();


        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new User object
        User user = new User(email, name, password, role);

        Call<String> call = apiService.signUp(user);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Sign-up successful!", Toast.LENGTH_SHORT).show();

                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("user_role", role);
                    editor.apply();

                    navigateToLogin();
                    Log.d("SignUp", "User created successfully: " + response.body());
                } else {
                    Toast.makeText(SignupActivity.this, "Sign-up failed. Try again!", Toast.LENGTH_SHORT).show();
                    Log.e("SignUp", "Failed to create user: " + response.code() + " " + response.message());
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("SignUp", "Error Body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}