package com.example.sportmot.ui.tournament.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.sportmot.R;
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ViewGameScheduleFragment extends Fragment {

    private TextView score1, score2, score3;

    @Override
    public  View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_schedule, container, false);
        Button closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        score1 = view.findViewById(R.id.score1);
        score2 = view.findViewById(R.id.score2);
        score3 = view.findViewById(R.id.score3);

        Button editScore1 = view.findViewById(R.id.editScore1);
        Button editScore2 = view.findViewById(R.id.editScore2);
        Button editScore3 = view.findViewById(R.id.editScore3);

        editScore1.setOnClickListener(v -> showEditDialog(score1));
        editScore2.setOnClickListener(v -> showEditDialog(score2));
        editScore3.setOnClickListener(v -> showEditDialog(score3));

        return view;
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
