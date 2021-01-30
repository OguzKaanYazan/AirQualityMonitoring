package com.iot.airqualitymonitoring.ui.summary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.iot.airqualitymonitoring.Measurement;
import com.iot.airqualitymonitoring.MeasurementListAdapter;
import com.iot.airqualitymonitoring.R;
import com.iot.airqualitymonitoring.Result;
import com.iot.airqualitymonitoring.UbidotsApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SummaryFragment extends Fragment {

    private View root;
    private static String TAG = "FragmentActivity";
    private ArrayList<Measurement> Measurements;
    private ListView listView;
    private MeasurementListAdapter listViewAdapter;
    private UbidotsApi ubidotsApi;
    private TextView textViewMax, textViewMin, textViewMean, textViewCount;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_summary, container, false);
        textViewMax = root.findViewById(R.id.airqmax);
        textViewMin = root.findViewById(R.id.airqmin);
        textViewMean = root.findViewById(R.id.airqmean);
        textViewCount = root.findViewById(R.id.airqcount);
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
                Measurements = measurements;
                maxValue(Measurements);
                minValue(Measurements);
                meanValue(Measurements);
                count(Measurements);
                Log.e(TAG, "measurement 1 " + measurements.get(0).getAir_quality());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e(TAG, "WEB SERVICE ERROR " + t);
            }
        });
    }
    public void maxValue(ArrayList<Measurement> m){
        List<Integer> values = new ArrayList<>();
        for (Measurement measurement : m) {
            values.add(measurement.getAir_quality().intValue());
        }
        int max = Integer.MIN_VALUE;

        for(int i=0; i<values.size(); i++){
            if(values.get(i) > max){
                max = values.get(i);
            }
        }

        textViewMax.setText(String.valueOf(max));

    }
    public void minValue(ArrayList<Measurement> m){
        List<Integer> minValues = new ArrayList<>();
        for (Measurement measurement : m) {
            minValues.add(measurement.getAir_quality().intValue());
        }
        int min = Integer.MAX_VALUE;

        for(int i=0; i<minValues.size(); i++){
            if(minValues.get(i) < min){
                min = minValues.get(i);
            }
        }

        textViewMin.setText(String.valueOf(min));

    }
    public void meanValue(ArrayList<Measurement> m){
        List<Integer> meanValues = new ArrayList<>();
        for (Measurement measurement : m) {
            meanValues.add(measurement.getAir_quality().intValue());
        }
        int sum = 0;

        for(int i=0; i<meanValues.size(); i++){
                sum += meanValues.get(i);

        }
        int mean = sum/meanValues.size();

        textViewMean.setText(String.valueOf(mean));

    }
    public void count(ArrayList<Measurement> m){
        List<Integer> countArray = new ArrayList<>();
        for (Measurement measurement : m) {
            countArray.add(measurement.getAir_quality().intValue());
        }


        textViewCount.setText(String.valueOf(countArray.size()));

    }

}