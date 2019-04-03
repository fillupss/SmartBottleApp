package com.example.smartbottledraft;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/*
    This service will transmit bluetooth data to the different mode screens
 */

public class MyService extends Service {

    final int RECIEVE_MESSAGE = 1;

    private static final String TAG = "BluetoothService";
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private Handler h;
    private StringBuilder sb = new StringBuilder();
    private ConnectedThread mConnectedThread;
    private ConnectingThread mConnectingThread;
    private  static String sbprint; // the data to be used in other activities

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC address of the HC-05 bluetooth module
    private static String address = "00:14:03:06:8E:DB";

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
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"Service Created");

    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service Started");
        // handler to display the data on the app
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf,0,msg.arg1);            // convert the data message to a string
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("~");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,
                            sbprint = sb.substring(1, endOfLineIndex);               // extract string, first index is #
                            sb.delete(0, sb.length());
                            Log.d(TAG, sbprint);
                        }
                        break;
                }
            };
        };
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
        return super.onStartCommand(intent, flags, startId);
    }


    // Fix this function
    private void checkBTState(){
        // device does not have bluetooth
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisable: Does not have BT capabilities");
            stopSelf();
        }
        else{
            if(mBluetoothAdapter.isEnabled()){
                try{
                    Log.d(TAG, "Attempting to connect to bluetooth device");
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    mConnectingThread = new ConnectingThread(device);
                    mConnectingThread.start();
                }catch(Exception e){
                    e.printStackTrace();
                    stopSelf();
                }
            }
            else{
                Log.d(TAG,"Bluetooth not turned on. Stopping service");
                stopSelf();
            }
        }
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        if(mConnectedThread != null){
            mConnectedThread.close();
        }
        stopSelf();
    }


    /*
        Create socket to connect to the server. Proceeds to ConnectedThread if successful
        Checks if the device is compatible
     */
    private class ConnectingThread extends Thread{

        private BluetoothSocket mSocket;
        private BluetoothDevice mDevice;

        public ConnectingThread(BluetoothDevice device){

            mDevice = device;

            try{
                btSocket = createBluetoothSocket(mDevice);
            }catch(Exception e){
                e.printStackTrace();
            }

            mSocket = btSocket;
        }

        @Override
        public void run(){
            super.run();
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "...Connecting...");
            try{
                mSocket.connect();
            }catch(IOException e){
                try{
                    mSocket.close();
                }catch(IOException e2){
                    e2.printStackTrace();
                }
            }

            Log.d(TAG, "...Create Socket...");
            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();
        }
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

        public void close(){
            try{
                btSocket.close();
            }catch(IOException e2){
                e2.printStackTrace();
            }
        }
    }

    public static String getData(){
        return sbprint;
    }

}
