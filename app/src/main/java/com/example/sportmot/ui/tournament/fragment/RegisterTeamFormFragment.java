package com.example.sportmot.ui.tournament.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.example.sportmot.api.ClubApiService;
import com.example.sportmot.api.ImageApiService;
import com.example.sportmot.api.ImageRetrofitClient;
import com.example.sportmot.api.RetrofitClient;
import com.example.sportmot.R;
import com.example.sportmot.api.ScheduleApiService;
import com.example.sportmot.api.ScheduleRetrofitClient;
import com.example.sportmot.api.TeamApiService;
import com.example.sportmot.api.TeamApiService.TeamWithoutClub;
import com.example.sportmot.api.TeamRegisterApiService;
import com.example.sportmot.api.TeamRetrofitClient;
import com.example.sportmot.api.TournamentApiService;
import com.example.sportmot.data.entities.ChallongeTeamRegister;
import com.example.sportmot.data.entities.ChallongeTeamRegisterWrapper;
import com.example.sportmot.data.entities.ChallongeTeamResponse;
import com.example.sportmot.data.entities.ChallongeTeamWrapper;
import com.example.sportmot.data.entities.Club;
import com.example.sportmot.data.entities.Team;
import com.example.sportmot.data.entities.Tournament;
import com.example.sportmot.data.entities.User;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RegisterTeamFormFragment extends Fragment {
    private TeamApiService teamApiService;
    private ImageApiService imageApiService;
    private ClubApiService clubApiService;
    private Button registerTeamButton;
    private Button addIconButton;
    private ImageView icon;
    private TextInputEditText teamNameInput, teamLevelInput;
    private Spinner teamClubInput;
    private Uri image;
    private File imageFile;
    private String imagePath;
    private TournamentApiService apiService;
    private int cId;
    private int tournamentId;




    public static RegisterTeamFormFragment newInstance(int tournamentId) {
        RegisterTeamFormFragment fragment = new RegisterTeamFormFragment();
        Bundle args = new Bundle();
        args.putInt("TOURNAMENT_ID", tournamentId);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tournamentId = getArguments().getInt("TOURNAMENT_ID");
            Log.d("FragmentDebug", "Received tournamentId: " + tournamentId);
        }


        View view = inflater.inflate(R.layout.fragment_register_form, container, false);

        teamNameInput = view.findViewById(R.id.teamNameInput);
        teamClubInput = view.findViewById(R.id.teamClubInput);
        teamLevelInput = view.findViewById(R.id.teamLevelInput);
        registerTeamButton = view.findViewById(R.id.registerTeamButton);
        addIconButton = view.findViewById(R.id.addTeamIcon);
        icon = view.findViewById(R.id.imageView);

        registerTeamButton.setOnClickListener(v -> registerTeam());
        addIconButton.setOnClickListener(v -> addIcon());

        image = null;


        loadClubs();
        apiService = RetrofitClient.getClient().create(TournamentApiService.class);
        fetchTournamentDetails(tournamentId);
        return view;
    }


    private void fetchTournamentDetails(int tournamentId) {
        Call<List<Tournament>> call = apiService.getAllTournaments();
        call.enqueue(new Callback<List<Tournament>>() {
            @Override
            public void onResponse(Call<List<Tournament>> call, Response<List<Tournament>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Tournament> tournaments = response.body();

                    for (Tournament tournament : tournaments) {
                        if (tournament.getId() == tournamentId) {
                            cId = tournament.getcId();
                            Log.d("cIdFetch", "Fetched cId: " + cId);
                            return;
                        }
                    }

                    Log.e("Tournament Lookup", "Tournament ID not found: " + tournamentId);
                } else {
                    Log.e("API", "Failed to get tournaments list: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Tournament>> call, Throwable t) {
                Log.e("API", "Error fetching tournaments", t);
            }
        });
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
                    Toast.makeText(getContext(), "Skráning tókst!", Toast.LENGTH_SHORT).show();

                    //uploadIcon(teamId);
                    if (image != null) {
                        getTeamIdAndUploadIcon(team.getTeamName(), team.getLevel());
                    }

                    // Either way, go back to the previous screen
                    requireActivity().getSupportFragmentManager().popBackStack();
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



        String clubName = teamClub.getName();


        registerTeamToChallonge(teamName, clubName,teamLevel, cId);
    }

    private void getTeamIdAndUploadIcon(String teamName, String teamLevel) {
        TeamApiService apiService = RetrofitClient.getApiService();
        Call<List<TeamWithoutClub>> call = apiService.getTeams();
        call.enqueue(new Callback<List<TeamWithoutClub>>() {
            @Override
            public void onResponse(Call<List<TeamWithoutClub>> call, Response<List<TeamWithoutClub>> response) {
                if (response.isSuccessful()) {
                    System.out.println(response.body());
                    List<TeamWithoutClub> teamList = response.body()
                            .stream()
                            .filter(t -> {
                                if (!Objects.equals(t.getTeamName(), teamName)) {
                                    System.out.println("Name not same: "+t.getTeamName()+" - "+teamName);
                                    return false;
                                }
                                if (!Objects.equals(t.getLevel(), teamLevel)) {
                                    System.out.println("Level not same: "+t.getLevel()+" - "+teamLevel);
                                    return false;
                                }
                                return true;
                            })
                            .collect(Collectors.toList());
                    System.out.println(teamList);
                    TeamWithoutClub team = teamList.stream().max(Comparator.comparing(TeamWithoutClub::getTeamId)).get();
                    int id = team.getTeamId();
                    uploadIcon(id);
                } else {
                    try {
                        Log.e("Team ID Get", "Error response: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("Team ID Get", "Error reading response body", e);
                    }
                    Log.e("Team ID Get", "Response code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<TeamWithoutClub>> call, Throwable t) {
                Log.e("Team ID Get", "Error: " + t.getMessage());
            }
        });
    }

    private void uploadIcon(int teamId) {
        ImageApiService imageApiService = ImageRetrofitClient.getApiService();

        System.out.println(image);
        System.out.println(image.getPath());
        System.out.println(imagePath);
        System.out.println(imageFile);
        System.out.println(MediaType.parse(getActivity().getContentResolver().getType(image)));

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getActivity().getContentResolver().getType(image)),
                        imageFile
                );

        MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

        RequestBody team = RequestBody.create(
                        okhttp3.MultipartBody.FORM, String.valueOf(teamId));

        Call<String> call = imageApiService.postImage(team, body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call,
                                   Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.v("Upload", "success");
                    requireActivity().getSupportFragmentManager().popBackStack();

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    private void addIcon() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                try {
                    image = data.getData();
                    imagePath = getPath(image);
                    imageFile = new File(imagePath);
                    InputStream is = getActivity().getContentResolver().openInputStream(image);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    icon.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        Integer column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }

    private void registerTeamToChallonge(String teamName, String club, String level, int tournamentCid) {
        // Store extra information in misc for Challonge. Challonge only needs a team name, but we want to store more data for future use.
        JSONObject miscJson = new JSONObject();
        try {
            miscJson.put("club", club);
            miscJson.put("level", level);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }



        ChallongeTeamRegister teamRegister = new ChallongeTeamRegister(teamName, miscJson.toString());
        ChallongeTeamRegisterWrapper wrapper = new ChallongeTeamRegisterWrapper(teamRegister);

        // Directly create the Retrofit instance for this call
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.challonge.com/v1/")  // Base URL for the Challonge API
                .addConverterFactory(GsonConverterFactory.create())  // Add Gson converter for handling JSON
                .build();

        // Create the API service for team registration
        TeamRegisterApiService challongeApi = retrofit.create(TeamRegisterApiService.class);

        // Make the API call to register the team
        Call<ChallongeTeamResponse> call = challongeApi.registerTeamToChallonge(
                cId,
                "tuIVdCZqQmHUhhc4QdjgOpwYLI2T2AAX7eq7lycr",
                wrapper

        );

        // Enqueue the call to register the team
        call.enqueue(new Callback<ChallongeTeamResponse>() {
            @Override
            public void onResponse(Call<ChallongeTeamResponse> call, Response<ChallongeTeamResponse> response) {
                if (response.isSuccessful()) {
                    ChallongeTeamResponse.TeamDetail data = response.body().teamDetail;
                    Log.d("Challonge", "Team registered to Challonge: " + data.name + " (ID: " + data.id + ")");
                    // Optionally update UI or show a Toast
                } else {
                    Log.e("Challonge", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChallongeTeamResponse> call, Throwable t) {
                Log.e("Challonge", "Failed to register team", t);
            }
        });
    }


}

