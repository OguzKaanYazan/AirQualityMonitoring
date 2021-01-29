package com.iot.airqualitymonitoring;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeasurementList extends AppCompatActivity {
    private static String TAG = "FragmentActivity";
    private ArrayList<Measurement> Measurements;
    private ListView listView;
    private MeasurementListAdapter listViewAdapter;
    private UbidotsApi ubidotsApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        initialize();
        getMeasurements();


    }
    private void initialize() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://industrial.api.ubidots.com/api/v1.6/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ubidotsApi = retrofit.create(UbidotsApi.class);
    }

    private void setMeasurementList(ArrayList<Measurement> measurements) {
        Measurements = measurements;
        listView = (ListView) findViewById(R.id.listView);
        listViewAdapter = new MeasurementListAdapter(MeasurementList.this, Measurements);
        listView.setAdapter(listViewAdapter);
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
                setMeasurementList(measurements);
                Log.e(TAG, "measurement 1 " + measurements.get(0).getAir_quality());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e(TAG, "WEB SERVICE ERROR " + t);
            }
        });
    }

}
