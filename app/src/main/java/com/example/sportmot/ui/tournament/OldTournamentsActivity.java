package com.example.sportmot.ui.tournament;

import android.os.Bundle;
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

public class OldTournamentsActivity extends AppCompatActivity {
    private TextView tournamentInfo;
    private TournamentApiService apiService;

    /*Hæ Tobba hérna, þarf að skoða þetta líka
    flækja í gangi og þarf að sjá hvað á að vera og
    hvað fær að fjúka ;)
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_current_tournament);

        tournamentInfo = findViewById(R.id.tournament_info);
        apiService = RetrofitClient.getClient().create(TournamentApiService.class);

        fetchOldTournaments(); // Fetch old tournaments
        setContentView(R.layout.activity_tournament_list);
        Button til_baka = findViewById(R.id.til_baka);
        TextView tournament_title = findViewById(R.id.tournament_title);
        tournament_title.setText("Gömul mót");
        til_baka.setOnClickListener((v) ->
                onBackPressed()
        );
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
        StringBuilder info = new StringBuilder();
        for (Tournament tournament : tournaments) {
            info.append("Tournament: ").append(tournament.getTournamentName())
                    .append("\nDate: ").append(formatDate(tournament.getTournamentDate()))
                    .append("\n\n");
        }
        tournamentInfo.setText(info.toString());
    }

    private String formatDate(List<Integer> date) {
        if (date != null && date.size() == 3) {
            return String.format("%04d-%02d-%02d", date.get(0), date.get(1), date.get(2));
        }
        return "Unknown Date";
    }
}
