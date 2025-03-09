package com.example.sportmot.ui.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sportmot.R;
import com.example.sportmot.databinding.ActivityMainBinding;
import com.example.sportmot.ui.tournament.CurrentTournamentActivity;
import com.example.sportmot.ui.tournament.OldTournamentsActivity;
import com.example.sportmot.ui.tournament.UpcomingTournamentActivity;
import com.example.sportmot.ui.userpage.LoginActivity;
import com.google.android.material.snackbar.Snackbar;

public class homepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Button button = findViewById(R.id.mot_i_dag);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(homepageActivity.this, CurrentTournamentActivity.class);
            startActivity(intent);
        });

        // ✅ Button for Upcoming Tournaments
        Button upcomingButton = findViewById(R.id.naestu_mot);
        upcomingButton.setOnClickListener(v -> {
            Intent intent = new Intent(homepageActivity.this, UpcomingTournamentActivity.class);
            startActivity(intent);
        });

        Button oldButton = findViewById(R.id.gomul_mot);
        oldButton.setOnClickListener(v -> {
            Intent intent = new Intent(homepageActivity.this, OldTournamentsActivity.class);
            startActivity(intent);
        });

        Button loginButton = findViewById(R.id.login_page);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(homepageActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}




