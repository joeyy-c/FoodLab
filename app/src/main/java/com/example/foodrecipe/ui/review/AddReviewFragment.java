package com.example.foodrecipe.ui.review;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.foodrecipe.R;
import com.example.foodrecipe.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddReviewFragment extends Fragment {
    private FirebaseFirestore db;
    private Button submitBtn,showBtn;
    private EditText mTitle, mComment;
    private String title, comment, rating;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String recipe_id;

    public static AddReviewFragment newInstance() {
        return new AddReviewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_review_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        mTitle = getView().findViewById(R.id.title);
        mComment = getView().findViewById(R.id.comment);
        radioGroup = getView().findViewById(R.id.radio_group);
        submitBtn = getView().findViewById(R.id.submit);
        showBtn = getView().findViewById(R.id.showReview);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = mTitle.getText().toString();
                comment = mComment.getText().toString();
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) getView().findViewById(selectedId);
                rating = radioButton.getText().toString();

                if(TextUtils.isEmpty(title)){
                    Toast.makeText(getActivity(),"Title is Empty",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(comment)){
                    Toast.makeText(getActivity(),"Comment is Empty",Toast.LENGTH_SHORT).show();
                }else {
                    addReview();
                }
            }
        });

        recipe_id = getArguments().getString("recipe_id");

        getView().findViewById(R.id.showReview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("recipe_id", recipe_id);
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_addReview_to_review, bundle);
            }
        });
    }
    //Methods
    public void addReview(){
        Map<String, Object> review = new HashMap<>();
        review.put("title",title);
        review.put("comment",comment);
        review.put("rating",rating);

        Map<String, Object>[] reviewArr = new HashMap[1];
        reviewArr[0] = review;

        String recipeId = getArguments().getString("recipe_id");

        db.collection("recipe").document(recipeId)
                .update("review", FieldValue.arrayUnion(reviewArr))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(),"Submitted",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}