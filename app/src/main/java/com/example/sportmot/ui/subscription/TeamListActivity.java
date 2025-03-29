package com.example.sportmot.ui.subscription;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sportmot.data.entities.Club;
import com.example.sportmot.data.entities.Team;

import java.util.Arrays;
import java.util.List;
import com.example.sportmot.R;

public class TeamListActivity extends AppCompatActivity {

    private Club ClubA = new Club();
    private List<Team> teams = Arrays.asList(
            new Team(123, "Team A",ClubA, "1"),
            new Team(456, "Team B",ClubA, "1"),
            new Team(789, "Team C",ClubA, "1")
    );

    private SubscriptionManager subscriptionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        subscriptionManager = new SubscriptionManager(this);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                teams.stream().map(Team::getTeamName).toArray(String[]::new)));

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            int teamId = teams.get(position).getTeamId();
            subscriptionManager.subscribeToTeam(String.valueOf(teamId));

            finish();
        });
    }
}