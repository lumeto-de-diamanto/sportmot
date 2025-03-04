package com.example.sportmot.ui.tournament;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportmot.R;

public class OldTournamentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_list);
        Button til_baka = findViewById(R.id.til_baka);
        TextView tournament_title = findViewById(R.id.tournament_title);
        tournament_title.setText("Gömul mót");
        til_baka.setOnClickListener((v) ->
                onBackPressed()
        );
    }
}