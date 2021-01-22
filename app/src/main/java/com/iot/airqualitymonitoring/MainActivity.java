package com.iot.airqualitymonitoring;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Measurement> measurements;
    private ListView listView;
    private MeasurementListAdapter listViewAdapter;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        fillArrayList(measurements);
    }

    private void initialize() {
        measurements = new ArrayList<Measurement>();
        listView = (ListView) findViewById(R.id.listView);
        listViewAdapter = new MeasurementListAdapter(MainActivity.this,measurements);
        listView.setAdapter(listViewAdapter);
    }

    private void fillArrayList(ArrayList<Measurement>measurements){
        for (int index = 0; index < 20; index++) {
            Measurement measurement = new Measurement("13.01.2021 18:00 ", 20 * index);
            measurements.add(measurement);
        }

    }

    private String serviceRequest(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}

