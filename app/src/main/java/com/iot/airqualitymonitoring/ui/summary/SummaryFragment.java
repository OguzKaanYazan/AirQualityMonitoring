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

import com.iot.airqualitymonitoring.Measurement;
import com.iot.airqualitymonitoring.MeasurementListAdapter;
import com.iot.airqualitymonitoring.R;
import com.iot.airqualitymonitoring.Result;
import com.iot.airqualitymonitoring.UbidotsApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SummaryFragment extends Fragment {

    private SummaryViewModel homeViewModel;
    private View root;
    private static String TAG = "FragmentActivity";
    private ArrayList<Measurement> Measurements;
    private ListView listView;
    private MeasurementListAdapter listViewAdapter;
    private UbidotsApi ubidotsApi;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(SummaryViewModel.class);
        root = inflater.inflate(R.layout.fragment_summary, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
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
                Log.e(TAG, "measurement 1 " + measurements.get(0).getAir_quality());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e(TAG, "WEB SERVICE ERROR " + t);
            }
        });
    }
}