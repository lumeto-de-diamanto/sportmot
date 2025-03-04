package com.example.sportmot.ui.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportmot.R;
import com.example.sportmot.ui.tournament.CurrentTournamentActivity;
import com.example.sportmot.ui.tournament.OldTournamentsActivity;
import com.example.sportmot.ui.tournament.UpcomingTournamentActivity;

public class homepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Button mot_i_dag = findViewById(R.id.mot_i_dag);
        Button naestu_mot = findViewById(R.id.naestu_mot);
        Button gomul_mot = findViewById(R.id.gomul_mot);

        mot_i_dag.setOnClickListener((v) ->
                startActivity(new Intent(homepageActivity.this, CurrentTournamentActivity.class))
        );

        naestu_mot.setOnClickListener((v) ->
                startActivity(new Intent(homepageActivity.this, UpcomingTournamentActivity.class))
        );

        gomul_mot.setOnClickListener((v) ->
                startActivity(new Intent(homepageActivity.this, OldTournamentsActivity.class))
        );
    }
}




