package com.example.smartbottledraft;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    final int RECIEVE_MESSAGE = 1;

    private static final String TAG = "MainActivity";
    private TextView bluetoothData;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private Handler h;
    private StringBuilder sb = new StringBuilder();
    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // change this line depending on our bluetooth module MAC address
    private static String address = "00:15:FF:F2:19:5F";

    private BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                // parameters are status of bluetooth, default error code
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: State OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: State turning OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: State ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onReceive: State turning ON");
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothData = (TextView)findViewById(R.id.bluetoothData);

        // handler to display the data on the app
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);          // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);               // extract string
                            sb.delete(0, sb.length());                                      // and clear
                            bluetoothData.setText("Data from Arduino: " + sbprint);            // update TextView
                        }
                        //Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;
                }
            };
        };
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try{
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[]{UUID.class} );
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
    }

    /*
        Attempt to connect to the bluetooth device by using a connected thread
        Need to create a bluetooth socket to work with the server
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "...onResume - try connect...");

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        try{
            btSocket = createBluetoothSocket(device);
        }catch(Exception e){
            e.printStackTrace();
        }

        mBluetoothAdapter.cancelDiscovery();
        Log.d(TAG, "...Connecting...");
        try{
            btSocket.connect();
        }catch(IOException e){
            try{
                btSocket.close();
            }catch(IOException e2){
                e2.printStackTrace();
            }
        }

        Log.d(TAG, "...Create Socket...");
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "...In onPause()...");
        try{
            btSocket.close();
        }catch(IOException e2){
            e2.printStackTrace();
        }
    }

    // go to the How to Play screen
    public void onClickTutorial(View view){
        // activity to launch
        Intent i = new Intent(this, HowToUseScreen.class);
        startActivity(i);
    }

    // quit the application
    public void onClickQuit(View view){
        finish();
        moveTaskToBack(true);
    }

    // go to the modes screen
    public void onClickFresh(View view){
        Intent i = new Intent(this, ModesScreen.class);
        startActivity(i);
    }

    public void onClickBluetooth(View view){
        // device does not have bluetooth
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisable: Does not have BT capabilities");
        }
        // enable bluetooth
        else if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT enable");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            // the intentfilter is to catch the status of bluetooth like on/off
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        // disable bluetooth
        else if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT disable");
            mBluetoothAdapter.disable();

            // the intentfilter is to catch the status of bluetooth like on/off
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
    }

    /*
        The connected thread is to connect between the app and bluetooth module
     */
    private class ConnectedThread extends Thread{
        private final InputStream mInputStream;
        public ConnectedThread(BluetoothSocket socket){
            InputStream tempIn = null;
            try{
                tempIn = socket.getInputStream();
            }catch (Exception e){
                e.printStackTrace();
            }
            mInputStream = tempIn;
        }

        // receive data via the thread
        public void run(){
            byte[] buffer = new byte[256];
            int bytes;
            while(true){
                try{
                    bytes = mInputStream.read(buffer);
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();
                }catch(Exception e){
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
