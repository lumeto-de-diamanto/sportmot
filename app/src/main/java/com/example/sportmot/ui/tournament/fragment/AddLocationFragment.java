package com.example.sportmot.ui.tournament.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sportmot.DatabaseHelper;
import com.example.sportmot.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddLocationFragment extends Fragment {
    private EditText editAddress;
    private Button buttonSaveLocation;

    private int tournamentID;

    // ✅ Use a no-argument constructor
    public AddLocationFragment() {
        // Required empty public constructor
    }




    public static AddLocationFragment newInstance(int tournamentID) {
        AddLocationFragment fragment = new AddLocationFragment();
        Bundle args = new Bundle();
        args.putInt("TOURNAMENT_ID", tournamentID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            tournamentID = getArguments().getInt("TOURNAMENT_ID", -1);
            Log.d("AddLocationFragment", "Received Tournament ID: " + tournamentID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_input, container, false);

        editAddress = view.findViewById(R.id.etAddress);


        buttonSaveLocation = view.findViewById(R.id.btnSaveLocation);

        buttonSaveLocation.setOnClickListener(v -> {
            String address = editAddress.getText().toString().trim();
            if (!address.isEmpty()) {
                new GeocodeTask().execute(address);
            } else {
                Toast.makeText(getContext(), "Vinsamlegast settu inn heimilisfang", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private class GeocodeTask extends AsyncTask<String, Void, double[]> {
        @Override
        protected double[] doInBackground(String... addresses) {
            String address = addresses[0];
            String urlString = "https://nominatim.openstreetmap.org/search?q=" + address + "&format=json&limit=1";
            double[] coordinates = null;

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    StringBuilder response = new StringBuilder();
                    int data;
                    while ((data = reader.read()) != -1) {
                        response.append((char) data);
                    }

                    JSONArray jsonArray = new JSONArray(response.toString());
                    if (jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        double latitude = jsonObject.getDouble("lat");
                        double longitude = jsonObject.getDouble("lon");
                        coordinates = new double[]{latitude, longitude};
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return coordinates;
        }

        @Override
        protected void onPostExecute(double[] coordinates) {
            if (coordinates != null) {
                // Update the location in the database instead of inserting
                DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                dbHelper.saveOrUpdateLocation(coordinates[0], coordinates[1], tournamentID);
                Toast.makeText(getContext(), "Heimilisfang vistað", Toast.LENGTH_SHORT).show();

                // Close the fragment after saving the address
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            } else {
                Toast.makeText(getContext(), "Ekki tókst að vista heimilisfang", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
