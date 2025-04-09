package com.example.sportmot.ui.subscription;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportmot.data.entities.ChallongeTeam;
import com.example.sportmot.data.entities.ChallongeTeamWrapper;
import com.example.sportmot.data.entities.TournamentNewWrapper;
import com.example.sportmot.data.entities.TournamentNew;
import com.example.sportmot.api.ImageApiService;
import com.example.sportmot.api.ImageRetrofitClient;
import com.example.sportmot.api.TournamentApiService;
import com.example.sportmot.data.entities.Tournament;
import com.example.sportmot.api.ScheduleApiService;
import com.example.sportmot.api.ScheduleRetrofitClient;
import com.example.sportmot.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TournamentListActivity extends AppCompatActivity {

    private LinearLayout tournamentListLayout;
    private TournamentApiService tournamentApiService;
    private ImageApiService imageApiService;
    private ScheduleApiService scheduleApiService;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_list);

        tournamentListLayout = findViewById(R.id.tournament_list);
        Button backButton = findViewById(R.id.til_baka);

        TextView tournament_title = findViewById(R.id.tournament_title);
        tournament_title.setText("Öll mót");

        backButton.setOnClickListener(v -> finish());

        imageApiService = ImageRetrofitClient.getClient().create(ImageApiService.class);
        scheduleApiService = ScheduleRetrofitClient.getApiService();
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        fetchChallongeTournaments(new ArrayList<>(), new HashSet<>());

    }
    private void fetchChallongeTournaments(List<TournamentNew> allTournaments, Set<String> tournamentIds) {
        ScheduleApiService scheduleApiService = ScheduleRetrofitClient.getApiService();
        String API_KEY = "tuIVdCZqQmHUhhc4QdjgOpwYLI2T2AAX7eq7lycr";
        scheduleApiService.getTournaments(API_KEY).enqueue(new Callback<List<TournamentNewWrapper>>() {
            @Override
            public void onResponse(Call<List<TournamentNewWrapper>> call, Response<List<TournamentNewWrapper>> response) {
                Log.d("API_RESPONSE", "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_RESPONSE", "Received tournaments: " + response.body().size());

                    for (TournamentNewWrapper wrapper : response.body()) {
                        TournamentNew tournamentNew = wrapper.getTournament();
                        Log.d("API_RESPONSE", "Tournament: " + tournamentNew.getTournamentName());

                        String tournamentId = String.valueOf(tournamentNew.getId());

                        // Ensure we don't add duplicate tournaments
                        if (!tournamentIds.contains(tournamentId)) {
                            allTournaments.add(tournamentNew);
                            tournamentIds.add(tournamentId);
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Failed to fetch tournaments from new API");
                }

                // Display ALL tournaments
                displayTournaments(allTournaments);
            }

            @Override
            public void onFailure(Call<List<TournamentNewWrapper>> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching tournaments from new API: " + t.getMessage());
                displayTournaments(allTournaments);
            }
        });
    }

    private void displayTournaments(List<TournamentNew> tournaments) {
        tournamentListLayout.removeAllViews();

        for (TournamentNew tournament : tournaments) {
            LinearLayout tournamentRow = new LinearLayout(this);
            tournamentRow.setOrientation(LinearLayout.HORIZONTAL);
            tournamentRow.setPadding(16, 16, 16, 16);
            tournamentRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView textView = new TextView(this);
            textView.setText(tournament.getTournamentName());
            textView.setTextSize(18);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(16, 16, 16, 16);
            textView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            Button viewTeamsButton = new Button(this);
            viewTeamsButton.setText("View Teams");
            viewTeamsButton.setOnClickListener(v -> showTeamsDialog(tournament));

            tournamentRow.addView(textView);
            tournamentRow.addView(viewTeamsButton);

            tournamentListLayout.addView(tournamentRow);
        }
    }

    private void showTeamsDialog(TournamentNew tournament) {
        int tournamentId = tournament.getId();  // Assuming tournament ID is a String
        String API_KEY = "tuIVdCZqQmHUhhc4QdjgOpwYLI2T2AAX7eq7lycr";  // Your API key

        String tournamentIdStr = String.valueOf(tournamentId);

        if (tournamentId <= 0) {
            Log.e("API_ERROR", "Invalid tournament ID: " + tournamentId);
            return;
        }

        // Fetch teams using the tournament ID
        scheduleApiService.getTeams(tournamentIdStr, API_KEY).enqueue(new Callback<List<ChallongeTeamWrapper>>() {
            @Override
            public void onResponse(Call<List<ChallongeTeamWrapper>> call, Response<List<ChallongeTeamWrapper>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> teams = new ArrayList<>();
                    for (ChallongeTeamWrapper wrapper : response.body()) {
                        ChallongeTeam team = wrapper.getTeam();
                        if (team != null && team.getName() != null) {
                            teams.add(team.getName());
                        } else {
                            Log.w("API_WARNING", "Found a team with no name or null data");
                        }
                    }

                    // Show team selection dialog with the list of team names
                    showTeamSelectionDialog(tournament.getTournamentName(), teams);
                } else {
                    Log.e("API_ERROR", "Failed to load teams for tournament " + tournamentId);
                    Toast.makeText(TournamentListActivity.this, "Failed to load teams", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ChallongeTeamWrapper>> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching teams: " + t.getMessage());
                Toast.makeText(TournamentListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTeamGames(String selectedTeam) {
        //Hardcoded games for now
        List<String> allGames = new ArrayList<>();
        allGames.add("10:00 - Team A vs Team B");
        allGames.add("11:30 - Team C vs Team D");
        allGames.add("13:00 - Team A vs Team C");
        allGames.add("14:30 - Team B vs Team D");

        TextView tournament_title = findViewById(R.id.tournament_title);
        tournament_title.setText(selectedTeam);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(VISIBLE);

        ImageView team_icon = findViewById(R.id.teamIcon);
        String teamID = (Objects.equals(selectedTeam, "Team A")) ? "1" : "2";

        imageApiService.getImage(teamID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InputStream stream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    System.out.println("picture get!");
                    team_icon.setVisibility(VISIBLE);
                    team_icon.setImageBitmap(bitmap);
                    progressBar.setVisibility(GONE);
                } else {
                    progressBar.setVisibility(GONE);
                    team_icon.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(GONE);
                team_icon.setVisibility(VISIBLE);
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });

        // Filter only the games for the selected team
        List<String> teamGames = new ArrayList<>();
        for (String game : allGames) {
            if (game.contains(selectedTeam)) {
                teamGames.add(game);
            }
        }

        displayTeamGames(teamGames);
    }

    private Bitmap downloadFile(ResponseBody body) throws IOException {

        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        return BitmapFactory.decodeStream(bis);
    }

    private void displayTeamGames(List<String> teamGames) {
        tournamentListLayout.removeAllViews();

        for (String game : teamGames) {
            TextView gameTextView = new TextView(this);
            gameTextView.setText(game);
            gameTextView.setTextSize(18);
            gameTextView.setTextColor(Color.BLACK);
            gameTextView.setGravity(Gravity.CENTER);
            gameTextView.setPadding(16, 16, 16, 16);
            tournamentListLayout.addView(gameTextView);
        }
    }

    private void subscribeToTeam(String teamName) {
        SharedPreferences prefs = getSharedPreferences("subscriptions", MODE_PRIVATE);
        Set<String> subscribedTeams = new HashSet<>(prefs.getStringSet("subscribed_teams", new HashSet<>()));

        // Add new team
        subscribedTeams.add(teamName);

        // Save back to SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("subscribed_teams", subscribedTeams);
        editor.apply();

        System.out.println("Subscribed to team: " + teamName);
    }

    private void showTeamSelectionDialog(String tournamentName, List<String> teams) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a team in " + tournamentName);

        builder.setItems(teams.toArray(new String[0]), (dialog, which) -> {
            String selectedTeam = teams.get(which);
            subscribeToTeam(selectedTeam);
            showTeamGames(selectedTeam);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}