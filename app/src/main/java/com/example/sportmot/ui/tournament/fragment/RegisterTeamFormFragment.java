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
import android.widget.Toast;

import com.example.sportmot.api.ClubApiService;
import com.example.sportmot.api.RetrofitClient;
import com.example.sportmot.R;
import com.example.sportmot.api.TeamApiService;
import com.example.sportmot.data.entities.Club;
import com.example.sportmot.data.entities.Team;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
        // Fetch the list of clubs from the API
        TeamApiService apiService = RetrofitClient.getApiService();
        Call<List<Club>> call = apiService.loadClubs();  // Assuming the endpoint exists

        call.enqueue(new Callback<List<Club>>() {
            @Override
            public void onResponse(Call<List<Club>> call, Response<List<Club>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Club> clubs = response.body();
                    // Populate the spinner with the list of clubs
                    ArrayAdapter<Club> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, clubs);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    private void registerTeam(){
        String teamName = teamNameInput.getText().toString().trim();
        Club teamClub = (Club) teamClubInput.getSelectedItem();
        String teamLevel = teamLevelInput.getText().toString().trim();

        Log.d("RegisterTeam", "Button clicked!");


        //check if input is empty
        if(teamName.isEmpty() || teamLevel.isEmpty() ){
            Toast.makeText(getContext(),"Vinsamlegast fyllið  alla reiti", Toast.LENGTH_SHORT).show();
            return;
        }

        String teamId = "TEAM_" + System.currentTimeMillis();

        TeamApiService apiService = RetrofitClient.getApiService();
        Team team = new Team(teamId, teamName, teamClub, teamLevel );

        Call<String> call = apiService.createTeam(team);
        call.enqueue(new Callback<String>(){
           /* @Override
            public void onResponese(Call<String> call, Response<String> response){
                if(response.isSuccessful()){
                    Toast.makeText(getContext(), "Skráning tókst", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Skráning mistókst", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t){
                Toast.makeText(getContext(),"Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }*/

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    // Success: The team was created successfully
                    Log.d("Skráning liðs", "Skráning liðs tókst");
                } else {
                    // Failure: Something went wrong on the server side
                    Log.e("Skráning liðs", "Skráning liðs mistókst. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Handle error: No internet, server is down, etc.
                Log.e("Skráning liðs", "Error: " + t.getMessage());
            }
        });



    }

}
