package edu.wwu.csci412.whatsfordinner.ui.shoppinglist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShoppingListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ShoppingListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Here is your shopping list");
    }

    public LiveData<String> getText() {
        return mText;
    }
}