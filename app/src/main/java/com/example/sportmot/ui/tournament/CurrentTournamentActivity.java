package com.example.sportmot.ui.tournament;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.example.sportmot.R;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.util.ArrayList;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.sportmot.api.RetrofitClient;
import com.example.sportmot.api.TournamentApiService;
import com.example.sportmot.data.entities.Tournament;

import com.example.sportmot.ui.tournament.fragment.AddLocationFragment;
import com.example.sportmot.ui.tournament.fragment.ViewGameScheduleFragment;


import com.example.sportmot.ui.homepage.NotificationReceiver;
import com.example.sportmot.ui.tournament.fragment.StatisticsFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

import java.util.Locale;


import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.content.SharedPreferences;

public class CurrentTournamentActivity extends AppCompatActivity {
    private LinearLayout tournamentContainer;
    private TextView tournamentInfo;
    private TournamentApiService apiService;
    private String role;

    //test
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tournament_list);
        tournamentContainer = findViewById(R.id.tournament_list);
        tournamentInfo = findViewById(R.id.loading_text);
        apiService = RetrofitClient.getClient().create(TournamentApiService.class);

        fetchCurrentTournaments();  // Fetch all tournaments now

        Intent intent = getIntent();
        if (intent != null) {
            String time1 = intent.getStringExtra("GAME_TIME_1");
            String time2 = intent.getStringExtra("GAME_TIME_2");
            String time3 = intent.getStringExtra("GAME_TIME_3");

            Log.d("DEBUG_NOTIFICATION", "Received times: " + time1 + ", " + time2 + ", " + time3);

            // Schedule notifications only if time is not null
            if (time1 != null && !time1.isEmpty()) {
                scheduleGameNotification(time1);
            }
            if (time2 != null && !time2.isEmpty()) {
                scheduleGameNotification(time2);
            }
            if (time3 != null && !time3.isEmpty()) {
                scheduleGameNotification(time3);
            }
        }

        //scheduleGameNotification(String);

        Button til_baka = findViewById(R.id.til_baka);
        TextView tournament_title = findViewById(R.id.tournament_title);
        tournament_title.setText("Mót í dag");
        til_baka.setOnClickListener((v) ->
                onBackPressed());

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        role = prefs.getString("user_role", "");
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

    // Filter tournaments that are currently ongoing
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
            //Calendar startTime = toCalendarTime(tournament.getStartTimeList()); // Use getStartTimeList() instead of getStartTime()
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

    // Check if current time is between start and end
    private boolean isBetween(Calendar start, Calendar end, int currentHour, int currentMinute) {
        int startHour = start.get(Calendar.HOUR_OF_DAY);
        int startMinute = start.get(Calendar.MINUTE);
        int endHour = end.get(Calendar.HOUR_OF_DAY);
        int endMinute = end.get(Calendar.MINUTE);

        return (currentHour > startHour || (currentHour == startHour && currentMinute >= startMinute)) &&
                (currentHour < endHour || (currentHour == endHour && currentMinute <= endMinute));
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
            Button viewMap = tournamentView.findViewById(R.id.view_map);
            Button addLocation = tournamentView.findViewById(R.id.add_location);
            Button registerTeamButton = tournamentView.findViewById(R.id.skra_lid); // Find the button
            Button viewStatisticsButton = tournamentView.findViewById(R.id.view_statistics_button);
            Button viewResultsButton = tournamentView.findViewById(R.id.view_results);

            name.setText(tournament.getTournamentName());
            details.setText("Date: " + formatDate(tournament.getTournamentDate()));

            viewSchedule.setOnClickListener(v -> showScheduleFragment(tournament.getId()));

            registerTeamButton.setVisibility(View.GONE); // Hides the button

            viewResultsButton.setVisibility(View.GONE);

            if (!role.equals("admin")) {
                viewStatisticsButton.setVisibility(View.GONE);
                Log.d("UserRoleCheck", "Hiding View Statistics button.");
            }

            viewStatisticsButton.setOnClickListener(v -> showStatisticsFragment());

            viewMap.setOnClickListener(v -> showMapActivity(tournament.getId()));

            if (!role.equals("admin")) {
                addLocation.setVisibility(View.GONE);
                Log.d("UserRoleCheck", "Hiding View Statistics button.");
            }
            addLocation.setOnClickListener(v -> openAddLocationFragment(tournament.getId()));

            tournamentContainer.addView(tournamentView);
        }
    }


    private void openAddLocationFragment(int tournamentID) {

        Log.d("TournamentLog", "Opening Add Location Fragment for Tournament ID: " + tournamentID);  // Log when location is added

        AddLocationFragment fragment = AddLocationFragment.newInstance(tournamentID);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }


    private void showScheduleFragment(int tournamentId) {


        View container = findViewById(R.id.ViewGameScheduleFragment);
        if (container != null) {
            container.setVisibility(View.VISIBLE);
        } else {
            Log.e("FragmentError", "Fragment container not found!");
        }

        //Load the fragment into the container
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ViewGameScheduleFragment, new ViewGameScheduleFragment())
                .addToBackStack(null)  // Allow going back
                .commit();
    }

    private void showMapActivity(int tournamentId) {
        // Create an Intent to start ViewMapActivity
        Intent intent = new Intent(this, ViewMapActivity.class);

        // Pass the tournament ID to ViewMapActivity
        intent.putExtra("TOURNAMENT_ID", tournamentId);

        // Start the activity
        startActivity(intent);
    }


    //  Format date from List<Integer> to yyyy-MM-dd
    private String formatDate(List<Integer> date) {
        if (date != null && date.size() == 3) {
            return String.format("%04d-%02d-%02d", date.get(0), date.get(1), date.get(2));
        }
        return "Unknown Date";
    }

    //  Format time from List<Integer> to HH:mm
    private String formatTime(List<Integer> time) {
        if (time != null && time.size() >= 2) {
            return String.format("%02d:%02d", time.get(0), time.get(1));
        }
        return "Unknown Time";
    }

    public void scheduleGameNotification(String time) {
        if (time == null || time.isEmpty()) {
            Log.e("DEBUG_NOTIFICATION", "scheduleGameNotification received a null or empty time");
            return; // Exit method early
        }

        Log.d("DEBUG_NOTIFICATION", "Received time: " + time);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(time); // Parse the time from string

            if (date != null) {
                Calendar gameTime = Calendar.getInstance();
                gameTime.setTime(date); // Set the parsed time

                // Set today's date
                Calendar now = Calendar.getInstance();
                gameTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
                gameTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
                gameTime.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));

                // Subtract 15 minutes
                gameTime.add(Calendar.MINUTE, -15);

                // Ensure the notification is scheduled for the future
                if (gameTime.after(now)) {
                    scheduleNotification(gameTime);
                    Log.d("DEBUG_NOTIFICATION", "Notification scheduled for: " + gameTime.getTime());
                } else {
                    Log.d("DEBUG_NOTIFICATION", "Game time is in the past. No notification scheduled.");
                }
            } else {
                Log.e("DEBUG_NOTIFICATION", "Parsed date is null");
            }
        } catch (ParseException e) {
            Log.e("DEBUG_NOTIFICATION", "ParseException: " + e.getMessage());
        }
    }

    private static int requestCodeCounter = 100; // Start from 100 to avoid conflicts
    private void scheduleNotification(Calendar calendar) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        int requestCode = requestCodeCounter++; // Unique code
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}