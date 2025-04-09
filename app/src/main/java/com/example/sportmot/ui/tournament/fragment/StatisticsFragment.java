package com.example.sportmot.ui.tournament.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.sportmot.R;
import com.example.sportmot.api.RetrofitClient;
import com.example.sportmot.api.TournamentApiService;
import com.example.sportmot.data.entities.TournamentStats;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class StatisticsFragment extends Fragment {

    private TextView registeredTeams;
    private TextView scheduledMatches;
    private TextView availableFields;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_statistics, container, false);

        // Initialize TextViews
        registeredTeams = view.findViewById(R.id.registered_teams);
        scheduledMatches = view.findViewById(R.id.scheduled_matches);
        availableFields = view.findViewById(R.id.available_fields);

        // Close button functionality
        Button closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Fetch data from API
        fetchTournamentStats();

        return view;
    }
    private void fetchTournamentStats() {
        TournamentApiService apiService = RetrofitClient.getClient().create(TournamentApiService.class);
        Call<TournamentStats> call = apiService.getTournamentStats();
        call.enqueue(new Callback<TournamentStats>() {
            @Override
            public void onResponse(Call<TournamentStats> call, Response<TournamentStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TournamentStats stats = response.body();
                    registeredTeams.setText("Registered Teams: " + stats.getRegisteredTeams());
                    scheduledMatches.setText("Scheduled Matches: " + stats.getScheduledMatches());
                    availableFields.setText("Available Fields: " + stats.getAvailableFields());
                } else {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<TournamentStats> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
