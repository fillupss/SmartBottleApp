package com.example.smartbottledraft;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class WorkoutScreen extends AppCompatActivity {

    private long secondsToAlarm = 70000; // will change later
    private CountDownTimer countDownTimer;
    private TextView displayTime, displayData;
    private MediaPlayer player;
    private int[] samples;
    private int sampleIndex, sum, previousValue, currentValue;
    private int difference = 50;
    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        samples = new int[8];
        sampleIndex = 0;
        sum = 0;
        currentValue = Integer.parseInt(MyService.getData());
        setContentView(R.layout.activity_workout_screen);
        displayTime = (TextView)findViewById(R.id.displayTime);
        displayData = (TextView)findViewById(R.id.displayData);
        startTimer();
        thread = new Thread(){
            @Override
            public void run(){
                try{
                    while(!isInterrupted()){
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateData();
                                if(Math.abs(previousValue - currentValue) > difference){
                                    resetTimer();
                                }
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void startTimer(){
        // 900 to avoid the bug of stopping at 1 second
        countDownTimer = new CountDownTimer(secondsToAlarm, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsToAlarm = millisUntilFinished;
                displayTimer();
            }

            @Override
            public void onFinish() {
                // play sound
                if(player == null){
                    player = MediaPlayer.create(WorkoutScreen.this, R.raw.sound);
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopPlaying();
                        }
                    });
                }
                player.start();
                countDownTimer.cancel();
            }
        }.start();
    }

    // stop alarm button clicked
    public void stopPlayer(View view){
        stopPlaying();
    }

    private void displayTimer(){
        long minutes = secondsToAlarm / 60000;
        long seconds = secondsToAlarm % 60000 / 1000;
        if(seconds < 10){
            displayTime.setText(minutes + ":0" + seconds);
        }
        else{
            displayTime.setText(minutes + ":" + seconds);
        }
    }

    // disable the alarm
    private void stopPlaying(){
        if(player != null){
            player.release();
            player = null;
        }
    }

    // release the media file after the app closes
    @Override
    protected void onStop() {
        super.onStop();
        stopPlaying();
    }

    // back button
    public void goBack(View view){
        countDownTimer.cancel();
        thread.interrupt();
        Intent i = new Intent(this, ModesScreen.class);
        startActivity(i);
    }

    private void resetTimer(){
        countDownTimer.cancel();
        countDownTimer.start();
    }

    private void updateData(){
        try{
            samples[sampleIndex] = Integer.parseInt(MyService.getData());
            sum += samples[sampleIndex];
        }catch (Exception e){
            e.printStackTrace();
        }
        if(sampleIndex == samples.length - 1){
            int average = sum / samples.length;
            displayData.setText(average);
            sum = 0;
            sampleIndex = 0;
            previousValue = currentValue;
            currentValue = average;
        }
        else{
            sampleIndex++;
        }
    }
}
