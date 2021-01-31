package com.iot.airqualitymonitoring.ui.bar_chart;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.iot.airqualitymonitoring.Measurement;
import com.iot.airqualitymonitoring.R;
import com.iot.airqualitymonitoring.Result;
import com.iot.airqualitymonitoring.UbidotsApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BarChartFragment extends Fragment {

    private View root;
    private static String TAG = "FragmentActivity";
    private BarChart barChart;
    private UbidotsApi ubidotsApi;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        barChart = (BarChart) root.findViewById(R.id.chart);
        initialize();
        return root;
    }

    private void initialize() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://industrial.api.ubidots.com/api/v1.6/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ubidotsApi = retrofit.create(UbidotsApi.class);
        getMeasurements();
    }

    public void getMeasurements() {
        Call<Result> call = ubidotsApi.getMeasurements("BBFF-norosSxHQrXBNKtipKjr5RiSM9n4wY",
                "6013d8a21d84722eebaac9f6");

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "UNSUCCESSFUL");
                    return;
                }
                ArrayList<Measurement> measurements = response.body().getResults();
                setChart(measurements);
                Log.e(TAG, "measurement 1 " + measurements.get(0).getAir_quality());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e(TAG, "WEB SERVICE ERROR " + t);
            }
        });
    }

    public void setChart(ArrayList<Measurement> m) {
        ArrayList<Integer> statusList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            statusList.add(0);
        }
        for (Measurement measurement : m) {
            switch (measurement.getStatusTxt()) {
                case "Great":
                    statusList.set(0, statusList.get(0) + 1);
                    break;
                case "Okay":
                    statusList.set(1, statusList.get(1) + 1);
                    break;
                case "Sensitive beware":
                    statusList.set(2, statusList.get(2) + 1);
                    break;
                case "Unhealthy":
                    statusList.set(3, statusList.get(3) + 1);
                    break;
                case "Very Unhealthy":
                    statusList.set(4, statusList.get(4) + 1);
                    break;
                default:
                    statusList.set(5, statusList.get(5) + 1);
            }
        }

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Great");
        labels.add("Okay");
        labels.add("Beware");
        labels.add("Unhealthy");
        labels.add("Very Unhealthy");
        labels.add("Hazardous");

        for (int i = 0; i < 6; i++) {
            Log.e(TAG, statusList.get(i).toString());
        }
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int measurement : statusList) {
            entries.add(new BarEntry(statusList.indexOf(measurement), measurement));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        BarData barData = new BarData(barDataSet);

        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setDrawGridLines(false);
        barChart.invalidate();
    }
}