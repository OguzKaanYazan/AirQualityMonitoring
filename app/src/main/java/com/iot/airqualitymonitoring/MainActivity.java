package com.iot.airqualitymonitoring;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private static UUID MY_UUID = UUID.fromString("7ff8a1fe-23fd-4f7b-84be-33d822e5868d");
    private static String TAG = "FragmentActivity";
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayList<Measurement> measurements;
    private ListView listView;
    private MeasurementListAdapter listViewAdapter;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enableBluetooth();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);
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

    private void enableBluetooth(){
        if (bluetoothAdapter != null) {

            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }else{
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
                Toast.makeText(getApplicationContext(),"Discovery Start",Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(getApplicationContext(),"Discovery End",Toast.LENGTH_SHORT).show();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getName().contains("HC")){ //E8:EC:A3:94:8B:75 Mi True Wireless EBs Basic_R
                    ConnectThread connectThread = new ConnectThread(device);
                    connectThread.start();
                    Log.e(TAG, "ARDUINO FOUND");
                }else{
                    Log.e(TAG,"Found Device " + device.getName() + " --- " + device.getAddress());
                }


            } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if(state == BluetoothAdapter.STATE_ON){
                    bluetoothAdapter.startDiscovery();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
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
                    Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};

                    Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[] {Integer.valueOf(1)};

                    mmSocket = (BluetoothSocket) m.invoke(mmSocket.getRemoteDevice(), params);
                    mmSocket.connect();
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | IOException e ) {
                    e.printStackTrace();
                }
               /* try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }*/
            }
            MyBluetoothService.ConnectedThread connectedThread = new MyBluetoothService.ConnectedThread(mmSocket);
             connectedThread.start();


            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            //manageMyConnectedSocket(mmSocket);
            //Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
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


        }




