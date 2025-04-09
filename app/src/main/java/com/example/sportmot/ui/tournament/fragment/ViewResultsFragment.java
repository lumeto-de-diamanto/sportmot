package com.example.sportmot.ui.tournament.fragment;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private TextView loadingText;
    private LinearLayout resultsList;
    private Map<Integer, String> playerNames = new HashMap<>();
    private List<Result> pendingResults = new ArrayList<>();


    public static ViewResultsFragment newInstance(int tournamentId) {
        ViewResultsFragment fragment = new ViewResultsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TOURNAMENT_ID, tournamentId);
        fragment.setArguments(args);
        return fragment;
    }

    //public ViewResultsFragment() {
        // Required empty public constructor
    //}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_results, container, false);

        Button closeButton = view.findViewById(R.id.closeButton);
        loadingText = view.findViewById(R.id.loading_text); // Make sure this TextView exists in your layout
        resultsList = view.findViewById(R.id.results_list);

        closeButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        if (getArguments() != null) {
            tournamentId = getArguments().getInt(ARG_TOURNAMENT_ID);
            fetchParticipants(tournamentId);
            //fetchResultsFromApi();

        }

        return view;
    }

    private void fetchResultsFromApi() {
        // Use ScheduleRetrofitClient instead of ResultsRetrofitClient
        ResultsApiService resultsApiService = ScheduleRetrofitClient
                .getClient()
                .create(ResultsApiService.class);
        String apiKey = "tuIVdCZqQmHUhhc4QdjgOpwYLI2T2AAX7eq7lycr";
        String tournamentIdStr = String.valueOf(tournamentId);

        //Call<List<Result>> call = resultsApiService.getResults(tournamentIdStr, apiKey);
        Call<List<MatchWrapper>> call = resultsApiService.getResults(tournamentIdStr, apiKey);

        call.enqueue(new Callback<List<MatchWrapper>>() {
            @Override
            public void onResponse(Call<List<MatchWrapper>> call, Response<List<MatchWrapper>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //List<Result> extractedResults = new ArrayList<>();
                    List<Result> actualResults = new ArrayList<>();
                    for (MatchWrapper wrapper : response.body()) {
                        //extractedResults.add(wrapper.getMatch());
                        actualResults.add(wrapper.getMatch());
                    }
                    pendingResults = actualResults;
                    tryDisplayResults();
                    //List<MatchWrapper> matchWrappers = response.body();
                    //List<Result> results = new ArrayList<>();
                    //pendingResults = response.body(); // Save for later
                    //tryDisplayResults(); // Try to display if participants are ready
                    //for (MatchWrapper wrapper : matchWrappers) {
                      //  results.add(wrapper.getMatch());
                    //}
                    //displayResultsWithNames(results);
                } else {
                    loadingText.setText("No results found.");
                }
            }

            @Override
            public void onFailure(Call<List<MatchWrapper>> call, Throwable t) {
                loadingText.setText("Failed to load results.");
                //Log.e("ViewResults", "Error fetching results", t);
            }
        });

        //Call<List<Result>> call = resultsApiService.getResults(tournamentIdStr, apiKey);

        //call.enqueue(new Callback<List<Result>>() {
           // @Override
            //public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
              //  if (response.isSuccessful() && response.body() != null) {
                    //displayResults(response.body());
                //    List<Result> results = response.body();
                  //  displayResultsWithNames(results);
                //} else {
                  //  resultsText.setText("No results found.");
                //}
        // }

          //  @Override
            //public void onFailure(Call<List<Result>> call, Throwable t) {
              //  resultsText.setText("Failed to load results.");
            //}
        //});
    }

    //private void fetchResultsFromApi() {
        //ResultsApiService apiService = ResultsRetrofitClient.getApiService();

        //apiService.getResults(tournamentId).enqueue(new Callback<List<Result>>() {
            //@Override
            //public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                //if (response.isSuccessful() && response.body() != null) {
                  //  displayResults(response.body());
                //} else {
                //    resultsText.setText("No results found.");
              //  }
            //}

           // @Override
           // public void onFailure(Call<List<Result>> call, Throwable t) {
          //      resultsText.setText("Failed to load results.");
        //    }
      //  });
    //}

    //private void displayResults(List<Result> results) {
      //  StringBuilder builder = new StringBuilder();
       // for (Result result : results) {
         //   builder.append("Match: ")
           //         .append(result.getPlayer1Name()).append(" vs ").append(result.getPlayer2Name()).append("\n")
             //       .append("Score: ").append(result.getScore1()).append(" - ").append(result.getScore2())
               //     .append("\n\n");
        //}
        //resultsText.setText(builder.toString());
   // }
    //private void displayResults(List<Result> results) {
      //  StringBuilder builder = new StringBuilder();
       // for (Result result : results) {
         //   builder.append("Match: ")
           //         .append("Player " + result.getPlayer1Id()).append(" vs Player " + result.getPlayer2Id()).append("\n")
             //       .append("Score: ").append(result.getScore1()).append(" - ").append(result.getScore2())
               //     .append("\n\n");
        //}
        //resultsText.setText(builder.toString());
    //}

    // Fetch participants (team names) based on the tournament ID
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
                    //List<ChallongeTeamWrapper> participants = response.body();
                    for (ChallongeTeamWrapper wrapper : response.body()) {
                        //for (ChallongeTeamWrapper wrapper : participants) {
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
        resultsList.removeAllViews();
        for (Result result : results) {
            System.out.println(result);
            View resultView = LayoutInflater.from(getActivity()).inflate(R.layout.result_item, resultsList, false);
            String teamAName = playerNames.get(result.getPlayer1Id());
            String teamBName = playerNames.get(result.getPlayer2Id());

            TextView matchText = resultView.findViewById(R.id.matchText);
            TextView scoreText = resultView.findViewById(R.id.scoreText);

            Button editResultsButton = resultView.findViewById(R.id.button);
            // Hide button if not admin

            SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
            String role = prefs.getString("user_role", "");

            if (!role.equals("admin")) {
                editResultsButton.setVisibility(GONE);
            }

            StringBuilder matchStringBuilder = new StringBuilder();
            matchStringBuilder.append("Match: ");
            matchStringBuilder.append(teamAName != null ? teamAName : "Player " + result.getPlayer1Id());
            matchStringBuilder.append(" vs ");
            matchStringBuilder.append(teamBName != null ? teamBName : "Player " + result.getPlayer2Id());
            matchText.setText(matchStringBuilder.toString());

            StringBuilder scoreStringBuilder = new StringBuilder();
            scoreStringBuilder.append("Score: ");
            scoreStringBuilder.append(result.getScore1()).append(" - ").append(result.getScore2());
            scoreText.setText(scoreStringBuilder.toString());

            int matchId = result.getMatchId();
            int tournamentId = result.getTournamentId();

            editResultsButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Enter Score");

                // Input field for score
                EditText input = new EditText(requireContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // OK Button - Updates score
                builder.setPositiveButton("OK", (dialog, which) -> {
                    String newScore = input.getText().toString();
                    updateScore(tournamentId, matchId, newScore, scoreText);
                });

                // Cancel Button
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
            });

            resultsList.addView(resultView);
        }
    }

    public void updateScore(int tournamentId, int matchId, String score, TextView scoreText) {
        String[] scores = score.split("-");
        if (scores.length == 2) {
            try {
                int Score1 = Integer.parseInt(scores[0]);
                int Score2 = Integer.parseInt(scores[1]);
                System.out.println(score);
                //scoreTextView.setText(newScore);
                ResultsApiService resultsApiService = ScheduleRetrofitClient
                        .getClient()
                        .create(ResultsApiService.class);
                String apiKey = "tuIVdCZqQmHUhhc4QdjgOpwYLI2T2AAX7eq7lycr";
                String tournamentIdStr = String.valueOf(tournamentId);
                String matchIdStr = String.valueOf(matchId);
                Call<MatchWrapper> call = resultsApiService.updateScore(tournamentIdStr, matchIdStr, score, apiKey);

                call.enqueue(new Callback<MatchWrapper>() {
                    @Override
                    public void onResponse(Call<MatchWrapper> call, Response<MatchWrapper> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            //List<ChallongeTeamWrapper> participants = response.body();
                            System.out.println(response.body());
                            scoreText.setText("Score: " + Score1 + " - " + Score2);
                        } else {
                            Log.e("UpdateScore", "Failed to change score");
                        }
                    }

                    @Override
                    public void onFailure(Call<MatchWrapper> call, Throwable t) {
                        Log.e("UpdateScore", "Failed to change score", t);
                    }
                });
            } catch (NumberFormatException ignored) {}
        }
    }

}
