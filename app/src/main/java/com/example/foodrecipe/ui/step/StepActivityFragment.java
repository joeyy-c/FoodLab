package com.example.foodrecipe.ui.step;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodrecipe.R;
import com.example.foodrecipe.ui.review.AddReviewFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class StepActivityFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView tvStep;
    Button btnNext, btnPrevious;
    ProgressBar progressBar;
    ArrayList<HashMap<String, String>> steps;
    int position = 0;
    int step = 1;

    public static AddReviewFragment newInstance() {
        return new AddReviewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_step_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvStep = getView().findViewById(R.id.tv_step);
        btnNext = getView().findViewById(R.id.next_btn);
        btnPrevious = getView().findViewById(R.id.previous_btn);
        progressBar = getView().findViewById(R.id.progressBar);
        steps = new ArrayList<>();

        String recipeId = getArguments().getString("recipe_id");

        // get all recipe steps
        db.collection("recipe").document(recipeId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                steps = (ArrayList<HashMap<String, String>>) documentSnapshot.get("step");
                                btnNext.setTag("0");
                                showStep(true);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                });

        // update steps when next btn on clicked
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipeId = getArguments().getString("recipe_id");

                showStep(true);
            }
        });//next button

        // update steps when previous btn on clicked
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipeId = getArguments().getString("recipe_id");
                db.collection("recipe").document(recipeId).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()) {
                                    showStep(false);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });//previous button
    }

    private void showStep(boolean nextStep) {
        String stepStr = "";
        //step4,pos3

        int position = Integer.valueOf(btnNext.getTag().toString());

        if (nextStep) {
            stepStr = "Step " + (position + 1) + "\nDescription: " + steps.get(position).get("step_description")
                    + "\nTime: " + steps.get(position).get("step_time") + " mins \n\n";
            position++;
        } else {
            position--;
            stepStr = "Step " + (position) + "\nDescription: " + steps.get(position).get("step_description")
                    + "\nTime: " + steps.get(position).get("step_time") + " mins \n\n";
        }

        tvStep.setText(stepStr);
        btnNext.setTag(position);

        if (position == steps.size()) {
            btnNext.setVisibility(View.GONE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
        }

        if (position == 0) {
            btnPrevious.setVisibility(View.GONE);
        } else {
            btnPrevious.setVisibility(View.VISIBLE);
        }
    }
}