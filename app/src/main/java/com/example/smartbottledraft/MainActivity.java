package com.example.smartbottledraft;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BluetoothAdapter mBluetoothAdapter;
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
}
