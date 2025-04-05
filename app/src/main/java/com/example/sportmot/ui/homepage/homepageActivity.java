package com.example.sportmot.ui.homepage;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sportmot.R;
import com.example.sportmot.databinding.ActivityMainBinding;
import com.example.sportmot.ui.startpage.startpageActivity;
import com.example.sportmot.ui.tournament.CurrentTournamentActivity;
import com.example.sportmot.ui.tournament.OldTournamentsActivity;
import com.example.sportmot.ui.tournament.UpcomingTournamentActivity;
import com.example.sportmot.ui.userpage.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
import com.example.sportmot.ui.subscription.SubscriptionActivity;

public class homepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String savedName = sharedPreferences.getString("user_name", "");
        String savedPassword = sharedPreferences.getString("user_password", "");

        if (savedName.isEmpty() || savedPassword.isEmpty()) {
            Intent intent = new Intent(homepageActivity.this, startpageActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_homepage);

        TextView textView = findViewById(R.id.textView);
        textView.setText("Velkomin/nn/ð,\n" + savedName);

        Button button = findViewById(R.id.mot_i_dag);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(homepageActivity.this, CurrentTournamentActivity.class);
            startActivity(intent);
        });



        //  Button for Upcoming Tournaments
        Button upcomingButton = findViewById(R.id.naestu_mot);
        upcomingButton.setOnClickListener(v -> {
            Intent intent = new Intent(homepageActivity.this, UpcomingTournamentActivity.class);
            startActivity(intent);
        });

        Button oldButton = findViewById(R.id.gomul_mot);
        oldButton.setOnClickListener(v -> {
            Intent intent = new Intent(homepageActivity.this, OldTournamentsActivity.class);
            startActivity(intent);
        });

        Button logoutButton = findViewById(R.id.log_out);
        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("user_name");
            editor.remove("user_password");
            editor.apply();
            Intent intent = new Intent(homepageActivity.this, startpageActivity.class);
            startActivity(intent);
            finish();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id",
                    "Game Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }
    public void openSubscription(View view) {
        Intent intent = new Intent(this, SubscriptionActivity.class);
        startActivity(intent);
    }



}

