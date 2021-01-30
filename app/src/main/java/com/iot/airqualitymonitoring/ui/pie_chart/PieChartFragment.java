package com.iot.airqualitymonitoring.ui.pie_chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.iot.airqualitymonitoring.R;

public class PieChartFragment extends Fragment {

    private View root;
    private static String TAG = "FragmentActivity";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        return root;
    }

}