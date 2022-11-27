package com.example.foodrecipe.ui.mypost;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipe.R;
import com.example.foodrecipe.databinding.FragmentMyPostBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyPostFragment extends Fragment {

    private MyPostViewModel myPostViewModel;

    private FragmentMyPostBinding binding;

    RecyclerView recyclerView;
    ArrayList<RecipeData> myFoodList;
    MyPostAdapter mypostAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyPostViewModel myPostViewModel =
                new ViewModelProvider(this).get(MyPostViewModel.class);

        binding = FragmentMyPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSaved;
        myPostViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Fetching Data");
        progressDialog.show();

        recyclerView = getView().findViewById(R.id.recycleView3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        db = FirebaseFirestore.getInstance();
        myFoodList = new ArrayList<RecipeData>();
        mypostAdapter = new MyPostAdapter(getActivity(), myFoodList);

        recyclerView.setAdapter(mypostAdapter);

        EventChangeListener();

    }


    private void EventChangeListener() {

        String recipe_id = db.collection("recipe").getId();

        CollectionReference recipeRef = db.collection("recipe");



        recipeRef.whereEqualTo("user_id" , "bs4k9NoVopfGlKwWNhuSvztNOEI2" )
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore error", error.getMessage());
                            return;

                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {

                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                myFoodList.add(dc.getDocument().toObject(RecipeData.class));

                            }

                            mypostAdapter.notifyDataSetChanged();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                        }

                    }
                });
    }
}