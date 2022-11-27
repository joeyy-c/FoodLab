package com.example.foodrecipe.ui.mypost;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodrecipe.R;

public class MyPostFragment extends Fragment {

    private MyPostViewModel mViewModel;

    public static MyPostFragment newInstance() {
        return new MyPostFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_post, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MyPostViewModel.class);
        // TODO: Use the ViewModel

        if (getArguments() != null) {
            Toast.makeText(getActivity(), getArguments().getString("recipe_id"), Toast.LENGTH_SHORT).show();
        }

    }

}