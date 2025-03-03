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
    }
}




