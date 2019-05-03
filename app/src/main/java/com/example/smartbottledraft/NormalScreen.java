package com.example.smartbottledraft;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class NormalScreen extends AppCompatActivity {

    final float RESOLUTION = (float)(33.75/1027);
    final float DIFFERENCE = 1.5f;
    private long secondsToAlarm = 15000; // will change later (15 seconds)
    private CountDownTimer countDownTimer;
    private TextView displayTime, displayData;
    private ImageView bottle;
    private MediaPlayer player;
    private int[] samples;
    private int sampleIndex, sum;
    private float previousValue, currentValue;
    private Thread thread;
    private String formattedWeight;
    private boolean isMusicPlaying, resetTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        samples = new int[4]; // change to 4
        sampleIndex = 0;
        sum = 0;
        isMusicPlaying = false;
        resetTime = false;
        currentValue = Integer.parseInt(MyService.getData()) * RESOLUTION;
        previousValue = currentValue;
        setContentView(R.layout.activity_normal_screen);
        displayTime = (TextView)findViewById(R.id.displayTime);
        displayData = (TextView)findViewById(R.id.displayData);
        bottle = (ImageView)findViewById(R.id.bottle);
        formattedWeight = String.format("%.02f", currentValue);
        displayData.setText(formattedWeight + " fluid oz");
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
                                // check if the bottle is in the platform
                                if(Integer.parseInt(MyService.getData()) > 200){
                                    updateData();
                                }
                                if(Math.abs(previousValue - currentValue) > DIFFERENCE && resetTime){
                                    resetTimer();
                                    resetTime = false;
                                }

                                // set the approximate water bottle content depending on the weight
                                if(currentValue > 20){
                                    bottle.setImageResource(R.mipmap.waterbottle4);
                                }
                                else if(currentValue > 17.5){
                                    bottle.setImageResource(R.mipmap.waterbottle3);
                                }
                                else if(currentValue > 15.0){
                                    bottle.setImageResource(R.mipmap.waterbottle2);
                                }
                                else if(currentValue > 12.5){
                                    bottle.setImageResource(R.mipmap.waterbottle1);
                                }
                                else{
                                    bottle.setImageResource(R.mipmap.waterbottle0);
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
                    player = MediaPlayer.create(NormalScreen.this, R.raw.sound);
//                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            stopPlaying();
//                        }
//                    });
                    player.setLooping(true);
                    player.setVolume(60,60);
                }
                player.start();
                isMusicPlaying = true;
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
        // button click only resets timer if the alarm is playing
        if(isMusicPlaying){
            resetTimer();
        }
        if(player != null){
            isMusicPlaying = false;
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
//        if(player != null){
//            isMusicPlaying = false;
//            player.release();
//            player = null;
//        }
        stopPlaying();
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
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
            float average = (sum * RESOLUTION / samples.length);
            formattedWeight = String.format("%.01f", average);
            displayData.setText(formattedWeight + " fluid oz");
            sum = 0;
            sampleIndex = 0;
            previousValue = currentValue;
            currentValue = average;
            resetTime = true;
        }
        else{
            sampleIndex++;
        }
    }
}

