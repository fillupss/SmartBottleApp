package com.example.smartbottledraft;

import android.content.ComponentName;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.content.ServiceConnection;
import android.widget.TextView;

import com.example.smartbottledraft.MyService.MyLocalBinder;

public class MainActivity extends AppCompatActivity {

    MyService myService;
    boolean isBound = false;
    private TextView bluetoothData;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothData = findViewById(R.id.bluetoothData);
    }

    /*
        Attempt to connect to the bluetooth device by using a connected thread
        Need to create a bluetooth socket to work with the server
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "" + isBound);
        if(isBound)
            bluetoothData.setText(myService.getData());
    }


    @Override
    public void onPause(){
        super.onPause();
    }

    // go to the How to Play screen
    public void onClickTutorial(View view){
        // activity to launch
        Intent i = new Intent(this, HowToUseScreen.class);
        startActivity(i);
    }

    // quit the application
    public void onClickQuit(View view){
        //finish();
        //moveTaskToBack(true);
        bluetoothData.setText(myService.getData());
    }

    // go to the modes screen
    public void onClickFresh(View view){
        Intent i = new Intent(this, ModesScreen.class);
        startActivity(i);
    }

    public void onClickBluetooth(View view){
        // Start connecting to service here
        Intent i = new Intent(this, MyService.class);
        //bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyLocalBinder binder = (MyLocalBinder) service;
            myService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
}
