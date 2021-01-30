package com.iot.airqualitymonitoring.ui.visual_report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReportViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ReportViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("VISUAL REPORT");
    }

    public LiveData<String> getText() {
        return mText;
    }
}