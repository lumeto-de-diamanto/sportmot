package com.example.sportmot.ui.tournament.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.sportmot.R;
import com.example.sportmot.ui.tournament.CurrentTournamentActivity;

import android.app.AlertDialog;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ViewGameScheduleFragment extends Fragment {

    private TextView score1, score2, score3;
    private Button editScore1, editScore2, editScore3;
    @Override
    public  View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_schedule, container, false);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        String role = prefs.getString("user_role", "");

        Button closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        score1 = view.findViewById(R.id.score1);
        score2 = view.findViewById(R.id.score2);
        score3 = view.findViewById(R.id.score3);

        editScore1 = view.findViewById(R.id.editScore1);
        editScore2 = view.findViewById(R.id.editScore2);
        editScore3 = view.findViewById(R.id.editScore3);

        if (!role.equals("admin")){
            editScore1.setVisibility(View.GONE);
            editScore2.setVisibility(View.GONE);
            editScore3.setVisibility(View.GONE);
        }

        editScore1.setOnClickListener(v -> showEditDialog(score1));
        editScore2.setOnClickListener(v -> showEditDialog(score2));
        editScore3.setOnClickListener(v -> showEditDialog(score3));

        TextView game1 = view.findViewById(R.id.game1);
        TextView game2 = view.findViewById(R.id.game2);
        TextView game3 = view.findViewById(R.id.game3);

        String time1 = extractTime(game1.getText().toString()); // Extract "12:00"
        String time2 = extractTime(game2.getText().toString()); // Extract "14:00"
        String time3 = extractTime(game3.getText().toString()); // Extract "16:00"

        // Tékka hvort þetta þurfi að vera? var að loka dagskrá móts fragment, held að þetta tengist notifications Bryndís??

        // Pass times to TournamentActivity
        Intent intent = new Intent(getActivity(), CurrentTournamentActivity.class);
        intent.putExtra("GAME_TIME_1", time1);
        intent.putExtra("GAME_TIME_2", time2);
        intent.putExtra("GAME_TIME_3", time3);
        startActivity(intent);

        return view;
    }
    private String extractTime(String gameText) {
        return gameText.split(" - ")[0]; // Get only the HH:mm time
    }
    private void showEditDialog(TextView scoreTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter Score");

        // Input field for score
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // OK Button - Updates score
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newScore = input.getText().toString();
            if (!newScore.isEmpty()) {
                scoreTextView.setText(newScore);
            }
        });

        // Cancel Button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
