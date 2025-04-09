package com.example.sportmot.ui.tournament.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

import com.example.sportmot.R;
import com.example.sportmot.data.entities.ChallongeTeamWrapper;
import com.example.sportmot.api.ResultsApiService;
import com.example.sportmot.data.entities.MatchWrapper;
import com.example.sportmot.data.entities.Result;
import com.example.sportmot.api.ScheduleRetrofitClient;
import com.example.sportmot.data.entities.ChallongeTeam;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewResultsFragment extends Fragment {

    private static final String ARG_TOURNAMENT_ID = "tournament_id";
    private int tournamentId;
    private TextView resultsText;
    private Map<Integer, String> playerNames = new HashMap<>();
    private List<Result> pendingResults = new ArrayList<>();

    public static ViewResultsFragment newInstance(int tournamentId) {
        ViewResultsFragment fragment = new ViewResultsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TOURNAMENT_ID, tournamentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("ScheduleFragment", "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_view_results, container, false);

        Button closeButton = view.findViewById(R.id.closeButton);
        resultsText = view.findViewById(R.id.results_text);

        closeButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        if (getArguments() != null) {
            tournamentId = getArguments().getInt(ARG_TOURNAMENT_ID);
            fetchParticipants(tournamentId);
        }
        return view;
    }

    private void fetchResultsFromApi() {
        ResultsApiService resultsApiService = ScheduleRetrofitClient
                .getClient()
                .create(ResultsApiService.class);
        String apiKey = "tuIVdCZqQmHUhhc4QdjgOpwYLI2T2AAX7eq7lycr";
        String tournamentIdStr = String.valueOf(tournamentId);

        Call<List<MatchWrapper>> call = resultsApiService.getResults(tournamentIdStr, apiKey);

        call.enqueue(new Callback<List<MatchWrapper>>() {
            @Override
            public void onResponse(Call<List<MatchWrapper>> call, Response<List<MatchWrapper>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Result> actualResults = new ArrayList<>();
                    for (MatchWrapper wrapper : response.body()) {
                        actualResults.add(wrapper.getMatch());
                    }
                    pendingResults = actualResults;
                    tryDisplayResults();
                } else {
                    resultsText.setText("No results found.");
                }
            }

            @Override
            public void onFailure(Call<List<MatchWrapper>> call, Throwable t) {
                resultsText.setText("Failed to load results.");
            }
        });
    }

    // Fetch team names based on the tournament ID
    private void fetchParticipants(int tournamentId) {
        ResultsApiService resultsApiService = ScheduleRetrofitClient
                .getClient()
                .create(ResultsApiService.class);
        String apiKey = "tuIVdCZqQmHUhhc4QdjgOpwYLI2T2AAX7eq7lycr";
        String tournamentIdStr = String.valueOf(tournamentId);
        // Make sure this API call uses the correct endpoint for participants
        Call<List<ChallongeTeamWrapper>> call = resultsApiService.getParticipants(tournamentIdStr, apiKey);

        call.enqueue(new Callback<List<ChallongeTeamWrapper>>() {
            @Override
            public void onResponse(Call<List<ChallongeTeamWrapper>> call, Response<List<ChallongeTeamWrapper>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (ChallongeTeamWrapper wrapper : response.body()) {
                        ChallongeTeam team = wrapper.getTeam();
                        playerNames.put(team.getId(), team.getName());
                        Log.d("ViewResults", "Player ID: " + team.getId() + ", Name: " + team.getName());
                    }
                    fetchResultsFromApi();
                } else {
                    Log.e("ViewResults", "Failed to fetch participants");
                }
            }

            @Override
            public void onFailure(Call<List<ChallongeTeamWrapper>> call, Throwable t) {
                Log.e("ViewResults", "Failed to fetch teamss", t);
            }
        });
    }

    private void tryDisplayResults() {
        if (pendingResults != null && !playerNames.isEmpty()) {
            displayResultsWithNames(pendingResults);
        }
    }
    private void displayResultsWithNames(List<Result> results) {
        StringBuilder builder = new StringBuilder();
        for (Result result : results) {
            String teamAName = playerNames.get(result.getPlayer1Id());
            String teamBName = playerNames.get(result.getPlayer2Id());
            Log.d("ViewResults", "Team A: " + (teamAName != null ? teamAName : "No Name") + ", Team B: " + (teamBName != null ? teamBName : "No Name"));
            Log.d("ViewResults", "Match between: "
                    + (teamAName != null ? teamAName : "Player " + result.getPlayer1Id())
                    + " vs "
                    + (teamBName != null ? teamBName : "Player " + result.getPlayer2Id()));

            builder.append("Match: ")
                    .append(teamAName != null ? teamAName : "Player " + result.getPlayer1Id())
                    .append(" vs ")
                    .append(teamBName != null ? teamBName : "Player " + result.getPlayer2Id())
                    .append("\n")
                    .append("Score: ").append(result.getScore1()).append(" - ").append(result.getScore2())
                    .append("\n\n");
        }
        resultsText.setText(builder.toString());
    }
}
