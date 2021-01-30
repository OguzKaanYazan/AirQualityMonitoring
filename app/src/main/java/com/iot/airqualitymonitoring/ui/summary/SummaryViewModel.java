package com.iot.airqualitymonitoring.ui.summary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SummaryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SummaryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("SUMMARY");
    }

    public LiveData<String> getText() {
        return mText;
    }
}