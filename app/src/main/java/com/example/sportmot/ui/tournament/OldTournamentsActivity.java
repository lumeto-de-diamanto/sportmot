package com.example.sportmot.ui.tournament;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.sportmot.R;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sportmot.api.RetrofitClient;
import com.example.sportmot.api.TournamentApiService;
import com.example.sportmot.data.entities.Tournament;
import com.example.sportmot.ui.tournament.fragment.ViewResultsFragment;
import com.example.sportmot.api.ScheduleApiService;
import com.example.sportmot.api.ScheduleRetrofitClient;
import com.example.sportmot.data.entities.TournamentNew;
import com.example.sportmot.data.entities.TournamentNewWrapper;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class OldTournamentsActivity extends AppCompatActivity {
    private TextView tournamentInfo;
    private TournamentApiService apiService;
    private LinearLayout tournamentContainer;
    private String role;
    private ScheduleApiService newApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_list);

        tournamentInfo = findViewById(R.id.tournament_info);
        tournamentContainer = findViewById(R.id.tournament_list);
        apiService = RetrofitClient.getClient().create(TournamentApiService.class);
        newApiService = ScheduleRetrofitClient.getApiService();

        Button til_baka = findViewById(R.id.til_baka);
        TextView tournament_title = findViewById(R.id.tournament_title);
        tournament_title.setText("Gömul mót");
        til_baka.setOnClickListener((v) ->
                onBackPressed()
        );
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        role = prefs.getString("user_role", "");

        //fetchOldTournaments(); // Fetch old tournaments
        fetchTournamentsFromApi();
    }

    private void fetchTournamentsFromApi(){
        String apiKey = "tuIVdCZqQmHUhhc4QdjgOpwYLI2T2AAX7eq7lycr";

        newApiService.getTournaments(apiKey).enqueue(new Callback<List<TournamentNewWrapper>>() {
            @Override
            public void onResponse(Call<List<TournamentNewWrapper>> call, Response<List<TournamentNewWrapper>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TournamentNewWrapper> tournamentWrappers = response.body();
                    if (!tournamentWrappers.isEmpty()) {
                        // Extract TournamentNew from the wrappers
                        List<TournamentNew> tournamentsNew = extractTournaments(tournamentWrappers);
                        List<Tournament> tournaments = convertToTournamentList(tournamentsNew);
                        displayTournaments(tournaments);
                    } else {
                        tournamentInfo.setText("No tournaments available.");
                    }
                } else {
                    tournamentInfo.setText("Failed to load tournament data.");
                }
            }

            @Override
            public void onFailure(Call<List<TournamentNewWrapper>> call, Throwable t) {
                tournamentInfo.setText("Error fetching tournament data.");
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });
    }
    private List<Tournament> convertToTournamentList(List<TournamentNew> tournamentsNew) {
        List<Tournament> tournaments = new ArrayList<>();
        for (TournamentNew tournamentNew : tournamentsNew) {
            // Convert TournamentNew to Tournament (this assumes they have similar fields)
            Tournament tournament = new Tournament();
            tournament.setId(tournamentNew.getId());
            tournament.setTournamentName(tournamentNew.getTournamentName());

            // Convert the string time values to List<Integer>
            tournament.setStartTime(Tournament.convertTimeStringToList(tournamentNew.getStartTime()));
            tournament.setEndTime(Tournament.convertTimeStringToList(tournamentNew.getEndTime()));
            tournament.setTournamentDate(Tournament.convertTimeStringToList(tournamentNew.getTournamentDate()));

            tournaments.add(tournament);
        }
        return tournaments;
    }


    private List<TournamentNew> extractTournaments(List<TournamentNewWrapper> tournamentWrappers) {
        List<TournamentNew> tournaments = new ArrayList<>();
        for (TournamentNewWrapper wrapper : tournamentWrappers) {
            tournaments.add(wrapper.getTournament()); // Extract the TournamentNew object
        }
        return tournaments;
    }

    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private void fetchOldTournaments() {
        String today = getTodayDate();

        apiService.getOldTournaments(today).enqueue(new Callback<List<Tournament>>() {
            @Override
            public void onResponse(Call<List<Tournament>> call, Response<List<Tournament>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Tournament> tournaments = filterPastTournaments(response.body());
                    if (!tournaments.isEmpty()) {
                        displayTournaments(tournaments);
                    } else {
                        tournamentInfo.setText("No past tournaments available.");
                    }
                } else {
                    tournamentInfo.setText("Failed to load tournament data.");
                }
            }

            @Override
            public void onFailure(Call<List<Tournament>> call, Throwable t) {
                tournamentInfo.setText("Error fetching tournament data.");
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });
    }

    private List<Tournament> filterPastTournaments(List<Tournament> tournaments) {
        List<Tournament> pastTournaments = new ArrayList<>();
        Calendar now = Calendar.getInstance();

        for (Tournament tournament : tournaments) {
            Calendar tournamentDate = toCalendarDate(tournament.getTournamentDate());
            if (tournamentDate != null && tournamentDate.before(now)) {
                pastTournaments.add(tournament);
            }
        }
        return pastTournaments;
    }

    private Calendar toCalendarDate(List<Integer> date) {
        if (date != null && date.size() == 3) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(date.get(0), date.get(1) - 1, date.get(2), 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar;
        }
        return null;
    }

    private void displayTournaments(List<Tournament> tournaments) {
        tournamentContainer.removeAllViews();
        for (Tournament tournament : tournaments) {
            View tournamentView = LayoutInflater.from(this).inflate(R.layout.tournament_item, tournamentContainer, false);

            TextView name = tournamentView.findViewById(R.id.tournament_name);
            TextView details = tournamentView.findViewById(R.id.tournament_details);
            Button viewResultsButton = tournamentView.findViewById(R.id.view_results);
            Button viewSchedule = tournamentView.findViewById(R.id.view_schedule);
            Button viewMap = tournamentView.findViewById(R.id.view_map);
            Button addLocation = tournamentView.findViewById(R.id.add_location);
            Button registerTeamButton = tournamentView.findViewById(R.id.skra_lid);
            Button viewStatisticsButton = tournamentView.findViewById(R.id.view_statistics_button);


            name.setText(tournament.getTournamentName());
            details.setText("Start Time: " + tournament.getStartTime() + "\nEnd Time: " + tournament.getEndTime());
            //details.setText("Date: " + formatDate(tournament.getTournamentDate()));

            viewSchedule.setVisibility(View.GONE);
            viewMap.setVisibility(View.GONE);
            addLocation.setVisibility(View.GONE);
            registerTeamButton.setVisibility(View.GONE);
            viewStatisticsButton.setVisibility(View.GONE);

            viewResultsButton.setOnClickListener(v -> {
                ViewResultsFragment fragment = ViewResultsFragment.newInstance(tournament.getId());
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            });

            tournamentContainer.addView(tournamentView);
        }
        //StringBuilder info = new StringBuilder();
        //for (Tournament tournament : tournaments) {
           // info.append("Tournament: ").append(tournament.getTournamentName())
            //        .append("\nDate: ").append(formatDate(tournament.getTournamentDate()))
              //      .append("\n\n");
        //}
        //tournamentInfo.setText(info.toString());
    }

    private String formatDate(List<Integer> date) {
        if (date != null && date.size() == 3) {
            return String.format("%04d-%02d-%02d", date.get(0), date.get(1), date.get(2));
        }
        return "Unknown Date";
    }
}
