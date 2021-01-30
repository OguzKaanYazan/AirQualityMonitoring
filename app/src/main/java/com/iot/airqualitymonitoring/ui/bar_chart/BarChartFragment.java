package com.iot.airqualitymonitoring.ui.bar_chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.iot.airqualitymonitoring.R;

public class BarChartFragment extends Fragment {

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        return root;
    }

}