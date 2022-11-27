package com.example.foodrecipe.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipe.R;
import com.example.foodrecipe.databinding.FragmentHomeBinding;
import com.example.foodrecipe.ui.RecipeDetailsFragment;
import com.example.foodrecipe.ui.login.Login;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    RecyclerView recyclerView;
    ArrayList<RecipeData> myFoodList;
    HomeAdapter homeAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    FirebaseStorage storage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Fetching Data");
        progressDialog.show();

        recyclerView = getView().findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        myFoodList = new ArrayList<RecipeData>();
        homeAdapter = new HomeAdapter(getActivity(), myFoodList);

        recyclerView.setAdapter(homeAdapter);


        EventChangeListener();

    }



    private void EventChangeListener() {

        String recipe_id = db.collection("recipe").getId();

        db.collection("recipe").orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error != null){

                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore error",error.getMessage());
                            return;

                        }

                        for(DocumentChange dc : value.getDocumentChanges()){

                            if(dc.getType() == DocumentChange.Type.ADDED){

                                RecipeData repData = dc.getDocument().toObject(RecipeData.class);
                                repData.setRecipe_id(dc.getDocument().getId());

                                myFoodList.add(dc.getDocument().toObject(RecipeData.class));



                            }

                            homeAdapter.notifyDataSetChanged();
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();

                        }

                        homeAdapter.setOnItemClickListener(new HomeAdapter.ClickListener() {
                            @Override
                            public void onItemClick(int position, View v) {
                                Toast.makeText(getActivity(), "onItemClick position: " + position, Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onItemLongClick(int position, View v) {
                                Toast.makeText(getActivity(), "onItemLongClick pos= " + position, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

    }

}