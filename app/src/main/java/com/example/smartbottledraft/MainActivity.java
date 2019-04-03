package com.example.smartbottledraft;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    MyService myService;
    boolean isBound = false;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void onResume() {
        super.onResume();
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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickQuit(View view){
        finishAffinity();
        System.exit(0);
    }

    // go to the modes screen
    public void onClickFresh(View view){
        Intent i = new Intent(this, ModesScreen.class);
        startActivity(i);
    }

    public void onClickBluetooth(View view){
        // Start connecting to service here
        Toast toast = Toast.makeText(this, "Connecting to service!", Toast.LENGTH_LONG);
        toast.show();
        Intent i = new Intent(this, MyService.class);
        startService(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}