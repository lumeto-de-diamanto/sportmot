package com.example.sportmot.ui.tournament.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.sportmot.R;

public class StatisticsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_statistics, container, false);

        Button closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        TextView registeredTeams = view.findViewById(R.id.registered_teams);
        TextView scheduledMatches = view.findViewById(R.id.scheduled_matches);
        TextView availableFields = view.findViewById(R.id.available_fields);

        registeredTeams.setText("Registered Teams: 10");
        scheduledMatches.setText("Scheduled Matches: 5");
        availableFields.setText("Available Fields: 3");

        return view;
    }
}

