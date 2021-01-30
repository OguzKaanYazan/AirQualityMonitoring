package com.iot.airqualitymonitoring.ui.line_chart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.iot.airqualitymonitoring.Measurement;
import com.iot.airqualitymonitoring.R;
import com.iot.airqualitymonitoring.Result;
import com.iot.airqualitymonitoring.UbidotsApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LineChartFragment extends Fragment {

    private View root;
    private static String TAG = "FragmentActivity";
    private UbidotsApi ubidotsApi;
    LineChart lineChart ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_line_chart, container, false);
        lineChart = (LineChart) root.findViewById(R.id.chart);
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
                setCharts(measurements);
                Log.e(TAG, "measurement 1 " + measurements.get(0).getAir_quality());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e(TAG, "WEB SERVICE ERROR " + t);
            }
        });
    }

    public void setCharts(ArrayList<Measurement> m){
        List<Entry> entries = new ArrayList<Entry>();

        for (Measurement measurement : m) {
            entries.add(new Entry(m.indexOf(measurement), measurement.getAir_quality().floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Air Quality"); // add entries to dataset
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();

    }
}