package com.example.foodrecipe.ui.mypost;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyPostViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public MyPostViewModel() {
        mText = new MutableLiveData<>();


    }

    public LiveData<String> getText() {
        return mText;
    }
}