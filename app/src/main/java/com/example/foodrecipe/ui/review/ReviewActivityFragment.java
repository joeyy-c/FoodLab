package com.example.foodrecipe.ui.review;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.foodrecipe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class ReviewActivityFragment extends Fragment {
    FirebaseFirestore db;
    TextView mTvReview;
    ProgressBar progressBar;
    String recipe_id;
    public static AddReviewFragment newInstance() {
        return new AddReviewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_review_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mTvReview = getView().findViewById(R.id.tv_review);
        progressBar = getView().findViewById(R.id.progressBar2);
        recipe_id = getArguments().getString("recipe_id");
        readData();
    }

    private void readData() {
        db.collection("recipe").document(recipe_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null && documentSnapshot.exists()){
                                ArrayList<HashMap<String, String>> review = (ArrayList<HashMap<String, String>>) documentSnapshot.get("review");
                                String reviews="";
                                for (int i = 0; i < review.size(); i++) {
                                    reviews += "User "+(i + 1) +"\nRating: "+ review.get(i).get("rating")
                                            + "\nComment: " + review.get(i).get("comment") + "\n\n";
                                }
                                mTvReview.setText(reviews);
                            }
                        }
                    }
                });
    }
}