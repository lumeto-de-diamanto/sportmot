package com.example.sportmot.ui.subscription;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import com.example.sportmot.R;
import com.example.sportmot.data.entities.Game;
import com.example.sportmot.ui.subscription.TournamentListActivity;
public class SubscriptionActivity extends AppCompatActivity {
    private SubscriptionManager subscriptionManager;
    private LinearLayout gamesLayout;
    private Button subscribeButton;

    //Hardcoded leikir - API vonandi seinna!!!
    private List<Game> allGames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        subscriptionManager = new SubscriptionManager(this);
        gamesLayout = findViewById(R.id.gamesLayout);
        subscribeButton = findViewById(R.id.subscribeButton);

        subscribeButton.setOnClickListener(v -> {
            Intent intent = new Intent(SubscriptionActivity.this, TournamentListActivity.class);
            startActivity(intent);
        });

        Button til_baka = findViewById(R.id.til_baka);
        til_baka.setOnClickListener((v) ->
                onBackPressed());

        //Hardcoded games
        allGames.add(new Game("team1", "Team B", "March 10", "10:00 AM"));
        allGames.add(new Game("team1", "Team C", "March 12", "12:00 PM"));
        allGames.add(new Game("team2", "Team D", "March 11", "11:00 AM"));
        updateUI();
    }

    private void loadSubscribedTeams() {
        SharedPreferences prefs = getSharedPreferences("subscriptions", MODE_PRIVATE);
        Set<String> subscribedTeams = prefs.getStringSet("teams", new HashSet<>());

        gamesLayout.removeAllViews(); // Clear UI before adding new elements

        if (subscribedTeams.isEmpty()) {
            subscribeButton.setVisibility(View.VISIBLE);
        } else {
            subscribeButton.setVisibility(View.GONE);
            displaySubscribedTeams(new ArrayList<>(subscribedTeams));
        }
    }

    private void displaySubscribedTeams(List<String> teams) {
        for (String team : teams) {
            TextView teamView = new TextView(this);
            teamView.setText(team);
            teamView.setTextSize(18);
            teamView.setTextColor(getResources().getColor(android.R.color.black));
            teamView.setGravity(Gravity.CENTER);
            teamView.setPadding(16, 16, 16, 16);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(10, 10, 10, 10);
            teamView.setLayoutParams(layoutParams);

            gamesLayout.addView(teamView);
        }
    }

    private void updateUI() {
        gamesLayout.removeAllViews();
        Set<String> subscribedTeams = subscriptionManager.getSubscribedTeams();

        if (subscribedTeams.isEmpty()) {
            subscribeButton.setVisibility(View.VISIBLE);
        } else {
            subscribeButton.setVisibility(View.GONE);
            for (String teamId : subscribedTeams) {
                for (Game game : allGames) {
                    if (game.getTeamId().equals(teamId)) {
                        TextView gameView = new TextView(this);
                        gameView.setText("VS " + game.getOpponent() + " - " + game.getDate() + " at " + game.getTime());
                        gamesLayout.addView(gameView);
                    }
                }
            }
        }
    }

    public void onSubscribeClicked(View view) {
        Intent intent = new Intent(this, TournamentListActivity.class);
        startActivity(intent);
    }
}
