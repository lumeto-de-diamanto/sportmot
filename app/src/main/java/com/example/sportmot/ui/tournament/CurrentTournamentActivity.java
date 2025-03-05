package com.example.sportmot.ui.tournament;

import android.content.Intent;
import android.os.Bundle;
import com.example.sportmot.R;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

public class CurrentTournamentActivity extends AppCompatActivity {
    private TextView tournamentInfo;
    private TournamentApiService apiService;

    //hæhæ, við þurfum að greiða úr þessari flækju. Þetta er bæði frá
    //Bryndísi og Grétari
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_current_tournament);

        tournamentInfo = findViewById(R.id.tournament_info);
        apiService = RetrofitClient.getClient().create(TournamentApiService.class);

        fetchCurrentTournaments();  // Fetch all tournaments now
        setContentView(R.layout.activity_tournament_list);
        Button til_baka = findViewById(R.id.til_baka);
        TextView tournament_title = findViewById(R.id.tournament_title);
        tournament_title.setText("Mót í dag");
        til_baka.setOnClickListener((v) ->
                onBackPressed()
        );
    }

    // Get today's date in yyyy-MM-dd format
    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    // Fetch tournaments for today
    public void fetchCurrentTournaments() {
        String today = getTodayDate(); // Get today's date

        apiService.getCurrentTournaments(today).enqueue(new Callback<List<Tournament>>() {
            @Override
            public void onResponse(Call<List<Tournament>> call, Response<List<Tournament>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Tournament> tournaments = response.body();
                    List<Tournament> currentTournaments = filterTournamentsByTime(tournaments);

                    if (!currentTournaments.isEmpty()) {
                        displayTournaments(currentTournaments); // Display active tournaments
                    } else {
                        tournamentInfo.setText("No ongoing tournaments at this time.");
                    }
                } else {
                    tournamentInfo.setText("Failed to load tournament data.");
                }
            }

            @Override
            public void onFailure(Call<List<Tournament>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch data", t);
                tournamentInfo.setText("Error fetching tournament data.");
            }
        });
    }

    private Calendar toCalendarTime(List<Integer> time) {
        if (time != null && time.size() >= 2) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, time.get(0));
            calendar.set(Calendar.MINUTE, time.get(1));
            calendar.set(Calendar.SECOND, 0); // Ensure seconds are zero
            return calendar;
        }
        return null;
    }

    // Convert List<Integer> (date) to Calendar object
    private Calendar toCalendarDate(List<Integer> date) {
        if (date != null && date.size() == 3) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, date.get(0));
            calendar.set(Calendar.MONTH, date.get(1) - 1); // Calendar.MONTH is 0-based
            calendar.set(Calendar.DAY_OF_MONTH, date.get(2));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar;
        }
        return null;
    }

    // ✅ Filter tournaments that are currently ongoing
    private List<Tournament> filterTournamentsByTime(List<Tournament> tournaments) {
        List<Tournament> ongoingTournaments = new ArrayList<>();
        Calendar now = Calendar.getInstance();

        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = now.get(Calendar.MONTH) + 1;
        int currentDay = now.get(Calendar.DAY_OF_MONTH);
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        for (Tournament tournament : tournaments) {
            Calendar tournamentDate = toCalendarDate(tournament.getTournamentDate());
            Calendar startTime = toCalendarTime(tournament.getStartTime());
            Calendar endTime = toCalendarTime(tournament.getEndTime());

            if (tournamentDate != null && startTime != null && endTime != null) {
                boolean sameDay = (tournamentDate.get(Calendar.YEAR) == currentYear &&
                        tournamentDate.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                        tournamentDate.get(Calendar.DAY_OF_MONTH) == currentDay);

                if (sameDay && isBetween(startTime, endTime, currentHour, currentMinute)) {
                    ongoingTournaments.add(tournament);
                }
            }
        }
        return ongoingTournaments;
    }

    // ✅ Check if current time is between start and end
    private boolean isBetween(Calendar start, Calendar end, int currentHour, int currentMinute) {
        int startHour = start.get(Calendar.HOUR_OF_DAY);
        int startMinute = start.get(Calendar.MINUTE);
        int endHour = end.get(Calendar.HOUR_OF_DAY);
        int endMinute = end.get(Calendar.MINUTE);

        return (currentHour > startHour || (currentHour == startHour && currentMinute >= startMinute)) &&
                (currentHour < endHour || (currentHour == endHour && currentMinute <= endMinute));
    }

    // ✅ Display the ongoing tournaments
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

    // ✅ Format date from List<Integer> to yyyy-MM-dd
    private String formatDate(List<Integer> date) {
        if (date != null && date.size() == 3) {
            return String.format("%04d-%02d-%02d", date.get(0), date.get(1), date.get(2));
        }
        return "Unknown Date";
    }

    // ✅ Format time from List<Integer> to HH:mm
    private String formatTime(List<Integer> time) {
        if (time != null && time.size() >= 2) {
            return String.format("%02d:%02d", time.get(0), time.get(1));
        }
        return "Unknown Time";
    }
}