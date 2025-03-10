package com.example.sportmot.ui.tournament.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportmot.api.ClubApiService;
import com.example.sportmot.api.RetrofitClient;
import com.example.sportmot.R;
import com.example.sportmot.api.TeamApiService;
import com.example.sportmot.data.entities.Club;
import com.example.sportmot.data.entities.Team;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RegisterTeamFormFragment extends Fragment {
    private TeamApiService teamApiService;
    private ClubApiService clubApiService;
    private Button registerTeamButton;
    private TextInputEditText teamNameInput, teamLevelInput;
    private Spinner teamClubInput;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register_form, container, false);
        teamNameInput = view.findViewById(R.id.teamNameInput);

        teamClubInput = view.findViewById(R.id.teamClubInput);
        teamLevelInput = view.findViewById(R.id.teamLevelInput);
        registerTeamButton = view.findViewById(R.id.registerTeamButton);

        registerTeamButton.setOnClickListener(v -> registerTeam());

        loadClubs();

        return view;
    }

    private void loadClubs() {
        clubApiService = RetrofitClient.getClubApiService();
        Call<List<Club>> call = clubApiService.getClubs();

        call.enqueue(new Callback<List<Club>>() {
            @Override
            public void onResponse(Call<List<Club>> call, Response<List<Club>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Club> clubs = response.body();


                    ArrayAdapter<Club> adapter = new ArrayAdapter<Club>(getContext(),
                            android.R.layout.simple_spinner_item, clubs) {
                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View view = super.getDropDownView(position, convertView, parent);
                            TextView textView = (TextView) view.findViewById(android.R.id.text1);
                            textView.setText(clubs.get(position).getName());
                            return view;
                        }

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView textView = (TextView) view.findViewById(android.R.id.text1);
                            textView.setText(clubs.get(position).getName());
                            return view;
                        }
                    };

                    // Set the adapter to the Spinner
                    teamClubInput.setAdapter(adapter);

                } else {
                    Toast.makeText(getContext(), "Failed to load clubs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Club>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void registerTeam() {
        String teamName = teamNameInput.getText().toString().trim();
        Club teamClub = (Club) teamClubInput.getSelectedItem();
        String teamLevel = teamLevelInput.getText().toString().trim();

        // Log the values to check for missing fields
        Log.d("RegisterTeam", "Team Name: " + teamName);
        Log.d("RegisterTeam", "Team Level: " + teamLevel);
        Log.d("RegisterTeam", "Selected Club: " + (teamClub != null ? teamClub.getName() : "None"));


        // Check if any fields are empty and log the missing ones
        if (teamName.isEmpty()) {
            Log.e("RegisterTeam", "Team Name is missing!");
        }
        if (teamLevel.isEmpty()) {
            Log.e("RegisterTeam", "Team Level is missing!");
        }
        if (teamClub == null) {
            Log.e("RegisterTeam", "Club is missing or not selected!");
        }

        if (teamName.isEmpty() || teamLevel.isEmpty()) {
            Toast.makeText(getContext(), "Vinsamlegast fyllið alla reiti", Toast.LENGTH_SHORT).show();
            return;
        }

        if (teamClub == null) {
            Log.e("Spinner Error", "No club selected!");
            return;
        }




        int teamId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

        Team team = new Team(teamId,teamName, teamClub,teamLevel);
        Log.d("Team Registration", "Sending Team: " + team.toString());

        TeamApiService apiService = RetrofitClient.getApiService();
        Call<String> call = apiService.createTeam(team);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d("Team Registration", "Team created successfully.");
                } else {
                    try {
                        Log.e("Team Registration", "Error response: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("Team Registration", "Error reading response body", e);
                    }
                    Log.e("Team Registration", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Team Registration", "Error: " + t.getMessage());
            }
        });
    }


}

