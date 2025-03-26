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

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportmot.api.ImageApiService;
import com.example.sportmot.api.ImageRetrofitClient;
import com.example.sportmot.api.RetrofitClient;
import com.example.sportmot.api.TournamentApiService;
import com.example.sportmot.data.entities.Tournament;
import com.example.sportmot.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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

        tournamentApiService = RetrofitClient.getClient().create(TournamentApiService.class);
        imageApiService = ImageRetrofitClient.getClient().create(ImageApiService.class);
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        loadTournaments();
    }

    private void loadTournaments() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        List<Tournament> allTournaments = new ArrayList<>();
        Set<String> tournamentIds = new HashSet<>();

        tournamentApiService.getCurrentTournaments(currentDate).enqueue(new Callback<List<Tournament>>() {
            @Override
            public void onResponse(Call<List<Tournament>> call, Response<List<Tournament>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Tournament tournament : response.body()) {
                        String tournamentId = String.valueOf(tournament.getId());

                        if (!tournamentIds.contains(tournamentId)) {
                            allTournaments.add(tournament);
                            tournamentIds.add(tournamentId);
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Failed to fetch current tournaments");
                }

                fetchUpcomingTournaments(currentDate, allTournaments, tournamentIds);
            }

            @Override
            public void onFailure(Call<List<Tournament>> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching current tournaments: " + t.getMessage());
            }
        });
    }

    private void fetchUpcomingTournaments(String currentDate, List<Tournament> allTournaments, Set<String> tournamentIds) {
        tournamentApiService.getUpcomingTournaments(currentDate).enqueue(new Callback<List<Tournament>>() {
            @Override
            public void onResponse(Call<List<Tournament>> call, Response<List<Tournament>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Tournament tournament : response.body()) {
                        String tournamentId = String.valueOf(tournament.getId());

                        if (!tournamentIds.contains(tournamentId)) {
                            allTournaments.add(tournament);
                            tournamentIds.add(tournamentId);
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Failed to fetch upcoming tournaments");
                }

                displayTournaments(allTournaments);
            }

            @Override
            public void onFailure(Call<List<Tournament>> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching upcoming tournaments: " + t.getMessage());
            }
        });
    }

    private void displayTournaments(List<Tournament> tournaments) {
        tournamentListLayout.removeAllViews();

        for (Tournament tournament : tournaments) {
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

    private void showTeamsDialog(Tournament tournament) {
        // **Hardcoded teams (since API is not working)**
        List<String> teams = new ArrayList<>();
        teams.add("Team A");
        teams.add("Team B");
        teams.add("Team C");
        teams.add("Team D");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a team in " + tournament.getTournamentName());

        builder.setItems(teams.toArray(new String[0]), (dialog, which) -> {
            String selectedTeam = teams.get(which);
            subscribeToTeam(selectedTeam);
            showTeamGames(selectedTeam);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showTeamGames(String selectedTeam) {
        // **Hardcoded games (since API is not working)**
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
        String teamID = (Objects.equals(selectedTeam, "Team A")) ? "1" : "2"; // Change this later

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
                    // display that bitmap somewhere
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

        /*
        try {
            System.out.println(responseBody);
            System.out.println(responseBody.execute());
            Bitmap bitmap = downloadFile(Objects.requireNonNull(responseBody.execute().body()));
            System.out.println("bitmap get!");
        } catch (IOException e) {
            e.printStackTrace();
        }
         */

        // **Filter only the games for the selected team**
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
        Set<String> subscribedTeams = prefs.getStringSet("teams", new HashSet<>());

        // Add new team to the set
        subscribedTeams.add(teamName);

        // Save back to SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("teams", subscribedTeams);
        editor.apply();

        System.out.println("Subscribed to team: " + teamName);
    }
}