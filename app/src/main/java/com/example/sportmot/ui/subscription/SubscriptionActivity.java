package com.example.sportmot.ui.subscription;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.util.HashMap;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import android.graphics.Color;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import com.example.sportmot.R;
import com.example.sportmot.api.ScheduleApiService;
import com.example.sportmot.api.ScheduleRetrofitClient;
import com.example.sportmot.data.entities.ChallongeMatch;
import com.example.sportmot.data.entities.ChallongeMatchWrapper;
import com.example.sportmot.data.entities.Game;
import com.example.sportmot.ui.subscription.TournamentListActivity;


import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

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
        fetchMatchesFromChallonge();

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

        loadSubscribedTeams();

        //Hardcoded games
        allGames.add(new Game("team1", "Team B", "March 10", "10:00 AM"));
        allGames.add(new Game("team1", "Team C", "March 12", "12:00 PM"));
        allGames.add(new Game("team2", "Team D", "March 11", "11:00 AM"));
        updateUI();
    }

    private void loadSubscribedTeams() {
        SharedPreferences prefs = getSharedPreferences("subscriptions", MODE_PRIVATE);
        Set<String> subscribedTeams = prefs.getStringSet("subscribed_teams", new HashSet<>());

        gamesLayout.removeAllViews();

        if (subscribedTeams.isEmpty()) {
            subscribeButton.setVisibility(View.VISIBLE);
        } else {
            subscribeButton.setVisibility(View.VISIBLE);
            displaySubscribedTeams(new ArrayList<>(subscribedTeams));
        }
    }

    private void displaySubscribedTeams(List<String> teams) {
        gamesLayout.removeAllViews();
        for (String team : teams) {
            LinearLayout teamCard = new LinearLayout(this);
            teamCard.setOrientation(LinearLayout.VERTICAL);
            teamCard.setPadding(30, 30, 30, 30);
            teamCard.setBackground(getResources().getDrawable(R.drawable.card_background));
            teamCard.setElevation(8);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            cardParams.setMargins(20, 20, 20, 20);
            teamCard.setLayoutParams(cardParams);

            TextView teamView = new TextView(this);
            teamView.setText(team);
            teamView.setTextSize(24);
            teamView.setTypeface(null, android.graphics.Typeface.BOLD);
            teamView.setTextColor(Color.WHITE);
            teamView.setGravity(Gravity.CENTER);
            teamView.setPadding(10, 10, 10, 10);

            teamCard.addView(teamView);

            String teamId = getTeamIdFromName(team);
            if (!teamId.isEmpty()) {
                displayTeamGames(teamId, teamCard);
            } else {
                System.out.println("❌ No teamId found for: " + team); // Debugging
            }

            Button unsubscribeButton = new Button(this);
            unsubscribeButton.setText("Unsubscribe");
            unsubscribeButton.setBackgroundColor(Color.DKGRAY);
            unsubscribeButton.setTextColor(Color.WHITE);
            unsubscribeButton.setPadding(10, 5, 10, 5);
            unsubscribeButton.setOnClickListener(v -> {
                unsubscribeTeam(team);
            });

            teamCard.addView(unsubscribeButton);

            gamesLayout.addView(teamCard);

        }
    }

    private void unsubscribeTeam(String team) {
        SharedPreferences prefs = getSharedPreferences("subscriptions", MODE_PRIVATE);
        Set<String> subscribedTeams = new HashSet<>(prefs.getStringSet("subscribed_teams", new HashSet<>()));

        if (subscribedTeams.contains(team)) {
            subscribedTeams.remove(team);
            prefs.edit().putStringSet("subscribed_teams", subscribedTeams).apply();
        }

        updateUI();
    }

    private String getTeamIdFromName(String teamName) {
        // Hardcoded mapping (Replace with actual API data later)
        HashMap<String, String> teamMap = new HashMap<>();
        teamMap.put("Team A", "team1");
        teamMap.put("Team B", "team1");
        teamMap.put("Team C", "team1");
        teamMap.put("Team D", "team2");

        return teamMap.getOrDefault(teamName, "");
    }

    private void displayTeamGames(String selectedTeam, LinearLayout teamCard) {
        for (Game game : allGames) {
            if (game.getTeamId().equals(selectedTeam)) {
                TextView gameTextView = new TextView(this);
                gameTextView.setText("VS " + game.getOpponent() + " - " + game.getDate() + " at " + game.getTime());
                gameTextView.setTextSize(18);
                gameTextView.setTextColor(Color.WHITE);
                gameTextView.setGravity(Gravity.CENTER);
                gameTextView.setPadding(10, 5, 10, 5);

                teamCard.addView(gameTextView);
            }
        }
    }

    private void updateUI() {
        gamesLayout.removeAllViews(); // Clear the layout once

        // Fetch and display matches
        fetchMatchesFromChallonge();

        // Now display subscribed teams
        Set<String> subscribedTeams = subscriptionManager.getSubscribedTeams();
        if (!subscribedTeams.isEmpty()) {
            displaySubscribedTeams(new ArrayList<>(subscribedTeams));
        } else {
            subscribeButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubscribedTeams();
    }

    public void onSubscribeClicked(View view) {
        Intent intent = new Intent(this, TournamentListActivity.class);
        startActivity(intent);
    }

    private void fetchMatchesFromChallonge() {
        ScheduleApiService api = ScheduleRetrofitClient.getApiService();
        Call<List<ChallongeMatchWrapper>> call = api.getMatches("your_tournament_id", "your_api_key");

        call.enqueue(new Callback<List<ChallongeMatchWrapper>>() {
            @Override
            public void onResponse(Call<List<ChallongeMatchWrapper>> call, Response<List<ChallongeMatchWrapper>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ChallongeMatchWrapper> matchWrappers = response.body();

                    // Display matches
                    for (ChallongeMatchWrapper wrapper : matchWrappers) {
                        ChallongeMatch match = wrapper.match;

                        TextView matchTextView = new TextView(SubscriptionActivity.this);
                        matchTextView.setText("Match: " + match.player1Id + " vs " + match.player2Id);
                        matchTextView.setTextSize(18);
                        matchTextView.setTextColor(Color.BLACK);
                        matchTextView.setPadding(10, 10, 10, 10);
                        gamesLayout.addView(matchTextView);
                    }
                } else {
                    Log.e("MATCHES", "Failed to get matches");
                }
            }

            @Override
            public void onFailure(Call<List<ChallongeMatchWrapper>> call, Throwable t) {
                Log.e("MATCHES", "Error: " + t.getMessage());
            }
        });
    }

}