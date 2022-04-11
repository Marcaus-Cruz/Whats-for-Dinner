package edu.wwu.csci412.whatsfordinner.ui.pantry;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PantryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PantryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Welcome to your pantry!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}