package com.example.sportmot.ui.tournament;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.sportmot.R;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportmot.R;
import com.example.sportmot.api.RetrofitClient;
import com.example.sportmot.api.TournamentApiService;
import com.example.sportmot.data.entities.Tournament;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import java.time.LocalDate;

import com.example.sportmot.ui.tournament.fragment.RegisterFormFragment;


public class UpcomingTournamentActivity extends AppCompatActivity {
    private TextView tournamentInfo;
    private TournamentApiService apiService;
/*hæhó, ég aftur sama dæmi og áður laga til ;)*/
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

        tournamentInfo = findViewById(R.id.tournament_info);
        apiService = RetrofitClient.getClient().create(TournamentApiService.class);

        fetchUpcomingTournaments(); // Fetch tournaments after today
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

    // Display upcoming tournaments
    private void displayTournaments(List<Tournament> tournaments) {
        StringBuilder info = new StringBuilder();
        for (Tournament tournament : tournaments) {
            info.append("Tournament: ").append(tournament.getTournamentName())
                    .append("\nDate: ").append(formatDate(tournament.getTournamentDate()))
                    .append("\nStart Time: ").append(formatTime(tournament.getStartTime()))
                    .append("\nFields: ").append(tournament.getFields())
                    .append("\nGroups: ").append(tournament.getNumberOfGroups())
                    .append("\nTeams per Group: ").append(tournament.getTeamsPerGroup())
                    .append("\nGame Length: ").append(tournament.getGameLength())
                    .append(" mins\n\n");
        }
        tournamentInfo.setText(info.toString());
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