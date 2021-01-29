package com.iot.airqualitymonitoring;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.Double.parseDouble;


public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private final static int REQUEST_ENABLE_BT = 1;
    private static UUID MY_UUID = UUID.fromString("7ff8a1fe-23fd-4f7b-84be-33d822e5868d");
    private static String TAG = "FragmentActivity";
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayList<Measurement> measurements;
    private ListView listView;
    private MeasurementListAdapter listViewAdapter;
    private UbidotsApi ubidotsApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);
        initialize();
        //fillArrayList(measurements);
        //getMeasurements();
        Measurement m = new Measurement("XX", 58.0);
        //insertMeasurement(m);
    }

    private void initialize() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableBluetooth();
        } else {
            requestLocationPermission();
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://industrial.api.ubidots.com/api/v1.6/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ubidotsApi = retrofit.create(UbidotsApi.class);
        measurements = new ArrayList<Measurement>();
        listView = (ListView) findViewById(R.id.listView);
        listViewAdapter = new MeasurementListAdapter(MainActivity.this, measurements);
        listView.setAdapter(listViewAdapter);
    }

    private void fillArrayList(ArrayList<Measurement> measurements) {
        for (int index = 0; index < 20; index++) {
            Measurement measurement = new Measurement("13.01.2021 18:00 ", 20.0 * index);
            measurements.add(measurement);
        }
    }

    public void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableBluetooth();
        }
    }


    private void enableBluetooth() {
        if (bluetoothAdapter != null) {

            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                bluetoothAdapter.startDiscovery();
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Discovery Start", Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Discovery End", Toast.LENGTH_SHORT).show();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName().contains("HC")) { //E8:EC:A3:94:8B:75 Mi True Wireless EBs Basic_R
                    ConnectThread connectThread = new ConnectThread(device);
                    connectThread.start();
                    Log.e(TAG, "ARDUINO FOUND");
                } else {
                    Log.e(TAG, "Found Device " + device.getName() + " --- " + device.getAddress());
                }


            } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    bluetoothAdapter.startDiscovery();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;
            Log.e(TAG, "Device" + mmDevice);

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
            Log.e(TAG, mmSocket.toString());
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {

                try {
                    Class<?> clazz = mmSocket.getRemoteDevice().getClass();
                    Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};

                    Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[]{Integer.valueOf(1)};

                    mmSocket = (BluetoothSocket) m.invoke(mmSocket.getRemoteDevice(), params);
                    mmSocket.connect();
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | IOException e) {
                    e.printStackTrace();
                }
            }
            MyBluetoothService.ConnectedThread connectedThread = new MyBluetoothService.ConnectedThread(mmSocket, mHandler);
            connectedThread.start();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }

    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            byte[] readBuf = (byte[]) msg.obj;
            // construct a string from the valid bytes in the buffer
            String readMessage = new String(readBuf, 0, msg.arg1);
            Measurement m = new Measurement("XX", parseDouble(readMessage));
            insertMeasurement(m);
            Log.e(TAG, "Air Quality Value: " + readMessage);
        }
    };

    public void getMeasurements() {
        Call<Result> call = ubidotsApi.getMeasurements("BBFF-ItcDdqK6QlvvhSoZfbiT5qqKMXM2Y0Xg2nJSOsXosSTBd0P1BVH3uIY",
                "5ffb67f51d847259aadf54e2");

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

    public void insertMeasurement(Measurement measurement) {
        Call<Measurement> call = ubidotsApi.insertMeasurement("BBFF-4WdB1TJgsLvbybwSMKB9sZvkH7Q37hUw98efPaSNDeiLqYHSQzbkBiX",
                measurement);

        call.enqueue(new Callback<Measurement>() {
            @Override
            public void onResponse(Call<Measurement> call, Response<Measurement> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "UNSUCCESSFUL " + response.message());
                    return;
                }
            }

            @Override
            public void onFailure(Call<Measurement> call, Throwable t) {
                Log.e(TAG, "WEB SERVICE ERROR " + t);
            }
        });
    }

}