package com.example.sportmot.ui.tournament;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportmot.R;

import com.example.sportmot.ui.tournament.fragment.RegisterFormFragment;


public class UpcomingTournamentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_tournament);
        Button til_baka = findViewById(R.id.til_baka);
        Button skra_lid = findViewById(R.id.skra_lid);
        TextView tournament_title = findViewById(R.id.tournament_title);
        tournament_title.setText("Næstu mót");
        til_baka.setOnClickListener((v) ->
                onBackPressed()
        );




        skra_lid.setOnClickListener(v ->{
            RegisterFormFragment fragment = new RegisterFormFragment();

            findViewById(R.id.formFragment).setVisibility(View.VISIBLE);

            getSupportFragmentManager().beginTransaction().replace(R.id.formFragment,fragment).addToBackStack(null).commit();
        });

    }
}
