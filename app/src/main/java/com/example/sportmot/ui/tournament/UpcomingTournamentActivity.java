package com.example.sportmot.ui.tournament;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.sportmot.R;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import com.example.sportmot.ui.tournament.fragment.ViewGameScheduleFragment;
import com.example.sportmot.ui.tournament.fragment.StatisticsFragment;
import com.example.sportmot.ui.tournament.fragment.RegisterTeamFormFragment;

import android.util.Log;
import android.widget.TextView;
import android.content.SharedPreferences;

import com.example.sportmot.api.RetrofitClient;
import com.example.sportmot.api.TournamentApiService;
import com.example.sportmot.data.entities.Tournament;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

import com.example.sportmot.ui.tournament.fragment.RegisterTeamFormFragment;


public class UpcomingTournamentActivity extends AppCompatActivity {
    private TextView tournamentInfo;
    private TournamentApiService apiService;
    private String role;
    private LinearLayout tournamentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tournament_list);
        tournamentInfo = findViewById(R.id.tournament_info);
        tournamentContainer = findViewById(R.id.tournament_list);
        apiService = RetrofitClient.getClient().create(TournamentApiService.class);

        Button til_baka = findViewById(R.id.til_baka);
        TextView tournament_title = findViewById(R.id.tournament_title);
        tournament_title.setText("Næstu mót");

        til_baka.setOnClickListener((v) ->
                onBackPressed()
        );

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        role = prefs.getString("user_role", "");

        // Fetch tournaments after today
        fetchUpcomingTournaments();
    }

    // Get today's date in yyyy-MM-dd format
    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    // Fetch tournaments happening after today
    private void fetchUpcomingTournaments() {
        String today = getTodayDate();

        apiService.getUpcomingTournaments(today).enqueue(new Callback<List<Tournament>>() {
            @Override
            public void onResponse(Call<List<Tournament>> call, Response<List<Tournament>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Tournament> tournaments = filterUpcomingTournaments(response.body());

                    if (!tournaments.isEmpty()) {
                        displayTournaments(tournaments);
                    } else {
                        tournamentInfo.setText("No upcoming tournaments available.");
                    }
                } else {
                    tournamentInfo.setText("Failed to load upcoming tournaments.");
                }
            }

            @Override
            public void onFailure(Call<List<Tournament>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch upcoming tournaments", t);
                tournamentInfo.setText("Error fetching tournament data.");
            }
        });
    }

    // Filter tournaments that are after today's date
    private List<Tournament> filterUpcomingTournaments(List<Tournament> tournaments) {
        List<Tournament> upcomingTournaments = new ArrayList<>();
        Calendar today = Calendar.getInstance();

        for (Tournament tournament : tournaments) {
            Calendar tournamentDate = toCalendarDate(tournament.getTournamentDate());

            if (tournamentDate != null && tournamentDate.after(today)) {
                upcomingTournaments.add(tournament);
            }
        }
        return upcomingTournaments;
    }

    // Convert List<Integer> to Calendar object
    private Calendar toCalendarDate(List<Integer> date) {
        if (date != null && date.size() == 3) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, date.get(0));
            calendar.set(Calendar.MONTH, date.get(1) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, date.get(2));
            return calendar;
        }
        return null;
    }
    private void showStatisticsFragment() {
        StatisticsFragment statisticsFragment = new StatisticsFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, statisticsFragment) // Ensure this ID exists in your XML
                .addToBackStack(null)
                .commit();
    }

    private void displayTournaments(List<Tournament> tournaments) {
        tournamentContainer.removeAllViews(); // Clear previous items

        for (Tournament tournament : tournaments) {
            View tournamentView = LayoutInflater.from(this).inflate(R.layout.tournament_item, tournamentContainer, false);

            TextView name = tournamentView.findViewById(R.id.tournament_name);
            TextView details = tournamentView.findViewById(R.id.tournament_details);
            Button viewSchedule = tournamentView.findViewById(R.id.view_schedule);
            Button registerTeamButton = tournamentView.findViewById(R.id.skra_lid); // Find the button
            Button viewStatisticsButton = tournamentView.findViewById(R.id.view_statistics_button);

            name.setText(tournament.getTournamentName());
            details.setText("Date: " + formatDate(tournament.getTournamentDate()));

            viewSchedule.setOnClickListener(v -> showScheduleFragment(tournament.getId()));

            if (!role.equals("admin")) {
                viewStatisticsButton.setVisibility(View.GONE);
            }

            // virkar ekki á coach af því að það þarf að bæta því role-i við database-inn!!
            if (!role.equals("coach")) {
                registerTeamButton.setVisibility(View.GONE);
                Log.d("UserRoleCheck", "Hiding View Statistics button.");
            } else {
                Log.d("UserRoleCheck", "Showing View Statistics button.");
                registerTeamButton.setOnClickListener(v -> showRegisterTeamFragment());
            }

            viewStatisticsButton.setOnClickListener(v -> showStatisticsFragment());

            tournamentContainer.addView(tournamentView);
        }
    }

    private void showScheduleFragment(int tournamentId) {
        View container = findViewById(R.id.ViewGameScheduleFragment);
        if (container != null) {
            container.setVisibility(View.VISIBLE);
        } else {
            Log.e("FragmentError", "Fragment container not found!");
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ViewGameScheduleFragment, new ViewGameScheduleFragment())
                .addToBackStack(null)
                .commit();
    }

    private void showRegisterTeamFragment() {
        View container = findViewById(R.id.formFragment); // Use correct ID
        if (container != null) {
            container.setVisibility(View.VISIBLE);
        } else {
            Log.e("FragmentError", "Fragment container for RegisterTeamFormFragment not found!");
            return; // Stop execution if the container is missing
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.formFragment, new RegisterTeamFormFragment()) // Correct ID
                .addToBackStack(null)
                .commit();
    }

    // Helper: Format date
    private String formatDate(List<Integer> date) {
        if (date != null && date.size() == 3) {
            return String.format("%04d-%02d-%02d", date.get(0), date.get(1), date.get(2));
        }
        return "Unknown Date";
    }

    // Helper: Format time
    private String formatTime(List<Integer> time) {
        if (time != null && time.size() >= 2) {
            return String.format("%02d:%02d", time.get(0), time.get(1));
        }
        return "Unknown Time";
    }
}