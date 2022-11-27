package com.example.foodrecipe.ui.saved;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SavedViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SavedViewModel() {
        mText = new MutableLiveData<>();


    }

    public LiveData<String> getText() {
        return mText;
    }
}